package club.catmc.utils.storage.rethinkdb;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

public class RethinkDB {

    private final String host;
    private final int port;
    private Connection connection;

    private final String username;
    private final String password;

    public RethinkDB(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void init() {
        this.connection = RethinkDB.r.connection().hostname(host).port(port).user(username, password).connect();
    }

    public void end() {
        if (this.connection != null && this.connection.isOpen()) {
            this.connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}