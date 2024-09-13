package controller;

import com.google.gson.Gson;
import dto.Cart_DTO;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Cart;
import entity.Product;
import entity.User;
import java.io.IOException;
import java.util.ArrayList;
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
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "AddToCart", urlPatterns = {"/AddToCart"})
public class AddToCart extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();

        String id = request.getParameter("id");
        String qty = request.getParameter("qty");

        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {

            if (!Validations.isInteger(id)) {
                //product not found
                response_DTO.setContent("Product not found");

            } else if (!Validations.isInteger(qty)) {
                //invalid quantity
                response_DTO.setContent("Invalid quantity");

            } else {

                int productId = Integer.parseInt(id);
                int productQty = Integer.parseInt(qty);

                if (productQty <= 0) {

                    //Quantity must be greater than 0
                    response_DTO.setContent("Quantity must be greater than 0");
                } else {

                    Product product = (Product) session.get(Product.class, productId);

                    if (product != null) {

                        //product found
                        if (request.getSession().getAttribute("user") != null) {

                            //DB Cart
                            User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");

                            //get db user
                            Criteria criteria1 = session.createCriteria(User.class);
                            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
                            User user = (User) criteria1.uniqueResult();

                            //check in db cart
                            Criteria criteria2 = session.createCriteria(Cart.class);
                            criteria2.add(Restrictions.eq("user", user));
                            criteria2.add(Restrictions.eq("product", product));

                            if (criteria2.list().isEmpty()) {

                                //cart item not found
                                if (productQty <= product.getQty()) {

                                    //add to cart
                                    Cart cart = new Cart();
                                    cart.setProduct(product);
                                    cart.setQty(productQty);
                                    cart.setUser(user);

                                    session.save(cart);
                                    session.getTransaction().commit();

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Product added to the Cart");

                                } else {

                                    //qty exceed
                                    response_DTO.setContent("Quantity limit exceed");
                                }

                            } else {

                                //item already found
                                Cart cartItem = (Cart) criteria2.uniqueResult();

                                if ((cartItem.getQty() + productQty) <= product.getQty()) {

                                    //add to cart
                                    cartItem.setQty(cartItem.getQty() + productQty);

                                    session.update(cartItem);
                                    transaction.commit();

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Product update successfully");

                                } else {

                                    //qty limit excced, qty not available
                                    response_DTO.setContent("Quantity limit exceed, not available");
                                }

                            }

                        } else {

                            //Session Cart
                            HttpSession httpSession = request.getSession();

                            if (httpSession.getAttribute("sessionCart") != null) {

                                //session cart found
                                ArrayList<Cart_DTO> sessionCart = (ArrayList<Cart_DTO>) httpSession.getAttribute("sessionCart");

                                Cart_DTO foundCart_DTO = null;

                                for (Cart_DTO cart_DTO : sessionCart) {

                                    if (cart_DTO.getProduct().getId() == product.getId()) {

                                        foundCart_DTO = cart_DTO;
                                        break;
                                    }

                                }

                                if (foundCart_DTO != null) {

                                    //product found
                                    if (foundCart_DTO.getQty() + productQty <= product.getQty()) {

                                        //update qty
                                        foundCart_DTO.setQty(foundCart_DTO.getQty() + productQty);

                                        response_DTO.setSuccess(true);
                                        response_DTO.setContent("Product updated successfully");

                                    } else {

                                        //qty limit excced, qty not available
                                        response_DTO.setContent("Quantity not available, limit excced");
                                    }

                                } else {

                                    //product not found
                                    if (productQty <= product.getQty()) {

                                        //add to session cart
                                        Cart_DTO cart_DTO = new Cart_DTO();
                                        cart_DTO.setProduct(product);
                                        cart_DTO.setQty(productQty);
                                        sessionCart.add(cart_DTO);

                                        response_DTO.setSuccess(true);
                                        response_DTO.setContent("Product added to the Cart");

                                    } else {

                                        //quantity not available
                                        response_DTO.setContent("Quantity not available");
                                    }

                                }

                            } else {

                                //session cart not found
                                if (productQty <= product.getQty()) {

                                    //add to session cart
                                    ArrayList<Cart_DTO> sessionCart = new ArrayList<>();

                                    Cart_DTO cart_DTO = new Cart_DTO();
                                    cart_DTO.setProduct(product);
                                    cart_DTO.setQty(productQty);
                                    sessionCart.add(cart_DTO);

                                    httpSession.setAttribute("sessionCart", sessionCart);

                                    response_DTO.setSuccess(true);
                                    response_DTO.setContent("Product added to the Cart");

                                } else {

                                    //quantity not available
                                    response_DTO.setContent("Quantity not available");
                                }

                            }

                        }

                    } else {

                        //product not found
                        response_DTO.setContent("Product not found");
                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            response_DTO.setContent("Unable to process your request");
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
        session.close();
    }

}
