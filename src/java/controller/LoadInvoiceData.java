package controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import entity.Order_Item;
import entity.Orders;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

@WebServlet(name = "LoadInvoiceData", urlPatterns = {"/LoadInvoiceData"})
public class LoadInvoiceData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();
        Session session = HibernateUtil.getSessionFactory().openSession();

        Gson gson = new Gson();

        String orderId = request.getParameter("orderId");

        try {

            if (!orderId.isEmpty()) {

                if (!Validations.isInteger(orderId)) {
                    //not int order id
                } else {

                    Criteria criteria = session.createCriteria(Orders.class);
                    criteria.add(Restrictions.eq("id", Integer.parseInt(orderId)));

                    if (criteria.uniqueResult() != null) {

                        Orders orders = (Orders) criteria.uniqueResult();

                        Criteria criteria1 = session.createCriteria(Order_Item.class);
                        criteria1.add(Restrictions.eq("order", orders));

                        List<Order_Item> orderItemsList = criteria1.list();

                        for (Order_Item order_Item : orderItemsList) {
                            order_Item.getOrder().getUser().setPassword(null);
                            order_Item.getProduct().getUser().setPassword(null);
                        }

                        JsonObject jsonObject = new JsonObject();
                        jsonObject.add("orderItemsList", gson.toJsonTree(orderItemsList));
                        jsonObject.add("orderData", gson.toJsonTree(orders));

                        System.out.println(orderItemsList);
                        response.getWriter().write(gson.toJson(jsonObject));

                    } else {
                        response.sendRedirect("index.html");
                    }
                }

            } else {

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
