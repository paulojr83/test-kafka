package br.com.ecommerce;

import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

public class OrdersDatabase implements Closeable {

    private final LocalDatabase database;

    public OrdersDatabase() throws SQLException {
        this.database = new LocalDatabase("orders_database");
        // you might want to save all data
        this.database.createIfNotExists("create table IF NOT EXISTS Orders(" +
                " uuid varchar(200) primary key)");

    }

    @Override
    public void close() throws IOException {
        this.database.close();
    }

    public boolean saveNew(Order order) throws SQLException {
        if( wasProcessed(order)) {
            return false;
        }
        this.database.update("insert into Orders (uuid) values (?)", order.getOrderId());
        return true;
    }

    private boolean wasProcessed(Order order) throws SQLException {
        var query = this.database.query("select uuid from Orders where uuid = ? limit 1", order.getOrderId());
        return query.next();
    }
}
