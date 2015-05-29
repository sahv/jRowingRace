package sk.insomnia.rowingRace.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DbConnection {

    private static final Properties properties = new Properties();
    private static Logger logger = LoggerFactory.getLogger(DbConnection.class.toString());
    private static final List<Connection> AVAILABLE_CONNECTIONS = new ArrayList<Connection>();
    private static final List<Connection> USED_CONNECTIONS = new ArrayList<Connection>();
    private static final String APP_PROPS_PATH = "/sk/insomnia/rowingRace/config/application.properties";

    private DbConnection(){}
    static {
        InputStream in = DbConnection.class.getResourceAsStream(APP_PROPS_PATH);
        try {
            properties.load(in);
            in.close();
        } catch (IOException e) {
            logger.error(String.format("Can't load input stream for %s.",APP_PROPS_PATH));
        }
        int maxConnections = Integer.parseInt(properties.getProperty("max.connections"));
        for (int i = 0; i < maxConnections; i++) {
            Connection connect = null;
            try {
                connect = connect();
                AVAILABLE_CONNECTIONS.add(connect);
            } catch (SQLException e) {
                logger.error("Can't create connection due to", e);
            }
        }
    }

    private static Connection connect() throws SQLException {
        return DriverManager.getConnection(properties.getProperty("db.url"));
    }
    public static boolean isConnectivity(){
       return !AVAILABLE_CONNECTIONS.isEmpty();
    }
    public static Connection getConnection() throws SQLException {
        if (AVAILABLE_CONNECTIONS.size() > 0) {
            Connection connection = AVAILABLE_CONNECTIONS.remove(AVAILABLE_CONNECTIONS.size() - 1);
            if (!connection.isValid(1000)){
                logger.info("Connection timeout limit expired. Reconnecting.");
                connection = connect();
            }
            USED_CONNECTIONS.add(connection);
            return connection;
        }
        throw new SQLException("No more connections available!");
    }

    public static void releaseConnection(Connection connection){
        USED_CONNECTIONS.remove(connection);
        AVAILABLE_CONNECTIONS.add(connection);
    }

    public static void closeAllConnections(){
        for (Connection connection : AVAILABLE_CONNECTIONS){
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Can't close connection. ",e);
            }
        }
        for (Connection connection : USED_CONNECTIONS){
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Can't close connection. ",e);
            }
        }
    }
}
