package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.User_DTO;
import entity.Address;
import entity.Cart;
import entity.City;
import entity.User;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sasa
 */
@WebServlet(name = "LoadCheckout", urlPatterns = {"/LoadCheckout"})
public class LoadCheckout extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);

        Gson gson = new Gson();

        HttpSession httpSession = request.getSession();
        Session session = HibernateUtil.getSessionFactory().openSession();

        if (httpSession.getAttribute("user") != null) {

            User_DTO user_DTO = (User_DTO) httpSession.getAttribute("user");

            // Get user from db
            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));
            User user = (User) criteria1.uniqueResult();

            if (user != null) {
                // Get user's last address from db
                Criteria criteria2 = session.createCriteria(Address.class);
                criteria2.add(Restrictions.eq("user", user));
                criteria2.addOrder(Order.desc("id"));
                criteria2.setMaxResults(1);

                List<Address> addressList = criteria2.list();
                Address address = addressList.isEmpty() ? null : addressList.get(0);

                // Get cities from db
                Criteria criteria3 = session.createCriteria(City.class);
                criteria3.addOrder(Order.asc("name"));
                List<City> cityList = criteria3.list();

                // Get cart items from db
                Criteria criteria4 = session.createCriteria(Cart.class);
                criteria4.add(Restrictions.eq("user", user));
                List<Cart> cartList = criteria4.list();

                if (cartList.isEmpty()) {
                    jsonObject.addProperty("message", "Please add products to your cart. Your cart is empty.");
                } else {
                    if (address != null) {
                        // Address in JSON object
                        address.setUser(null);  
                        jsonObject.add("address", gson.toJsonTree(address));
                    } else {
                        jsonObject.addProperty("message", "No address found.");
                    }

                    // Cities in JSON object
                    jsonObject.add("cityList", gson.toJsonTree(cityList));

                    // Cart items in JSON object
                    for (Cart cart : cartList) {
                        cart.setUser(null);  // Avoid circular reference
                        cart.getProduct().setUser(null);
                    }

                    jsonObject.add("cartList", gson.toJsonTree(cartList));

                    jsonObject.addProperty("success", true);
                }
            } else {
                jsonObject.addProperty("message", "User not found.");
            }

        } else {
            jsonObject.addProperty("message", "Not signed in.");
        }
        
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));
        session.close();
    }
}
