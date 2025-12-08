package com.carmaintenance.gui;

import com.carmaintenance.dao.TechnicianDAO;
import com.carmaintenance.model.Technician;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TechnicianRegistrationForm extends JFrame {

    // الحقول
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<String> specializationComboBox;
    private JTextField hireDateField;
    private JTextField salaryField;
    private JTextArea addressArea;
    private JComboBox<String> statusComboBox;

    // الأزرار
    private JButton saveButton;
    private JButton clearButton;
    private JButton viewAllButton;
    private JButton searchButton;
    private JButton statsButton;
    private JButton updateButton;
    private JButton deleteButton;

    // الجدول
    private JTable techniciansTable;
    private DefaultTableModel tableModel;

    // DAO
    private TechnicianDAO technicianDAO;

    // تنسيق التاريخ
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ID المحدد حاليًا
    private int selectedTechnicianId = -1;

    public TechnicianRegistrationForm() {
        // تهيئة DAO
        technicianDAO = new TechnicianDAO();

        // إعداد النافذة
        setupWindow();
        initComponents();
        layoutComponents();

        // تحميل البيانات
        loadAllTechnicians();
        updateTitle();
    }

    private void setupWindow() {
        setTitle("نظام صيانة السيارات - إدارة الفنيين");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // الحقول
        nameField = createTextField();
        phoneField = createTextField();
        emailField = createTextField();

        // ComboBox للتخصصات
        specializationComboBox = new JComboBox<>(new String[]{
                "اختر التخصص", "ميكانيكا محركات", "كهرباء سيارات", "سمكرة ودهان",
                "تكييف سيارات", "فك وتركيب", "صيانة عامة", "إلكترونيات سيارات"
        });

        hireDateField = createTextField();
        hireDateField.setText(LocalDate.now().format(dateFormatter));

        salaryField = createTextField();
        salaryField.setText("0.0");

        addressArea = new JTextArea(4, 30);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        // ComboBox للحالة
        statusComboBox = new JComboBox<>(new String[]{
                "Active", "On Leave", "Terminated"
        });

        // الأزرار
        saveButton = createButton("💾 حفظ الفني", new Color(40, 167, 69));
        updateButton = createButton("✏️ تحديث البيانات", new Color(255, 193, 7));
        deleteButton = createButton("🗑️ حذف الفني", new Color(220, 53, 69));
        clearButton = createButton("🧹 مسح الحقول", new Color(108, 117, 125));
        searchButton = createButton("🔍 بحث بالاسم", new Color(0, 123, 255));
        viewAllButton = createButton("👥 عرض الجميع", new Color(111, 66, 193));
        statsButton = createButton("📊 الإحصائيات", new Color(32, 201, 151));

        // إضافة المستمعين
        saveButton.addActionListener(e -> saveTechnician());
        updateButton.addActionListener(e -> updateTechnician());
        deleteButton.addActionListener(e -> deleteTechnician());
        clearButton.addActionListener(e -> clearFields());
        searchButton.addActionListener(e -> searchTechnicians());
        viewAllButton.addActionListener(e -> loadAllTechnicians());
        statsButton.addActionListener(e -> showStatistics());

        // إعداد الجدول
        String[] columns = {"ID", "الاسم", "الهاتف", "التخصص", "تاريخ التوظيف", "الراتب", "الحالة"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        techniciansTable = new JTable(tableModel);
        techniciansTable.setRowHeight(25);
        techniciansTable.setFont(new Font("Arial", Font.PLAIN, 12));
        techniciansTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // إضافة اختيار الصف
        techniciansTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = techniciansTable.getSelectedRow();
                if (selectedRow != -1) {
                    loadTechnicianFromTable(selectedRow);
                }
            }
        });

        // تهيئة زر التحديث والحذف
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // لوحة العنوان
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("👨‍🔧 إدارة الفنيين");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // لوحة الحقول (اليسار)
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 2),
                "بيانات الفني"
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // الصف 0: الاسم
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("👤 الاسم الكامل:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(nameField, gbc);

        // الصف 1: الهاتف
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("📱 رقم الهاتف:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(phoneField, gbc);

        // الصف 2: البريد الإلكتروني
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("📧 البريد الإلكتروني:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(emailField, gbc);

        // الصف 3: التخصص
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("🔧 التخصص:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(specializationComboBox, gbc);

        // الصف 4: تاريخ التوظيف
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("📅 تاريخ التوظيف (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(hireDateField, gbc);

        // الصف 5: الراتب
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("💰 الراتب:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(salaryField, gbc);

        // الصف 6: الحالة
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("📊 الحالة:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(statusComboBox, gbc);

        // الصف 7: العنوان
        gbc.gridx = 0; gbc.gridy = 7; gbc.anchor = GridBagConstraints.NORTHWEST;
        fieldsPanel.add(createLabel("📍 العنوان:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setPreferredSize(new Dimension(300, 80));
        fieldsPanel.add(addressScroll, gbc);

        add(fieldsPanel, BorderLayout.WEST);

        // لوحة الأزرار (الوسط)
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        buttonPanel.add(saveButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(viewAllButton);
        buttonPanel.add(statsButton);

        add(buttonPanel, BorderLayout.CENTER);

        // لوحة الجدول (اليمين)
        JScrollPane tableScroll = new JScrollPane(techniciansTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("قائمة الفنيين"));
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

    private void loadAllTechnicians() {
        // مسح الجدول الحالي
        tableModel.setRowCount(0);

        List<Technician> technicians = technicianDAO.getAllTechnicians();

        for (Technician technician : technicians) {
            Object[] row = {
                    technician.getId(),
                    technician.getName(),
                    technician.getPhone(),
                    technician.getSpecialization(),
                    technician.getHireDate() != null ? technician.getHireDate().format(dateFormatter) : "غير محدد",
                    String.format("%.2f", technician.getSalary()),
                    technician.getStatus()
            };
            tableModel.addRow(row);
        }

        updateTitle();
        clearSelection();
    }

    private void loadTechnicianFromTable(int rowIndex) {
        int id = (int) tableModel.getValueAt(rowIndex, 0);
        Technician technician = technicianDAO.getTechnicianById(id);

        if (technician != null) {
            loadTechnicianToForm(technician);
            selectedTechnicianId = id;

            // تفعيل أزرار التحديث والحذف
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
            saveButton.setEnabled(false);
        }
    }

    private void loadTechnicianToForm(Technician technician) {
        nameField.setText(technician.getName());
        phoneField.setText(technician.getPhone());
        emailField.setText(technician.getEmail() != null ? technician.getEmail() : "");

        // اختيار التخصص
        specializationComboBox.setSelectedItem(technician.getSpecialization());

        // تاريخ التوظيف
        if (technician.getHireDate() != null) {
            hireDateField.setText(technician.getHireDate().format(dateFormatter));
        }

        salaryField.setText(String.valueOf(technician.getSalary()));
        addressArea.setText(technician.getAddress() != null ? technician.getAddress() : "");
        statusComboBox.setSelectedItem(technician.getStatus());

        nameField.requestFocus();
    }

    private void saveTechnician() {
        System.out.println("\n👨‍🔧 === محاولة حفظ فني ===");

        // التحقق من البيانات
        if (!validateInput()) {
            return;
        }

        // إنشاء كائن الفني
        Technician technician = createTechnicianFromForm();
        if (technician == null) {
            return;
        }

        try {
            // حفظ في قاعدة البيانات
            boolean success = technicianDAO.addTechnician(technician);

            if (success) {
                showSuccess("✅ تم حفظ الفني بنجاح!\n" +
                        "🆔 رقم الفني: " + technician.getId() + "\n" +
                        "👤 الاسم: " + technician.getName() + "\n" +
                        "🔧 التخصص: " + technician.getSpecialization());

                clearFields();
                loadAllTechnicians();

            } else {
                showError("❌ فشل في حفظ الفني!");
            }

        } catch (Exception e) {
            showError("❌ حدث خطأ غير متوقع: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateTechnician() {
        if (selectedTechnicianId == -1) {
            showError("⚠️ يرجى اختيار فني للتحديث");
            return;
        }

        System.out.println("\n✏️ === محاولة تحديث فني ===");

        // التحقق من البيانات
        if (!validateInput()) {
            return;
        }

        // إنشاء كائن الفني
        Technician technician = createTechnicianFromForm();
        if (technician == null) {
            return;
        }

        technician.setId(selectedTechnicianId);

        try {
            // تحديث في قاعدة البيانات
            boolean success = technicianDAO.updateTechnician(technician);

            if (success) {
                showSuccess("✅ تم تحديث بيانات الفني بنجاح!\n" +
                        "🆔 رقم الفني: " + technician.getId() + "\n" +
                        "👤 الاسم: " + technician.getName());

                clearFields();
                loadAllTechnicians();

            } else {
                showError("❌ فشل في تحديث الفني!");
            }

        } catch (Exception e) {
            showError("❌ حدث خطأ غير متوقع: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteTechnician() {
        if (selectedTechnicianId == -1) {
            showError("⚠️ يرجى اختيار فني للحذف");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "هل أنت متأكد من حذف الفني؟\nهذا الإجراء لا يمكن التراجع عنه.",
                "تأكيد الحذف", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("\n🗑️ === محاولة حذف فني ===");

            boolean success = technicianDAO.deleteTechnician(selectedTechnicianId);

            if (success) {
                showSuccess("✅ تم حذف الفني بنجاح!");
                clearFields();
                loadAllTechnicians();
            } else {
                showError("❌ فشل في حذف الفني!");
            }
        }
    }

    private boolean validateInput() {
        // التحقق من الاسم
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("⚠️ يرجى إدخال اسم الفني");
            nameField.requestFocus();
            return false;
        }

        // التحقق من الهاتف
        String phone = phoneField.getText().trim();
        if (phone.isEmpty()) {
            showError("⚠️ يرجى إدخال رقم الهاتف");
            phoneField.requestFocus();
            return false;
        }

        // التحقق من التخصص
        String specialization = (String) specializationComboBox.getSelectedItem();
        if (specialization == null || "اختر التخصص".equals(specialization)) {
            showError("⚠️ يرجى اختيار تخصص الفني");
            specializationComboBox.requestFocus();
            return false;
        }

        // التحقق من الراتب
        String salaryText = salaryField.getText().trim();
        try {
            double salary = Double.parseDouble(salaryText);
            if (salary < 0) {
                showError("⚠️ الراتب يجب أن يكون رقم موجب");
                salaryField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("⚠️ الراتب يجب أن يكون رقم صحيح");
            salaryField.requestFocus();
            return false;
        }

        return true;
    }

    private Technician createTechnicianFromForm() {
        try {
            Technician technician = new Technician();

            technician.setName(nameField.getText().trim());
            technician.setPhone(phoneField.getText().trim());
            technician.setEmail(emailField.getText().trim());
            technician.setSpecialization((String) specializationComboBox.getSelectedItem());

            // تاريخ التوظيف
            String hireDateText = hireDateField.getText().trim();
            if (!hireDateText.isEmpty()) {
                try {
                    LocalDate hireDate = LocalDate.parse(hireDateText, dateFormatter);
                    technician.setHireDate(hireDate);
                } catch (DateTimeParseException e) {
                    showError("⚠️ تاريخ التوظيف غير صحيح!\nيجب أن يكون بالتنسيق yyyy-MM-dd");
                    hireDateField.requestFocus();
                    return null;
                }
            }

            // الراتب
            try {
                technician.setSalary(Double.parseDouble(salaryField.getText().trim()));
            } catch (NumberFormatException e) {
                technician.setSalary(0.0);
            }

            technician.setAddress(addressArea.getText().trim());
            technician.setStatus((String) statusComboBox.getSelectedItem());

            return technician;

        } catch (Exception e) {
            showError("❌ خطأ في إنشاء كائن الفني: " + e.getMessage());
            return null;
        }
    }

    private void searchTechnicians() {
        String name = JOptionPane.showInputDialog(this,
                "أدخل اسم الفني للبحث:", "بحث عن فني", JOptionPane.QUESTION_MESSAGE);

        if (name != null && !name.trim().isEmpty()) {
            // مسح الجدول الحالي
            tableModel.setRowCount(0);

            List<Technician> technicians = technicianDAO.searchTechniciansByName(name.trim());

            if (technicians.isEmpty()) {
                showInfo("🔍 لم يتم العثور على فنيين بهذا الاسم");
                return;
            }

            for (Technician technician : technicians) {
                Object[] row = {
                        technician.getId(),
                        technician.getName(),
                        technician.getPhone(),
                        technician.getSpecialization(),
                        technician.getHireDate() != null ? technician.getHireDate().format(dateFormatter) : "غير محدد",
                        String.format("%.2f", technician.getSalary()),
                        technician.getStatus()
                };
                tableModel.addRow(row);
            }

            showInfo("🔍 تم العثور على " + technicians.size() + " فني");
        }
    }

    private void showStatistics() {
        String stats = technicianDAO.getStatistics();

        JTextArea textArea = new JTextArea(stats, 20, 40);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(248, 249, 250));

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane,
                "📊 إحصائيات الفنيين", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        specializationComboBox.setSelectedIndex(0);
        hireDateField.setText(LocalDate.now().format(dateFormatter));
        salaryField.setText("0.0");
        addressArea.setText("");
        statusComboBox.setSelectedIndex(0);
        nameField.requestFocus();

        clearSelection();
    }

    private void clearSelection() {
        techniciansTable.clearSelection();
        selectedTechnicianId = -1;

        // تعطيل أزرار التحديث والحذف
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        saveButton.setEnabled(true);
    }

    private void updateTitle() {
        int total = technicianDAO.getTechnicianCount();
        int active = technicianDAO.getActiveTechnicianCount();
        setTitle("إدارة الفنيين - الإجمالي: " + total + " | النشطين: " + active);
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