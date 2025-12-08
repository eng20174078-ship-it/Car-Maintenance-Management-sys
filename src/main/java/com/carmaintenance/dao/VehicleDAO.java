package com.carmaintenance.dao;

import com.carmaintenance.model.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO {

    // إضافة سيارة جديدة
    public boolean addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (plate_number, model, year, owner_id, color, engine_type, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        System.out.println("🚗 === محاولة إضافة سيارة ===");
        System.out.println("📋 البيانات: " + vehicle.toString());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehicle.getPlateNumber());
            pstmt.setString(2, vehicle.getModel());
            pstmt.setInt(3, vehicle.getYear());
            pstmt.setInt(4, vehicle.getOwnerId());
            pstmt.setString(5, vehicle.getColor());
            pstmt.setString(6, vehicle.getEngineType());
            pstmt.setString(7, vehicle.getNotes());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("📊 عدد الصفوف المتأثرة: " + affectedRows);

            if (affectedRows > 0) {
                System.out.println("✅ تم إضافة السيارة بنجاح!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ SQL أثناء إضافة السيارة:");
            System.err.println("   • الرسالة: " + e.getMessage());
            System.err.println("   • الخطأ: " + e.getErrorCode());
            System.err.println("   • الحالة: " + e.getSQLState());

            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.err.println("⚠️ رقم اللوحة مسجل مسبقاً!");
            } else if (e.getErrorCode() == 1452) { // Foreign key constraint
                System.err.println("⚠️ رقم العميل غير موجود!");
            }
            return false;
        }
        return false;
    }

    // الحصول على جميع السيارات
    public List<Vehicle> getAllVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT v.*, c.name as owner_name, c.phone as owner_phone " +
                "FROM vehicles v " +
                "LEFT JOIN customers c ON v.owner_id = c.id " +
                "ORDER BY v.plate_number";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setPlateNumber(rs.getString("plate_number"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setYear(rs.getInt("year"));
                vehicle.setOwnerId(rs.getInt("owner_id"));
                vehicle.setColor(rs.getString("color"));
                vehicle.setEngineType(rs.getString("engine_type"));
                vehicle.setNotes(rs.getString("notes"));

                // يمكن إضافة معلومات المالك هنا لاحقاً

                vehicles.add(vehicle);
            }

            System.out.println("✅ تم جلب " + vehicles.size() + " سيارة");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب السيارات: " + e.getMessage());
        }
        return vehicles;
    }

    // الحصول على سيارة برقم اللوحة
    public Vehicle getVehicleByPlate(String plateNumber) {
        String sql = "SELECT * FROM vehicles WHERE plate_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plateNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setPlateNumber(rs.getString("plate_number"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setYear(rs.getInt("year"));
                vehicle.setOwnerId(rs.getInt("owner_id"));
                vehicle.setColor(rs.getString("color"));
                vehicle.setEngineType(rs.getString("engine_type"));
                vehicle.setNotes(rs.getString("notes"));
                return vehicle;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب السيارة: " + e.getMessage());
        }
        return null;
    }

    // تحديث بيانات سيارة
    public boolean updateVehicle(Vehicle vehicle) {
        String sql = "UPDATE vehicles SET model = ?, year = ?, owner_id = ?, " +
                "color = ?, engine_type = ?, notes = ? WHERE plate_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, vehicle.getModel());
            pstmt.setInt(2, vehicle.getYear());
            pstmt.setInt(3, vehicle.getOwnerId());
            pstmt.setString(4, vehicle.getColor());
            pstmt.setString(5, vehicle.getEngineType());
            pstmt.setString(6, vehicle.getNotes());
            pstmt.setString(7, vehicle.getPlateNumber());

            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في تحديث السيارة: " + e.getMessage());
        }
        return false;
    }

    // حذف سيارة
    public boolean deleteVehicle(String plateNumber) {
        String sql = "DELETE FROM vehicles WHERE plate_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plateNumber);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في حذف السيارة: " + e.getMessage());
        }
        return false;
    }

    // التحقق من وجود رقم لوحة
    public boolean isPlateExists(String plateNumber) {
        String sql = "SELECT COUNT(*) FROM vehicles WHERE plate_number = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, plateNumber);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("🚗 رقم اللوحة " + plateNumber + " موجود " + count + " مرة");
                return count > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في التحقق من رقم اللوحة: " + e.getMessage());
        }
        return false;
    }

    // الحصول على سيارات عميل معين
    public List<Vehicle> getVehiclesByOwner(int ownerId) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE owner_id = ? ORDER BY model";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ownerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Vehicle vehicle = new Vehicle();
                vehicle.setPlateNumber(rs.getString("plate_number"));
                vehicle.setModel(rs.getString("model"));
                vehicle.setYear(rs.getInt("year"));
                vehicle.setOwnerId(rs.getInt("owner_id"));
                vehicle.setColor(rs.getString("color"));
                vehicle.setEngineType(rs.getString("engine_type"));
                vehicle.setNotes(rs.getString("notes"));

                vehicles.add(vehicle);
            }

            System.out.println("✅ تم جلب " + vehicles.size() + " سيارة للعميل " + ownerId);

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب سيارات العميل: " + e.getMessage());
        }
        return vehicles;
    }

    // عدد السيارات الإجمالي
    public int getVehicleCount() {
        String sql = "SELECT COUNT(*) as count FROM vehicles";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في عد السيارات: " + e.getMessage());
        }
        return 0;
    }
}