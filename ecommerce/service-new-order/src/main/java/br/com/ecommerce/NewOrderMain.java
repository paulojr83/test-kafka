package br.com.ecommerce;

import br.com.ecommerce.dispatcher.KafkaDispatcher;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class NewOrderMain {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try (var orderDispatcher = new KafkaDispatcher<Order>() ){
            var email = Math.random() + "@email.com";
            for (int i = 0; i < 10; i++) {

                var orderId = UUID.randomUUID().toString();
                var amount = new BigDecimal( Math.random() * 5000 + 1 );
                CorrelationId correlationId = new CorrelationId(NewOrderMain.class.getSimpleName());

                Order order = new Order(orderId, amount, email);
                orderDispatcher.send(
                        "ECOMMERCE_NEW_ORDER",
                        email,
                        correlationId,
                        order);
            }
        }
    }
}
