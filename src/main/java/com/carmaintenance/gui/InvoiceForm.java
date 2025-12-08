package com.carmaintenance.gui;

import com.carmaintenance.dao.*;
import com.carmaintenance.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoiceForm extends JFrame {

    // DAOs
    private InvoiceDAO invoiceDAO;
    private MaintenanceOrderDAO orderDAO;

    // الحقول
    private JComboBox<String> orderComboBox;
    private JTextField totalAmountField;
    private JTextField taxPercentageField;
    private JTextField discountPercentageField;
    private JTextField taxAmountField;
    private JTextField discountAmountField;
    private JTextField finalAmountField;
    private JTextField issuedDateField;
    private JTextField dueDateField;
    private JComboBox<String> paymentMethodComboBox;
    private JCheckBox paidCheckBox;
    private JTextField paymentDateField;
    private JTextArea notesArea;

    // تسميات المعلومات
    private JLabel orderInfoLabel;
    private JLabel vehicleInfoLabel;
    private JLabel customerInfoLabel;

    // الأزرار
    private JButton generateButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton searchButton;
    private JButton viewAllButton;
    private JButton statsButton;
    private JButton markPaidButton;
    private JButton printButton;

    // الجدول
    private JTable invoicesTable;
    private DefaultTableModel tableModel;

    // ID المحدد حاليًا
    private int selectedInvoiceId = -1;

    // تنسيق التاريخ
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // ضريبة الافتراضية
    private static final double DEFAULT_TAX_PERCENTAGE = 15.0;

    public InvoiceForm() {
        // تهيئة DAOs
        invoiceDAO = new InvoiceDAO();
        orderDAO = new MaintenanceOrderDAO();

        // إعداد النافذة
        setupWindow();
        initComponents();
        layoutComponents();

        // تحميل البيانات
        loadCompletedOrders();
        loadAllInvoices();
        updateTitle();

        // حساب القيم الافتراضية
        calculateAmounts();
    }

    private void setupWindow() {
        setTitle("نظام صيانة السيارات - إدارة الفواتير");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // ComboBox للطلبات المكتملة
        orderComboBox = new JComboBox<>();
        orderComboBox.addActionListener(e -> updateOrderInfo());

        // حقول المبالغ
        totalAmountField = createTextField();
        totalAmountField.setEditable(false);

        taxPercentageField = createTextField();
        taxPercentageField.setText(String.valueOf(DEFAULT_TAX_PERCENTAGE));
        taxPercentageField.addActionListener(e -> calculateTax());

        discountPercentageField = createTextField();
        discountPercentageField.setText("0");
        discountPercentageField.addActionListener(e -> calculateDiscount());

        taxAmountField = createTextField();
        taxAmountField.setEditable(false);

        discountAmountField = createTextField();
        discountAmountField.setEditable(false);

        finalAmountField = createTextField();
        finalAmountField.setEditable(false);
        finalAmountField.setFont(new Font("Arial", Font.BOLD, 16));
        finalAmountField.setForeground(new Color(40, 167, 69));

        // حقول التاريخ
        issuedDateField = createTextField();
        issuedDateField.setText(LocalDateTime.now().format(dateFormatter));

        dueDateField = createTextField();
        dueDateField.setText(LocalDateTime.now().plusDays(30).format(dateFormatter));

        // ComboBox لطريقة الدفع
        paymentMethodComboBox = new JComboBox<>(new String[]{
                "نقدي", "بطاقة ائتمان", "تحويل بنكي", "شيك", "أخرى"
        });

        // CheckBox و حقل تاريخ الدفع
        paidCheckBox = new JCheckBox("مدفوع");
        paidCheckBox.addActionListener(e -> updatePaymentFields());

        paymentDateField = createTextField();
        paymentDateField.setText(paidCheckBox.isSelected() ? LocalDateTime.now().format(dateFormatter) : "");

        notesArea = new JTextArea(4, 40);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        // تسميات المعلومات
        orderInfoLabel = createInfoLabel("📋 اختر طلب صيانة مكتمل");
        vehicleInfoLabel = createInfoLabel("🚗 معلومات السيارة ستظهر هنا");
        customerInfoLabel = createInfoLabel("👤 معلومات العميل ستظهر هنا");

        // الأزرار
        generateButton = createButton("🧾 إنشاء فاتورة", new Color(40, 167, 69));
        updateButton = createButton("✏️ تحديث الفاتورة", new Color(255, 193, 7));
        deleteButton = createButton("🗑️ حذف الفاتورة", new Color(220, 53, 69));
        clearButton = createButton("🧹 مسح الحقول", new Color(108, 117, 125));
        searchButton = createButton("🔍 بحث عن فاتورة", new Color(0, 123, 255));
        viewAllButton = createButton("📄 عرض الجميع", new Color(111, 66, 193));
        statsButton = createButton("📊 الإحصائيات", new Color(32, 201, 151));
        markPaidButton = createButton("💵 تسديد الفاتورة", new Color(23, 162, 184));
        printButton = createButton("🖨️ طباعة الفاتورة", new Color(52, 58, 64));

        // إضافة المستمعين
        generateButton.addActionListener(e -> generateInvoice());
        updateButton.addActionListener(e -> updateInvoice());
        deleteButton.addActionListener(e -> deleteInvoice());
        clearButton.addActionListener(e -> clearFields());
        searchButton.addActionListener(e -> searchInvoices());
        viewAllButton.addActionListener(e -> loadAllInvoices());
        statsButton.addActionListener(e -> showStatistics());
        markPaidButton.addActionListener(e -> markAsPaid());
        printButton.addActionListener(e -> printInvoice());

        // إعداد الجدول
        String[] columns = {"ID", "طلب #", "المبلغ", "الضريبة", "الخصم", "الإجمالي", "تاريخ الإصدار", "تاريخ الاستحقاق", "طريقة الدفع", "الحالة"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        invoicesTable = new JTable(tableModel);
        invoicesTable.setRowHeight(25);
        invoicesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = invoicesTable.getSelectedRow();
                if (selectedRow != -1) {
                    loadInvoiceFromTable(selectedRow);
                }
            }
        });

        // تلوين الصفوف حسب حالة الدفع
        invoicesTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    String status = table.getValueAt(row, 9).toString();
                    if (status.contains("مدفوع")) {
                        c.setBackground(new Color(220, 255, 220)); // أخضر فاتح للمدفوع
                    } else if (status.contains("متأخر")) {
                        c.setBackground(new Color(255, 220, 220)); // أحمر فاتح للمتأخر
                    } else {
                        c.setBackground(new Color(255, 255, 220)); // أصفر للغير مدفوع
                    }
                }

                return c;
            }
        });

        // تعطيل الأزرار
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        markPaidButton.setEnabled(false);
        printButton.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // لوحة العنوان
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("🧾 إدارة الفواتير");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // لوحة المحتوى الرئيسية
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // لوحة اليسار (الحقول والمعلومات)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // لوحة المعلومات السريعة
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("معلومات سريعة"));
        infoPanel.add(createInfoPanel("📋 معلومات الطلب", orderInfoLabel));
        infoPanel.add(createInfoPanel("🚗 معلومات السيارة", vehicleInfoLabel));
        infoPanel.add(createInfoPanel("👤 معلومات العميل", customerInfoLabel));

        // لوحة اختيار الطلب
        JPanel orderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        orderPanel.setBorder(BorderFactory.createTitledBorder("اختيار طلب الصيانة"));
        orderPanel.add(createLabel("📋 طلب الصيانة:"));
        orderPanel.add(orderComboBox);

        // لوحة المبالغ
        JPanel amountsPanel = new JPanel(new GridBagLayout());
        amountsPanel.setBorder(BorderFactory.createTitledBorder("المبالغ المالية"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // الصف 0: المبلغ الإجمالي
        gbc.gridx = 0; gbc.gridy = 0;
        amountsPanel.add(createLabel("💰 المبلغ الإجمالي:"), gbc);
        gbc.gridx = 1;
        amountsPanel.add(totalAmountField, gbc);

        // الصف 1: نسبة الضريبة
        gbc.gridx = 0; gbc.gridy = 1;
        amountsPanel.add(createLabel("📊 نسبة الضريبة (%):"), gbc);
        gbc.gridx = 1;
        amountsPanel.add(taxPercentageField, gbc);
        gbc.gridx = 2;
        amountsPanel.add(createLabel("💸 قيمة الضريبة:"), gbc);
        gbc.gridx = 3;
        amountsPanel.add(taxAmountField, gbc);

        // الصف 2: نسبة الخصم
        gbc.gridx = 0; gbc.gridy = 2;
        amountsPanel.add(createLabel("🎁 نسبة الخصم (%):"), gbc);
        gbc.gridx = 1;
        amountsPanel.add(discountPercentageField, gbc);
        gbc.gridx = 2;
        amountsPanel.add(createLabel("💵 قيمة الخصم:"), gbc);
        gbc.gridx = 3;
        amountsPanel.add(discountAmountField, gbc);

        // الصف 3: المبلغ النهائي
        gbc.gridx = 0; gbc.gridy = 3;
        amountsPanel.add(createLabel("💎 المبلغ النهائي:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        amountsPanel.add(finalAmountField, gbc);

        // لوحة معلومات الدفع
        JPanel paymentPanel = new JPanel(new GridBagLayout());
        paymentPanel.setBorder(BorderFactory.createTitledBorder("معلومات الدفع"));

        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // الصف 0: تاريخ الإصدار
        gbc.gridx = 0; gbc.gridy = 0;
        paymentPanel.add(createLabel("📅 تاريخ الإصدار:"), gbc);
        gbc.gridx = 1;
        paymentPanel.add(issuedDateField, gbc);

        // الصف 1: تاريخ الاستحقاق
        gbc.gridx = 0; gbc.gridy = 1;
        paymentPanel.add(createLabel("⏳ تاريخ الاستحقاق:"), gbc);
        gbc.gridx = 1;
        paymentPanel.add(dueDateField, gbc);

        // الصف 2: طريقة الدفع
        gbc.gridx = 0; gbc.gridy = 2;
        paymentPanel.add(createLabel("💳 طريقة الدفع:"), gbc);
        gbc.gridx = 1;
        paymentPanel.add(paymentMethodComboBox, gbc);

        // الصف 3: حالة الدفع
        gbc.gridx = 0; gbc.gridy = 3;
        paymentPanel.add(createLabel("✅ حالة الدفع:"), gbc);
        gbc.gridx = 1;
        paymentPanel.add(paidCheckBox, gbc);
        gbc.gridx = 2;
        paymentPanel.add(createLabel("📅 تاريخ الدفع:"), gbc);
        gbc.gridx = 3;
        paymentPanel.add(paymentDateField, gbc);

        // لوحة الملاحظات
        JPanel notesPanel = new JPanel(new BorderLayout());
        notesPanel.setBorder(BorderFactory.createTitledBorder("📝 ملاحظات"));
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(300, 100));
        notesPanel.add(notesScroll, BorderLayout.CENTER);

        // تجميع اللوحات اليسرى
        leftPanel.add(infoPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(orderPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(amountsPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(paymentPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(notesPanel);

        // لوحة الجدول (اليمين)
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("قائمة الفواتير"));
        tablePanel.add(new JScrollPane(invoicesTable), BorderLayout.CENTER);

        // لوحة الأزرار السفلية
        JPanel buttonPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(generateButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(markPaidButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(viewAllButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(printButton);

        // إضافة المكونات الرئيسية
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        return field;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(new Color(73, 80, 87));
        return label;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel("<html>" + text.replace("\n", "<br>") + "</html>");
        label.setFont(new Font("Arial", Font.PLAIN, 11));
        label.setForeground(new Color(52, 58, 64));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return label;
    }

    private JPanel createInfoPanel(String title, JLabel contentLabel) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        titleLabel.setForeground(new Color(0, 123, 255));
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentLabel, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1));
        return panel;
    }

    private void loadCompletedOrders() {
        orderComboBox.removeAllItems();
        orderComboBox.addItem("اختر طلب صيانة مكتمل");

        List<MaintenanceOrder> orders = orderDAO.getCompletedMaintenanceOrders();
        for (MaintenanceOrder order : orders) {
            // التحقق مما إذا كان للطلب فاتورة بالفعل
            Invoice existingInvoice = invoiceDAO.getInvoiceByOrderId(order.getId());
            if (existingInvoice == null) { // عرض فقط الطلبات بدون فواتير
                orderComboBox.addItem("طلب #" + order.getId() + " - " + order.getVehiclePlate() +
                        " - " + String.format("%.2f", order.getActualCost()));
            }
        }
    }

    private void loadAllInvoices() {
        tableModel.setRowCount(0);
        List<Invoice> invoices = invoiceDAO.getAllInvoices();

        for (Invoice invoice : invoices) {
            Object[] row = {
                    invoice.getId(),
                    invoice.getOrderId(),
                    String.format("%.2f", invoice.getTotalAmount()),
                    String.format("%.2f", invoice.getTaxAmount()),
                    String.format("%.2f", invoice.getDiscountAmount()),
                    String.format("%.2f", invoice.getFinalAmount()),
                    invoice.getIssuedDate() != null ? invoice.getIssuedDate().format(dateFormatter) : "غير محدد",
                    invoice.getDueDate() != null ? invoice.getDueDate().format(dateFormatter) : "غير محدد",
                    invoice.getPaymentMethod(),
                    invoice.getPaymentStatus()
            };
            tableModel.addRow(row);
        }

        updateTitle();
        clearSelection();
    }

    private void loadInvoiceFromTable(int rowIndex) {
        int id = (int) tableModel.getValueAt(rowIndex, 0);
        Invoice invoice = invoiceDAO.getInvoiceById(id);

        if (invoice != null) {
            loadInvoiceToForm(invoice);
            selectedInvoiceId = id;

            // تفعيل الأزرار
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
            markPaidButton.setEnabled(!invoice.isPaid());
            printButton.setEnabled(true);
            generateButton.setEnabled(false);
        }
    }

    private void loadInvoiceToForm(Invoice invoice) {
        // تعطيل تحديث المعلومات أثناء التحميل
        orderComboBox.removeActionListener(orderComboBox.getActionListeners()[0]);

        // تعبئة الحقول
        if (invoice.getOrder() != null) {
            orderComboBox.setSelectedItem("طلب #" + invoice.getOrderId() + " - " +
                    invoice.getOrder().getVehiclePlate() + " - " +
                    String.format("%.2f", invoice.getOrder().getActualCost()));
        }

        totalAmountField.setText(String.format("%.2f", invoice.getTotalAmount()));

        // حساب النسب
        double taxPercentage = (invoice.getTotalAmount() > 0) ?
                (invoice.getTaxAmount() / invoice.getTotalAmount()) * 100 : 0;
        double discountPercentage = (invoice.getTotalAmount() > 0) ?
                (invoice.getDiscountAmount() / invoice.getTotalAmount()) * 100 : 0;

        taxPercentageField.setText(String.format("%.2f", taxPercentage));
        discountPercentageField.setText(String.format("%.2f", discountPercentage));
        taxAmountField.setText(String.format("%.2f", invoice.getTaxAmount()));
        discountAmountField.setText(String.format("%.2f", invoice.getDiscountAmount()));
        finalAmountField.setText(String.format("%.2f", invoice.getFinalAmount()));

        issuedDateField.setText(invoice.getIssuedDate() != null ?
                invoice.getIssuedDate().format(dateFormatter) : LocalDateTime.now().format(dateFormatter));

        dueDateField.setText(invoice.getDueDate() != null ?
                invoice.getDueDate().format(dateFormatter) : LocalDateTime.now().plusDays(30).format(dateFormatter));

        paymentMethodComboBox.setSelectedItem(invoice.getPaymentMethod());
        paidCheckBox.setSelected(invoice.isPaid());

        paymentDateField.setText(invoice.getPaymentDate() != null ?
                invoice.getPaymentDate().format(dateFormatter) :
                (invoice.isPaid() ? LocalDateTime.now().format(dateFormatter) : ""));

        notesArea.setText(invoice.getNotes() != null ? invoice.getNotes() : "");

        // تحديث معلومات الطلب
        updateOrderInfo();

        // إعادة إضافة المستمع
        orderComboBox.addActionListener(e -> updateOrderInfo());
    }

    private void updateOrderInfo() {
        String selected = (String) orderComboBox.getSelectedItem();
        if (selected != null && !selected.equals("اختر طلب صيانة مكتمل")) {
            try {
                int orderId = Integer.parseInt(selected.split("#")[1].split(" - ")[0]);
                MaintenanceOrder order = orderDAO.getMaintenanceOrderById(orderId);

                if (order != null) {
                    // تحديث المبلغ الإجمالي
                    totalAmountField.setText(String.format("%.2f", order.getActualCost()));
                    calculateAmounts();

                    // تحديث معلومات الطلب
                    orderInfoLabel.setText("<html>📋 <b>طلب الصيانة #" + orderId + "</b><br>" +
                            "🚗 السيارة: " + order.getVehiclePlate() + "<br>" +
                            "📝 الوصف: " + (order.getDescription() != null && order.getDescription().length() > 50 ?
                            order.getDescription().substring(0, 50) + "..." : order.getDescription()) + "<br>" +
                            "👨‍🔧 الفني: " + (order.getTechnician() != null ? order.getTechnician().getName() : "غير محدد") + "<br>" +
                            "💰 التكلفة: " + String.format("%.2f", order.getActualCost()));

                    // تحديث معلومات السيارة
                    if (order.getVehicle() != null) {
                        Vehicle vehicle = order.getVehicle();
                        vehicleInfoLabel.setText("<html>🚗 <b>" + vehicle.getModel() + "</b><br>" +
                                "🔢 اللوحة: " + vehicle.getPlateNumber() + "<br>" +
                                "📅 السنة: " + vehicle.getYear() + "<br>" +
                                "🎨 اللون: " + (vehicle.getColor() != null ? vehicle.getColor() : "غير محدد"));
                    }

                    // تحديث معلومات العميل
                    if (order.getVehicle() != null && order.getVehicle().getOwner() != null) {
                        Customer customer = order.getVehicle().getOwner();
                        customerInfoLabel.setText("<html>👤 <b>" + customer.getName() + "</b><br>" +
                                "📱 الهاتف: " + customer.getPhone() + "<br>" +
                                "📧 البريد: " + (customer.getEmail() != null ? customer.getEmail() : "غير محدد") + "<br>" +
                                "🏠 العنوان: " + (customer.getAddress() != null ? customer.getAddress() : "غير محدد"));
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ خطأ في تحديث معلومات الطلب: " + e.getMessage());
            }
        }
    }

    private void calculateTax() {
        try {
            double totalAmount = Double.parseDouble(totalAmountField.getText());
            double taxPercentage = Double.parseDouble(taxPercentageField.getText());
            double taxAmount = totalAmount * (taxPercentage / 100);
            taxAmountField.setText(String.format("%.2f", taxAmount));
            calculateFinalAmount();
        } catch (NumberFormatException e) {
            taxAmountField.setText("0.00");
        }
    }

    private void calculateDiscount() {
        try {
            double totalAmount = Double.parseDouble(totalAmountField.getText());
            double discountPercentage = Double.parseDouble(discountPercentageField.getText());
            double discountAmount = totalAmount * (discountPercentage / 100);
            discountAmountField.setText(String.format("%.2f", discountAmount));
            calculateFinalAmount();
        } catch (NumberFormatException e) {
            discountAmountField.setText("0.00");
        }
    }

    private void calculateAmounts() {
        calculateTax();
        calculateDiscount();
    }

    private void calculateFinalAmount() {
        try {
            double totalAmount = Double.parseDouble(totalAmountField.getText());
            double taxAmount = Double.parseDouble(taxAmountField.getText());
            double discountAmount = Double.parseDouble(discountAmountField.getText());
            double finalAmount = totalAmount + taxAmount - discountAmount;
            finalAmountField.setText(String.format("%.2f", finalAmount));
        } catch (NumberFormatException e) {
            finalAmountField.setText("0.00");
        }
    }

    private void updatePaymentFields() {
        if (paidCheckBox.isSelected()) {
            paymentDateField.setText(LocalDateTime.now().format(dateFormatter));
            paymentDateField.setEnabled(true);
        } else {
            paymentDateField.setText("");
            paymentDateField.setEnabled(false);
        }
    }

    private void generateInvoice() {
        if (!validateInput()) {
            return;
        }

        Invoice invoice = createInvoiceFromForm();
        if (invoice == null) {
            return;
        }

        boolean success = invoiceDAO.addInvoice(invoice);
        if (success) {
            showSuccess("✅ تم إنشاء الفاتورة بنجاح!\nرقم الفاتورة: #" + invoice.getId());
            clearFields();
            loadCompletedOrders();
            loadAllInvoices();
        } else {
            showError("❌ فشل في إنشاء الفاتورة!");
        }
    }

    private void updateInvoice() {
        if (selectedInvoiceId == -1) {
            showError("⚠️ يرجى اختيار فاتورة للتحديث");
            return;
        }

        if (!validateInput()) {
            return;
        }

        Invoice invoice = createInvoiceFromForm();
        if (invoice == null) {
            return;
        }

        invoice.setId(selectedInvoiceId);
        boolean success = invoiceDAO.updateInvoice(invoice);
        if (success) {
            showSuccess("✅ تم تحديث الفاتورة بنجاح!");
            clearFields();
            loadCompletedOrders();
            loadAllInvoices();
        } else {
            showError("❌ فشل في تحديث الفاتورة!");
        }
    }

    private void deleteInvoice() {
        if (selectedInvoiceId == -1) {
            showError("⚠️ يرجى اختيار فاتورة للحذف");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "هل أنت متأكد من حذف الفاتورة؟\nهذا الإجراء لا يمكن التراجع عنه.",
                "تأكيد الحذف", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = invoiceDAO.deleteInvoice(selectedInvoiceId);
            if (success) {
                showSuccess("✅ تم حذف الفاتورة بنجاح!");
                clearFields();
                loadCompletedOrders();
                loadAllInvoices();
            } else {
                showError("❌ فشل في حذف الفاتورة!");
            }
        }
    }

    private void markAsPaid() {
        if (selectedInvoiceId == -1) {
            showError("⚠️ يرجى اختيار فاتورة للتسديد");
            return;
        }

        Invoice invoice = invoiceDAO.getInvoiceById(selectedInvoiceId);
        if (invoice == null) {
            showError("❌ الفاتورة غير موجودة!");
            return;
        }

        if (invoice.isPaid()) {
            showError("⚠️ الفاتورة مدفوعة بالفعل!");
            return;
        }

        String paymentMethod = (String) JOptionPane.showInputDialog(this,
                "اختر طريقة الدفع:", "تسديد الفاتورة",
                JOptionPane.QUESTION_MESSAGE, null,
                new String[]{"نقدي", "بطاقة ائتمان", "تحويل بنكي", "شيك", "أخرى"},
                "نقدي");

        if (paymentMethod != null) {
            boolean success = invoiceDAO.markAsPaid(selectedInvoiceId, paymentMethod);
            if (success) {
                showSuccess("✅ تم تسديد الفاتورة بنجاح!");
                loadAllInvoices();
                loadInvoiceToForm(invoiceDAO.getInvoiceById(selectedInvoiceId));
            } else {
                showError("❌ فشل في تسديد الفاتورة!");
            }
        }
    }

    private void printInvoice() {
        if (selectedInvoiceId == -1) {
            showError("⚠️ يرجى اختيار فاتورة للطباعة");
            return;
        }

        Invoice invoice = invoiceDAO.getInvoiceById(selectedInvoiceId);
        if (invoice == null) {
            showError("❌ الفاتورة غير موجودة!");
            return;
        }

        // إنشاء محتوى الفاتورة للطباعة
        String invoiceContent = createPrintableInvoice(invoice);

        // عرض الفاتورة في نافذة نصية
        JTextArea textArea = new JTextArea(invoiceContent, 40, 60);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane,
                "🧾 فاتورة #" + invoice.getId(), JOptionPane.INFORMATION_MESSAGE);
    }

    private String createPrintableInvoice(Invoice invoice) {
        StringBuilder sb = new StringBuilder();

        sb.append("=".repeat(60)).append("\n");
        sb.append(" ".repeat(20)).append("فاتورة صيانة السيارات").append("\n");
        sb.append("=".repeat(60)).append("\n\n");

        sb.append("معلومات الفاتورة:\n");
        sb.append("-".repeat(30)).append("\n");
        sb.append("رقم الفاتورة: #").append(invoice.getId()).append("\n");
        sb.append("رقم الطلب: #").append(invoice.getOrderId()).append("\n");
        sb.append("تاريخ الإصدار: ").append(invoice.getIssuedDate().format(dateFormatter)).append("\n");
        sb.append("تاريخ الاستحقاق: ").append(invoice.getDueDate().format(dateFormatter)).append("\n");
        sb.append("طريقة الدفع: ").append(invoice.getPaymentMethod()).append("\n");
        sb.append("حالة الدفع: ").append(invoice.getPaymentStatus()).append("\n");

        if (invoice.getOrder() != null) {
            sb.append("\nمعلومات الطلب:\n");
            sb.append("-".repeat(30)).append("\n");
            sb.append("وصف العمل: ").append(invoice.getOrder().getDescription()).append("\n");
            sb.append("الفني: ").append(invoice.getOrder().getTechnician() != null ?
                    invoice.getOrder().getTechnician().getName() : "غير محدد").append("\n");

            if (invoice.getOrder().getVehicle() != null) {
                sb.append("\nمعلومات السيارة:\n");
                sb.append("-".repeat(30)).append("\n");
                Vehicle vehicle = invoice.getOrder().getVehicle();
                sb.append("الموديل: ").append(vehicle.getModel()).append("\n");
                sb.append("رقم اللوحة: ").append(vehicle.getPlateNumber()).append("\n");
                sb.append("السنة: ").append(vehicle.getYear()).append("\n");

                if (vehicle.getOwner() != null) {
                    sb.append("\nمعلومات العميل:\n");
                    sb.append("-".repeat(30)).append("\n");
                    Customer customer = vehicle.getOwner();
                    sb.append("الاسم: ").append(customer.getName()).append("\n");
                    sb.append("الهاتف: ").append(customer.getPhone()).append("\n");
                    sb.append("العنوان: ").append(customer.getAddress() != null ? customer.getAddress() : "غير محدد").append("\n");
                }
            }
        }

        sb.append("\nالتفاصيل المالية:\n");
        sb.append("-".repeat(30)).append("\n");
        sb.append(String.format("المبلغ الإجمالي: %40.2f\n", invoice.getTotalAmount()));
        sb.append(String.format("الضريبة: %45.2f\n", invoice.getTaxAmount()));
        sb.append(String.format("الخصم: %46.2f\n", invoice.getDiscountAmount()));
        sb.append("-".repeat(60)).append("\n");
        sb.append(String.format("المبلغ النهائي: %40.2f\n", invoice.getFinalAmount()));

        sb.append("\n").append("=".repeat(60)).append("\n");
        sb.append(" ".repeat(20)).append("شكراً لتعاملكم معنا").append("\n");
        sb.append("=".repeat(60)).append("\n");

        if (invoice.getNotes() != null && !invoice.getNotes().isEmpty()) {
            sb.append("\nملاحظات: ").append(invoice.getNotes()).append("\n");
        }

        return sb.toString();
    }

    private boolean validateInput() {
        // التحقق من الطلب
        String selectedOrder = (String) orderComboBox.getSelectedItem();
        if (selectedOrder == null || selectedOrder.equals("اختر طلب صيانة مكتمل")) {
            showError("⚠️ يرجى اختيار طلب صيانة مكتمل");
            orderComboBox.requestFocus();
            return false;
        }

        // التحقق من المبلغ الإجمالي
        try {
            double totalAmount = Double.parseDouble(totalAmountField.getText());
            if (totalAmount <= 0) {
                showError("⚠️ المبلغ الإجمالي يجب أن يكون أكبر من صفر");
                totalAmountField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("⚠️ المبلغ الإجمالي يجب أن يكون رقم صحيح");
            totalAmountField.requestFocus();
            return false;
        }

        return true;
    }

    private Invoice createInvoiceFromForm() {
        try {
            Invoice invoice = new Invoice();

            // استخراج رقم الطلب من النص المختار
            String selectedOrder = (String) orderComboBox.getSelectedItem();
            int orderId = Integer.parseInt(selectedOrder.split("#")[1].split(" - ")[0]);
            invoice.setOrderId(orderId);

            invoice.setTotalAmount(Double.parseDouble(totalAmountField.getText()));
            invoice.setTaxAmount(Double.parseDouble(taxAmountField.getText()));
            invoice.setDiscountAmount(Double.parseDouble(discountAmountField.getText()));

            // التواريخ
            invoice.setIssuedDate(LocalDateTime.parse(issuedDateField.getText(), dateFormatter));
            invoice.setDueDate(LocalDateTime.parse(dueDateField.getText(), dateFormatter));

            invoice.setPaymentMethod((String) paymentMethodComboBox.getSelectedItem());
            invoice.setPaid(paidCheckBox.isSelected());

            if (paidCheckBox.isSelected() && !paymentDateField.getText().isEmpty()) {
                invoice.setPaymentDate(LocalDateTime.parse(paymentDateField.getText(), dateFormatter));
            }

            invoice.setNotes(notesArea.getText().trim());

            return invoice;

        } catch (Exception e) {
            showError("❌ خطأ في إنشاء الفاتورة: " + e.getMessage());
            return null;
        }
    }

    private void searchInvoices() {
        String searchTerm = JOptionPane.showInputDialog(this,
                "أدخل رقم الفاتورة أو رقم الطلب للبحث:", "بحث عن فواتير",
                JOptionPane.QUESTION_MESSAGE);

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            tableModel.setRowCount(0);

            try {
                // محاولة البحث برقم الفاتورة
                int invoiceId = Integer.parseInt(searchTerm.trim());
                Invoice invoice = invoiceDAO.getInvoiceById(invoiceId);
                if (invoice != null) {
                    addInvoiceToTable(invoice);
                    showInfo("🔍 تم العثور على فاتورة #" + invoiceId);
                    return;
                }
            } catch (NumberFormatException e) {
                // ليس رقم فاتورة، قد يكون رقم طلب
            }

            try {
                // البحث برقم الطلب
                int orderId = Integer.parseInt(searchTerm.trim());
                Invoice invoice = invoiceDAO.getInvoiceByOrderId(orderId);
                if (invoice != null) {
                    addInvoiceToTable(invoice);
                    showInfo("🔍 تم العثور على فاتورة للطلب #" + orderId);
                    return;
                }
            } catch (NumberFormatException e) {
                // ليس رقم طلب
            }

            showInfo("🔍 لم يتم العثور على فواتير تطابق البحث");
        }
    }

    private void addInvoiceToTable(Invoice invoice) {
        Object[] row = {
                invoice.getId(),
                invoice.getOrderId(),
                String.format("%.2f", invoice.getTotalAmount()),
                String.format("%.2f", invoice.getTaxAmount()),
                String.format("%.2f", invoice.getDiscountAmount()),
                String.format("%.2f", invoice.getFinalAmount()),
                invoice.getIssuedDate() != null ? invoice.getIssuedDate().format(dateFormatter) : "غير محدد",
                invoice.getDueDate() != null ? invoice.getDueDate().format(dateFormatter) : "غير محدد",
                invoice.getPaymentMethod(),
                invoice.getPaymentStatus()
        };
        tableModel.addRow(row);
    }

    private void showStatistics() {
        String stats = invoiceDAO.getStatistics();

        JTextArea textArea = new JTextArea(stats, 25, 60);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(248, 249, 250));

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane,
                "📊 إحصائيات الفواتير", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields() {
        orderComboBox.setSelectedIndex(0);
        totalAmountField.setText("0.00");
        taxPercentageField.setText(String.valueOf(DEFAULT_TAX_PERCENTAGE));
        discountPercentageField.setText("0");
        taxAmountField.setText("0.00");
        discountAmountField.setText("0.00");
        finalAmountField.setText("0.00");
        issuedDateField.setText(LocalDateTime.now().format(dateFormatter));
        dueDateField.setText(LocalDateTime.now().plusDays(30).format(dateFormatter));
        paymentMethodComboBox.setSelectedIndex(0);
        paidCheckBox.setSelected(false);
        paymentDateField.setText("");
        notesArea.setText("");

        orderInfoLabel.setText("📋 اختر طلب صيانة مكتمل");
        vehicleInfoLabel.setText("🚗 معلومات السيارة ستظهر هنا");
        customerInfoLabel.setText("👤 معلومات العميل ستظهر هنا");

        clearSelection();
    }

    private void clearSelection() {
        invoicesTable.clearSelection();
        selectedInvoiceId = -1;

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        markPaidButton.setEnabled(false);
        printButton.setEnabled(false);
        generateButton.setEnabled(true);
    }

    private void updateTitle() {
        int total = invoiceDAO.getInvoiceCount();
        double sales = invoiceDAO.getTotalSales();
        double receivables = invoiceDAO.getTotalReceivables();
        setTitle("إدارة الفواتير - العدد: " + total + " | المبيعات: " +
                String.format("%.2f", sales) + " | المستحقات: " +
                String.format("%.2f", receivables));
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "خطأ",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "نجاح",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "معلومات",
                JOptionPane.INFORMATION_MESSAGE);
    }
}