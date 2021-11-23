package ai.platon.pulsar.examples.search.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Db {
    private final String url = "jdbc:mysql://master:3306/gongzhitech?autoReconnect=true";
    private final String username = "";
    private final String password = "";

    public Connection createConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        return DriverManager.getConnection(url, username, password);
    }
}
