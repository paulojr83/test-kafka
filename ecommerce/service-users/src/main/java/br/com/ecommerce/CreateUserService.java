package br.com.ecommerce;

import br.com.ecommerce.consumer.ConsumerService;
import br.com.ecommerce.consumer.ServiceRunner;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.*;
import java.util.UUID;

public class CreateUserService implements ConsumerService<Order> {

    private final LocalDatabase database;

    public CreateUserService() throws SQLException {
        this.database = new LocalDatabase("users_database");
        database.createIfNotExists("create table Users (" +
                " uuid varchar(200) primary key," +
                " email varchar(200))");

    }

    public static void main(String[] args) {
        new ServiceRunner<>(CreateUserService::new).start(1);
    }

    @Override
    public String getTopic() {
        return "ECOMMERCE_NEW_ORDER";
    }

    @Override
    public String getConsumerGroup() {
        return CreateUserService.class.getSimpleName();
    }
    public void parse(ConsumerRecord<String, Message<Order>> record)  {
        System.out.println("------------------------------------------------");
        System.out.println("Processing new order, checking for new user");
        System.out.println(record.key());
        System.out.println(record.value());

        Message<Order> message = record.value();
        var order = message.getPayload();
        try {
            if( !isNewUser(order.getEmail()) ){
                insertNewUser(order.getEmail());
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private void insertNewUser(String email) throws SQLException {
        var uuid =  UUID.randomUUID().toString();
        this.database.update("insert into Users ( uuid, email ) " +
                " values (?, ? )", uuid, email);

        System.out.println("User created id "+ uuid + " E-mail " + email);
    }

    private boolean isNewUser(String email) throws SQLException {
        var existsUser= this.database.query("select uuid from users where email = ? limit 1", email);
        return existsUser.next();
    }

}
