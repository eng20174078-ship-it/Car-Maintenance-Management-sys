//package com.carmaintenance;
//
//import com.carmaintenance.gui.*;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class Main {
//    public static void main(String[] args) {
//        System.out.println("🚀 بدء تشغيل نظام إدارة صيانة السيارات");
//
//        // تعيين واجهة النظام
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            System.out.println("✓ تم تعيين واجهة النظام");
//        } catch (Exception e) {
//            System.out.println("✗ خطأ في تعيين واجهة النظام: " + e.getMessage());
//        }
//
//        // تشغيل الواجهة الرئيسية
//        SwingUtilities.invokeLater(() -> {
//            createMainMenu();
//        });
//    }
//
//    private static void createMainMenu() {
//        JFrame frame = new JFrame("نظام إدارة صيانة السيارات");
//        frame.setSize(400, 400);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setLocationRelativeTo(null);
//
//        // لوحة العنوان
//        JPanel titlePanel = new JPanel();
//        JLabel titleLabel = new JLabel("🚗 نظام إدارة صيانة السيارات");
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
//        titleLabel.setForeground(new Color(0, 102, 204));
//        titlePanel.add(titleLabel);
//
//        // لوحة الأزرار
//        JPanel buttonPanel = new JPanel(new GridLayout(6, 1, 10, 10));
//        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//
//        // إنشاء الأزرار
//        JButton customerBtn = createMenuButton("👥 إدارة العملاء", new Color(40, 167, 69));
//        JButton vehicleBtn = createMenuButton("🚗 إدارة السيارات", new Color(0, 123, 255));
//        JButton technicianBtn = createMenuButton("👨‍🔧 إدارة الفنيين", new Color(255, 193, 7));
//        JButton partsBtn = createMenuButton("🔩 إدارة قطع الغيار", new Color(108, 117, 125));
//        JButton ordersBtn = createMenuButton("📋 إدارة طلبات الصيانة", new Color(220, 53, 69));
//        JButton invoicesBtn = createMenuButton("🧾 إدارة الفواتير", new Color(111, 66, 193));
//        JButton exitBtn = createMenuButton("🚪 خروج", new Color(52, 58, 64));
//
//        // إضافة المستمعين
//        customerBtn.addActionListener(e -> {
//            new CustomerRegistrationForm().setVisible(true);
//        });
//
//        vehicleBtn.addActionListener(e -> {
//            new VehicleRegistrationForm().setVisible(true);
//        });
//
//        technicianBtn.addActionListener(e -> {
//            new TechnicianRegistrationForm().setVisible(true);
//        });
//
//        partsBtn.addActionListener(e -> {
//            new SparePartForm().setVisible(true);
//        });
//
//        ordersBtn.addActionListener(e -> {
//            new MaintenanceOrderForm().setVisible(true);
//        });
//        invoicesBtn.addActionListener(e -> {
//            new InvoiceForm().setVisible(true);
//        });
//
//        exitBtn.addActionListener(e -> {
//            frame.dispose();
//            System.exit(0);
//        });
//
//        // إضافة الأزرار للوحة
//        buttonPanel.add(customerBtn);
//        buttonPanel.add(vehicleBtn);
//        buttonPanel.add(technicianBtn);
//        buttonPanel.add(partsBtn);
//        buttonPanel.add(ordersBtn);
//        buttonPanel.add(invoicesBtn);
//        buttonPanel.add(exitBtn);
//
//        // إضافة المكونات للإطار
//        frame.setLayout(new BorderLayout());
//        frame.add(titlePanel, BorderLayout.NORTH);
//        frame.add(buttonPanel, BorderLayout.CENTER);
//
//        // معلومات النسخة
//        JLabel versionLabel = new JLabel("الإصدار 2.0 - Omar Al-Mukhtar University - PGCS653",
//                SwingConstants.CENTER);
//        versionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
//        versionLabel.setForeground(Color.GRAY);
//        frame.add(versionLabel, BorderLayout.SOUTH);
//
//        frame.setVisible(true);
//        System.out.println("✓ تم تحميل القائمة الرئيسية");
//    }
//
//    private static JButton createMenuButton(String text, Color color) {
//        JButton button = new JButton(text);
//        button.setFont(new Font("Arial", Font.BOLD, 16));
//        button.setBackground(color);
//        button.setForeground(Color.black);
//        button.setFocusPainted(false);
//        button.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(color.darker(), 2),
//                BorderFactory.createEmptyBorder(15, 30, 15, 30)
//        ));
//        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        return button;
//    }
//}

package com.carmaintenance;

import com.carmaintenance.gui.Dashboard;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 بدء تشغيل نظام إدارة صيانة السيارات");

        // تعيين واجهة النظام
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.out.println("✓ تم تعيين واجهة النظام");
        } catch (Exception e) {
            System.out.println("✗ خطأ في تعيين واجهة النظام: " + e.getMessage());
        }

        // تشغيل لوحة التحكم
        SwingUtilities.invokeLater(() -> {
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
            System.out.println("✓ تم تحميل لوحة التحكم");
        });
    }
}