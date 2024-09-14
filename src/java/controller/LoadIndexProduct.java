package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Category;
import entity.Product;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sasa
 */
@WebServlet(name = "LoadIndexProduct", urlPatterns = {"/LoadIndexProduct"})
public class LoadIndexProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        Session session = HibernateUtil.getSessionFactory().openSession();
        try {

            Criteria criteria1 = session.createCriteria(Category.class);
            criteria1.add(Restrictions.eq("name", "Fantasy"));
            Category category = (Category) criteria1.uniqueResult();

            Criteria criteria2 = session.createCriteria(Product.class);
            criteria2.add(Restrictions.eq("category", category));
            criteria2.setMaxResults(7);

            List<Product> productList = criteria2.list();
            for (Product product1 : productList) {
                product1.getUser().setPassword(null);
                product1.getUser().setVerification(null);
                product1.getUser().setEmail(null);
            }

            // new products 
            Criteria criteria3 = session.createCriteria(Product.class);
            criteria3.addOrder(Order.desc("date_time"));
            criteria3.setMaxResults(7);

            List<Product> productListDate = criteria3.list();
            for (Product product1 : productListDate) {
                product1.getUser().setPassword(null);
                product1.getUser().setVerification(null);
                product1.getUser().setEmail(null);
            }

            JsonObject jsonObject = new JsonObject();
            jsonObject.add("productList", gson.toJsonTree(productList));
            jsonObject.add("productListDate", gson.toJsonTree(productListDate));

            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(jsonObject));

            System.out.println(gson.toJson(jsonObject));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
