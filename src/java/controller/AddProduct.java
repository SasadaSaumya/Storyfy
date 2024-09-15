package controller;

import com.google.gson.Gson;
import dto.Response_DTO;
import dto.User_DTO;
import entity.Author;
import entity.Category;
import entity.Product;
import entity.Product_Status;
import entity.Publisher;
import entity.User;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import model.HibernateUtil;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sasa
 */
@MultipartConfig
@WebServlet(name = "AddProduct", urlPatterns = {"/AddProduct"})
public class AddProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();
        Gson gson = new Gson();

        String categoryId = request.getParameter("categoryId");
        String authorId = request.getParameter("authorId");
        String publisherId = request.getParameter("publisherId");
        String isbn = request.getParameter("isbn");
        String pages = request.getParameter("pages");
        String statusId = request.getParameter("statusId");
        String publishedDate = request.getParameter("publishedDate");
        String title = request.getParameter("title");
        String description = request.getParameter("description");
        String qty = request.getParameter("qty");
        String price = request.getParameter("price");

        System.out.println(categoryId + authorId + publisherId + isbn + pages + statusId + publishedDate + title + description + qty + price);

        Part image1 = request.getPart("image1");

        Session session = HibernateUtil.getSessionFactory().openSession();

        if (!Validations.isInteger(categoryId)) {
            response_DTO.setContent("Invalid category");

        } else if (!Validations.isInteger(authorId)) {
            response_DTO.setContent("Invalid model");

        } else if (!Validations.isInteger(publisherId)) {
            response_DTO.setContent("Invalid storage");

        } else if (!Validations.isInteger(statusId)) {
            response_DTO.setContent("Invalid color");

        } else if (Integer.parseInt(categoryId) == 0) {
            response_DTO.setContent("Please Select your Category");

        } else if (Integer.parseInt(authorId) == 0) {
            response_DTO.setContent("Please Select your Author");

        } else if (Integer.parseInt(publisherId) == 0) {
            response_DTO.setContent("Please Select your publisher");

        } else if (title.isEmpty()) {
            response_DTO.setContent("Please fill Product Title");

        } else if (isbn.isEmpty()) {
            response_DTO.setContent("Please fill ISBN Number");

        } else if (pages.isEmpty()) {
            response_DTO.setContent("Please fill pages count");

        } else if (publishedDate.isEmpty()) {
            response_DTO.setContent("Please fill book Published Date");

        } else if (price.isEmpty()) {
            response_DTO.setContent("Please fill book price");

        } else if (qty.isEmpty()) {
            response_DTO.setContent("Please fill book qty");

        } else if (description.isEmpty()) {
            response_DTO.setContent("Please fill book description");

        } else if (!Validations.isInteger(pages)) {

            response_DTO.setContent("Invalid page count");

        } else if (!Validations.isDouble(price)) {
            response_DTO.setContent("Invalid price");

        } else if (Double.parseDouble(price) <= 0) {
            response_DTO.setContent("Invalid price");

        } else if (!Validations.isInteger(qty)) {
            response_DTO.setContent("Invalid qty");

        } else if (!Validations.isValidDate(publishedDate)) {
            response_DTO.setContent("Invalid published Date");

        } else if (Integer.parseInt(qty) <= 0) {
            response_DTO.setContent("Invalid qty");

        } else if (Integer.parseInt(statusId) == 0) {
            response_DTO.setContent("Please Select your Status");

        } else if (image1.getSubmittedFileName() == null) {
            response_DTO.setContent("please upload image 1");

        } else {
            Category category = (Category) session.get(Category.class, Integer.parseInt(categoryId));

            if (category == null) {
                response_DTO.setContent("invalid category");

            } else {

                Author author = (Author) session.get(Author.class, Integer.parseInt(authorId));

                if (author == null) {
                    response_DTO.setContent("invalid author");

                } else {

                    Publisher publisher = (Publisher) session.get(Publisher.class, Integer.parseInt(publisherId));

                    if (publisher == null) {
                        response_DTO.setContent("please select a valid publisher");

                    } else {

                        Product_Status product_Status = (Product_Status) session.get(Product_Status.class, Integer.parseInt(statusId));

                        if (product_Status == null) {
                            response_DTO.setContent("please select a valid Product Status");

                        } else {

                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                            LocalDate localDate = LocalDate.parse(publishedDate, formatter);

                            //all validations done
                            Product product = new Product();
                            product.setTitle(title);
                            product.setPage(Integer.parseInt(pages));
                            product.setIsbn(isbn);
                            product.setPublished_date(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                            product.setDate_time(new Date());

                            product.setAuthor(author);
                            product.setPublisher(publisher);
                            product.setCategory(category);
                            product.setProduct_status(product_Status);
                            product.setDescription(description);

                            product.setQty(Integer.parseInt(qty));
                            product.setPrice(Double.parseDouble(price));

                            User_DTO user_DTO = (User_DTO) request.getSession().getAttribute("user");

                            Criteria criteria1 = session.createCriteria(User.class);
                            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));

                            User user = (User) criteria1.uniqueResult();
                            product.setUser(user);

                            int pid = (int) session.save(product);
                            session.beginTransaction().commit();

                            String applicationPath = request.getServletContext().getRealPath("");
//                            String newApplicationPath = applicationPath.replace("build" + File.separator + "web", "web");

                            File folder = new File(applicationPath + "//product-images//" + pid);
                            if (!folder.exists()) {
                                folder.mkdirs();
                            }

                            File file1 = new File(folder, "image1.png");
                            InputStream inputStream1 = image1.getInputStream();
                            Files.copy(inputStream1, file1.toPath(), StandardCopyOption.REPLACE_EXISTING);

                            response_DTO.setSuccess(true);
                            response_DTO.setContent("New product added");

                        }

                    }

                }

            }

        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));
        System.out.println(gson.toJson(response_DTO));

        session.close();

    }

}
