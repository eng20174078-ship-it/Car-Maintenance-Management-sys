package com.carmaintenance.gui;

import com.carmaintenance.dao.*;
import com.carmaintenance.model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MaintenanceOrderForm extends JFrame {

    // DAOs
    private MaintenanceOrderDAO orderDAO;
    private VehicleDAO vehicleDAO;
    private TechnicianDAO technicianDAO;
    private SparePartDAO sparePartDAO;
    private CustomerDAO customerDAO;

    // الحقول الأساسية
    private JComboBox<String> vehicleComboBox;
    private JComboBox<String> technicianComboBox;
    private JTextArea descriptionArea;
    private JComboBox<String> statusComboBox;
    private JTextArea customerNotesArea;
    private JTextArea internalNotesArea;
    private JTextField estimatedCostField;
    private JTextField actualCostField;

    // الحقول المتعلقة بالسيارة والفني
    private JLabel vehicleInfoLabel;
    private JLabel customerInfoLabel;
    private JLabel technicianInfoLabel;

    // الأزرار الرئيسية
    private JButton saveButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton searchButton;
    private JButton viewAllButton;
    private JButton statsButton;
    private JButton addPartButton;
    private JButton completeButton;

    // أزرار الحالة
    private JButton pendingButton;
    private JButton inProgressButton;
    private JButton waitingPartsButton;
    private JButton completedButton;
    private JButton cancelledButton;

    // الجدول الرئيسي
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;

    // جدول قطع الغيار المستخدمة
    private JTable usedPartsTable;
    private DefaultTableModel usedPartsTableModel;

    // ComboBox لقطع الغيار
    private JComboBox<String> partsComboBox;
    private JTextField partQuantityField;

    // ID المحدد حاليًا
    private int selectedOrderId = -1;

    // تنسيق التاريخ
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MaintenanceOrderForm() {
        // تهيئة DAOs
        orderDAO = new MaintenanceOrderDAO();
        vehicleDAO = new VehicleDAO();
        technicianDAO = new TechnicianDAO();
        sparePartDAO = new SparePartDAO();
        customerDAO = new CustomerDAO();

        // إعداد النافذة
        setupWindow();
        initComponents();
        layoutComponents();

        // تحميل البيانات
        loadVehicles();
        loadTechnicians();
        loadSpareParts();
        loadAllOrders();
        updateTitle();
    }

    private void setupWindow() {
        setTitle("نظام صيانة السيارات - إدارة طلبات الصيانة");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // ComboBox للسيارات
        vehicleComboBox = new JComboBox<>();
        vehicleComboBox.addActionListener(e -> updateVehicleInfo());

        // ComboBox للفنيين
        technicianComboBox = new JComboBox<>();
        technicianComboBox.addActionListener(e -> updateTechnicianInfo());

        // الحقول الأخرى
        descriptionArea = new JTextArea(3, 40);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        statusComboBox = new JComboBox<>(new String[]{
                MaintenanceOrder.STATUS_PENDING,
                MaintenanceOrder.STATUS_IN_PROGRESS,
                MaintenanceOrder.STATUS_WAITING_PARTS,
                MaintenanceOrder.STATUS_COMPLETED,
                MaintenanceOrder.STATUS_CANCELLED
        });

        customerNotesArea = new JTextArea(3, 40);
        customerNotesArea.setLineWrap(true);
        customerNotesArea.setWrapStyleWord(true);

        internalNotesArea = new JTextArea(3, 40);
        internalNotesArea.setLineWrap(true);
        internalNotesArea.setWrapStyleWord(true);

        estimatedCostField = createTextField();
        estimatedCostField.setText("0.0");

        actualCostField = createTextField();
        actualCostField.setText("0.0");
        actualCostField.setEditable(false); // تحسب تلقائياً من القطع المستخدمة

        // تسميات المعلومات
        vehicleInfoLabel = createInfoLabel("🚗 اختر سيارة");
        customerInfoLabel = createInfoLabel("👤 معلومات العميل ستظهر هنا");
        technicianInfoLabel = createInfoLabel("👨‍🔧 اختر فني");

        // الأزرار الرئيسية
        saveButton = createButton("💾 حفظ الطلب", new Color(40, 167, 69));
        updateButton = createButton("✏️ تحديث الطلب", new Color(255, 193, 7));
        deleteButton = createButton("🗑️ حذف الطلب", new Color(220, 53, 69));
        clearButton = createButton("🧹 مسح الحقول", new Color(108, 117, 125));
        searchButton = createButton("🔍 بحث عن طلب", new Color(0, 123, 255));
        viewAllButton = createButton("📋 عرض الجميع", new Color(111, 66, 193));
        statsButton = createButton("📊 الإحصائيات", new Color(32, 201, 151));
        addPartButton = createButton("🔩 إضافة قطعة", new Color(23, 162, 184));
        completeButton = createButton("✅ إكمال الطلب", new Color(40, 167, 69));

        // أزرار الحالة السريعة
        pendingButton = createStatusButton("⏳ قيد الانتظار", new Color(255, 193, 7));
        inProgressButton = createStatusButton("🔧 قيد التنفيذ", new Color(0, 123, 255));
        waitingPartsButton = createStatusButton("⏳ انتظار قطع", new Color(253, 126, 20));
        completedButton = createStatusButton("✅ مكتمل", new Color(40, 167, 69));
        cancelledButton = createStatusButton("❌ ملغى", new Color(220, 53, 69));

        // ComboBox وكمية قطع الغيار
        partsComboBox = new JComboBox<>();
        partQuantityField = createTextField();
        partQuantityField.setText("1");

        // إضافة المستمعين للأزرار
        saveButton.addActionListener(e -> saveOrder());
        updateButton.addActionListener(e -> updateOrder());
        deleteButton.addActionListener(e -> deleteOrder());
        clearButton.addActionListener(e -> clearFields());
        searchButton.addActionListener(e -> searchOrders());
        viewAllButton.addActionListener(e -> loadAllOrders());
        statsButton.addActionListener(e -> showStatistics());
        addPartButton.addActionListener(e -> addUsedPart());
        completeButton.addActionListener(e -> completeOrder());

        pendingButton.addActionListener(e -> setStatus(MaintenanceOrder.STATUS_PENDING));
        inProgressButton.addActionListener(e -> setStatus(MaintenanceOrder.STATUS_IN_PROGRESS));
        waitingPartsButton.addActionListener(e -> setStatus(MaintenanceOrder.STATUS_WAITING_PARTS));
        completedButton.addActionListener(e -> setStatus(MaintenanceOrder.STATUS_COMPLETED));
        cancelledButton.addActionListener(e -> setStatus(MaintenanceOrder.STATUS_CANCELLED));

        // إعداد جدول الطلبات
        String[] ordersColumns = {"ID", "السيارة", "الفني", "الوصف", "الحالة", "التاريخ", "التكلفة"};
        ordersTableModel = new DefaultTableModel(ordersColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setRowHeight(25);
        ordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = ordersTable.getSelectedRow();
                if (selectedRow != -1) {
                    loadOrderFromTable(selectedRow);
                }
            }
        });

        // إعداد جدول قطع الغيار المستخدمة
        String[] partsColumns = {"اسم القطعة", "الكمية", "سعر الوحدة", "الإجمالي"};
        usedPartsTableModel = new DefaultTableModel(partsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        usedPartsTable = new JTable(usedPartsTableModel);
        usedPartsTable.setRowHeight(25);

        // تعطيل الأزرار
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        completeButton.setEnabled(false);
        addPartButton.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // لوحة العنوان
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("🔧 إدارة طلبات الصيانة");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // لوحة الحقول الرئيسية (اليسار)
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // لوحة المعلومات العلوية
        JPanel infoPanel = new JPanel(new GridLayout(1, 3, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("معلومات سريعة"));
        infoPanel.add(createInfoPanel("🚗 معلومات السيارة", vehicleInfoLabel));
        infoPanel.add(createInfoPanel("👤 معلومات العميل", customerInfoLabel));
        infoPanel.add(createInfoPanel("👨‍🔧 معلومات الفني", technicianInfoLabel));

        // لوحة الحقول الأساسية
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("بيانات الطلب"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // الصف 0: السيارة
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(createLabel("🚗 السيارة:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(vehicleComboBox, gbc);

        // الصف 1: الفني
        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(createLabel("👨‍🔧 الفني:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(technicianComboBox, gbc);

        // الصف 2: الوصف
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST;
        fieldsPanel.add(createLabel("📝 وصف المشكلة:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(300, 60));
        fieldsPanel.add(descScroll, gbc);

        // الصف 3: الحالة
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("📊 الحالة:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(statusComboBox, gbc);

        // الصف 4: ملاحظات العميل
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.NORTHWEST;
        fieldsPanel.add(createLabel("💬 ملاحظات العميل:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane customerNotesScroll = new JScrollPane(customerNotesArea);
        customerNotesScroll.setPreferredSize(new Dimension(300, 60));
        fieldsPanel.add(customerNotesScroll, gbc);

        // الصف 5: ملاحظات داخلية
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.NORTHWEST;
        fieldsPanel.add(createLabel("📋 ملاحظات داخلية:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane internalNotesScroll = new JScrollPane(internalNotesArea);
        internalNotesScroll.setPreferredSize(new Dimension(300, 60));
        fieldsPanel.add(internalNotesScroll, gbc);

        // الصف 6: التكاليف
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("💰 التكلفة المتوقعة:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(estimatedCostField, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("💵 التكلفة الفعلية:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(actualCostField, gbc);

        // لوحة أزرار الحالة السريعة
        JPanel statusButtonsPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        statusButtonsPanel.setBorder(BorderFactory.createTitledBorder("تغيير الحالة السريع"));
        statusButtonsPanel.add(pendingButton);
        statusButtonsPanel.add(inProgressButton);
        statusButtonsPanel.add(waitingPartsButton);
        statusButtonsPanel.add(completedButton);
        statusButtonsPanel.add(cancelledButton);

        // لوحة إضافة قطع الغيار
        JPanel addPartsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        addPartsPanel.setBorder(BorderFactory.createTitledBorder("إضافة قطع غيار مستخدمة"));
        addPartsPanel.add(createLabel("🔩 القطعة:"));
        addPartsPanel.add(partsComboBox);
        addPartsPanel.add(createLabel("الكمية:"));
        partQuantityField.setPreferredSize(new Dimension(50, 25));
        addPartsPanel.add(partQuantityField);
        addPartsPanel.add(addPartButton);

        // لوحة جدول القطع المستخدمة
        JPanel usedPartsPanel = new JPanel(new BorderLayout());
        usedPartsPanel.setBorder(BorderFactory.createTitledBorder("القطع المستخدمة في الطلب"));
        usedPartsPanel.add(new JScrollPane(usedPartsTable), BorderLayout.CENTER);

        // تجميع اللوحات اليسرى
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(infoPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(fieldsPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(statusButtonsPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(addPartsPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        leftPanel.add(usedPartsPanel);

        // لوحة الجدول الرئيسي (اليمين)
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("قائمة طلبات الصيانة"));
        tablePanel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        // لوحة الأزرار السفلية
        JPanel buttonPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        buttonPanel.add(saveButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(completeButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(viewAllButton);
        buttonPanel.add(statsButton);

        // إضافة المكونات الرئيسية
        mainPanel.add(leftPanel, BorderLayout.WEST);
        mainPanel.add(tablePanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
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

    private JButton createStatusButton(String text, Color color) {
        JButton button = createButton(text, color);
        button.setFont(new Font("Arial", Font.BOLD, 11));
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

    private void loadVehicles() {
        vehicleComboBox.removeAllItems();
        vehicleComboBox.addItem("اختر سيارة");

        List<Vehicle> vehicles = vehicleDAO.getAllVehicles();
        for (Vehicle vehicle : vehicles) {
            vehicleComboBox.addItem(vehicle.getPlateNumber() + " - " + vehicle.getModel());
        }
    }

    private void loadTechnicians() {
        technicianComboBox.removeAllItems();
        technicianComboBox.addItem("اختر فني");

        List<Technician> technicians = technicianDAO.getActiveTechnicians();
        for (Technician technician : technicians) {
            technicianComboBox.addItem(technician.getName() + " - " + technician.getSpecialization());
        }
    }

    private void loadSpareParts() {
        partsComboBox.removeAllItems();
        partsComboBox.addItem("اختر قطعة غيار");

        List<SparePart> parts = sparePartDAO.getAllSpareParts();
        for (SparePart part : parts) {
            if (part.getQuantity() > 0) { // عرض القطع المتوفرة فقط
                partsComboBox.addItem(part.getName() + " (" + part.getQuantity() + " متوفر)");
            }
        }
    }

    private void loadAllOrders() {
        ordersTableModel.setRowCount(0);
        List<MaintenanceOrder> orders = orderDAO.getAllMaintenanceOrders();

        for (MaintenanceOrder order : orders) {
            Object[] row = {
                    order.getId(),
                    order.getVehiclePlate(),
                    order.getTechnician() != null ? order.getTechnician().getName() : "غير محدد",
                    order.getDescription() != null && order.getDescription().length() > 30 ?
                            order.getDescription().substring(0, 30) + "..." : order.getDescription(),
                    order.getStatus(),
                    order.getCreatedAt() != null ? order.getCreatedAt().format(dateFormatter) : "غير محدد",
                    String.format("%.2f", order.getActualCost())
            };
            ordersTableModel.addRow(row);
        }

        updateTitle();
        clearSelection();
    }

    private void loadOrderFromTable(int rowIndex) {
        int id = (int) ordersTableModel.getValueAt(rowIndex, 0);
        MaintenanceOrder order = orderDAO.getMaintenanceOrderById(id);

        if (order != null) {
            loadOrderToForm(order);
            selectedOrderId = id;

            // تفعيل الأزرار
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
            completeButton.setEnabled(true);
            addPartButton.setEnabled(true);
            saveButton.setEnabled(false);
        }
    }

    private void loadOrderToForm(MaintenanceOrder order) {
        // السيارة
        vehicleComboBox.setSelectedItem(order.getVehiclePlate() + " - " +
                (order.getVehicle() != null ? order.getVehicle().getModel() : ""));

        // الفني
        if (order.getTechnician() != null) {
            technicianComboBox.setSelectedItem(order.getTechnician().getName() + " - " +
                    order.getTechnician().getSpecialization());
        }

        descriptionArea.setText(order.getDescription() != null ? order.getDescription() : "");
        statusComboBox.setSelectedItem(order.getStatus());
        customerNotesArea.setText(order.getCustomerNotes() != null ? order.getCustomerNotes() : "");
        internalNotesArea.setText(order.getInternalNotes() != null ? order.getInternalNotes() : "");
        estimatedCostField.setText(String.valueOf(order.getEstimatedCost()));
        actualCostField.setText(String.valueOf(order.getActualCost()));

        // تحديث معلومات السيارة والعميل والفني
        updateVehicleInfo();
        updateTechnicianInfo();

        // تحميل القطع المستخدمة
        loadUsedParts(order);
    }

    private void loadUsedParts(MaintenanceOrder order) {
        usedPartsTableModel.setRowCount(0);
        double totalCost = 0;

        for (MaintenanceOrder.OrderPart orderPart : order.getUsedParts()) {
            SparePart part = sparePartDAO.getSparePartById(orderPart.getPartId());
            if (part != null) {
                double total = orderPart.getQuantity() * orderPart.getUnitPrice();
                totalCost += total;

                Object[] row = {
                        part.getName(),
                        orderPart.getQuantity(),
                        String.format("%.2f", orderPart.getUnitPrice()),
                        String.format("%.2f", total)
                };
                usedPartsTableModel.addRow(row);
            }
        }

        // تحديث التكلفة الفعلية
        actualCostField.setText(String.format("%.2f", totalCost));
    }

    private void updateVehicleInfo() {
        String selected = (String) vehicleComboBox.getSelectedItem();
        if (selected != null && !selected.equals("اختر سيارة")) {
            String plateNumber = selected.split(" - ")[0];
            Vehicle vehicle = vehicleDAO.getVehicleByPlate(plateNumber);
            if (vehicle != null) {
                Customer owner = customerDAO.getCustomerById(vehicle.getOwnerId());
                String info = "<html>🚗 <b>" + vehicle.getModel() + "</b><br>" +
                        "🔢 اللوحة: " + vehicle.getPlateNumber() + "<br>" +
                        "📅 السنة: " + vehicle.getYear() + "<br>" +
                        (owner != null ? "👤 المالك: " + owner.getName() + "<br>📱 الهاتف: " + owner.getPhone() : "");
                vehicleInfoLabel.setText(info);

                if (owner != null) {
                    customerInfoLabel.setText("<html>👤 <b>" + owner.getName() + "</b><br>" +
                            "📱 الهاتف: " + owner.getPhone() + "<br>" +
                            "📧 البريد: " + (owner.getEmail() != null ? owner.getEmail() : "غير محدد") + "<br>" +
                            "🏠 العنوان: " + (owner.getAddress() != null ? owner.getAddress() : "غير محدد"));
                }
                return;
            }
        }
        vehicleInfoLabel.setText("🚗 اختر سيارة");
        customerInfoLabel.setText("👤 معلومات العميل ستظهر هنا");
    }

    private void updateTechnicianInfo() {
        String selected = (String) technicianComboBox.getSelectedItem();
        if (selected != null && !selected.equals("اختر فني")) {
            String technicianName = selected.split(" - ")[0];
            List<Technician> technicians = technicianDAO.searchTechniciansByName(technicianName);
            if (!technicians.isEmpty()) {
                Technician tech = technicians.get(0);
                String info = "<html>👨‍🔧 <b>" + tech.getName() + "</b><br>" +
                        "🔧 التخصص: " + tech.getSpecialization() + "<br>" +
                        "📱 الهاتف: " + tech.getPhone() + "<br>" +
                        "📊 الحالة: " + tech.getStatus();
                technicianInfoLabel.setText(info);
                return;
            }
        }
        technicianInfoLabel.setText("👨‍🔧 اختر فني");
    }

    private void saveOrder() {
        if (!validateInput()) {
            return;
        }

        MaintenanceOrder order = createOrderFromForm();
        if (order == null) {
            return;
        }

        boolean success = orderDAO.addMaintenanceOrder(order);
        if (success) {
            showSuccess("✅ تم إنشاء طلب الصيانة بنجاح!\nرقم الطلب: #" + order.getId());
            clearFields();
            loadAllOrders();
        } else {
            showError("❌ فشل في إنشاء طلب الصيانة!");
        }
    }

    private void updateOrder() {
        if (selectedOrderId == -1) {
            showError("⚠️ يرجى اختيار طلب للتحديث");
            return;
        }

        if (!validateInput()) {
            return;
        }

        MaintenanceOrder order = createOrderFromForm();
        if (order == null) {
            return;
        }

        order.setId(selectedOrderId);
        boolean success = orderDAO.updateMaintenanceOrder(order);
        if (success) {
            showSuccess("✅ تم تحديث طلب الصيانة بنجاح!");
            clearFields();
            loadAllOrders();
        } else {
            showError("❌ فشل في تحديث طلب الصيانة!");
        }
    }

    private void deleteOrder() {
        if (selectedOrderId == -1) {
            showError("⚠️ يرجى اختيار طلب للحذف");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "هل أنت متأكد من حذف طلب الصيانة؟\nهذا الإجراء لا يمكن التراجع عنه.",
                "تأكيد الحذف", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // هنا يجب أولاً حذف القطع المرتبطة ثم حذف الطلب
            boolean success = false; // orderDAO.deleteMaintenanceOrder(selectedOrderId);

            if (success) {
                showSuccess("✅ تم حذف طلب الصيانة بنجاح!");
                clearFields();
                loadAllOrders();
            } else {
                showError("❌ فشل في حذف طلب الصيانة!");
            }
        }
    }

    private void completeOrder() {
        if (selectedOrderId == -1) {
            showError("⚠️ يرجى اختيار طلب للإكمال");
            return;
        }

        statusComboBox.setSelectedItem(MaintenanceOrder.STATUS_COMPLETED);
        updateOrder();
    }

    private void addUsedPart() {
        if (selectedOrderId == -1) {
            showError("⚠️ يرجى اختيار طلب أولاً");
            return;
        }

        String selectedPart = (String) partsComboBox.getSelectedItem();
        if (selectedPart == null || selectedPart.equals("اختر قطعة غيار")) {
            showError("⚠️ يرجى اختيار قطعة غيار");
            return;
        }

        String quantityText = partQuantityField.getText().trim();
        if (quantityText.isEmpty()) {
            showError("⚠️ يرجى إدخال الكمية");
            return;
        }

        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                showError("⚠️ الكمية يجب أن تكون أكبر من صفر");
                return;
            }

            // استخراج اسم القطعة من النص
            String partName = selectedPart.split(" \\(")[0];
            List<SparePart> parts = sparePartDAO.searchSparePartsByName(partName);
            if (parts.isEmpty()) {
                showError("❌ لم يتم العثور على القطعة");
                return;
            }

            SparePart part = parts.get(0);
            if (part.getQuantity() < quantity) {
                showError("❌ الكمية المتاحة غير كافية!\nالمتاح: " + part.getQuantity());
                return;
            }

            // إضافة القطعة إلى الطلب
            boolean success = orderDAO.addUsedPart(selectedOrderId, part.getId(), quantity);
            if (success) {
                showSuccess("✅ تم إضافة القطعة إلى الطلب بنجاح!");
                loadOrderToForm(orderDAO.getMaintenanceOrderById(selectedOrderId));
                loadSpareParts(); // تحديث قائمة القطع المتاحة
            } else {
                showError("❌ فشل في إضافة القطعة!");
            }

        } catch (NumberFormatException e) {
            showError("⚠️ الكمية يجب أن تكون رقم صحيح");
        }
    }

    private void setStatus(String status) {
        if (selectedOrderId == -1) {
            showError("⚠️ يرجى اختيار طلب أولاً");
            return;
        }

        statusComboBox.setSelectedItem(status);
        updateOrderStatus(selectedOrderId, status);
    }

    private void updateOrderStatus(int orderId, String status) {
        boolean success = orderDAO.updateOrderStatus(orderId, status);
        if (success) {
            showSuccess("✅ تم تغيير حالة الطلب إلى: " + status);
            loadAllOrders();
            loadOrderToForm(orderDAO.getMaintenanceOrderById(orderId));
        } else {
            showError("❌ فشل في تغيير حالة الطلب!");
        }
    }

    private boolean validateInput() {
        // التحقق من السيارة
        String selectedVehicle = (String) vehicleComboBox.getSelectedItem();
        if (selectedVehicle == null || selectedVehicle.equals("اختر سيارة")) {
            showError("⚠️ يرجى اختيار سيارة");
            vehicleComboBox.requestFocus();
            return false;
        }

        // التحقق من الفني
        String selectedTechnician = (String) technicianComboBox.getSelectedItem();
        if (selectedTechnician == null || selectedTechnician.equals("اختر فني")) {
            showError("⚠️ يرجى اختيار فني");
            technicianComboBox.requestFocus();
            return false;
        }

        // التحقق من الوصف
        String description = descriptionArea.getText().trim();
        if (description.isEmpty()) {
            showError("⚠️ يرجى إدخال وصف للمشكلة");
            descriptionArea.requestFocus();
            return false;
        }

        return true;
    }

    private MaintenanceOrder createOrderFromForm() {
        try {
            MaintenanceOrder order = new MaintenanceOrder();

            // استخراج رقم اللوحة من النص المختار
            String selectedVehicle = (String) vehicleComboBox.getSelectedItem();
            String plateNumber = selectedVehicle.split(" - ")[0];
            order.setVehiclePlate(plateNumber);

            // استخراج اسم الفني من النص المختار
            String selectedTechnician = (String) technicianComboBox.getSelectedItem();
            String technicianName = selectedTechnician.split(" - ")[0];
            List<Technician> technicians = technicianDAO.searchTechniciansByName(technicianName);
            if (!technicians.isEmpty()) {
                order.setTechnicianId(technicians.get(0).getId());
            }

            order.setDescription(descriptionArea.getText().trim());
            order.setStatus((String) statusComboBox.getSelectedItem());
            order.setCustomerNotes(customerNotesArea.getText().trim());
            order.setInternalNotes(internalNotesArea.getText().trim());

            try {
                order.setEstimatedCost(Double.parseDouble(estimatedCostField.getText().trim()));
                order.setActualCost(Double.parseDouble(actualCostField.getText().trim()));
            } catch (NumberFormatException e) {
                order.setEstimatedCost(0.0);
                order.setActualCost(0.0);
            }

            return order;

        } catch (Exception e) {
            showError("❌ خطأ في إنشاء طلب الصيانة: " + e.getMessage());
            return null;
        }
    }

    private void searchOrders() {
        String searchTerm = JOptionPane.showInputDialog(this,
                "أدخل رقم السيارة أو وصف للبحث:", "بحث عن طلبات الصيانة",
                JOptionPane.QUESTION_MESSAGE);

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            ordersTableModel.setRowCount(0);

            // البحث برقم السيارة
            List<MaintenanceOrder> orders = orderDAO.getMaintenanceOrdersByVehicle(searchTerm.trim());

            // إذا لم توجد نتائج، البحث في الوصف
            if (orders.isEmpty()) {
                List<MaintenanceOrder> allOrders = orderDAO.getAllMaintenanceOrders();
                for (MaintenanceOrder order : allOrders) {
                    if (order.getDescription() != null &&
                            order.getDescription().toLowerCase().contains(searchTerm.toLowerCase())) {
                        orders.add(order);
                    }
                }
            }

            if (orders.isEmpty()) {
                showInfo("🔍 لم يتم العثور على طلبات تطابق البحث");
                return;
            }

            for (MaintenanceOrder order : orders) {
                Object[] row = {
                        order.getId(),
                        order.getVehiclePlate(),
                        order.getTechnician() != null ? order.getTechnician().getName() : "غير محدد",
                        order.getDescription() != null && order.getDescription().length() > 30 ?
                                order.getDescription().substring(0, 30) + "..." : order.getDescription(),
                        order.getStatus(),
                        order.getCreatedAt() != null ? order.getCreatedAt().format(dateFormatter) : "غير محدد",
                        String.format("%.2f", order.getActualCost())
                };
                ordersTableModel.addRow(row);
            }

            showInfo("🔍 تم العثور على " + orders.size() + " طلب صيانة");
        }
    }

    private void showStatistics() {
        String stats = orderDAO.getStatistics();

        JTextArea textArea = new JTextArea(stats, 20, 50);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(248, 249, 250));

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane,
                "📊 إحصائيات طلبات الصيانة", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields() {
        vehicleComboBox.setSelectedIndex(0);
        technicianComboBox.setSelectedIndex(0);
        descriptionArea.setText("");
        statusComboBox.setSelectedIndex(0);
        customerNotesArea.setText("");
        internalNotesArea.setText("");
        estimatedCostField.setText("0.0");
        actualCostField.setText("0.0");
        usedPartsTableModel.setRowCount(0);

        vehicleInfoLabel.setText("🚗 اختر سيارة");
        customerInfoLabel.setText("👤 معلومات العميل ستظهر هنا");
        technicianInfoLabel.setText("👨‍🔧 اختر فني");

        clearSelection();
    }

    private void clearSelection() {
        ordersTable.clearSelection();
        selectedOrderId = -1;

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        completeButton.setEnabled(false);
        addPartButton.setEnabled(false);
        saveButton.setEnabled(true);
    }

    private void updateTitle() {
        int total = orderDAO.getMaintenanceOrderCount();
        setTitle("إدارة طلبات الصيانة - العدد: " + total);
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