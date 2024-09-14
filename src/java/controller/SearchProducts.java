package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import entity.Author;
import entity.Category;
import entity.Product;
import entity.Publisher;
import java.io.IOException;
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

@WebServlet(name = "SearchProducts", urlPatterns = {"/SearchProducts"})
public class SearchProducts extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        // Get request JSON
        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);
        System.out.println(requestJsonObject);
        Session session = HibernateUtil.getSessionFactory().openSession();
        

     
            Criteria criteria1 = session.createCriteria(Product.class);

            // Add category filter
            if (requestJsonObject.has("categoryName")) {
                String categoryName = requestJsonObject.get("categoryName").getAsString();
                Criteria criteria2 = session.createCriteria(Category.class);
                criteria2.add(Restrictions.eq("name", categoryName));
                Category category = (Category) criteria2.uniqueResult();
                if (category != null) {
                    criteria1.add(Restrictions.eq("category", category));
                }
            }

            // Add author filter
            if (requestJsonObject.has("authorName")) {
                String authorName = requestJsonObject.get("authorName").getAsString();
                Criteria criteria4 = session.createCriteria(Author.class);
                criteria4.add(Restrictions.eq("name", authorName));
                Author author = (Author) criteria4.uniqueResult();
                if (author != null) {
                    criteria1.add(Restrictions.eq("author", author));
                }
            }

            // Add publisher filter
            if (requestJsonObject.has("publisherName")) {
                String publisherName = requestJsonObject.get("publisherName").getAsString();
                Criteria criteria5 = session.createCriteria(Publisher.class);
                criteria5.add(Restrictions.eq("name", publisherName));
                Publisher publisher = (Publisher) criteria5.uniqueResult();
                if (publisher != null) {
                    criteria1.add(Restrictions.eq("publisher", publisher));
                }
            }

            // Add price range filter
            double priceRangeStart = requestJsonObject.has("priceRangeStart") ? requestJsonObject.get("priceRangeStart").getAsDouble() : 0;
            double priceRangeEnd = requestJsonObject.has("priceRangeEnd") ? requestJsonObject.get("priceRangeEnd").getAsDouble() : Double.MAX_VALUE;
            criteria1.add(Restrictions.ge("price", priceRangeStart));
            criteria1.add(Restrictions.le("price", priceRangeEnd));

            // Add sorting
            String sortText = requestJsonObject.get("sortText").getAsString();
            if ("Sort by Latest".equals(sortText)) {
                criteria1.addOrder(Order.desc("id"));
            } else if ("Sort by Oldest".equals(sortText)) {
                criteria1.addOrder(Order.asc("id"));
            } else if ("Sort by Name".equals(sortText)) {
                criteria1.addOrder(Order.asc("title"));
            } else if ("Sort by Price".equals(sortText)) {
                criteria1.addOrder(Order.asc("price"));
            }

            // Get all product count
            responseJsonObject.addProperty("allProductCount", criteria1.list().size());

            // Set product range
            int firstResult = requestJsonObject.get("firstResult").getAsInt();
            criteria1.setFirstResult(firstResult);
            criteria1.setMaxResults(6);

            // Get product list
            List<Product> productList = criteria1.list();

            // Remove user from product
            productList.forEach(product -> product.setUser(null));

            responseJsonObject.addProperty("success", true);
            responseJsonObject.add("productList", gson.toJsonTree(productList));

            // Send response
            response.setContentType("application/json");
            response.getWriter().write(gson.toJson(responseJsonObject));
            System.out.println(gson.toJson(responseJsonObject));
      
    }
}
