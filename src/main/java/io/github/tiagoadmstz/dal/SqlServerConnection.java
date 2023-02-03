package io.github.tiagoadmstz.dal;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SqlServerConnection {

    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    private static final String URL = "jdbc:sqlserver://%s;databaseName=%s;user=%s;";
    private HashMap<String, Connection> connections = new HashMap<>();

    public Connection getConnection(String serverAddress, String database, String user, String password) {
        this.connectDataBase(serverAddress, database, user, password);
        return connections.get(database);
    }

    public void releaseAllConnections() {
        try {
            if (!connections.isEmpty()) {
                for (String database: connections.keySet()) {
                    connections.get(database).close();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void connectDataBase(String serverAddress, String database, String user, String password) {
        try {
            if (!connections.containsKey(database)) {
                Class.forName(DRIVER);
                String urlJdbc = String.format(URL, serverAddress, database, user, password);
                Connection connection = DriverManager.getConnection(urlJdbc);
                connections.put(database, connection);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
