package com.carmaintenance.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static Connection connection = null;
    private static final String CONFIG_FILE = "config.properties";

    public static Connection getConnection() {

        if (connection != null && isConnectionValid()) {
            return connection;
        }

        System.out.println("🔗 محاولة الاتصال بقاعدة البيانات...");

        try {
            // تحميل ملف الإعدادات
            InputStream input = DatabaseConnection.class.getClassLoader()
                    .getResourceAsStream(CONFIG_FILE);

            if (input == null) {
                throw new RuntimeException("❌ ملف config.properties غير موجود في resources");
            }

            Properties props = new Properties();
            props.load(input);

            String url = props.getProperty("db.url");
            url += "?useUnicode=true&characterEncoding=utf8&useSSL=false";
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");

            System.out.println("📌 إعدادات الاتصال:");
            System.out.println("   • URL: " + url);
            System.out.println("   • User: " + user);
            System.out.println("   • Password: " + (password.isEmpty() ? "[فارغ]" : "******"));

            // تحميل Driver
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("✅ تم تحميل MySQL Driver");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("❌ لم يتم العثور على MySQL Driver", e);
            }

            // إنشاء الاتصال
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ الاتصال بقاعدة البيانات ناجح");

            // اختبار الاتصال
            if (connection.isValid(5)) {
                System.out.println("✅ الاتصال فعّال وصالح للاستخدام");
            }

        } catch (Exception e) {
            System.err.println("❌ فشل الاتصال بقاعدة البيانات: " + e.getMessage());
            e.printStackTrace();
            connection = null;
        }

        return connection;
    }

    private static boolean isConnectionValid() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✅ تم إغلاق اتصال قاعدة البيانات");
            } catch (SQLException e) {
                System.err.println("⚠️ خطأ في إغلاق الاتصال: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }

    public static void testConnection() {
        Connection conn = getConnection();
        if (conn != null) {
            System.out.println("🎉 اختبار الاتصال ناجح!");
        } else {
            System.out.println("💔 اختبار الاتصال فاشل!");
        }
    }
}