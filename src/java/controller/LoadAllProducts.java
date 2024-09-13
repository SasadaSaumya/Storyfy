
package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Author;
import entity.Category;
import entity.Product;
import entity.Publisher;
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
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

/**
 *
 * @author sasa
 */
@WebServlet(name = "LoadAllProducts", urlPatterns = {"/LoadAllProducts"})
public class LoadAllProducts extends HttpServlet {

   @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("success", false);

        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        //main code 
        //get category list form db
        Criteria criteria1 = session.createCriteria(Category.class);
        List<Category> categoryList = criteria1.list();
        jsonObject.add("categoryList", gson.toJsonTree(categoryList));

        //get conditionList form db
        Criteria criteria2 = session.createCriteria(Author.class);
        List<Author> authorList = criteria2.list();
        jsonObject.add("authorList", gson.toJsonTree(authorList));

        //get color list form db
        Criteria criteria3 = session.createCriteria(Publisher.class);
        List<Publisher> publisherList = criteria3.list();
        jsonObject.add("publisherList", gson.toJsonTree(publisherList));


        //get product list form db
        Criteria criteria5 = session.createCriteria(Product.class);

        //latest product
        criteria5.addOrder(Order.desc("id"));
        jsonObject.addProperty("allProductCount", criteria5.list().size());

        //1st  product eke idalad 6k ganna kiyala kiyannr
        criteria5.setFirstResult(0);
        criteria5.setMaxResults(6);

        List<Product> productList = criteria5.list();

        for (Product product : productList) {
            product.setUser(null);
        }

        jsonObject.add("productList", gson.toJsonTree(productList));
        jsonObject.addProperty("success", true);

        //main code
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(jsonObject));
        System.out.println(gson.toJson(jsonObject));

        session.close();
    }
}
