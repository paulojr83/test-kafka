package br.com.ecommerce;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher orderDispatcher = new KafkaDispatcher<Order>();
    private final KafkaDispatcher emailDispatcher = new KafkaDispatcher<Email>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
        emailDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var orderId = UUID.randomUUID().toString();

            String email = req.getParameter("email");
            if( email == null ) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().println("E-mail can't be null");
                return;
            }

            if( req.getParameter("amount") == null ) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().println("Amount can't be null");
                return;
            }
            var amount = new BigDecimal( req.getParameter("amount") );
            Order order = new Order(orderId, amount, email);
            orderDispatcher.send(
                    "ECOMMERCE_NEW_ORDER",
                    email,
                    new CorrelationId(NewOrderServlet.class.getSimpleName()),
                    order);

            var emailCode = new Email(email,
                    "Thank you for your order! We are processing your order!", orderId);
            emailDispatcher.send(
                    "ECOMMERCE_SEND_EMAIL",
                    email,
                    new CorrelationId(NewOrderServlet.class.getSimpleName()),
                    emailCode);
            var message ="New order sent successfully";
            System.out.println(message);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(message);

        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
