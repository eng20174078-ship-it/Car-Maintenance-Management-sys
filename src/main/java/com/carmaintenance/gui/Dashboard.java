package com.carmaintenance.gui;

import com.carmaintenance.dao.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard extends JFrame {

    // DAOs
    private CustomerDAO customerDAO;
    private VehicleDAO vehicleDAO;
    private TechnicianDAO technicianDAO;
    private SparePartDAO sparePartDAO;
    private MaintenanceOrderDAO orderDAO;
    private InvoiceDAO invoiceDAO;

    // تسميات الإحصائيات
    private JLabel customersLabel;
    private JLabel vehiclesLabel;
    private JLabel techniciansLabel;
    private JLabel partsLabel;
    private JLabel ordersLabel;
    private JLabel invoicesLabel;
    private JLabel salesLabel;
    private JLabel receivablesLabel;

    public Dashboard() {
        // تهيئة DAOs
        customerDAO = new CustomerDAO();
        vehicleDAO = new VehicleDAO();
        technicianDAO = new TechnicianDAO();
        sparePartDAO = new SparePartDAO();
        orderDAO = new MaintenanceOrderDAO();
        invoiceDAO = new InvoiceDAO();

        // إعداد النافذة
        setupWindow();
        initComponents();
        layoutComponents();

        // تحديث الإحصائيات
        updateStatistics();
    }

    private void setupWindow() {
        setTitle("نظام إدارة صيانة السيارات - لوحة التحكم");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // تسميات الإحصائيات
        customersLabel = createStatLabel("👥 العملاء: 0", new Color(40, 167, 69));
        vehiclesLabel = createStatLabel("🚗 السيارات: 0", new Color(0, 123, 255));
        techniciansLabel = createStatLabel("👨‍🔧 الفنيون: 0", new Color(255, 193, 7));
        partsLabel = createStatLabel("🔩 قطع الغيار: 0", new Color(108, 117, 125));
        ordersLabel = createStatLabel("🔧 طلبات الصيانة: 0", new Color(220, 53, 69));
        invoicesLabel = createStatLabel("🧾 الفواتير: 0", new Color(111, 66, 193));
        salesLabel = createStatLabel("💰 إجمالي المبيعات: 0.00", new Color(32, 201, 151));
        receivablesLabel = createStatLabel("📈 المستحقات: 0.00", new Color(253, 126, 20));
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // لوحة العنوان
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("🚗 نظام إدارة صيانة السيارات - لوحة التحكم");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // لوحة الإحصائيات
        JPanel statsPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("📊 إحصائيات النظام"));

        statsPanel.add(customersLabel);
        statsPanel.add(vehiclesLabel);
        statsPanel.add(techniciansLabel);
        statsPanel.add(partsLabel);
        statsPanel.add(ordersLabel);
        statsPanel.add(invoicesLabel);
        statsPanel.add(salesLabel);
        statsPanel.add(receivablesLabel);

        add(statsPanel, BorderLayout.CENTER);

        // لوحة الأزرار السريعة
        JPanel quickAccessPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        quickAccessPanel.setBorder(BorderFactory.createTitledBorder("⚡ وصول سريع"));

        quickAccessPanel.add(createQuickButton("➕ عميل جديد", e -> new CustomerRegistrationForm().setVisible(true)));
        quickAccessPanel.add(createQuickButton("🚗 سيارة جديدة", e -> new VehicleRegistrationForm().setVisible(true)));
        quickAccessPanel.add(createQuickButton("👨‍🔧 فني جديد", e -> new TechnicianRegistrationForm().setVisible(true)));
        quickAccessPanel.add(createQuickButton("🔩 قطعة غيار", e -> new SparePartForm().setVisible(true)));
        quickAccessPanel.add(createQuickButton("🔧 طلب صيانة", e -> new MaintenanceOrderForm().setVisible(true)));
        quickAccessPanel.add(createQuickButton("🧾 فاتورة جديدة", e -> new InvoiceForm().setVisible(true)));
        quickAccessPanel.add(createQuickButton("📋 تقرير العملاء", e -> showReport("customers")));
        quickAccessPanel.add(createQuickButton("📊 تقرير المبيعات", e -> showReport("sales")));
        quickAccessPanel.add(createQuickButton("🔄 تحديث الإحصائيات", e -> updateStatistics()));

        add(quickAccessPanel, BorderLayout.SOUTH);
    }

    private JLabel createStatLabel(String text, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 16));
        label.setForeground(Color.WHITE);
        label.setBackground(color);
        label.setOpaque(true);
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(20, 10, 20, 10)
        ));
        return label;
    }

    private JButton createQuickButton(String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(52, 58, 64));
        button.setForeground(Color.black);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(33, 37, 41), 2),
                BorderFactory.createEmptyBorder(10, 5, 10, 5)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(listener);
        return button;
    }

    private void updateStatistics() {
        // تحديث التسميات مع البيانات الحقيقية
        customersLabel.setText("👥 العملاء: " + customerDAO.getCustomerCount());
        vehiclesLabel.setText("🚗 السيارات: " + vehicleDAO.getVehicleCount());
        techniciansLabel.setText("👨‍🔧 الفنيون: " + technicianDAO.getTechnicianCount() +
                " (نشطين: " + technicianDAO.getActiveTechnicianCount() + ")");
        partsLabel.setText("🔩 قطع الغيار: " + sparePartDAO.getSparePartCount());
        ordersLabel.setText("🔧 طلبات الصيانة: " + orderDAO.getMaintenanceOrderCount());
        invoicesLabel.setText("🧾 الفواتير: " + invoiceDAO.getInvoiceCount());
        salesLabel.setText("💰 إجمالي المبيعات: " + String.format("%.2f", invoiceDAO.getTotalSales()));
        receivablesLabel.setText("📈 المستحقات: " + String.format("%.2f", invoiceDAO.getTotalReceivables()));
    }

    private void showReport(String reportType) {
        switch (reportType) {
            case "customers":
                showCustomerReport();
                break;
            case "sales":
                showSalesReport();
                break;
            default:
                JOptionPane.showMessageDialog(this, "التقرير غير متوفر", "معلومات",
                        JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void showCustomerReport() {
        int count = customerDAO.getCustomerCount();
        String report = "📊 تقرير العملاء\n" +
                "==============\n" +
                "👥 العدد الإجمالي: " + count + "\n\n" +
                "📞 يتم تحديث التقرير الكامل قريباً...";

        JOptionPane.showMessageDialog(this, report, "تقرير العملاء",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSalesReport() {
        double sales = invoiceDAO.getTotalSales();
        double receivables = invoiceDAO.getTotalReceivables();
        String report = "📊 تقرير المبيعات\n" +
                "===============\n" +
                "💰 إجمالي المبيعات: " + String.format("%.2f", sales) + "\n" +
                "📈 إجمالي المستحقات: " + String.format("%.2f", receivables) + "\n" +
                "💎 صافي الإيرادات: " + String.format("%.2f", sales + receivables) + "\n\n" +
                "📞 يتم تحديث التقرير الكامل قريباً...";

        JOptionPane.showMessageDialog(this, report, "تقرير المبيعات",
                JOptionPane.INFORMATION_MESSAGE);
    }
}