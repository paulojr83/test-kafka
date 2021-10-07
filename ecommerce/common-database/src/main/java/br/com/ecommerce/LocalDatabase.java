package br.com.ecommerce;

import java.io.IOException;
import java.sql.*;

public class LocalDatabase {

    private final Connection connection;

    public LocalDatabase(String name) throws SQLException {
        String url = "jdbc:sqlite:" + name +".db";
        this.connection = DriverManager.getConnection(url);
    }

    // Yes, this is way too generic.
    // According to your database tool, avoid injection.
    public void createIfNotExists(String sql){
        try {
            this.connection.createStatement().execute(sql);
        }catch (SQLException ex) {
            // be careful, the sql could be wrong, be really careful
            ex.printStackTrace();
        }
    }

    public boolean update(String statement, String ...params) throws SQLException {
        return prepare(statement, params).execute();
    }

    public ResultSet query(String query, String... params)  throws SQLException {
        return prepare(query, params).executeQuery();
    }

    private PreparedStatement prepare(String query, String[] params) throws SQLException {
        var prepareStatement = this.connection.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            prepareStatement.setString(i + 1, params[i].toString());
        }
        return prepareStatement;
    }

    public void close() throws IOException {
        try {
            this.connection.close();
        } catch (SQLException e) {
          throw new IOException(e);
        };
    }
}
