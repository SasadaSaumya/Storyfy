package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import dto.Response_DTO;
import dto.User_DTO;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.HibernateUtil;
import model.Mail;
import model.Validations;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author sasa
 */
@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Response_DTO response_DTO = new Response_DTO();

        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        User_DTO user_DTO = gson.fromJson(request.getReader(), User_DTO.class);

        if (user_DTO.getFirst_name().isEmpty()) {

            response_DTO.setContent("Please enter your first name");

        } else if (user_DTO.getLast_name().isEmpty()) {

            response_DTO.setContent("Please enter your last name");

        } else if (user_DTO.getEmail().isEmpty()) {

            response_DTO.setContent("Please enter your email");

        } else if (!Validations.isEmailValid(user_DTO.getEmail())) {

            response_DTO.setContent("Please enter valid email");

        } else if (user_DTO.getPassword().isEmpty()) {

            response_DTO.setContent("Please enter your password");

        } else if (!Validations.isPasswordValid(user_DTO.getPassword())) {

            response_DTO.setContent("Password must include at least one uppercase letter, \"\n"
                    + "                    + \"lowercase letter, \"\n"
                    + "                    + \"number, \"\n"
                    + "                    + \"special character, \"\n"
                    + "                    + \"minimum 8 characters");

        } else {

            Session session = HibernateUtil.getSessionFactory().openSession();

            Criteria criteria1 = session.createCriteria(User.class);
            criteria1.add(Restrictions.eq("email", user_DTO.getEmail()));

            if (!criteria1.list().isEmpty()) {

                response_DTO.setContent("User already exsist");

            } else {

                //generate verify code 
                int code = (int) (Math.random() * 1000000);

                final User user = new User();
                user.setEmail(user_DTO.getEmail());
                user.setFirst_name(user_DTO.getFirst_name());
                user.setLast_name(user_DTO.getLast_name());
                user.setPassword(user_DTO.getPassword());
                user.setVerification(String.valueOf(code));

                //send verification code
                Thread sendMailThread = new Thread() {

                    @Override
                    public void run() {

                        String htmlContent = "<!DOCTYPE html>"
                                + "<html lang=\"en\">"
                                + "<head>"
                                + "<meta charset=\"UTF-8\">"
                                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">"
                                + "<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"
                                + "<title>Verification Code</title>"
                                + "<style>"
                                + "body {font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0;}"
                                + ".email-container {width: 100%; max-width: 600px; margin: 0 auto; background-color: #ffffff; border: 1px solid #ddd; padding: 20px;}"
                                + ".header {text-align: center; padding: 20px 0; background-color: #333; color: #fff;}"
                                + ".content {padding: 20px;}"
                                + ".footer {text-align: center; padding: 20px; font-size: 12px; color: #666;}"
                                + "</style>"
                                + "</head>"
                                + "<body>"
                                + "<div class=\"email-container\">"
                                + "<div class=\"header\"><h1>Verify Your Email Address</h1></div>"
                                + "<div class=\"content\">"
                                + "<h2>Hello,</h2>"
                                + "<p>Thank you for registering on Storyfy. Please use the verification code below to verify your email address:</p>"
                                + "<h3>Your Verification Code: <strong>" + user.getVerification() + "</strong></h3>"
                                + "<p>If you did not request this, please ignore this email.</p>"
                                + "</div>"
                                + "<div class=\"footer\"><p>Thank you for joining Storyfy!</p></div>"
                                + "</div>"
                                + "</body>"
                                + "</html>";

                        Mail.sendMail(
                                user_DTO.getEmail(),
                                "Storyfy Verification code",
                                htmlContent
                        );
                    }

                };

                sendMailThread.start();
                session.save(user);
                session.beginTransaction().commit();

                request.getSession().setAttribute("email", user_DTO.getEmail());
                response_DTO.setSuccess(true);
                response_DTO.setContent("Registration Complete. Please check your email for verify code.");

            }

            session.close();
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(response_DTO));

    }

}
