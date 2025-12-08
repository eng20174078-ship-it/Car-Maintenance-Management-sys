package com.carmaintenance.dao;

import com.carmaintenance.model.MaintenanceOrder;
import com.carmaintenance.model.SparePart;
import com.carmaintenance.model.Vehicle;
import com.carmaintenance.model.Technician;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceOrderDAO {

    private VehicleDAO vehicleDAO;
    private TechnicianDAO technicianDAO;
    private SparePartDAO sparePartDAO;

    public MaintenanceOrderDAO() {
        this.vehicleDAO = new VehicleDAO();
        this.technicianDAO = new TechnicianDAO();
        this.sparePartDAO = new SparePartDAO();
    }

    // إضافة طلب صيانة جديد
    public boolean addMaintenanceOrder(MaintenanceOrder order) {
        String sql = "INSERT INTO maintenance_orders (vehicle_plate, technician_id, description, " +
                "status, customer_notes, internal_notes, estimated_cost, actual_cost) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.println("📋 === محاولة إضافة طلب صيانة ===");
        System.out.println("📋 البيانات: " + order.toString());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, order.getVehiclePlate());
            pstmt.setInt(2, order.getTechnicianId());
            pstmt.setString(3, order.getDescription());
            pstmt.setString(4, order.getStatus());
            pstmt.setString(5, order.getCustomerNotes());
            pstmt.setString(6, order.getInternalNotes());
            pstmt.setDouble(7, order.getEstimatedCost());
            pstmt.setDouble(8, order.getActualCost());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("📊 عدد الصفوف المتأثرة: " + affectedRows);

            if (affectedRows > 0) {
                // الحصول على الـ ID المولد
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        order.setId(newId);
                        System.out.println("🆔 ID المولد للطلب: " + newId);
                    }
                }
                System.out.println("✅ تم إضافة طلب الصيانة بنجاح!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ SQL أثناء إضافة طلب الصيانة:");
            System.err.println("   • الرسالة: " + e.getMessage());
            System.err.println("   • الخطأ: " + e.getErrorCode());
            System.err.println("   • الحالة: " + e.getSQLState());
            return false;
        }
        return false;
    }

    // الحصول على جميع طلبات الصيانة
    public List<MaintenanceOrder> getAllMaintenanceOrders() {
        List<MaintenanceOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_orders ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MaintenanceOrder order = resultSetToMaintenanceOrder(rs);
                loadRelatedData(order);
                orders.add(order);
            }

            System.out.println("✅ تم جلب " + orders.size() + " طلب صيانة");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب طلبات الصيانة: " + e.getMessage());
        }
        return orders;
    }

    // الحصول على طلبات الصيانة النشطة
    public List<MaintenanceOrder> getActiveMaintenanceOrders() {
        List<MaintenanceOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_orders WHERE status IN ('Pending', 'In Progress', 'Waiting for Parts') " +
                "ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MaintenanceOrder order = resultSetToMaintenanceOrder(rs);
                loadRelatedData(order);
                orders.add(order);
            }

            System.out.println("✅ تم جلب " + orders.size() + " طلب صيانة نشط");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب طلبات الصيانة النشطة: " + e.getMessage());
        }
        return orders;
    }

    // الحصول على طلبات الصيانة المكتملة
    public List<MaintenanceOrder> getCompletedMaintenanceOrders() {
        List<MaintenanceOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_orders WHERE status = 'Completed' " +
                "ORDER BY completed_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                MaintenanceOrder order = resultSetToMaintenanceOrder(rs);
                loadRelatedData(order);
                orders.add(order);
            }

            System.out.println("✅ تم جلب " + orders.size() + " طلب صيانة مكتمل");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب طلبات الصيانة المكتملة: " + e.getMessage());
        }
        return orders;
    }

    // الحصول على طلب صيانة بالرقم
    public MaintenanceOrder getMaintenanceOrderById(int id) {
        String sql = "SELECT * FROM maintenance_orders WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                MaintenanceOrder order = resultSetToMaintenanceOrder(rs);
                loadRelatedData(order);
                loadUsedParts(order);
                return order;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب طلب الصيانة: " + e.getMessage());
        }
        return null;
    }

    // الحصول على طلبات الصيانة لسيارة معينة
    public List<MaintenanceOrder> getMaintenanceOrdersByVehicle(String vehiclePlate) {
        List<MaintenanceOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_orders WHERE vehicle_plate = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehiclePlate);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MaintenanceOrder order = resultSetToMaintenanceOrder(rs);
                loadRelatedData(order);
                orders.add(order);
            }

            System.out.println("✅ تم جلب " + orders.size() + " طلب صيانة للسيارة " + vehiclePlate);

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب طلبات الصيانة للسيارة: " + e.getMessage());
        }
        return orders;
    }

    // الحصول على طلبات الصيانة لفني معين
    public List<MaintenanceOrder> getMaintenanceOrdersByTechnician(int technicianId) {
        List<MaintenanceOrder> orders = new ArrayList<>();
        String sql = "SELECT * FROM maintenance_orders WHERE technician_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, technicianId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                MaintenanceOrder order = resultSetToMaintenanceOrder(rs);
                loadRelatedData(order);
                orders.add(order);
            }

            System.out.println("✅ تم جلب " + orders.size() + " طلب صيانة للفني " + technicianId);

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب طلبات الصيانة للفني: " + e.getMessage());
        }
        return orders;
    }

    // تحديث طلب الصيانة
    public boolean updateMaintenanceOrder(MaintenanceOrder order) {
        String sql = "UPDATE maintenance_orders SET vehicle_plate = ?, technician_id = ?, " +
                "description = ?, status = ?, customer_notes = ?, internal_notes = ?, " +
                "estimated_cost = ?, actual_cost = ?, completed_at = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, order.getVehiclePlate());
            pstmt.setInt(2, order.getTechnicianId());
            pstmt.setString(3, order.getDescription());
            pstmt.setString(4, order.getStatus());
            pstmt.setString(5, order.getCustomerNotes());
            pstmt.setString(6, order.getInternalNotes());
            pstmt.setDouble(7, order.getEstimatedCost());
            pstmt.setDouble(8, order.getActualCost());

            // تاريخ الإكمال
            if (order.isCompleted() && order.getCompletedAt() != null) {
                pstmt.setTimestamp(9, Timestamp.valueOf(order.getCompletedAt()));
            } else {
                pstmt.setTimestamp(9, null);
            }

            pstmt.setInt(10, order.getId());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("📊 عدد الصفوف المتأثرة: " + affectedRows);

            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في تحديث طلب الصيانة: " + e.getMessage());
        }
        return false;
    }

    // تحديث حالة طلب الصيانة
    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE maintenance_orders SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في تحديث حالة طلب الصيانة: " + e.getMessage());
        }
        return false;
    }

    // إضافة قطعة غيار مستخدمة في الطلب
    public boolean addUsedPart(int orderId, int partId, int quantity) {
        String sql = "INSERT INTO order_parts (order_id, part_id, quantity_used) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, partId);
            pstmt.setInt(3, quantity);

            // تحديث كمية قطعة الغيار
            sparePartDAO.updateQuantity(partId, -quantity);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في إضافة قطعة غيار مستخدمة: " + e.getMessage());
        }
        return false;
    }

    // إزالة قطعة غيار من الطلب
    public boolean removeUsedPart(int orderId, int partId, int quantity) {
        String sql = "DELETE FROM order_parts WHERE order_id = ? AND part_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            pstmt.setInt(2, partId);

            // إعادة الكمية إلى المخزون
            sparePartDAO.updateQuantity(partId, quantity);

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في إزالة قطعة غيار: " + e.getMessage());
        }
        return false;
    }

    // جلب القطع المستخدمة في الطلب
    private void loadUsedParts(MaintenanceOrder order) {
        String sql = "SELECT op.*, sp.name as part_name, sp.price as unit_price " +
                "FROM order_parts op " +
                "JOIN spare_parts sp ON op.part_id = sp.id " +
                "WHERE op.order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, order.getId());
            ResultSet rs = pstmt.executeQuery();

            List<MaintenanceOrder.OrderPart> usedParts = new ArrayList<>();
            while (rs.next()) {
                MaintenanceOrder.OrderPart orderPart = new MaintenanceOrder.OrderPart(
                        rs.getInt("order_id"),
                        rs.getInt("part_id"),
                        rs.getInt("quantity_used"),
                        rs.getDouble("unit_price")
                );
                usedParts.add(orderPart);
            }

            order.setUsedParts(usedParts);
            order.updateCosts(); // تحديث التكاليف بعد تحميل القطع

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب القطع المستخدمة: " + e.getMessage());
        }
    }

    // جلب إحصائيات طلبات الصيانة
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();

        stats.append("📊 إحصائيات طلبات الصيانة:\n");
        stats.append("======================\n");

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // العدد الإجمالي
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM maintenance_orders");
            if (rs.next()) {
                stats.append("📋 العدد الإجمالي: ").append(rs.getInt("total")).append("\n");
            }

            // حسب الحالة
            rs = stmt.executeQuery(
                    "SELECT status, COUNT(*) as count FROM maintenance_orders GROUP BY status ORDER BY count DESC");
            stats.append("\n📊 حسب الحالة:\n");
            while (rs.next()) {
                stats.append("   • ").append(rs.getString("status"))
                        .append(": ").append(rs.getInt("count")).append("\n");
            }

            // إيرادات الشهر الحالي
            rs = stmt.executeQuery(
                    "SELECT SUM(actual_cost) as monthly_revenue " +
                            "FROM maintenance_orders " +
                            "WHERE status = 'Completed' AND MONTH(created_at) = MONTH(CURRENT_DATE()) " +
                            "AND YEAR(created_at) = YEAR(CURRENT_DATE())");
            if (rs.next()) {
                stats.append("\n💰 إيرادات الشهر الحالي: ").append(String.format("%.2f", rs.getDouble("monthly_revenue"))).append("\n");
            }

            // متوسط تكلفة الطلب
            rs = stmt.executeQuery(
                    "SELECT AVG(actual_cost) as avg_cost FROM maintenance_orders WHERE status = 'Completed'");
            if (rs.next()) {
                stats.append("📈 متوسط تكلفة الطلب: ").append(String.format("%.2f", rs.getDouble("avg_cost"))).append("\n");
            }

            // الفني الأكثر إنتاجية
            rs = stmt.executeQuery(
                    "SELECT t.name, COUNT(mo.id) as order_count " +
                            "FROM maintenance_orders mo " +
                            "JOIN technicians t ON mo.technician_id = t.id " +
                            "WHERE mo.status = 'Completed' " +
                            "GROUP BY mo.technician_id " +
                            "ORDER BY order_count DESC " +
                            "LIMIT 1");
            if (rs.next()) {
                stats.append("👨‍🔧 الفني الأكثر إنتاجية: ").append(rs.getString("name"))
                        .append(" (").append(rs.getInt("order_count")).append(" طلب)\n");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب إحصائيات طلبات الصيانة: " + e.getMessage());
        }

        return stats.toString();
    }

    // عدد طلبات الصيانة
    public int getMaintenanceOrderCount() {
        String sql = "SELECT COUNT(*) as count FROM maintenance_orders";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في عد طلبات الصيانة: " + e.getMessage());
        }
        return 0;
    }

    // تحويل ResultSet إلى كائن MaintenanceOrder
    private MaintenanceOrder resultSetToMaintenanceOrder(ResultSet rs) throws SQLException {
        MaintenanceOrder order = new MaintenanceOrder();
        order.setId(rs.getInt("id"));
        order.setVehiclePlate(rs.getString("vehicle_plate"));
        order.setTechnicianId(rs.getInt("technician_id"));
        order.setDescription(rs.getString("description"));
        order.setStatus(rs.getString("status"));

        // تحويل Timestamp إلى LocalDateTime
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            order.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp completedAt = rs.getTimestamp("completed_at");
        if (completedAt != null) {
            order.setCompletedAt(completedAt.toLocalDateTime());
        }

        order.setCustomerNotes(rs.getString("customer_notes"));
        order.setInternalNotes(rs.getString("internal_notes"));
        order.setEstimatedCost(rs.getDouble("estimated_cost"));
        order.setActualCost(rs.getDouble("actual_cost"));

        return order;
    }

    // تحميل البيانات المرتبطة (السيارة والفني)
    private void loadRelatedData(MaintenanceOrder order) {
        // تحميل بيانات السيارة
        Vehicle vehicle = vehicleDAO.getVehicleByPlate(order.getVehiclePlate());
        order.setVehicle(vehicle);

        // تحميل بيانات الفني
        Technician technician = technicianDAO.getTechnicianById(order.getTechnicianId());
        order.setTechnician(technician);
    }
}