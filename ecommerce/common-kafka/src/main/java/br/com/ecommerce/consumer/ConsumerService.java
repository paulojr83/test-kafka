package br.com.ecommerce.consumer;

import br.com.ecommerce.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerService<T> {

    // You may argue that a ConsumerServiceException would be better
    // nad its ok, it can better
    void parse(ConsumerRecord<String, Message<T>> record) throws Exception;
    String getTopic();
    String getConsumerGroup();

}
