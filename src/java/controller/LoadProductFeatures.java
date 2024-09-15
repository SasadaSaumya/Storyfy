package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Address;
import entity.Author;
import entity.Category;
import entity.Product;
import entity.Product_Status;
import entity.Publisher;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sasa
 */
@WebServlet(name = "LoadProductFeatures", urlPatterns = {"/LoadProductFeatures"})
public class LoadProductFeatures extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();

        Gson gson = new Gson();

        Session session = HibernateUtil.getSessionFactory().openSession();

        Criteria criteria2 = session.createCriteria(Author.class);
        criteria2.addOrder(Order.asc("name"));
        List<Author> authorList = criteria2.list();

        Criteria criteria1 = session.createCriteria(Category.class);
        criteria1.addOrder(Order.asc("name"));
        List<Category> categoryList = criteria1.list();

        Criteria criteria3 = session.createCriteria(Product_Status.class);
        criteria3.addOrder(Order.asc("name"));
        List<Product_Status> statusList = criteria3.list();

        Criteria criteria4 = session.createCriteria(Publisher.class);
        criteria4.addOrder(Order.asc("id"));
        List<Publisher> publisherList = criteria4.list();

        //get user details 
        User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");

        //get user all data
        Criteria criteria6 = session.createCriteria(User.class);
        criteria6.add(Restrictions.eq("email", user_DTO.getEmail()));
        User user = (User) criteria6.uniqueResult();

        //get user address list 
        Criteria criteria5 = session.createCriteria(Address.class);
        criteria5.add(Restrictions.eq("user", user));
        List<Address> userAddressList = criteria5.list();

        for (Address address : userAddressList) {
            address.setUser(null);
        }

        //get user added product list 
        Criteria criteria7 = session.createCriteria(Product.class);
        criteria7.add(Restrictions.eq("user", user));
        List<Product> userAddedProductList = criteria7.list();

        JsonObject jsonObject = new JsonObject();
        jsonObject.add("authorList", gson.toJsonTree(authorList));
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));
        jsonObject.add("statusList", gson.toJsonTree(statusList));
        jsonObject.add("publisherList", gson.toJsonTree(publisherList));
        jsonObject.add("userData", gson.toJsonTree(user_DTO));
        jsonObject.add("userAddressList", gson.toJsonTree(userAddressList));
        jsonObject.add("userAddedProductList", gson.toJsonTree(userAddedProductList));

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));
        session.close();
    }

}
