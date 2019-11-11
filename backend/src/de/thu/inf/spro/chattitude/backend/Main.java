package de.thu.inf.spro.chattitude.backend;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {
    public static void main(String[] args) throws SQLException, InterruptedException {
        System.out.println("Starte ChattitudeWebSocketServer…");
        var webSocketServer = new ChattitudeWebSocketServer(8080);
        webSocketServer.start();

        Thread.sleep(5000);
        connectMySQL();
    }

    private static void connectMySQL() throws SQLException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser(System.getenv("MYSQL_USER"));
        dataSource.setPassword(System.getenv("MYSQL_PASSWORD"));
        dataSource.setServerName(System.getenv("MYSQL_HOSTNAME"));
        dataSource.setDatabaseName(System.getenv("MYSQL_DATABASE"));

        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SHOW TABLES;");

        System.out.println("Ausgeführt");

        rs.close();
        stmt.close();
        conn.close();
    }
}
