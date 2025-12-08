package com.carmaintenance;

import com.carmaintenance.dao.DatabaseConnection;
import java.sql.*;

public class TestConnection {
    public static void main(String[] args) {
        System.out.println("🔍 === اختبار اتصال قاعدة البيانات ===");

        Connection conn = null;
        try {
            // 1. اختبار الاتصال
            conn = DatabaseConnection.getConnection();
            System.out.println("✅ الاتصال بقاعدة البيانات ناجح");

            // 2. اختبار استعلام بسيط
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 as test");
            if (rs.next()) {
                System.out.println("✅ استعلام SQL الأساسي يعمل");
            }
            rs.close();

            // 3. التحقق من وجود قاعدة البيانات
            rs = stmt.executeQuery("SELECT DATABASE() as db");
            if (rs.next()) {
                System.out.println("✅ قاعدة البيانات الحالية: " + rs.getString("db"));
            }
            rs.close();

            // 4. التحقق من وجود جدول customers
            rs = stmt.executeQuery("SHOW TABLES LIKE 'customers'");
            if (rs.next()) {
                System.out.println("✅ جدول customers موجود");

                // 5. عرض عدد العملاء
                rs = stmt.executeQuery("SELECT COUNT(*) as count FROM customers");
                if (rs.next()) {
                    System.out.println("✅ عدد العملاء الحاليين: " + rs.getInt("count"));
                }
            } else {
                System.out.println("⚠️ جدول customers غير موجود!");
                System.out.println("📝 قم بتشغيل ملف SQL لإنشاء الجداول");
            }

            stmt.close();
            System.out.println("✅ جميع الاختبارات ناجحة!");

        } catch (SQLException e) {
            System.out.println("❌ خطأ في قاعدة البيانات: " + e.getMessage());
            System.out.println("❌ رمز الخطأ: " + e.getErrorCode());
            System.out.println("❌ حالة SQL: " + e.getSQLState());
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("✅ تم إغلاق الاتصال");
                } catch (SQLException e) {
                    System.out.println("⚠️ خطأ في إغلاق الاتصال: " + e.getMessage());
                }
            }
        }
    }
}