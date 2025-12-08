package com.carmaintenance.dao;

import com.carmaintenance.model.SparePart;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SparePartDAO {

    // إضافة قطعة غيار جديدة
    public boolean addSparePart(SparePart sparePart) {
        String sql = "INSERT INTO spare_parts (name, description, category, brand, " +
                "part_number, price, quantity, min_threshold, location) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.println("🔩 === محاولة إضافة قطعة غيار ===");
        System.out.println("📋 البيانات: " + sparePart.toString());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, sparePart.getName());
            pstmt.setString(2, sparePart.getDescription());
            pstmt.setString(3, sparePart.getCategory());
            pstmt.setString(4, sparePart.getBrand());
            pstmt.setString(5, sparePart.getPartNumber());
            pstmt.setDouble(6, sparePart.getPrice());
            pstmt.setInt(7, sparePart.getQuantity());
            pstmt.setInt(8, sparePart.getMinThreshold());
            pstmt.setString(9, sparePart.getLocation());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("📊 عدد الصفوف المتأثرة: " + affectedRows);

            if (affectedRows > 0) {
                // الحصول على الـ ID المولد
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        sparePart.setId(newId);
                        System.out.println("🆔 ID المولد للقطعة: " + newId);
                    }
                }
                System.out.println("✅ تم إضافة قطعة الغيار بنجاح!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ SQL أثناء إضافة قطعة الغيار:");
            System.err.println("   • الرسالة: " + e.getMessage());
            System.err.println("   • الخطأ: " + e.getErrorCode());
            System.err.println("   • الحالة: " + e.getSQLState());
            return false;
        }
        return false;
    }

    // الحصول على جميع قطع الغيار
    public List<SparePart> getAllSpareParts() {
        List<SparePart> spareParts = new ArrayList<>();
        String sql = "SELECT * FROM spare_parts ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SparePart sparePart = resultSetToSparePart(rs);
                spareParts.add(sparePart);
            }

            System.out.println("✅ تم جلب " + spareParts.size() + " قطعة غيار");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب قطع الغيار: " + e.getMessage());
        }
        return spareParts;
    }

    // الحصول على قطعة غيار بالرقم
    public SparePart getSparePartById(int id) {
        String sql = "SELECT * FROM spare_parts WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return resultSetToSparePart(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب قطعة الغيار: " + e.getMessage());
        }
        return null;
    }

    // البحث عن قطع غيار بالاسم
    public List<SparePart> searchSparePartsByName(String name) {
        List<SparePart> spareParts = new ArrayList<>();
        String sql = "SELECT * FROM spare_parts WHERE name LIKE ? ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SparePart sparePart = resultSetToSparePart(rs);
                spareParts.add(sparePart);
            }

            System.out.println("🔍 تم العثور على " + spareParts.size() + " قطعة غيار باسم " + name);

        } catch (SQLException e) {
            System.err.println("❌ خطأ في البحث عن قطع الغيار: " + e.getMessage());
        }
        return spareParts;
    }

    // البحث عن قطع غيار حسب الفئة
    public List<SparePart> getSparePartsByCategory(String category) {
        List<SparePart> spareParts = new ArrayList<>();
        String sql = "SELECT * FROM spare_parts WHERE category LIKE ? ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + category + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                SparePart sparePart = resultSetToSparePart(rs);
                spareParts.add(sparePart);
            }

            System.out.println("✅ تم جلب " + spareParts.size() + " قطعة غيار من فئة " + category);

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب قطع الغيار حسب الفئة: " + e.getMessage());
        }
        return spareParts;
    }

    // تحديث بيانات قطعة غيار
    public boolean updateSparePart(SparePart sparePart) {
        String sql = "UPDATE spare_parts SET name = ?, description = ?, category = ?, " +
                "brand = ?, part_number = ?, price = ?, quantity = ?, " +
                "min_threshold = ?, location = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, sparePart.getName());
            pstmt.setString(2, sparePart.getDescription());
            pstmt.setString(3, sparePart.getCategory());
            pstmt.setString(4, sparePart.getBrand());
            pstmt.setString(5, sparePart.getPartNumber());
            pstmt.setDouble(6, sparePart.getPrice());
            pstmt.setInt(7, sparePart.getQuantity());
            pstmt.setInt(8, sparePart.getMinThreshold());
            pstmt.setString(9, sparePart.getLocation());
            pstmt.setInt(10, sparePart.getId());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("📊 عدد الصفوف المتأثرة: " + affectedRows);

            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في تحديث قطعة الغيار: " + e.getMessage());
        }
        return false;
    }

    // حذف قطعة غيار
    public boolean deleteSparePart(int id) {
        String sql = "DELETE FROM spare_parts WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في حذف قطعة الغيار: " + e.getMessage());
        }
        return false;
    }

    // تحديث الكمية (زيادة/نقصان)
    public boolean updateQuantity(int id, int quantityChange) {
        String sql = "UPDATE spare_parts SET quantity = quantity + ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, quantityChange);
            pstmt.setInt(2, id);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في تحديث كمية قطعة الغيار: " + e.getMessage());
        }
        return false;
    }

    // الحصول على قطع الغيار المنخفضة المخزون
    public List<SparePart> getLowStockParts() {
        List<SparePart> spareParts = new ArrayList<>();
        String sql = "SELECT * FROM spare_parts WHERE quantity <= min_threshold ORDER BY quantity";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SparePart sparePart = resultSetToSparePart(rs);
                spareParts.add(sparePart);
            }

            System.out.println("⚠️ تم جلب " + spareParts.size() + " قطعة غيار منخفضة المخزون");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب قطع الغيار المنخفضة: " + e.getMessage());
        }
        return spareParts;
    }

    // الحصول على قطع الغيار المنتهية
    public List<SparePart> getOutOfStockParts() {
        List<SparePart> spareParts = new ArrayList<>();
        String sql = "SELECT * FROM spare_parts WHERE quantity = 0 ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SparePart sparePart = resultSetToSparePart(rs);
                spareParts.add(sparePart);
            }

            System.out.println("🚨 تم جلب " + spareParts.size() + " قطعة غيار منتهية");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب قطع الغيار المنتهية: " + e.getMessage());
        }
        return spareParts;
    }

    // عدد قطع الغيار الإجمالي
    public int getSparePartCount() {
        String sql = "SELECT COUNT(*) as count FROM spare_parts";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في عد قطع الغيار: " + e.getMessage());
        }
        return 0;
    }

    // القيمة الإجمالية للمخزون
    public double getTotalInventoryValue() {
        String sql = "SELECT SUM(price * quantity) as total_value FROM spare_parts";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total_value");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في حساب القيمة الإجمالية: " + e.getMessage());
        }
        return 0.0;
    }

    // جلب الإحصائيات
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();

        stats.append("📊 إحصائيات قطع الغيار:\n");
        stats.append("====================\n");
        stats.append("🔩 العدد الإجمالي: ").append(getSparePartCount()).append("\n");
        stats.append("💰 القيمة الإجمالية للمخزون: ").append(String.format("%.2f", getTotalInventoryValue())).append("\n");

        // قطع الغيار المنخفضة
        List<SparePart> lowStock = getLowStockParts();
        stats.append("⚠️ قطع الغيار المنخفضة: ").append(lowStock.size()).append("\n");

        // قطع الغيار المنتهية
        List<SparePart> outOfStock = getOutOfStockParts();
        stats.append("🚨 قطع الغيار المنتهية: ").append(outOfStock.size()).append("\n");

        // حسب الفئة
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT category, COUNT(*) as count, SUM(quantity) as total_qty, " +
                             "SUM(price * quantity) as category_value " +
                             "FROM spare_parts GROUP BY category ORDER BY category_value DESC")) {

            stats.append("\n🏷️ حسب الفئة:\n");
            while (rs.next()) {
                stats.append("   • ").append(rs.getString("category"))
                        .append(": ").append(rs.getInt("count")).append(" قطعة، ")
                        .append(rs.getInt("total_qty")).append(" وحدة، قيمة: ")
                        .append(String.format("%.2f", rs.getDouble("category_value"))).append("\n");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب إحصائيات الفئات: " + e.getMessage());
        }

        return stats.toString();
    }

    // تحويل ResultSet إلى كائن SparePart
    private SparePart resultSetToSparePart(ResultSet rs) throws SQLException {
        SparePart sparePart = new SparePart();
        sparePart.setId(rs.getInt("id"));
        sparePart.setName(rs.getString("name"));
        sparePart.setDescription(rs.getString("description"));
        sparePart.setCategory(rs.getString("category"));
        sparePart.setBrand(rs.getString("brand"));
        sparePart.setPartNumber(rs.getString("part_number"));
        sparePart.setPrice(rs.getDouble("price"));
        sparePart.setQuantity(rs.getInt("quantity"));
        sparePart.setMinThreshold(rs.getInt("min_threshold"));
        sparePart.setLocation(rs.getString("location"));

        return sparePart;
    }
}