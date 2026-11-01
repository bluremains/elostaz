package com.elostaz.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static Properties config;

    private DBConnection() {
    }

    private static Properties loadConfig() throws SQLException {
        if (config == null) {
            Properties p = new Properties();
            try (InputStreamReader in = new InputStreamReader(
                    new FileInputStream("config.properties"), StandardCharsets.UTF_8)) {
                p.load(in);
            } catch (IOException e) {
                throw new SQLException("config.properties file not found", e);
            }
            config = p;
        }
        return config;
    }

    public static Connection getConnection() throws SQLException {
        Properties c = loadConfig();
        return DriverManager.getConnection(
                c.getProperty("db.url"),
                c.getProperty("db.user"),
                c.getProperty("db.password", ""));
    }
}
