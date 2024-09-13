package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.User_DTO;
import entity.Address;
import entity.Cart;
import entity.City;
import entity.Order_Item;
import entity.Order_Status;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "Checkout", urlPatterns = {"/Checkout"})
public class Checkout extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();

        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        HttpSession httpSession = request.getSession();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        boolean isCurrentAddress = requestJsonObject.get("isCurrentAddress").getAsBoolean();
        String first_name = requestJsonObject.get("first_name").getAsString();
        String last_name = requestJsonObject.get("last_name").getAsString();
        String city_id = requestJsonObject.get("city_id").getAsString();
        String address1 = requestJsonObject.get("address1").getAsString();
        String address2 = requestJsonObject.get("address2").getAsString();
        String postal_code = requestJsonObject.get("postal_code").getAsString();
        String mobile = requestJsonObject.get("mobile").getAsString();

        if (httpSession.getAttribute("user") != null) {
            //user signed in

            //get user from db
            User_DTO user_DTO = (User_DTO) httpSession.getAttribute("user");
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
            User user = (User) criteria1.uniqueResult();

            if (isCurrentAddress) {
                //get current address

                Criteria criteria2 = session.createCriteria(Address.class);
                criteria2.add(Restrictions.eq("user", user));
                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);

                if (criteria2.list().isEmpty()) {
                    //Current address not found. Please create a new address.
                    responseJsonObject.addProperty("message", "Current address not found. Please create a new address.");
                } else {
                    //Get the current address
                    Address address = (Address) criteria2.list().get(0);

                    //***Complete the checkout process
                    saveOrders(session, transaction, user, address, responseJsonObject);
                }

            } else {
                //create new address

                if (first_name.isEmpty()) {
                    responseJsonObject.addProperty("message", "Please fill First Name");

                } else if (last_name.isEmpty()) {
                    responseJsonObject.addProperty("message", "Please fill Last Name");

                } else if (!Validations.isInteger(city_id)) {
                    responseJsonObject.addProperty("message", "Invalid City");

                } else {
                    //Check City from DB

                    Criteria criteria3 = session.createCriteria(City.class);
                    criteria3.add(Restrictions.eq("id", Integer.parseInt(city_id)));

                    if (criteria3.list().isEmpty()) {
                        responseJsonObject.addProperty("message", "Invalid City selected");

                    } else {
                        //City found
                        City city = (City) criteria3.list().get(0);

                        if (address1.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill Address Line 1");

                        } else if (address2.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill Address Line 2");
                        } else if (postal_code.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill Postal Code");

                        } else if (postal_code.length() != 5) {
                            responseJsonObject.addProperty("message", "Invalid Postal Code");

                        } else if (!Validations.isInteger(postal_code)) {
                            responseJsonObject.addProperty("message", "Invalid Postal Code");

                        } else if (mobile.isEmpty()) {
                            responseJsonObject.addProperty("message", "Please fill Mobile Number");

                        } else if (!Validations.isMobileNumber(mobile)) {
                            responseJsonObject.addProperty("message", "Invalid Mobil Number");
                        } else {
                            //Create new address

                            Address address = new Address();
                            address.setCity(city);
                            address.setFirst_name(first_name);
                            address.setLast_name(last_name);
                            address.setLine1(address1);
                            address.setLine2(address2);
                            address.setMobile(mobile);
                            address.setPostal_code(postal_code);
                            address.setUser(user);

                            session.save(address);
                            //*** Complete the checkout process

                            saveOrders(session, transaction, user, address, responseJsonObject);
                        }
                    }

                }
            }

        } else {
            //user not
            responseJsonObject.addProperty("message", "User not signed in");
        }
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJsonObject));
    }

    private void saveOrders(Session session, Transaction transaction, User user, Address address, JsonObject responseJsonObject) {

        try {
            //Create Order in DB

            entity.Orders orders = new entity.Orders();
            orders.setAddress(address);
            orders.setDate_time(new Date());
            orders.setUser(user);
            session.save(orders);

            //Get Cart Items
            Criteria criteria4 = session.createCriteria(Cart.class);
            criteria4.add(Restrictions.eq("user", user));
            List<Cart> cartList = criteria4.list();
            //Get Order Status (1.Paid) from DB

            Order_Status order_Status = (Order_Status) session.get(Order_Status.class, 1);
            //Create Order Item in DB
            for (Cart cartItem : cartList) {
                //Get Product

                Product product = cartItem.getProduct();

                Order_Item order_Item = new Order_Item();
                order_Item.setOrder(orders);
                order_Item.setOrder_Status(order_Status);
                order_Item.setProduct(product);
                order_Item.setQty(cartItem.getQty());
                session.save(order_Item);

                //Update Product Qty in DB
                product.setQty(product.getQty() - cartItem.getQty());
                session.update(product);

                //Delete Cart Item from DB
                session.delete(cartItem);
            }
            transaction.commit();

            responseJsonObject.addProperty("success", true);
            responseJsonObject.addProperty("message", "Checkout Completed");

        } catch (Exception e) {
            transaction.rollback();
        }

    }
}
