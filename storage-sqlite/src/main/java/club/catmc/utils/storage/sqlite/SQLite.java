package club.catmc.utils.storage.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLite {

    private String url;
    private Connection connection;

    public SQLite(String url) {
        this.url = url;
    }

    public void init() throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + url);
    }

    public void end() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}