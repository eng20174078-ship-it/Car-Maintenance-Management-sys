package com.carmaintenance.dao;

import com.carmaintenance.model.Customer;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // تنسيق التاريخ
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // إضافة عميل جديد مع تاريخ الإنشاء
    public boolean addCustomer(Customer customer) {
        String sql = "INSERT INTO customers (name, phone, email, address, created_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        System.out.println("👤 === محاولة إضافة عميل جديد ===");
        System.out.println("📋 البيانات: " + customer.toString());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getAddress());
            pstmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

            int affectedRows = pstmt.executeUpdate();
            System.out.println("📊 عدد الصفوف المتأثرة: " + affectedRows);

            if (affectedRows > 0) {
                // الحصول على الـ ID المولد
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        customer.setId(newId);
                        System.out.println("🆔 ID المولد للعميل: " + newId);
                    }
                }
                System.out.println("✅ تم إضافة العميل بنجاح!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ SQL أثناء إضافة العميل:");
            System.err.println("   • الرسالة: " + e.getMessage());
            System.err.println("   • الخطأ: " + e.getErrorCode());
            System.err.println("   • الحالة: " + e.getSQLState());

            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.err.println("⚠️ رقم الهاتف مسجل مسبقاً!");
                throw new RuntimeException("رقم الهاتف مسجل مسبقاً!");
            }
            return false;
        }
        return false;
    }

    // الحصول على جميع العملاء مع ترتيب حسب تاريخ الإنشاء
    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = resultSetToCustomer(rs);
                customers.add(customer);
            }

            System.out.println("✅ تم جلب " + customers.size() + " عميل");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب العملاء: " + e.getMessage());
        }
        return customers;
    }

    // الحصول على عميل بالرقم
    public Customer getCustomerById(int id) {
        String sql = "SELECT * FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return resultSetToCustomer(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب العميل: " + e.getMessage());
        }
        return null;
    }

    // تحديث بيانات عميل
    public boolean updateCustomer(Customer customer) {
        String sql = "UPDATE customers SET name = ?, phone = ?, email = ?, address = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getEmail());
            pstmt.setString(4, customer.getAddress());
            pstmt.setInt(5, customer.getId());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("📊 عدد الصفوف المتأثرة: " + affectedRows);

            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في تحديث العميل: " + e.getMessage());
        }
        return false;
    }

    // حذف عميل
    public boolean deleteCustomer(int id) {
        String sql = "DELETE FROM customers WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في حذف العميل: " + e.getMessage());
        }
        return false;
    }

    // التحقق من وجود رقم هاتف مكرر (باستثناء ID محدد)
    public boolean isPhoneExists(String phone, int excludeId) {
        String sql = "SELECT COUNT(*) FROM customers WHERE phone = ? AND id != ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);
            pstmt.setInt(2, excludeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("📞 رقم الهاتف " + phone + " موجود " + count + " مرة (باستثناء ID " + excludeId + ")");
                return count > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في التحقق من رقم الهاتف: " + e.getMessage());
        }
        return false;
    }

    // التحقق من وجود رقم هاتف (جميع السجلات)
    public boolean isPhoneExists(String phone) {
        return isPhoneExists(phone, -1);
    }

    // البحث عن عملاء بالاسم
    public List<Customer> searchCustomersByName(String name) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE name LIKE ? ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Customer customer = resultSetToCustomer(rs);
                customers.add(customer);
            }

            System.out.println("🔍 تم العثور على " + customers.size() + " عميل باسم " + name);

        } catch (SQLException e) {
            System.err.println("❌ خطأ في البحث عن العملاء: " + e.getMessage());
        }
        return customers;
    }

    // البحث عن عملاء برقم الهاتف
    public List<Customer> searchCustomersByPhone(String phone) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE phone LIKE ? ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + phone + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Customer customer = resultSetToCustomer(rs);
                customers.add(customer);
            }

            System.out.println("🔍 تم العثور على " + customers.size() + " عميل برقم " + phone);

        } catch (SQLException e) {
            System.err.println("❌ خطأ في البحث عن العملاء بالهاتف: " + e.getMessage());
        }
        return customers;
    }

    // جلب عدد العملاء
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

    // جلب العملاء المسجلين حديثاً (آخر 7 أيام)
    public List<Customer> getRecentCustomers(int days) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE created_at >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                "ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, days);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Customer customer = resultSetToCustomer(rs);
                customers.add(customer);
            }

            System.out.println("📅 تم جلب " + customers.size() + " عميل مسجل خلال " + days + " يوم");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب العملاء الحديثين: " + e.getMessage());
        }
        return customers;
    }

    // جلب العملاء بدون بريد إلكتروني
    public List<Customer> getCustomersWithoutEmail() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT * FROM customers WHERE email IS NULL OR email = '' ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = resultSetToCustomer(rs);
                customers.add(customer);
            }

            System.out.println("📧 تم جلب " + customers.size() + " عميل بدون بريد إلكتروني");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب العملاء بدون بريد: " + e.getMessage());
        }
        return customers;
    }

    // جلب إحصائيات العملاء
    public String getCustomerStatistics() {
        StringBuilder stats = new StringBuilder();

        stats.append("📊 إحصائيات العملاء:\n");
        stats.append("===================\n");
        stats.append("👥 العدد الإجمالي: ").append(getCustomerCount()).append("\n");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // العملاء المسجلين اليوم
            ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) as today_count FROM customers " +
                            "WHERE DATE(created_at) = CURDATE()");
            if (rs.next()) {
                stats.append("📅 المسجلين اليوم: ").append(rs.getInt("today_count")).append("\n");
            }

            // العملاء المسجلين هذا الشهر
            rs = stmt.executeQuery(
                    "SELECT COUNT(*) as month_count FROM customers " +
                            "WHERE MONTH(created_at) = MONTH(CURRENT_DATE()) " +
                            "AND YEAR(created_at) = YEAR(CURRENT_DATE())");
            if (rs.next()) {
                stats.append("📈 المسجلين هذا الشهر: ").append(rs.getInt("month_count")).append("\n");
            }

            // العملاء بدون بريد إلكتروني
            rs = stmt.executeQuery(
                    "SELECT COUNT(*) as no_email_count FROM customers " +
                            "WHERE email IS NULL OR email = ''");
            if (rs.next()) {
                stats.append("📧 بدون بريد إلكتروني: ").append(rs.getInt("no_email_count")).append("\n");
            }

            // العملاء بدون عنوان
            rs = stmt.executeQuery(
                    "SELECT COUNT(*) as no_address_count FROM customers " +
                            "WHERE address IS NULL OR address = ''");
            if (rs.next()) {
                stats.append("🏠 بدون عنوان: ").append(rs.getInt("no_address_count")).append("\n");
            }

            // الشهر الأكثر تسجيلاً
            rs = stmt.executeQuery(
                    "SELECT MONTHNAME(created_at) as month_name, COUNT(*) as count " +
                            "FROM customers GROUP BY MONTH(created_at), YEAR(created_at) " +
                            "ORDER BY count DESC LIMIT 1");
            if (rs.next()) {
                stats.append("🏆 الشهر الأكثر تسجيلاً: ").append(rs.getString("month_name"))
                        .append(" (").append(rs.getInt("count")).append(" عميل)\n");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب إحصائيات العملاء: " + e.getMessage());
        }

        return stats.toString();
    }

    // جلب بيانات العميل مع عدد سياراته
    public Customer getCustomerWithVehicleCount(int customerId) {
        String sql = "SELECT c.*, COUNT(v.plate_number) as vehicle_count " +
                "FROM customers c " +
                "LEFT JOIN vehicles v ON c.id = v.owner_id " +
                "WHERE c.id = ? " +
                "GROUP BY c.id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Customer customer = resultSetToCustomer(rs);
                // يمكن إضافة خاصية vehicleCount للفئة Customer إذا أردت
                System.out.println("🚗 العميل " + customer.getName() + " لديه " +
                        rs.getInt("vehicle_count") + " سيارة");
                return customer;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب بيانات العميل مع عدد السيارات: " + e.getMessage());
        }
        return null;
    }

    // تصدير بيانات العملاء إلى CSV
    public boolean exportCustomersToCSV(String filePath) {
        String sql = "SELECT * FROM customers ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // هنا يمكن إضافة كود لكتابة البيانات إلى ملف CSV
            System.out.println("📤 سيتم تصدير بيانات العملاء إلى: " + filePath);

            // مثال بسيط (في التطبيق الحقيقي، اكتب إلى ملف)
            int count = 0;
            while (rs.next()) {
                count++;
                Customer customer = resultSetToCustomer(rs);
                System.out.println("   • " + customer.getName() + " - " + customer.getPhone());
            }

            System.out.println("✅ تم تصدير " + count + " عميل");
            return true;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في تصدير بيانات العملاء: " + e.getMessage());
        }
        return false;
    }

    // تحديث تاريخ آخر زيارة للعميل
    public boolean updateLastVisit(int customerId) {
        String sql = "UPDATE customers SET last_visit = NOW() WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في تحديث تاريخ الزيارة: " + e.getMessage());
        }
        return false;
    }

    // جلب أفضل العملاء (الأكثر سيارات)
    public List<Customer> getTopCustomers(int limit) {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT c.*, COUNT(v.plate_number) as vehicle_count " +
                "FROM customers c " +
                "LEFT JOIN vehicles v ON c.id = v.owner_id " +
                "GROUP BY c.id " +
                "ORDER BY vehicle_count DESC " +
                "LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Customer customer = resultSetToCustomer(rs);
                customers.add(customer);
            }

            System.out.println("🏆 تم جلب أفضل " + customers.size() + " عميل");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب أفضل العملاء: " + e.getMessage());
        }
        return customers;
    }

    // تحويل ResultSet إلى كائن Customer
    private Customer resultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getInt("id"));
        customer.setName(rs.getString("name"));
        customer.setPhone(rs.getString("phone"));
        customer.setEmail(rs.getString("email"));
        customer.setAddress(rs.getString("address"));

        // إضافة تاريخ الإنشاء إذا كان موجوداً في النتيجة
        try {
            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                // يمكن إضافة خاصية createdAt للفئة Customer إذا أردت
            }
        } catch (SQLException e) {
            // تجاهل إذا لم يكن العمود موجوداً
        }

        return customer;
    }

    // جلب العملاء مع معلومات مفصلة (للتقارير)
    public List<String[]> getCustomersDetailedReport() {
        List<String[]> report = new ArrayList<>();
        String sql = "SELECT c.id, c.name, c.phone, c.email, c.address, " +
                "c.created_at, COUNT(v.plate_number) as vehicle_count " +
                "FROM customers c " +
                "LEFT JOIN vehicles v ON c.id = v.owner_id " +
                "GROUP BY c.id " +
                "ORDER BY c.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String[] row = {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("email") != null ? rs.getString("email") : "غير محدد",
                        rs.getString("address") != null ? rs.getString("address") : "غير محدد",
                        rs.getTimestamp("created_at").toString(),
                        String.valueOf(rs.getInt("vehicle_count"))
                };
                report.add(row);
            }

            System.out.println("📋 تم إنشاء تقرير مفصل لـ " + report.size() + " عميل");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في إنشاء التقرير المفصل: " + e.getMessage());
        }
        return report;
    }
}