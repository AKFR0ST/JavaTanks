package edu.school21.tanks.app;

import com.zaxxer.hikari.HikariDataSource;
import edu.school21.tanks.server.Server;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import edu.school21.tanks.config.SocketsApplicationConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringJoiner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to Tanks!");
        int port = 8088;
        ApplicationContext context = new AnnotationConfigApplicationContext(SocketsApplicationConfig.class);
        Server server = context.getBean("server", Server.class);
        createTable(context);
        server.run(context, port);

    }

    private static void createTable(ApplicationContext context) {
        DataSource dataSource = context.getBean("hikariDataSource", HikariDataSource.class);
        try {
            Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement();
            StringJoiner sjUsers = new StringJoiner(" ");
            sjUsers.add("DROP TABLE IF EXISTS games;");
            sjUsers.add("CREATE TABLE IF NOT EXISTS games (");
            sjUsers.add("id BIGSERIAL PRIMARY KEY,");
            sjUsers.add("p1name VARCHAR(255) NOT NULL,");
            sjUsers.add("p2name VARCHAR(255) NOT NULL,");
            sjUsers.add("p1total BIGSERIAL NOT NULL,");
            sjUsers.add("p2total BIGSERIAL NOT NULL,");
            sjUsers.add("p1strike BIGSERIAL NOT NULL,");
            sjUsers.add("p2strike BIGSERIAL NOT NULL,");
            sjUsers.add("p1missing BIGSERIAL NOT NULL,");
            sjUsers.add("p2missing BIGSERIAL NOT NULL);");
            statement.executeUpdate(sjUsers.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}