package br.com.ecommerce;

import br.com.ecommerce.dispatcher.KafkaDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;

public class NewOrderServlet extends HttpServlet {

    private final KafkaDispatcher orderDispatcher = new KafkaDispatcher<Order>();

    @Override
    public void destroy() {
        super.destroy();
        orderDispatcher.close();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var orderId = req.getParameter("uuid");
            if( orderId == null ) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().println("Uuid can't be null");
                return;
            }

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

            try(var database = new OrdersDatabase()) {
                var message ="New order sent successfully";

                if ( database.saveNew(order) ) {
                    orderDispatcher.send(
                            "ECOMMERCE_NEW_ORDER",
                            email,
                            new CorrelationId(NewOrderServlet.class.getSimpleName()),
                            order);

                    System.out.println(message);
                }else {
                    message ="Old order received";
                }

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().println(message);
            }

        } catch (ExecutionException e) {
            throw new ServletException(e);
        } catch (InterruptedException | SQLException e) {
            throw new ServletException(e);
        }
    }
}
