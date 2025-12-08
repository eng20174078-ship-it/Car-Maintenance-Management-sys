package com.carmaintenance.dao;

import com.carmaintenance.model.Technician;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TechnicianDAO {

    // إضافة فني جديد
    public boolean addTechnician(Technician technician) {
        String sql = "INSERT INTO technicians (name, phone, email, specialization, " +
                "hire_date, salary, address, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.println("👨‍🔧 === محاولة إضافة فني ===");
        System.out.println("📋 البيانات: " + technician.toString());

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, technician.getName());
            pstmt.setString(2, technician.getPhone());
            pstmt.setString(3, technician.getEmail());
            pstmt.setString(4, technician.getSpecialization());

            // تحويل LocalDate إلى java.sql.Date
            if (technician.getHireDate() != null) {
                pstmt.setDate(5, Date.valueOf(technician.getHireDate()));
            } else {
                pstmt.setDate(5, Date.valueOf(LocalDate.now()));
            }

            pstmt.setDouble(6, technician.getSalary());
            pstmt.setString(7, technician.getAddress());
            pstmt.setString(8, technician.getStatus());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("📊 عدد الصفوف المتأثرة: " + affectedRows);

            if (affectedRows > 0) {
                // الحصول على الـ ID المولد
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        technician.setId(newId);
                        System.out.println("🆔 ID المولد للفني: " + newId);
                    }
                }
                System.out.println("✅ تم إضافة الفني بنجاح!");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ SQL أثناء إضافة الفني:");
            System.err.println("   • الرسالة: " + e.getMessage());
            System.err.println("   • الخطأ: " + e.getErrorCode());
            System.err.println("   • الحالة: " + e.getSQLState());

            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.err.println("⚠️ رقم الهاتف مسجل مسبقاً!");
            }
            return false;
        }
        return false;
    }

    // الحصول على جميع الفنيين
    public List<Technician> getAllTechnicians() {
        List<Technician> technicians = new ArrayList<>();
        String sql = "SELECT * FROM technicians ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Technician technician = resultSetToTechnician(rs);
                technicians.add(technician);
            }

            System.out.println("✅ تم جلب " + technicians.size() + " فني");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب الفنيين: " + e.getMessage());
        }
        return technicians;
    }

    // الحصول على الفنيين النشطين فقط
    public List<Technician> getActiveTechnicians() {
        List<Technician> technicians = new ArrayList<>();
        String sql = "SELECT * FROM technicians WHERE status = 'Active' ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Technician technician = resultSetToTechnician(rs);
                technicians.add(technician);
            }

            System.out.println("✅ تم جلب " + technicians.size() + " فني نشط");

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب الفنيين النشطين: " + e.getMessage());
        }
        return technicians;
    }

    // الحصول على فني بالرقم
    public Technician getTechnicianById(int id) {
        String sql = "SELECT * FROM technicians WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return resultSetToTechnician(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب الفني: " + e.getMessage());
        }
        return null;
    }

    // البحث عن فني بالاسم
    public List<Technician> searchTechniciansByName(String name) {
        List<Technician> technicians = new ArrayList<>();
        String sql = "SELECT * FROM technicians WHERE name LIKE ? ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + name + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Technician technician = resultSetToTechnician(rs);
                technicians.add(technician);
            }

            System.out.println("🔍 تم العثور على " + technicians.size() + " فني باسم " + name);

        } catch (SQLException e) {
            System.err.println("❌ خطأ في البحث عن الفنيين: " + e.getMessage());
        }
        return technicians;
    }

    // تحديث بيانات فني
    public boolean updateTechnician(Technician technician) {
        String sql = "UPDATE technicians SET name = ?, phone = ?, email = ?, " +
                "specialization = ?, hire_date = ?, salary = ?, " +
                "address = ?, status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, technician.getName());
            pstmt.setString(2, technician.getPhone());
            pstmt.setString(3, technician.getEmail());
            pstmt.setString(4, technician.getSpecialization());

            if (technician.getHireDate() != null) {
                pstmt.setDate(5, Date.valueOf(technician.getHireDate()));
            } else {
                pstmt.setDate(5, null);
            }

            pstmt.setDouble(6, technician.getSalary());
            pstmt.setString(7, technician.getAddress());
            pstmt.setString(8, technician.getStatus());
            pstmt.setInt(9, technician.getId());

            int affectedRows = pstmt.executeUpdate();
            System.out.println("📊 عدد الصفوف المتأثرة: " + affectedRows);

            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في تحديث الفني: " + e.getMessage());
        }
        return false;
    }

    // حذف فني
    public boolean deleteTechnician(int id) {
        String sql = "DELETE FROM technicians WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("❌ خطأ في حذف الفني: " + e.getMessage());
        }
        return false;
    }

    // التحقق من وجود رقم هاتف
    public boolean isPhoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM technicians WHERE phone = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                System.out.println("📱 رقم الهاتف " + phone + " موجود " + count + " مرة لدى الفنيين");
                return count > 0;
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في التحقق من رقم الهاتف: " + e.getMessage());
        }
        return false;
    }

    // عدد الفنيين الإجمالي
    public int getTechnicianCount() {
        String sql = "SELECT COUNT(*) as count FROM technicians";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في عد الفنيين: " + e.getMessage());
        }
        return 0;
    }

    // عدد الفنيين النشطين
    public int getActiveTechnicianCount() {
        String sql = "SELECT COUNT(*) as count FROM technicians WHERE status = 'Active'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في عد الفنيين النشطين: " + e.getMessage());
        }
        return 0;
    }

    // جلب الفنيين حسب التخصص
    public List<Technician> getTechniciansBySpecialization(String specialization) {
        List<Technician> technicians = new ArrayList<>();
        String sql = "SELECT * FROM technicians WHERE specialization LIKE ? AND status = 'Active' ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "%" + specialization + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Technician technician = resultSetToTechnician(rs);
                technicians.add(technician);
            }

            System.out.println("✅ تم جلب " + technicians.size() + " فني بتخصص " + specialization);

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب الفنيين حسب التخصص: " + e.getMessage());
        }
        return technicians;
    }

    // تحويل ResultSet إلى كائن Technician
    private Technician resultSetToTechnician(ResultSet rs) throws SQLException {
        Technician technician = new Technician();
        technician.setId(rs.getInt("id"));
        technician.setName(rs.getString("name"));
        technician.setPhone(rs.getString("phone"));
        technician.setEmail(rs.getString("email"));
        technician.setSpecialization(rs.getString("specialization"));

        // تحويل java.sql.Date إلى LocalDate
        Date hireDate = rs.getDate("hire_date");
        if (hireDate != null) {
            technician.setHireDate(hireDate.toLocalDate());
        }

        technician.setSalary(rs.getDouble("salary"));
        technician.setAddress(rs.getString("address"));
        technician.setStatus(rs.getString("status"));

        return technician;
    }

    // جلب الإحصائيات
    public String getStatistics() {
        StringBuilder stats = new StringBuilder();

        stats.append("📊 إحصائيات الفنيين:\n");
        stats.append("==================\n");
        stats.append("👥 العدد الإجمالي: ").append(getTechnicianCount()).append("\n");
        stats.append("✅ النشطين: ").append(getActiveTechnicianCount()).append("\n");

        // عدد الفنيين حسب التخصص
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                     "SELECT specialization, COUNT(*) as count " +
                             "FROM technicians WHERE status = 'Active' " +
                             "GROUP BY specialization ORDER BY count DESC")) {

            stats.append("\n🔧 التخصصات:\n");
            while (rs.next()) {
                stats.append("   • ").append(rs.getString("specialization"))
                        .append(": ").append(rs.getInt("count")).append("\n");
            }

        } catch (SQLException e) {
            System.err.println("❌ خطأ في جلب إحصائيات التخصصات: " + e.getMessage());
        }

        return stats.toString();
    }
}