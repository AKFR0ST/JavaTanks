package edu.school21.tanks.app;

import edu.school21.tanks.client.Client;
import edu.school21.tanks.config.SocketsApplicationConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(SocketsApplicationConfig.class);
        Client client = context.getBean("client", Client.class);
        client.run();
    }
}