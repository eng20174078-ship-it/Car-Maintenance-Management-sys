package com.carmaintenance.dao;

import com.carmaintenance.model.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    public boolean addCustomer(Customer customer) {
        System.out.println("\n📝 === محاولة إضافة عميل جديد ===");
        System.out.println("📋 البيانات: " + customer.toString());

        String sql = "INSERT INTO customers (name, phone, email, address) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("❌ لا يوجد اتصال بقاعدة البيانات");
                return false;
            }

            System.out.println("✅ تم الحصول على الاتصال");

            // إعداد PreparedStatement
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getAddress());

            System.out.println("📤 تنفيذ SQL: " + sql);

            // التنفيذ
            int affectedRows = pstmt.executeUpdate();
            System.out.println("📊 عدد الصفوف المتأثرة: " + affectedRows);

            if (affectedRows == 0) {
                System.err.println("❌ لم تتأثر أي صفوف - فشل الإدخال");
                return false;
            }

            // الحصول على المفتاح المولد
            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int newId = generatedKeys.getInt(1);
                customer.setId(newId);
                System.out.println("🆔 ID المولد للعميل: " + newId);
                System.out.println("🎉 تم إضافة العميل بنجاح!");
                return true;
            } else {
                System.err.println("❌ لم يتم إنشاء ID للعميل");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ SQL أثناء إضافة العميل:");
            System.err.println("   • الرسالة: " + e.getMessage());
            System.err.println("   • الخطأ: " + e.getErrorCode());
            System.err.println("   • الحالة: " + e.getSQLState());

            // عرض رسالة مناسبة للمستخدم
            if (e.getErrorCode() == 1062) { // خطأ duplicate entry
                System.err.println("⚠️ رقم الهاتف مسجل مسبقاً!");
            }
            return false;

        } finally {
            // تنظيف الموارد
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmt != null) pstmt.close();
            } catch (SQLException e) {
                System.err.println("⚠️ خطأ في إغلاق الموارد: " + e.getMessage());
            }
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                );
                customers.add(customer);
            }

            System.out.println("✅ تم جلب " + customers.size() + " عميل");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب العملاء: " + e.getMessage());
        }
        return customers;
    }

    public Customer getCustomerById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("address")
                );
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب العميل: " + e.getMessage());
        }
        return null;
    }

    public boolean isPhoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM customers WHERE phone = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("📞 رقم الهاتف " + phone + " موجود " + count + " مرة");
                return count > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في التحقق من رقم الهاتف: " + e.getMessage());
        }
        return false;
    }

    public int getCustomerCount() {
        String sql = "SELECT COUNT(*) as count FROM customers";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في عد العملاء: " + e.getMessage());
        }
        return 0;
    }

    // دالة لإنشاء جدول customers إذا لم يكن موجوداً
    public void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS customers (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "phone VARCHAR(20) UNIQUE NOT NULL," +
                "email VARCHAR(100)," +
                "address TEXT" +
                ")";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println("✅ تم إنشاء/التحقق من جدول customers");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في إنشاء الجدول: " + e.getMessage());
        }
    }
}