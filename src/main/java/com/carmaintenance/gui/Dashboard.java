package com.carmaintenance.gui;

import com.carmaintenance.dao.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard extends JFrame {

    private CustomerDAO customerDAO;
    private VehicleDAO vehicleDAO;
    private TechnicianDAO technicianDAO;
    private SparePartDAO sparePartDAO;
    private MaintenanceOrderDAO orderDAO;
    private InvoiceDAO invoiceDAO;

    // لوحات البطاقات
    private JPanel statsPanel;
    private JPanel quickActionsPanel;
    private JPanel recentActivityPanel;

    public Dashboard() {
        // تطبيق السمة
        ThemeManager.applyTheme();

        // تهيئة DAOs
        initializeDAOs();

        // إعداد النافذة
        setupWindow();
        initComponents();
        layoutComponents();

        // تحديث الإحصائيات
        updateStatistics();

        // تحديث النشاط الأخير
        updateRecentActivity();
    }

    private void initializeDAOs() {
        customerDAO = new CustomerDAO();
        vehicleDAO = new VehicleDAO();
        technicianDAO = new TechnicianDAO();
        sparePartDAO = new SparePartDAO();
        orderDAO = new MaintenanceOrderDAO();
        invoiceDAO = new InvoiceDAO();
    }

    private void setupWindow() {
        setTitle("🚗 نظام إدارة صيانة السيارات - لوحة التحكم");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // إضافة أيقونة النافذة
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(
                    getClass().getResource("/icon.png")));
        } catch (Exception e) {
            // تجاهل إذا لم توجد الأيقونة
        }
    }

    private void initComponents() {
        // إنشاء لوحة الإحصائيات
        statsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statsPanel.setBackground(ThemeManager.LIGHT_COLOR);

        // إنشاء لوحة الإجراءات السريعة
        quickActionsPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        quickActionsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ThemeManager.PRIMARY_COLOR, 2),
                "⚡ إجراءات سريعة",
                0, 0,
                ThemeManager.TITLE_FONT,
                ThemeManager.PRIMARY_COLOR
        ));
        quickActionsPanel.setBackground(Color.WHITE);

        // إنشاء لوحة النشاط الأخير
        recentActivityPanel = new JPanel(new BorderLayout());
        recentActivityPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ThemeManager.INFO_COLOR, 2),
                "📋 النشاط الأخير",
                0, 0,
                ThemeManager.TITLE_FONT,
                ThemeManager.INFO_COLOR
        ));
        recentActivityPanel.setBackground(Color.WHITE);

        // إضافة الأزرار السريعة
        addQuickActionButtons();
    }

    private void layoutComponents() {
        // استخدام BorderLayout مع هوامش
        setLayout(new BorderLayout(0, 0));

        // شريط العنوان العلوي
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // المحتوى الرئيسي مع ScrollPane
        JPanel mainContentPanel = new JPanel(new BorderLayout(20, 20));
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainContentPanel.setBackground(ThemeManager.LIGHT_COLOR);

        // إضافة لوحة الإحصائيات
        mainContentPanel.add(statsPanel, BorderLayout.NORTH);

        // لوحة وسطية للإجراءات والنشاط
        JPanel middlePanel = new JPanel(new GridLayout(1, 2, 20, 0));
        middlePanel.add(quickActionsPanel);
        middlePanel.add(recentActivityPanel);

        mainContentPanel.add(middlePanel, BorderLayout.CENTER);

        // لوحة سفلية للتقارير السريعة
        JPanel reportsPanel = createQuickReportsPanel();
        mainContentPanel.add(reportsPanel, BorderLayout.SOUTH);

        // إضافة ScrollPane للمحتوى الرئيسي
        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);

        // شريط الحالة السفلي
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeManager.DARK_COLOR);
        header.setPreferredSize(new Dimension(0, 70));
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // العنوان والشعار
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(ThemeManager.DARK_COLOR);

        JLabel logoLabel = new JLabel("🚗");
        logoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 32));
        logoLabel.setForeground(Color.WHITE);

        JLabel titleLabel = new JLabel("نظام إدارة صيانة السيارات");
        titleLabel.setFont(ThemeManager.HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);

        titlePanel.add(logoLabel);
        titlePanel.add(titleLabel);

        // أزرار التحكم
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlPanel.setBackground(ThemeManager.DARK_COLOR);

        JButton refreshButton = ThemeManager.createStyledButton("🔄 تحديث", ThemeManager.INFO_COLOR);
        refreshButton.addActionListener(e -> updateAll());

        JButton exitButton = ThemeManager.createStyledButton("🚪 خروج", ThemeManager.DANGER_COLOR);
        exitButton.addActionListener(e -> System.exit(0));

        controlPanel.add(refreshButton);
        controlPanel.add(exitButton);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(controlPanel, BorderLayout.EAST);

        return header;
    }

    private class QuickAction {
        String name_arab;
        String name;
        Color color;


        QuickAction(String name_arab, String name, Color color) {
            this.name = name;
            this.color = color;
            this.name_arab = name_arab;
        }
    }

    private void addQuickActionButtons() {
        QuickAction[] actions = {
                new QuickAction("عميل جديد ","CustomerRegistrationForm", ThemeManager.PRIMARY_COLOR),
                new QuickAction("🚗 سيارة جديدة","VehicleRegistrationForm", ThemeManager.SECONDARY_COLOR),
                new QuickAction( "👨‍🔧 فني جديد","TechnicianRegistrationForm", ThemeManager.ACCENT_COLOR),
                new QuickAction("🔩 قطعة غيار","SparePartForm", ThemeManager.INFO_COLOR),
                new QuickAction("🔧 طلب صيانة","MaintenanceOrderForm", ThemeManager.WARNING_COLOR),
                new QuickAction("🧾 فاتورة جديدة","InvoiceForm", ThemeManager.DARK_COLOR),
                new QuickAction("📊 تقرير العملاء","CustomerReport", ThemeManager.PRIMARY_COLOR),
                new QuickAction("💰 تقرير المبيعات","SalesReport", ThemeManager.SECONDARY_COLOR),
                new QuickAction("⚙️ إعدادات النظام","Settings", ThemeManager.INFO_COLOR)
        };

        for (QuickAction action : actions) {
            JButton button = ThemeManager.createStyledButton(action.name_arab, action.color);
            button.addActionListener(e -> handleQuickAction(action.name));
            // mainPanel.add(button);
            quickActionsPanel.add(button);

        }
    }

    private void handleQuickAction(String action) {
        switch (action) {
            case "CustomerRegistrationForm":
                new CustomerRegistrationForm().setVisible(true);
                break;
            case "VehicleRegistrationForm":
                new VehicleRegistrationForm().setVisible(true);
                break;
            case "TechnicianRegistrationForm":
                new TechnicianRegistrationForm().setVisible(true);
                break;
            case "SparePartForm":
                new SparePartForm().setVisible(true);
                break;
            case "MaintenanceOrderForm":
                new MaintenanceOrderForm().setVisible(true);
                break;
            case "InvoiceForm":
                new InvoiceForm().setVisible(true);
                break;
            case "CustomerReport":
                showCustomerReport();
                break;
            case "SalesReport":
                showSalesReport();
                break;
            case "Settings":
                JOptionPane.showMessageDialog(this, "إعدادات النظام قيد التطوير",
                        "قيد التطوير", JOptionPane.INFORMATION_MESSAGE);
                break;
        }
    }

    private void updateStatistics() {
        statsPanel.removeAll();

        // إضافة بطاقات الإحصائيات
        statsPanel.add(ThemeManager.createInfoCard("العملاء",
                String.valueOf(customerDAO.getCustomerCount()),
                ThemeManager.PRIMARY_COLOR, "👥"));

        statsPanel.add(ThemeManager.createInfoCard("السيارات",
                String.valueOf(vehicleDAO.getVehicleCount()),
                ThemeManager.SECONDARY_COLOR, "🚗"));

        statsPanel.add(ThemeManager.createInfoCard("الفنيون النشطون",
                String.valueOf(technicianDAO.getActiveTechnicianCount()),
                ThemeManager.ACCENT_COLOR, "👨‍🔧"));

        statsPanel.add(ThemeManager.createInfoCard("قطع الغيار",
                String.valueOf(sparePartDAO.getSparePartCount()),
                ThemeManager.INFO_COLOR, "🔩"));

        statsPanel.add(ThemeManager.createInfoCard("طلبات الصيانة النشطة",
                String.valueOf(orderDAO.getActiveMaintenanceOrders().size()),
                ThemeManager.WARNING_COLOR, "🔧"));

        statsPanel.add(ThemeManager.createInfoCard("الفواتير",
                String.valueOf(invoiceDAO.getInvoiceCount()),
                ThemeManager.DARK_COLOR, "🧾"));

        statsPanel.add(ThemeManager.createInfoCard("إجمالي المبيعات",
                String.format("%.2f", invoiceDAO.getTotalSales()),
                ThemeManager.SECONDARY_COLOR, "💰"));

        statsPanel.add(ThemeManager.createInfoCard("المستحقات",
                String.format("%.2f", invoiceDAO.getTotalReceivables()),
                ThemeManager.DANGER_COLOR, "📈"));

        statsPanel.revalidate();
        statsPanel.repaint();
    }

    private void updateRecentActivity() {
        recentActivityPanel.removeAll();

        // إنشاء نموذج للجدول
        String[] columns = {"النشاط", "التاريخ", "الحالة"};
        Object[][] data = {
                {"طلب صيانة جديد #1001", "2025-12-10", "قيد التنفيذ"},
                {"فاتورة #500 دُفعت", "2025-12-09", "✅"},
                {"إضافة قطع غيار جديدة", "2025-12-09", "تم"},
                {"تسجيل عميل جديد", "2025-12-08", "تم"},
                {"تحديث بيانات فني", "2025-12-08", "تم"}
        };

        JTable activityTable = ThemeManager.createStyledTable(data, columns);
        activityTable.setPreferredScrollableViewportSize(new Dimension(400, 200));

        JScrollPane scrollPane = new JScrollPane(activityTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        recentActivityPanel.add(scrollPane, BorderLayout.CENTER);

        // زر عرض المزيد
        JButton viewMoreButton = ThemeManager.createStyledButton("عرض المزيد", ThemeManager.INFO_COLOR);
        viewMoreButton.addActionListener(e -> showAllActivity());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(viewMoreButton);

        recentActivityPanel.add(buttonPanel, BorderLayout.SOUTH);

        recentActivityPanel.revalidate();
        recentActivityPanel.repaint();
    }

    private JPanel createQuickReportsPanel() {
        JPanel reportsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        reportsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(ThemeManager.WARNING_COLOR, 2),
                "📋 تقارير سريعة",
                0, 0,
                ThemeManager.TITLE_FONT,
                ThemeManager.WARNING_COLOR
        ));
        reportsPanel.setBackground(Color.WHITE);

        // تقرير المخزون المنخفض
        JPanel lowStockPanel = createReportPanel("⚠️ المخزون المنخفض",
                sparePartDAO.getLowStockParts().size() + " قطعة",
                ThemeManager.WARNING_COLOR, "عرض التفاصيل",
                e -> showLowStockReport());

        // تقرير الفواتير المتأخرة
        JPanel overdueInvoicesPanel = createReportPanel("⏰ فواتير متأخرة",
                invoiceDAO.getOverdueInvoices().size() + " فاتورة",
                ThemeManager.DANGER_COLOR, "عرض التفاصيل",
                e -> showOverdueInvoices());

        // تقرير الطلبات المنتظرة
        JPanel pendingOrdersPanel = createReportPanel("⏳ طلبات منتظرة",
                orderDAO.getActiveMaintenanceOrders().size() + " طلب",
                ThemeManager.INFO_COLOR, "عرض التفاصيل",
                e -> showPendingOrders());

        reportsPanel.add(lowStockPanel);
        reportsPanel.add(overdueInvoicesPanel);
        reportsPanel.add(pendingOrdersPanel);

        return reportsPanel;
    }

    private JPanel createReportPanel(String title, String value, Color color,
                                     String buttonText, ActionListener listener) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(ThemeManager.SUBTITLE_FONT);
        titleLabel.setForeground(color);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        valueLabel.setForeground(ThemeManager.DARK_COLOR);

        JButton actionButton = ThemeManager.createStyledButton(buttonText, color);
        actionButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        actionButton.addActionListener(listener);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        panel.add(actionButton, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(ThemeManager.DARK_COLOR);
        statusBar.setPreferredSize(new Dimension(0, 30));
        statusBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // معلومات النظام
        JLabel systemInfo = new JLabel("الإصدار 2.0 | نظام إدارة صيانة السيارات | Omar Al-Mukhtar University");
        systemInfo.setFont(ThemeManager.BODY_FONT);
        systemInfo.setForeground(Color.WHITE);

        // حالة الاتصال
        JLabel connectionStatus = new JLabel("✅ متصل بقاعدة البيانات");
        connectionStatus.setFont(ThemeManager.BODY_FONT);
        connectionStatus.setForeground(Color.WHITE);

        statusBar.add(systemInfo, BorderLayout.WEST);
        statusBar.add(connectionStatus, BorderLayout.EAST);

        return statusBar;
    }

    private void updateAll() {
        updateStatistics();
        updateRecentActivity();
        JOptionPane.showMessageDialog(this, "✅ تم تحديث جميع البيانات",
                "تحديث", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showCustomerReport() {
        String report = customerDAO.getAllCustomers().stream()
                .map(c -> "👤 " + c.getName() + " - 📱 " + c.getPhone())
                .reduce("📊 تقرير العملاء:\n\n", (a, b) -> a + b + "\n");

        showReportDialog("تقرير العملاء", report);
    }

    private void showSalesReport() {
        String report = "💰 تقرير المبيعات:\n\n" +
                "إجمالي المبيعات: " + String.format("%.2f", invoiceDAO.getTotalSales()) + "\n" +
                "المستحقات: " + String.format("%.2f", invoiceDAO.getTotalReceivables()) + "\n" +
                "الفواتير المتأخرة: " + invoiceDAO.getOverdueInvoices().size() + "\n" +
                "الفواتير المدفوعة: " + (invoiceDAO.getInvoiceCount() - invoiceDAO.getUnpaidInvoices().size());

        showReportDialog("تقرير المبيعات", report);
    }

    private void showLowStockReport() {
        StringBuilder report = new StringBuilder("⚠️ المخزون المنخفض:\n\n");
        sparePartDAO.getLowStockParts().forEach(p ->
                report.append("🔩 ").append(p.getName())
                        .append(" - متوفر: ").append(p.getQuantity())
                        .append(" / الحد الأدنى: ").append(p.getMinThreshold())
                        .append("\n"));

        showReportDialog("المخزون المنخفض", report.toString());
    }

    private void showOverdueInvoices() {
        StringBuilder report = new StringBuilder("⏰ الفواتير المتأخرة:\n\n");
        invoiceDAO.getOverdueInvoices().forEach(i ->
                report.append("🧾 فاتورة #").append(i.getId())
                        .append(" - المبلغ: ").append(String.format("%.2f", i.getFinalAmount()))
                        .append(" - تأخر: ").append(i.getDaysOverdue()).append(" يوم\n"));

        showReportDialog("الفواتير المتأخرة", report.toString());
    }

    private void showPendingOrders() {
        StringBuilder report = new StringBuilder("⏳ الطلبات المنتظرة:\n\n");
        orderDAO.getActiveMaintenanceOrders().forEach(o ->
                report.append("🔧 طلب #").append(o.getId())
                        .append(" - السيارة: ").append(o.getVehiclePlate())
                        .append(" - الحالة: ").append(o.getStatus())
                        .append("\n"));

        showReportDialog("الطلبات المنتظرة", report.toString());
    }

    private void showAllActivity() {
        // في التطبيق الحقيقي، هنا يتم جلب جميع النشاطات من قاعدة البيانات
        String allActivity = "📋 النشاط الكامل:\n\n" +
                "1. تسجيل عميل جديد - أحمد محمد (10:00)\n" +
                "2. إنشاء طلب صيانة #1001 (11:30)\n" +
                "3. إضافة قطع غيار للمخزون (12:15)\n" +
                "4. تسديد فاتورة #500 (14:00)\n" +
                "5. تحديث بيانات فني (15:30)\n" +
                "6. إنشاء فاتورة جديدة #501 (16:45)";

        showReportDialog("النشاط الكامل", allActivity);
    }

    private void showReportDialog(String title, String content) {
        JTextArea textArea = new JTextArea(content, 20, 40);
        textArea.setFont(ThemeManager.BODY_FONT);
        textArea.setEditable(false);
        textArea.setBackground(ThemeManager.LIGHT_COLOR);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.PRIMARY_COLOR, 1));

        JOptionPane.showMessageDialog(this, scrollPane, title,
                JOptionPane.INFORMATION_MESSAGE);
    }
}