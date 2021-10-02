package br.com.ecommerce;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class BatchSendMessageService {

    private final Connection connection;
    private final KafkaDispatcher userDispatcher = new KafkaDispatcher<String>();

    public BatchSendMessageService() throws SQLException {
        String url = "jdbc:sqlite:users_database.db";
        this.connection = DriverManager.getConnection(url);
        try {
            this.connection.createStatement().execute("create table Users (" +
                    " uuid varchar(200) primary key," +
                    " email varchar(200))");
        }catch (SQLException ex) {
            // be careful, the sql could be wrong, be really careful
            ex.printStackTrace();
        }

    }

    public static void main(String[] args) throws SQLException {

        var batchSendMessageService = new BatchSendMessageService();
        try(var service = new KafkaService(
                BatchSendMessageService.class.getSimpleName(),
                "ECOMMERCE_SEND_MESSAGE_TO_ALL_USERS", batchSendMessageService::parse,
                new HashMap<String, String>())){
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, Message<String>> record) throws SQLException, ExecutionException, InterruptedException {
        System.out.println("------------------------------------------------");
        System.out.println("Processing new batch");
        var message = record.value();
        var topic = message.getPayload();
        for (User user : getAllUsers()) {
            System.out.println(topic);
            System.out.println(user.toString());
            userDispatcher.send(
                    topic,
                    user.getUuid(),
                    message.getId().continueWith(BatchSendMessageService.class.getSimpleName()),
                    user);
        }
    }

    private List<User> getAllUsers() throws SQLException {

        var results=
                this.connection.prepareStatement("select uuid from users")
                        .executeQuery();
        List<User> users = new ArrayList<>();
        while (results.next() ){
            users.add(new User(results.getString(1)));
        }
        return users;
    }


}
