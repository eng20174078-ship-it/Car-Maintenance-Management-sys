package com.carmaintenance.dao;

import java.sql.*;

public class DatabaseCreator {

    public static void createDatabaseIfNotExists() {
        System.out.println("🗄️ === إنشاء قاعدة البيانات والجداول ===");

        Connection conn = null;
        Statement stmt = null;

        try {
            // 1. الاتصال بـ MySQL بدون تحديد قاعدة بيانات
            String url = "jdbc:mysql://localhost:3306/";
            String user = "root";
            String password = "";

            conn = DriverManager.getConnection(url, user, password);
            System.out.println("✅ الاتصال بـ MySQL ناجح");

            stmt = conn.createStatement();

            // 2. إنشاء قاعدة البيانات إذا لم تكن موجودة
            String createDbSQL = "CREATE DATABASE IF NOT EXISTS car_maintenance_db " +
                    "CHARACTER SET utf8mb4 " +
                    "COLLATE utf8mb4_unicode_ci";

            stmt.executeUpdate(createDbSQL);
            System.out.println("✅ تم إنشاء/التحقق من قاعدة البيانات");

            // 3. استخدام قاعدة البيانات
            stmt.executeUpdate("USE car_maintenance_db");
            System.out.println("✅ تم التبديل إلى قاعدة البيانات");

            // 4. إنشاء الجداول
            createTables(conn);

            // 5. إضافة بيانات تجريبية
            insertSampleData(conn);

            System.out.println("🎉 تم إنشاء قاعدة البيانات والجداول بنجاح!");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في إنشاء قاعدة البيانات: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // تنظيف الموارد
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
                System.out.println("✅ تم إغلاق الاتصال");
            } catch (SQLException e) {
                System.err.println("⚠️ خطأ في إغلاق الموارد: " + e.getMessage());
            }
        }
    }

    private static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // 1. جدول العملاء
        String createCustomersTable =
                "CREATE TABLE IF NOT EXISTS customers (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL," +
                        "  phone VARCHAR(20) UNIQUE NOT NULL," +
                        "  email VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  address TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createCustomersTable);
        System.out.println("✅ جدول العملاء جاهز");

        // 2. جدول السيارات
        String createVehiclesTable =
                "CREATE TABLE IF NOT EXISTS vehicles (" +
                        "  plate_number VARCHAR(20) PRIMARY KEY," +
                        "  model VARCHAR(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL," +
                        "  year INT," +
                        "  owner_id INT NOT NULL," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  FOREIGN KEY (owner_id) REFERENCES customers(id) ON DELETE CASCADE" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createVehiclesTable);
        System.out.println("✅ جدول السيارات جاهز");

        // 3. جدول الفنيين
        String createTechniciansTable =
                "CREATE TABLE IF NOT EXISTS technicians (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL," +
                        "  phone VARCHAR(20) UNIQUE NOT NULL," +
                        "  specialization VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  hire_date DATE," +
                        "  salary DECIMAL(10, 2)," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createTechniciansTable);
        System.out.println("✅ جدول الفنيين جاهز");

        // 4. جدول قطع الغيار
        String createSparePartsTable =
                "CREATE TABLE IF NOT EXISTS spare_parts (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  name VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL," +
                        "  price DECIMAL(10, 2) NOT NULL," +
                        "  quantity INT DEFAULT 0," +
                        "  min_threshold INT DEFAULT 5," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createSparePartsTable);
        System.out.println("✅ جدول قطع الغيار جاهز");

        // 5. جدول طلبات الصيانة
        String createMaintenanceOrdersTable =
                "CREATE TABLE IF NOT EXISTS maintenance_orders (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  vehicle_plate VARCHAR(20) NOT NULL," +
                        "  technician_id INT NOT NULL," +
                        "  description TEXT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci," +
                        "  status ENUM('Pending', 'In Progress', 'Waiting for Parts', 'Completed') DEFAULT 'Pending'," +
                        "  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  completed_at TIMESTAMP NULL," +
                        "  FOREIGN KEY (vehicle_plate) REFERENCES vehicles(plate_number) ON DELETE CASCADE," +
                        "  FOREIGN KEY (technician_id) REFERENCES technicians(id) ON DELETE CASCADE" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createMaintenanceOrdersTable);
        System.out.println("✅ جدول طلبات الصيانة جاهز");

        // 6. جدول الفواتير
        String createInvoicesTable =
                "CREATE TABLE IF NOT EXISTS invoices (" +
                        "  id INT AUTO_INCREMENT PRIMARY KEY," +
                        "  order_id INT NOT NULL," +
                        "  total_amount DECIMAL(10, 2) NOT NULL," +
                        "  issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                        "  paid BOOLEAN DEFAULT FALSE," +
                        "  FOREIGN KEY (order_id) REFERENCES maintenance_orders(id) ON DELETE CASCADE" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createInvoicesTable);
        System.out.println("✅ جدول الفواتير جاهز");

        // 7. جدول العلاقة بين الطلبات وقطع الغيار
        String createOrderPartsTable =
                "CREATE TABLE IF NOT EXISTS order_parts (" +
                        "  order_id INT," +
                        "  part_id INT," +
                        "  quantity_used INT NOT NULL," +
                        "  PRIMARY KEY (order_id, part_id)," +
                        "  FOREIGN KEY (order_id) REFERENCES maintenance_orders(id) ON DELETE CASCADE," +
                        "  FOREIGN KEY (part_id) REFERENCES spare_parts(id) ON DELETE CASCADE" +
                        ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        stmt.executeUpdate(createOrderPartsTable);
        System.out.println("✅ جدول العلاقة بين الطلبات وقطع الغيار جاهز");

        stmt.close();
    }

    private static void insertSampleData(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        // التحقق مما إذا كانت الجداول فارغة
        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM customers");
        rs.next();
        int customerCount = rs.getInt("count");

        if (customerCount == 0) {
            System.out.println("📝 إضافة بيانات تجريبية...");

            // 1. إضافة عملاء
            String insertCustomers =
                    "INSERT INTO customers (name, phone, email, address) VALUES " +
                            "('أحمد محمد', '0912345678', 'ahmed@example.com', 'بنغازي - الحي الأول'), " +
                            "('فاطمة علي', '0923456789', 'fatima@example.com', 'طرابلس - حي الأندلس'), " +
                            "('خالد حسين', '0934567890', NULL, 'درنة - وسط المدينة')";

            stmt.executeUpdate(insertCustomers);
            System.out.println("✅ تم إضافة 3 عملاء");

            // 2. إضافة فنيين
            String insertTechnicians =
                    "INSERT INTO technicians (name, phone, specialization, hire_date, salary) VALUES " +
                            "('محمود سالم', '0945678901', 'ميكانيكا محركات', '2023-01-15', 2500.00), " +
                            "('سالم الكيومي', '0956789012', 'كهرباء سيارات', '2023-03-20', 2200.00)";

            stmt.executeUpdate(insertTechnicians);
            System.out.println("✅ تم إضافة 2 فني");

            // 3. إضافة قطع غيار
            String insertSpareParts =
                    "INSERT INTO spare_parts (name, price, quantity) VALUES " +
                            "('فلتر زيت', 15.50, 25), " +
                            "('شمعة احتراق', 8.75, 40), " +
                            "('مكابح أمامية', 45.00, 12), " +
                            "('بطارية', 120.00, 8)";

            stmt.executeUpdate(insertSpareParts);
            System.out.println("✅ تم إضافة 4 قطع غيار");

            // 4. إضافة سيارات
            String insertVehicles =
                    "INSERT INTO vehicles (plate_number, model, year, owner_id) VALUES " +
                            "('12345', 'تويوتا كورولا', 2020, 1), " +
                            "('54321', 'هيونداي أكسنت', 2019, 2), " +
                            "('67890', 'كيا سيراتو', 2021, 3)";

            stmt.executeUpdate(insertVehicles);
            System.out.println("✅ تم إضافة 3 سيارات");

        } else {
            System.out.println("📊 قاعدة البيانات تحتوي على بيانات حالياً (" + customerCount + " عميل)");
        }

        // عرض الإحصائيات
        System.out.println("\n📈 إحصائيات قاعدة البيانات:");

        String[] tables = {"customers", "technicians", "spare_parts", "vehicles"};
        String[] arabicNames = {"العملاء", "الفنيين", "قطع الغيار", "السيارات"};

        for (int i = 0; i < tables.length; i++) {
            rs = stmt.executeQuery("SELECT COUNT(*) as count FROM " + tables[i]);
            if (rs.next()) {
                System.out.println("   • " + arabicNames[i] + ": " + rs.getInt("count"));
            }
        }

        rs.close();
        stmt.close();
    }

    public static void dropAndRecreateDatabase() {
        System.out.println("🔄 === إعادة إنشاء قاعدة البيانات من الصفر ===");

        Connection conn = null;
        Statement stmt = null;

        try {
            // الاتصال بـ MySQL بدون تحديد قاعدة بيانات
            String url = "jdbc:mysql://localhost:3306/";
            String user = "root";
            String password = "";

            conn = DriverManager.getConnection(url, user, password);
            stmt = conn.createStatement();

            // حذف قاعدة البيانات إذا كانت موجودة
            stmt.executeUpdate("DROP DATABASE IF EXISTS car_maintenance_db");
            System.out.println("🗑️ تم حذف قاعدة البيانات القديمة");

            // إعادة إنشاء كل شيء
            createDatabaseIfNotExists();

        } catch (SQLException e) {
            System.err.println("❌ خطأ في إعادة الإنشاء: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("⚠️ خطأ في إغلاق الموارد: " + e.getMessage());
            }
        }
    }

    public static void testDatabaseConnection() {
        System.out.println("🔍 === اختبار اتصال قاعدة البيانات ===");

        try {
            String url = "jdbc:mysql://localhost:3306/car_maintenance_db";
            String user = "root";
            String password = "";

            Connection conn = DriverManager.getConnection(url, user, password);

            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ الاتصال بقاعدة البيانات ناجح");

                // اختبار الاستعلامات
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SHOW TABLES");

                System.out.println("📋 الجداول الموجودة:");
                int tableCount = 0;
                while (rs.next()) {
                    tableCount++;
                    System.out.println("   • " + rs.getString(1));
                }

                System.out.println("📊 العدد الإجمالي للجداول: " + tableCount);

                rs.close();
                stmt.close();
                conn.close();

                System.out.println("🎉 جميع الاختبارات ناجحة!");
            }

        } catch (SQLException e) {
            System.err.println("❌ فشل الاتصال: " + e.getMessage());

            if (e.getErrorCode() == 1049) { // قاعدة البيانات غير موجودة
                System.out.println("💡 الحل: قاعدة البيانات غير موجودة، سيتم إنشاؤها...");
                createDatabaseIfNotExists();
            }
        }
    }
}