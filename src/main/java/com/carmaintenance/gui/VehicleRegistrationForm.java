package com.carmaintenance.gui;

import com.carmaintenance.dao.CustomerDAO;
import com.carmaintenance.dao.VehicleDAO;
import com.carmaintenance.model.Customer;
import com.carmaintenance.model.Vehicle;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class VehicleRegistrationForm extends JFrame {

    private JTextField plateField;
    private JTextField modelField;
    private JTextField yearField;
    private JComboBox<Customer> ownerComboBox;
    private JTextField colorField;
    private JTextField engineField;
    private JTextArea notesArea;
    private JButton saveButton;
    private JButton clearButton;
    private JButton viewAllButton;
    private JButton searchButton;

    private VehicleDAO vehicleDAO;
    private CustomerDAO customerDAO;

    private JTable vehiclesTable;
    private DefaultTableModel tableModel;

    public VehicleRegistrationForm() {
        // تهيئة DAO
        vehicleDAO = new VehicleDAO();
        customerDAO = new CustomerDAO();

        // إعداد النافذة
        setupWindow();
        initComponents();
        layoutComponents();
        loadCustomers();
        loadAllVehicles();

        updateTitle();
    }

    private void setupWindow() {
        setTitle("نظام صيانة السيارات - تسجيل سيارة جديدة");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // الحقول
        plateField = createTextField();
        modelField = createTextField();
        yearField = createTextField();
        ownerComboBox = new JComboBox<>();
        colorField = createTextField();
        engineField = createTextField();
        notesArea = new JTextArea(4, 30);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        // تحميل العملاء في ComboBox
        loadCustomers();

        // الأزرار
        saveButton = createButton("💾 حفظ السيارة", new Color(40, 167, 69));
        clearButton = createButton("🗑️ مسح الحقول", new Color(220, 53, 69));
        viewAllButton = createButton("🚗 عرض جميع السيارات", new Color(0, 123, 255));
        searchButton = createButton("🔍 بحث عن سيارة", new Color(255, 193, 7));

        // إضافة المستمعين
        saveButton.addActionListener(e -> saveVehicle());
        clearButton.addActionListener(e -> clearFields());
        viewAllButton.addActionListener(e -> viewAllVehicles());
        searchButton.addActionListener(e -> searchVehicle());

        // إعداد الجدول
        String[] columns = {"رقم اللوحة", "الموديل", "السنة", "المالك", "الهاتف", "اللون", "المحرك"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // جعل الجدول للقراءة فقط
            }
        };

        vehiclesTable = new JTable(tableModel);
        vehiclesTable.setRowHeight(25);
        vehiclesTable.setFont(new Font("Arial", Font.PLAIN, 12));
        vehiclesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // إضافة اختيار الصف عند النقر
        vehiclesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = vehiclesTable.getSelectedRow();
                if (selectedRow != -1) {
                    loadVehicleFromTable(selectedRow);
                }
            }
        });
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // لوحة العنوان
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("🚗 تسجيل سيارة جديدة");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // لوحة الحقول الرئيسية
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 2),
                "معلومات السيارة"
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // الصف 0: رقم اللوحة
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("🔢 رقم اللوحة:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(plateField, gbc);

        // الصف 1: الموديل
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("🚙 الموديل:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(modelField, gbc);

        // الصف 2: السنة
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("📅 سنة الصنع:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(yearField, gbc);

        // الصف 3: المالك
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("👤 المالك:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(ownerComboBox, gbc);

        // الصف 4: اللون
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("🎨 اللون:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(colorField, gbc);

        // الصف 5: نوع المحرك
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("⚙️ نوع المحرك:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(engineField, gbc);

        // الصف 6: ملاحظات
        gbc.gridx = 0; gbc.gridy = 6; gbc.anchor = GridBagConstraints.NORTHWEST;
        fieldsPanel.add(createLabel("📝 ملاحظات:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(300, 80));
        fieldsPanel.add(notesScroll, gbc);

        // لوحة الأزرار
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(viewAllButton);

        add(fieldsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // لوحة الجدول (في اليمين أو أسفل)
        JScrollPane tableScroll = new JScrollPane(vehiclesTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("قائمة السيارات المسجلة"));
        add(tableScroll, BorderLayout.EAST);
        tableScroll.setPreferredSize(new Dimension(500, 0));
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(20);
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        return field;
    }

    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(73, 80, 87));
        return label;
    }

    private void loadCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();
        ownerComboBox.removeAllItems();

        // إضافة عنصر افتراضي
        ownerComboBox.addItem(new Customer(0, "اختر المالك", "", "", ""));

        for (Customer customer : customers) {
            ownerComboBox.addItem(customer);
        }

        // عرض الاسم والهاتف في ComboBox
        ownerComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (value instanceof Customer) {
                    Customer customer = (Customer) value;
                    if (customer.getId() == 0) {
                        setText(customer.getName());
                    } else {
                        setText(customer.getName() + " - " + customer.getPhone());
                    }
                }
                return this;
            }
        });
    }

    private void loadAllVehicles() {
        // مسح الجدول الحالي
        tableModel.setRowCount(0);

        List<Vehicle> vehicles = vehicleDAO.getAllVehicles();

        for (Vehicle vehicle : vehicles) {
            // جلب معلومات المالك
            Customer owner = customerDAO.getCustomerById(vehicle.getOwnerId());
            String ownerName = (owner != null) ? owner.getName() : "غير معروف";
            String ownerPhone = (owner != null) ? owner.getPhone() : "";

            // إضافة الصف للجدول
            Object[] row = {
                    vehicle.getPlateNumber(),
                    vehicle.getModel(),
                    vehicle.getYear(),
                    ownerName,
                    ownerPhone,
                    vehicle.getColor(),
                    vehicle.getEngineType()
            };
            tableModel.addRow(row);
        }

        updateTitle();
    }

    private void saveVehicle() {
        System.out.println("\n🚗 === محاولة حفظ سيارة ===");

        // جمع البيانات
        String plateNumber = plateField.getText().trim().toUpperCase();
        String model = modelField.getText().trim();
        String yearText = yearField.getText().trim();
        Customer selectedOwner = (Customer) ownerComboBox.getSelectedItem();
        String color = colorField.getText().trim();
        String engineType = engineField.getText().trim();
        String notes = notesArea.getText().trim();

        // التحقق من البيانات الإجبارية
        if (plateNumber.isEmpty()) {
            showError("⚠️ يرجى إدخال رقم اللوحة");
            plateField.requestFocus();
            return;
        }

        if (model.isEmpty()) {
            showError("⚠️ يرجى إدخال موديل السيارة");
            modelField.requestFocus();
            return;
        }

        if (yearText.isEmpty()) {
            showError("⚠️ يرجى إدخال سنة الصنع");
            yearField.requestFocus();
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearText);
            if (year < 1900 || year > 2025) {
                showError("⚠️ سنة الصنع يجب أن تكون بين 1900 و 2025");
                yearField.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            showError("⚠️ سنة الصنع يجب أن تكون رقماً");
            yearField.requestFocus();
            return;
        }

        if (selectedOwner == null || selectedOwner.getId() == 0) {
            showError("⚠️ يرجى اختيار مالك السيارة");
            ownerComboBox.requestFocus();
            return;
        }

        // التحقق من تكرار رقم اللوحة
        if (vehicleDAO.isPlateExists(plateNumber)) {
            showError("❌ رقم اللوحة مسجل مسبقاً!");
            plateField.selectAll();
            plateField.requestFocus();
            return;
        }

        // إنشاء كائن السيارة
        Vehicle vehicle = new Vehicle();
        vehicle.setPlateNumber(plateNumber);
        vehicle.setModel(model);
        vehicle.setYear(year);
        vehicle.setOwnerId(selectedOwner.getId());
        vehicle.setColor(color.isEmpty() ? "غير محدد" : color);
        vehicle.setEngineType(engineType.isEmpty() ? "غير محدد" : engineType);
        vehicle.setNotes(notes);

        try {
            // حفظ في قاعدة البيانات
            boolean success = vehicleDAO.addVehicle(vehicle);

            if (success) {
                showSuccess("✅ تم حفظ السيارة بنجاح!\n" +
                        "🔢 رقم اللوحة: " + vehicle.getPlateNumber() + "\n" +
                        "🚙 الموديل: " + vehicle.getModel() + "\n" +
                        "👤 المالك: " + selectedOwner.getName());

                clearFields();
                loadAllVehicles(); // تحديث الجدول

            } else {
                showError("❌ فشل في حفظ السيارة!");
            }

        } catch (Exception e) {
            showError("❌ حدث خطأ غير متوقع: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        plateField.setText("");
        modelField.setText("");
        yearField.setText("");
        ownerComboBox.setSelectedIndex(0);
        colorField.setText("");
        engineField.setText("");
        notesArea.setText("");
        plateField.requestFocus();

        // إلغاء تحديد الصف في الجدول
        vehiclesTable.clearSelection();

        System.out.println("🗑️ تم مسح جميع الحقول");
    }

    private void viewAllVehicles() {
        loadAllVehicles();
        showInfo("📊 عدد السيارات المسجلة: " + vehicleDAO.getVehicleCount());
    }

    private void searchVehicle() {
        String plateNumber = JOptionPane.showInputDialog(this,
                "أدخل رقم لوحة السيارة:", "بحث عن سيارة", JOptionPane.QUESTION_MESSAGE);

        if (plateNumber != null && !plateNumber.trim().isEmpty()) {
            Vehicle vehicle = vehicleDAO.getVehicleByPlate(plateNumber.trim().toUpperCase());

            if (vehicle != null) {
                // عرض معلومات السيارة
                StringBuilder info = new StringBuilder();
                info.append("✅ تم العثور على السيارة:\n\n");
                info.append("🔢 رقم اللوحة: ").append(vehicle.getPlateNumber()).append("\n");
                info.append("🚙 الموديل: ").append(vehicle.getModel()).append("\n");
                info.append("📅 السنة: ").append(vehicle.getYear()).append("\n");

                Customer owner = customerDAO.getCustomerById(vehicle.getOwnerId());
                if (owner != null) {
                    info.append("👤 المالك: ").append(owner.getName()).append("\n");
                    info.append("📱 هاتف المالك: ").append(owner.getPhone()).append("\n");
                }

                info.append("🎨 اللون: ").append(vehicle.getColor()).append("\n");
                info.append("⚙️ نوع المحرك: ").append(vehicle.getEngineType()).append("\n");
                info.append("📝 الملاحظات: ").append(vehicle.getNotes()).append("\n");

                JTextArea textArea = new JTextArea(info.toString(), 15, 40);
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

                JScrollPane scrollPane = new JScrollPane(textArea);
                JOptionPane.showMessageDialog(this, scrollPane,
                        "معلومات السيارة", JOptionPane.INFORMATION_MESSAGE);

                // تحميل البيانات في الحقول للتعديل
                loadVehicleToForm(vehicle);

            } else {
                showError("❌ لا توجد سيارة بهذا الرقم!");
            }
        }
    }

    private void loadVehicleToForm(Vehicle vehicle) {
        plateField.setText(vehicle.getPlateNumber());
        modelField.setText(vehicle.getModel());
        yearField.setText(String.valueOf(vehicle.getYear()));
        colorField.setText(vehicle.getColor());
        engineField.setText(vehicle.getEngineType());
        notesArea.setText(vehicle.getNotes());

        // اختيار المالك في ComboBox
        for (int i = 0; i < ownerComboBox.getItemCount(); i++) {
            Customer customer = ownerComboBox.getItemAt(i);
            if (customer.getId() == vehicle.getOwnerId()) {
                ownerComboBox.setSelectedIndex(i);
                break;
            }
        }

        plateField.requestFocus();
    }

    private void loadVehicleFromTable(int rowIndex) {
        String plateNumber = (String) tableModel.getValueAt(rowIndex, 0);
        Vehicle vehicle = vehicleDAO.getVehicleByPlate(plateNumber);

        if (vehicle != null) {
            loadVehicleToForm(vehicle);
        }
    }

    private void updateTitle() {
        int count = vehicleDAO.getVehicleCount();
        setTitle("نظام صيانة السيارات - تسجيل سيارة (السيارات: " + count + ")");
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