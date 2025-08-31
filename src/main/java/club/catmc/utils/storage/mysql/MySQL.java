package club.catmc.utils.storage.mysql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    private String url;
    private String host;
    private int port;
    private String databaseName;
    private String username;
    private String password;
    private boolean usePooling;
    private Connection connection;
    private HikariDataSource dataSource;

    public MySQL(String url, boolean usePooling) {
        this.url = url;
        this.usePooling = usePooling;
    }

    public MySQL(String host, int port, String databaseName, String username, String password, boolean usePooling) {
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
        this.usePooling = usePooling;
    }

    public void init() throws SQLException {
        if (usePooling) {
            HikariConfig config = new HikariConfig();
            if (url != null) {
                config.setJdbcUrl(url);
            } else {
                config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + databaseName);
                config.setUsername(username);
                config.setPassword(password);
            }
            this.dataSource = new HikariDataSource(config);
        } else {
            if (url != null) {
                this.connection = DriverManager.getConnection(url);
            } else {
                String url = "jdbc:mysql://" + host + ":" + port + "/" + databaseName;
                this.connection = DriverManager.getConnection(url, username, password);
            }
        }
    }

    public void end() throws SQLException {
        if (usePooling) {
            if (this.dataSource != null && !this.dataSource.isClosed()) {
                this.dataSource.close();
            }
        } else {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        }
    }

    public Connection getConnection() throws SQLException {
        if (usePooling) {
            return dataSource.getConnection();
        } else {
            return connection;
        }
    }
}