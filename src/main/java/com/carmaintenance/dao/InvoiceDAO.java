package com.carmaintenance.dao;

import com.carmaintenance.model.Invoice;
import com.carmaintenance.model.MaintenanceOrder;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    private MaintenanceOrderDAO orderDAO;

    public InvoiceDAO() {
        this.orderDAO = new MaintenanceOrderDAO();
    }

    // إنشاء فاتورة جديدة
    public boolean addInvoice(Invoice invoice) {
        String sql = "INSERT INTO invoices (order_id, total_amount, tax_amount, discount_amount, " +
                "issued_date, due_date, payment_method, paid, payment_date, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.println("🧾 === محاولة إنشاء فاتورة ===");
        System.out.println("📋 البيانات: " + invoice.toString());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, invoice.getOrderId());
            pstmt.setDouble(2, invoice.getTotalAmount());
            pstmt.setDouble(3, invoice.getTaxAmount());
            pstmt.setDouble(4, invoice.getDiscountAmount());
            pstmt.setTimestamp(5, Timestamp.valueOf(invoice.getIssuedDate()));
            pstmt.setTimestamp(6, Timestamp.valueOf(invoice.getDueDate()));
            pstmt.setString(7, invoice.getPaymentMethod());
            pstmt.setBoolean(8, invoice.isPaid());

            if (invoice.getPaymentDate() != null) {
                pstmt.setTimestamp(9, Timestamp.valueOf(invoice.getPaymentDate()));
            } else {
                pstmt.setTimestamp(9, null);
            }

            pstmt.setString(10, invoice.getNotes());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("📊 عدد الصفوف المتأثرة: " + affectedRows);

            if (affectedRows > 0) {
                // الحصول على الـ ID المولد
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        invoice.setId(newId);
                        System.out.println("🆔 ID المولد للفاتورة: " + newId);
                    }
                }
                System.out.println("✅ تم إنشاء الفاتورة بنجاح!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ SQL أثناء إنشاء الفاتورة:");
            System.err.println("   • الرسالة: " + e.getMessage());
            System.err.println("   • الخطأ: " + e.getErrorCode());
            System.err.println("   • الحالة: " + e.getSQLState());
            return false;
        }
        return false;
    }

    // الحصول على جميع الفواتير
    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY issued_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Invoice invoice = resultSetToInvoice(rs);
                loadRelatedData(invoice);
                invoices.add(invoice);
            }

            System.out.println("✅ تم جلب " + invoices.size() + " فاتورة");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب الفواتير: " + e.getMessage());
        }
        return invoices;
    }

    // الحصول على الفواتير غير المدفوعة
    public List<Invoice> getUnpaidInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices WHERE paid = false ORDER BY due_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Invoice invoice = resultSetToInvoice(rs);
                loadRelatedData(invoice);
                invoices.add(invoice);
            }

            System.out.println("✅ تم جلب " + invoices.size() + " فاتورة غير مدفوعة");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب الفواتير غير المدفوعة: " + e.getMessage());
        }
        return invoices;
    }

    // الحصول على الفواتير المتأخرة
    public List<Invoice> getOverdueInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices WHERE paid = false AND due_date < CURRENT_TIMESTAMP ORDER BY due_date";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Invoice invoice = resultSetToInvoice(rs);
                loadRelatedData(invoice);
                invoices.add(invoice);
            }

            System.out.println("⚠️ تم جلب " + invoices.size() + " فاتورة متأخرة");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب الفواتير المتأخرة: " + e.getMessage());
        }
        return invoices;
    }

    // الحصول على فاتورة بالرقم
    public Invoice getInvoiceById(int id) {
        String sql = "SELECT * FROM invoices WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Invoice invoice = resultSetToInvoice(rs);
                loadRelatedData(invoice);
                return invoice;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب الفاتورة: " + e.getMessage());
        }
        return null;
    }

    // الحصول على فاتورة لطلب معين
    public Invoice getInvoiceByOrderId(int orderId) {
        String sql = "SELECT * FROM invoices WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Invoice invoice = resultSetToInvoice(rs);
                loadRelatedData(invoice);
                return invoice;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب فاتورة الطلب: " + e.getMessage());
        }
        return null;
    }

    // تحديث فاتورة
    public boolean updateInvoice(Invoice invoice) {
        String sql = "UPDATE invoices SET total_amount = ?, tax_amount = ?, discount_amount = ?, " +
                "issued_date = ?, due_date = ?, payment_method = ?, paid = ?, " +
                "payment_date = ?, notes = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, invoice.getTotalAmount());
            pstmt.setDouble(2, invoice.getTaxAmount());
            pstmt.setDouble(3, invoice.getDiscountAmount());
            pstmt.setTimestamp(4, Timestamp.valueOf(invoice.getIssuedDate()));
            pstmt.setTimestamp(5, Timestamp.valueOf(invoice.getDueDate()));
            pstmt.setString(6, invoice.getPaymentMethod());
            pstmt.setBoolean(7, invoice.isPaid());

            if (invoice.getPaymentDate() != null) {
                pstmt.setTimestamp(8, Timestamp.valueOf(invoice.getPaymentDate()));
            } else {
                pstmt.setTimestamp(8, null);
            }

            pstmt.setString(9, invoice.getNotes());
            pstmt.setInt(10, invoice.getId());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("📊 عدد الصفوف المتأثرة: " + affectedRows);

            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في تحديث الفاتورة: " + e.getMessage());
        }
        return false;
    }

    // حذف فاتورة
    public boolean deleteInvoice(int id) {
        String sql = "DELETE FROM invoices WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في حذف الفاتورة: " + e.getMessage());
        }
        return false;
    }

    // تسديد فاتورة
    public boolean markAsPaid(int invoiceId, String paymentMethod) {
        String sql = "UPDATE invoices SET paid = true, payment_method = ?, payment_date = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, paymentMethod);
            pstmt.setInt(2, invoiceId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في تسديد الفاتورة: " + e.getMessage());
        }
        return false;
    }

    // عدد الفواتير الإجمالي
    public int getInvoiceCount() {
        String sql = "SELECT COUNT(*) as count FROM invoices";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في عد الفواتير: " + e.getMessage());
        }
        return 0;
    }

    // إجمالي المبيعات
    public double getTotalSales() {
        String sql = "SELECT SUM(final_amount) as total_sales FROM (SELECT total_amount + tax_amount - discount_amount as final_amount FROM invoices WHERE paid = true) as sales";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total_sales");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في حساب إجمالي المبيعات: " + e.getMessage());
        }
        return 0.0;
    }

    // إجمالي المستحقات
    public double getTotalReceivables() {
        String sql = "SELECT SUM(total_amount + tax_amount - discount_amount) as total_receivables FROM invoices WHERE paid = false";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total_receivables");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في حساب إجمالي المستحقات: " + e.getMessage());
        }
        return 0.0;
    }

    // جلب الإحصائيات
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();

        stats.append("📊 إحصائيات الفواتير:\n");
        stats.append("===================\n");
        stats.append("🧾 العدد الإجمالي: ").append(getInvoiceCount()).append("\n");
        stats.append("💰 إجمالي المبيعات: ").append(String.format("%.2f", getTotalSales())).append("\n");
        stats.append("📈 إجمالي المستحقات: ").append(String.format("%.2f", getTotalReceivables())).append("\n");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // الفواتير المتأخرة
            ResultSet rs = stmt.executeQuery(
                    "SELECT COUNT(*) as overdue_count, SUM(total_amount + tax_amount - discount_amount) as overdue_amount " +
                            "FROM invoices WHERE paid = false AND due_date < CURRENT_TIMESTAMP");
            if (rs.next()) {
                stats.append("⚠️ الفواتير المتأخرة: ").append(rs.getInt("overdue_count"))
                        .append(" (قيمة: ").append(String.format("%.2f", rs.getDouble("overdue_amount"))).append(")\n");
            }

            // حسب طريقة الدفع
            rs = stmt.executeQuery(
                    "SELECT payment_method, COUNT(*) as count, SUM(total_amount + tax_amount - discount_amount) as amount " +
                            "FROM invoices WHERE paid = true GROUP BY payment_method ORDER BY amount DESC");
            stats.append("\n💳 حسب طريقة الدفع:\n");
            while (rs.next()) {
                stats.append("   • ").append(rs.getString("payment_method"))
                        .append(": ").append(rs.getInt("count")).append(" فاتورة، ")
                        .append(String.format("%.2f", rs.getDouble("amount"))).append("\n");
            }

            // الإيرادات الشهرية
            rs = stmt.executeQuery(
                    "SELECT MONTH(issued_date) as month, YEAR(issued_date) as year, " +
                            "COUNT(*) as count, SUM(total_amount + tax_amount - discount_amount) as revenue " +
                            "FROM invoices WHERE paid = true " +
                            "GROUP BY YEAR(issued_date), MONTH(issued_date) " +
                            "ORDER BY year DESC, month DESC " +
                            "LIMIT 6");
            stats.append("\n📅 الإيرادات الشهرية (آخر 6 أشهر):\n");
            while (rs.next()) {
                stats.append("   • ").append(rs.getInt("year")).append("-").append(rs.getInt("month"))
                        .append(": ").append(rs.getInt("count")).append(" فاتورة، ")
                        .append(String.format("%.2f", rs.getDouble("revenue"))).append("\n");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب إحصائيات الفواتير: " + e.getMessage());
        }

        return stats.toString();
    }

    // تحويل ResultSet إلى كائن Invoice
    private Invoice resultSetToInvoice(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getInt("id"));
        invoice.setOrderId(rs.getInt("order_id"));
        invoice.setTotalAmount(rs.getDouble("total_amount"));
        invoice.setTaxAmount(rs.getDouble("tax_amount"));
        invoice.setDiscountAmount(rs.getDouble("discount_amount"));

        // تحويل التواريخ
        Timestamp issuedDate = rs.getTimestamp("issued_date");
        if (issuedDate != null) {
            invoice.setIssuedDate(issuedDate.toLocalDateTime());
        }

        Timestamp dueDate = rs.getTimestamp("due_date");
        if (dueDate != null) {
            invoice.setDueDate(dueDate.toLocalDateTime());
        }

        Timestamp paymentDate = rs.getTimestamp("payment_date");
        if (paymentDate != null) {
            invoice.setPaymentDate(paymentDate.toLocalDateTime());
        }

        invoice.setPaymentMethod(rs.getString("payment_method"));
        invoice.setPaid(rs.getBoolean("paid"));
        invoice.setNotes(rs.getString("notes"));

        return invoice;
    }

    // تحميل البيانات المرتبطة (طلب الصيانة)
    private void loadRelatedData(Invoice invoice) {
        MaintenanceOrder order = orderDAO.getMaintenanceOrderById(invoice.getOrderId());
        invoice.setOrder(order);
    }
}