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
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "SearchProducts", urlPatterns = {"/SearchProducts"})
public class SearchProducts extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Gson gson = new Gson();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("success", false);

        //Get request json
        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);
        System.out.println(requestJsonObject);
        Session session = HibernateUtil.getSessionFactory().openSession();

        //search all products
        Criteria criteria1 = session.createCriteria(Product.class);

        //add category filter 
        if (requestJsonObject.has("categoryName")) {
            //category selected
            String categoryName = requestJsonObject.get("categoryName").getAsString();

            //get category list form db
            Criteria criteria2 = session.createCriteria(Category.class);
            criteria2.add(Restrictions.eq("name", categoryName));
            Category category = (Category) criteria2.uniqueResult();

            //Filter product by model list from db
            criteria1.add(Restrictions.eq("category", category));
        }

        //add author filter 
        if (requestJsonObject.has("authorName")) {
            //author selected
            String authorName = requestJsonObject.get("authorName").getAsString();

            //get author form db
            Criteria criteria4 = session.createCriteria(Author.class);
            criteria4.add(Restrictions.eq("name", authorName));
            Author author = (Author) criteria4.uniqueResult();

            //Filter product by author from db
            criteria1.add(Restrictions.eq("author", author));
        }

        if (requestJsonObject.has("publisherName")) {
            //publisher selected
            String publisherName = requestJsonObject.get("publisherName").getAsString();

            //get publisher form db
            Criteria criteria5 = session.createCriteria(Publisher.class);
            criteria5.add(Restrictions.eq("name", publisherName));
            Publisher publisher = (Publisher) criteria5.uniqueResult();

            //Filter product by publisher from db
            criteria1.add(Restrictions.eq("publisher", publisher));
        }

        double priceRangeStart = requestJsonObject.get("priceRangeStart").getAsDouble();
        double priceRangeEnd = requestJsonObject.get("priceRangeEnd").getAsDouble();

        criteria1.add(Restrictions.ge("price", priceRangeStart));
        criteria1.add(Restrictions.le("price", priceRangeEnd));

        String sortText = requestJsonObject.get("sortText").getAsString();

        if (sortText.equals("Sort by Latest")) {

            criteria1.addOrder(Order.desc("id"));

        } else if (sortText.equals("Sort by Oldest")) {

            criteria1.addOrder(Order.asc("id"));

        } else if (sortText.equals("Sort by Name")) {

            criteria1.addOrder(Order.asc("title"));

        } else if (sortText.equals("Sort by Price")) {

            criteria1.addOrder(Order.asc("price"));

        }

        //get all product count
        responseJsonObject.addProperty("allProductCount", criteria1.list().size());

        //set product range
        int firstResult = requestJsonObject.get("firstResult").getAsInt();

        criteria1.setFirstResult(firstResult);
        criteria1.setMaxResults(6);

        //get product list
        List<Product> productList = criteria1.list();

        // remove user from product
        for (Product product : productList) {
            product.setUser(null);
        }

        responseJsonObject.addProperty("success", true);
        responseJsonObject.add("productList", gson.toJsonTree(productList));

        //send response
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseJsonObject));
        System.out.println(gson.toJson(responseJsonObject));

    }

}
