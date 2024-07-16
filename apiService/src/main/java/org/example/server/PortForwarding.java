package org.example.server.service;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PortForwarding {
    private static final Logger logger = Logger.getLogger(PortForwarding.class);
    private static Session session = null;

    // Подключение к удаленному серверу
    private static void connectToServer(Properties properties, JSch jsch) {
        try {
            session = jsch.getSession(
                    properties.getProperty("username"),
                    properties.getProperty("remote_host"), Integer.parseInt(
                            properties.getProperty("remote_port"))); // замените на нужный вам порт SSH
        } catch (NumberFormatException e) {
            logger.error("Некорректные данные в конфиге для подключения к серверу: " + e);
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }

    public static int portForwarding() {
        Properties properties;
        properties = getProperties();

        JSch jsch = new JSch();


        connectToServer(properties, jsch);

        session.setConfig("StrictHostKeyChecking", "no");

        session.setPassword(properties.getProperty("password"));

        // Подключение к сессии SSH
        try {
            session.connect();
        } catch (JSchException e) {
            logger.error("Невозможно подключится к сессии SSH: " + e);
            throw new RuntimeException(e);
        }
        var localPort = 51123;
        while (localPort < 65192) {
            try {
                // Проброс портов
                int assignedPort = session.setPortForwardingL(
                        localPort,
                        "localhost", Integer.parseInt(properties.getProperty(
                                "psql_port")));
                logger.info(
                        "Порт проброшен с локалхоста: " +
                                assignedPort + " -> " +
                                "localhost" + ":"
                                + Integer.parseInt(properties.getProperty("psql_port")));
//                    System.out.println("Port forwarding localhost:" + assignedPort + " -> " + "localhost" + ":" + Integer.parseInt(properties.getProperty("psql_port")));
                return assignedPort;
            } catch (JSchException e) {
                localPort++;
                logger.info("Локальный порт занят, попробуем подключится к следующему");
            }
        }
        throw new RuntimeException();
    }

    public static void closeSession() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

    private static Properties getProperties() {
        Properties properties = new Properties();
        try (InputStream is = PortForwarding.class.getClassLoader().getResourceAsStream("connect_to_server.properties")) {
            if (is == null) {
                throw new IOException("Невозможно найти connect_to_server.properties");
            }
            properties.load(is);
            return properties;
        } catch (IOException e) {
            logger.error("Не удалось загрузить connect_to_server.properties: " + e);
            throw new RuntimeException(e);
        }
    }
}