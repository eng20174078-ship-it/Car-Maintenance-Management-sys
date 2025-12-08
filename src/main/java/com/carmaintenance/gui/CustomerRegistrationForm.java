package com.carmaintenance.gui;

import com.carmaintenance.dao.CustomerDAO;
import com.carmaintenance.model.Customer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CustomerRegistrationForm extends JFrame {

    private CustomerDAO customerDAO;

    // مكونات الواجهة المحسنة
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea addressArea;

    // الأزرار
    private JButton saveButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton searchButton;
    private JButton refreshButton;
    private JButton exportButton;

    // الجدول
    private JTable customersTable;
    private DefaultTableModel tableModel;

    // ID المحدد
    private int selectedCustomerId = -1;

    // لوحات إضافية
    private JPanel customerInfoPanel;
    private JPanel actionButtonsPanel;
    private JPanel searchPanel;

    public CustomerRegistrationForm() {
        // تطبيق السمة
        ThemeManager.applyTheme();

        // تهيئة DAO
        customerDAO = new CustomerDAO();

        // إعداد النافذة
        setupWindow();
        initComponents();
        layoutComponents();

        // تحميل البيانات
        loadAllCustomers();
        updateTitle();
    }

    private void setupWindow() {
        setTitle("👥 إدارة العملاء - نظام صيانة السيارات");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // إضافة أيقونة
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(
                    getClass().getResource("/customers.png")));
        } catch (Exception e) {
            // تجاهل الخطأ
        }
    }

    private void initComponents() {
        // إنشاء حقول النص المحسنة
        nameField = ThemeManager.createStyledTextField(25);
        phoneField = ThemeManager.createStyledTextField(25);
        emailField = ThemeManager.createStyledTextField(25);

        addressArea = new JTextArea(4, 25);
        addressArea.setFont(ThemeManager.BODY_FONT);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        addressArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // إنشاء الأزرار
        saveButton = ThemeManager.createStyledButton("💾 حفظ", ThemeManager.SECONDARY_COLOR);
        updateButton = ThemeManager.createStyledButton("✏️ تحديث", ThemeManager.ACCENT_COLOR);
        deleteButton = ThemeManager.createStyledButton("🗑️ حذف", ThemeManager.DANGER_COLOR);
        clearButton = ThemeManager.createStyledButton("🧹 مسح", ThemeManager.INFO_COLOR);
        searchButton = ThemeManager.createStyledButton("🔍 بحث", ThemeManager.PRIMARY_COLOR);
        refreshButton = ThemeManager.createStyledButton("🔄 تحديث", ThemeManager.INFO_COLOR);
        exportButton = ThemeManager.createStyledButton("📤 تصدير", ThemeManager.DARK_COLOR);

        // إضافة المستمعين
        saveButton.addActionListener(e -> saveCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        clearButton.addActionListener(e -> clearFields());
        searchButton.addActionListener(e -> searchCustomer());
        refreshButton.addActionListener(e -> loadAllCustomers());
        exportButton.addActionListener(e -> exportData());

        // إعداد نموذج الجدول
        String[] columns = {"ID", "الاسم", "الهاتف", "البريد الإلكتروني", "العنوان", "تاريخ التسجيل"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customersTable = new JTable(tableModel);
        customersTable.setFont(ThemeManager.BODY_FONT);
        customersTable.setRowHeight(35);
        customersTable.getTableHeader().setFont(ThemeManager.BUTTON_FONT);
        customersTable.getTableHeader().setBackground(ThemeManager.DARK_COLOR);
        customersTable.getTableHeader().setForeground(Color.WHITE);
        customersTable.setSelectionBackground(ThemeManager.PRIMARY_COLOR);
        customersTable.setSelectionForeground(Color.WHITE);
        customersTable.setGridColor(new Color(222, 226, 230));
        customersTable.setShowGrid(true);

        // تلوين الصفوف بالتناوب
        customersTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(248, 249, 250));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }

                return c;
            }
        });

        // إضافة مستمع لاختيار الصف
        customersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = customersTable.getSelectedRow();
                if (selectedRow != -1) {
                    loadCustomerFromTable(selectedRow);
                }
            }
        });

        // تعطيل أزرار التحديث والحذف في البداية
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void layoutComponents() {
        // استخدام BorderLayout مع هوامش
        setLayout(new BorderLayout(0, 0));

        // شريط العنوان
        add(createHeaderPanel(), BorderLayout.NORTH);

        // المحتوى الرئيسي في TabbedPane
        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(ThemeManager.BUTTON_FONT);

        // تبويب تسجيل العميل
        tabbedPane.addTab("➕ تسجيل عميل جديد", createRegistrationTab());

        // تبويب عرض العملاء
        tabbedPane.addTab("👥 عرض جميع العملاء", createViewTab());

        // تبويب البحث
        tabbedPane.addTab("🔍 بحث عن عميل", createSearchTab());

        add(tabbedPane, BorderLayout.CENTER);

        // شريط الحالة
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ThemeManager.DARK_COLOR);
        header.setPreferredSize(new Dimension(0, 60));
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        // العنوان
        JLabel titleLabel = new JLabel("👥 إدارة العملاء");
        titleLabel.setFont(ThemeManager.HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);

        // إحصائيات سريعة
        JLabel statsLabel = new JLabel("عدد العملاء: " + customerDAO.getCustomerCount());
        statsLabel.setFont(ThemeManager.BODY_FONT);
        statsLabel.setForeground(Color.WHITE);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(statsLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createRegistrationTab() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(ThemeManager.LIGHT_COLOR);

        // لوحة معلومات العميل
        customerInfoPanel = ThemeManager.createStyledPanel("معلومات العميل");
        customerInfoPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // الصف 0: الاسم
        gbc.gridx = 0; gbc.gridy = 0;
        customerInfoPanel.add(ThemeManager.createStyledLabel("👤 الاسم الكامل:",
                ThemeManager.SUBTITLE_FONT, ThemeManager.DARK_COLOR), gbc);
        gbc.gridx = 1;
        customerInfoPanel.add(nameField, gbc);

        // الصف 1: الهاتف
        gbc.gridx = 0; gbc.gridy = 1;
        customerInfoPanel.add(ThemeManager.createStyledLabel("📱 رقم الهاتف:",
                ThemeManager.SUBTITLE_FONT, ThemeManager.DARK_COLOR), gbc);
        gbc.gridx = 1;
        customerInfoPanel.add(phoneField, gbc);

        // الصف 2: البريد الإلكتروني
        gbc.gridx = 0; gbc.gridy = 2;
        customerInfoPanel.add(ThemeManager.createStyledLabel("📧 البريد الإلكتروني:",
                ThemeManager.SUBTITLE_FONT, ThemeManager.DARK_COLOR), gbc);
        gbc.gridx = 1;
        customerInfoPanel.add(emailField, gbc);

        // الصف 3: العنوان
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        customerInfoPanel.add(ThemeManager.createStyledLabel("🏠 العنوان:",
                ThemeManager.SUBTITLE_FONT, ThemeManager.DARK_COLOR), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setPreferredSize(new Dimension(300, 100));
        customerInfoPanel.add(addressScroll, gbc);

        panel.add(customerInfoPanel, BorderLayout.CENTER);

        // لوحة الأزرار
        actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        actionButtonsPanel.setBackground(ThemeManager.LIGHT_COLOR);
        actionButtonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        actionButtonsPanel.add(saveButton);
        actionButtonsPanel.add(updateButton);
        actionButtonsPanel.add(deleteButton);
        actionButtonsPanel.add(clearButton);

        panel.add(actionButtonsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createViewTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(ThemeManager.LIGHT_COLOR);

        // لوحة التحكم في الجدول
        JPanel tableControlPanel = new JPanel(new BorderLayout());
        tableControlPanel.setBackground(ThemeManager.LIGHT_COLOR);

        JLabel tableTitle = new JLabel("📋 قائمة العملاء المسجلين");
        tableTitle.setFont(ThemeManager.TITLE_FONT);
        tableTitle.setForeground(ThemeManager.DARK_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(ThemeManager.LIGHT_COLOR);
        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);

        tableControlPanel.add(tableTitle, BorderLayout.WEST);
        tableControlPanel.add(buttonPanel, BorderLayout.EAST);

        // الجدول
        JScrollPane tableScroll = new JScrollPane(customersTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230), 1));
        tableScroll.getViewport().setBackground(Color.WHITE);

        // معلومات الجدول
        JLabel tableInfo = new JLabel("👈 اختر عميلاً من القائمة لتعديل أو حذف بياناته");
        tableInfo.setFont(ThemeManager.SUBTITLE_FONT);
        tableInfo.setForeground(ThemeManager.INFO_COLOR);
        tableInfo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        panel.add(tableControlPanel, BorderLayout.NORTH);
        panel.add(tableScroll, BorderLayout.CENTER);
        panel.add(tableInfo, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createSearchTab() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(ThemeManager.LIGHT_COLOR);

        // لوحة البحث
        searchPanel = ThemeManager.createStyledPanel("بحث عن عميل");
        searchPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel searchLabel = new JLabel("🔍 أدخل اسم العميل أو رقم الهاتف:");
        searchLabel.setFont(ThemeManager.TITLE_FONT);
        searchLabel.setForeground(ThemeManager.DARK_COLOR);

        JTextField searchField = ThemeManager.createStyledTextField(30);

        JButton searchActionButton = ThemeManager.createStyledButton("بحث", ThemeManager.PRIMARY_COLOR);
        searchActionButton.addActionListener(e -> performSearch(searchField.getText()));

        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(searchLabel, gbc);

        gbc.gridy = 1;
        searchPanel.add(searchField, gbc);

        gbc.gridy = 2;
        searchPanel.add(searchActionButton, gbc);

        panel.add(searchPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(ThemeManager.DARK_COLOR);
        statusBar.setPreferredSize(new Dimension(0, 30));
        statusBar.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel statusLabel = new JLabel("جاهز");
        statusLabel.setFont(ThemeManager.BODY_FONT);
        statusLabel.setForeground(Color.WHITE);

        JLabel recordCount = new JLabel("السجلات: 0");
        recordCount.setFont(ThemeManager.BODY_FONT);
        recordCount.setForeground(Color.WHITE);

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(recordCount, BorderLayout.EAST);

        return statusBar;
    }

    private void loadAllCustomers() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerDAO.getAllCustomers();

        for (Customer customer : customers) {
            Object[] row = {
                    customer.getId(),
                    customer.getName(),
                    customer.getPhone(),
                    customer.getEmail() != null ? customer.getEmail() : "غير محدد",
                    customer.getAddress() != null ?
                            (customer.getAddress().length() > 30 ?
                                    customer.getAddress().substring(0, 30) + "..." : customer.getAddress()) : "غير محدد",
                    "2025-12-10" // هنا يجب استخدام التاريخ الحقيقي من قاعدة البيانات
            };
            tableModel.addRow(row);
        }

        updateTitle();
        clearSelection();
        updateStatusBar("تم تحميل " + customers.size() + " عميل");
    }

    private void loadCustomerFromTable(int rowIndex) {
        int id = (int) tableModel.getValueAt(rowIndex, 0);
        Customer customer = customerDAO.getCustomerById(id);

        if (customer != null) {
            nameField.setText(customer.getName());
            phoneField.setText(customer.getPhone());
            emailField.setText(customer.getEmail() != null ? customer.getEmail() : "");
            addressArea.setText(customer.getAddress() != null ? customer.getAddress() : "");

            selectedCustomerId = id;

            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
            saveButton.setEnabled(false);

            updateStatusBar("تم تحميل بيانات العميل: " + customer.getName());
        }
    }

    private void saveCustomer() {
        if (!validateInput()) return;

        Customer customer = new Customer(
                nameField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                addressArea.getText().trim()
        );

        boolean success = customerDAO.addCustomer(customer);

        if (success) {
            showMessage("✅ تم حفظ العميل بنجاح!", "نجاح", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadAllCustomers();
        } else {
            showMessage("❌ فشل في حفظ العميل!", "خطأ", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateCustomer() {
        if (selectedCustomerId == -1) {
            showMessage("⚠️ يرجى اختيار عميل للتحديث", "تحذير", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!validateInput()) return;

        Customer customer = new Customer(
                selectedCustomerId,
                nameField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                addressArea.getText().trim()
        );

        boolean success = customerDAO.updateCustomer(customer);

        if (success) {
            showMessage("✅ تم تحديث بيانات العميل بنجاح!", "نجاح", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            loadAllCustomers();
        } else {
            showMessage("❌ فشل في تحديث العميل!", "خطأ", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteCustomer() {
        if (selectedCustomerId == -1) {
            showMessage("⚠️ يرجى اختيار عميل للحذف", "تحذير", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "هل أنت متأكد من حذف العميل؟\nهذا الإجراء لا يمكن التراجع عنه.",
                "تأكيد الحذف", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = customerDAO.deleteCustomer(selectedCustomerId);

            if (success) {
                showMessage("✅ تم حذف العميل بنجاح!", "نجاح", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
                loadAllCustomers();
            } else {
                showMessage("❌ فشل في حذف العميل!", "خطأ", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void searchCustomer() {
        String searchTerm = JOptionPane.showInputDialog(this,
                "أدخل اسم العميل أو رقم الهاتف:", "بحث عن عميل", JOptionPane.QUESTION_MESSAGE);

        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            performSearch(searchTerm.trim());
        }
    }

    private void performSearch(String searchTerm) {
        tableModel.setRowCount(0);

        // البحث في جميع العملاء
        List<Customer> allCustomers = customerDAO.getAllCustomers();
        int foundCount = 0;

        for (Customer customer : allCustomers) {
            if (customer.getName().toLowerCase().contains(searchTerm.toLowerCase()) ||
                    customer.getPhone().contains(searchTerm)) {

                Object[] row = {
                        customer.getId(),
                        customer.getName(),
                        customer.getPhone(),
                        customer.getEmail() != null ? customer.getEmail() : "غير محدد",
                        customer.getAddress() != null ?
                                (customer.getAddress().length() > 30 ?
                                        customer.getAddress().substring(0, 30) + "..." : customer.getAddress()) : "غير محدد",
                        "2025-12-10"
                };
                tableModel.addRow(row);
                foundCount++;
            }
        }

        if (foundCount == 0) {
            showMessage("🔍 لم يتم العثور على عملاء بهذا الاسم", "نتيجة البحث",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            updateStatusBar("تم العثور على " + foundCount + " عميل");
        }
    }

    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("📤 تصدير بيانات العملاء");
        fileChooser.setSelectedFile(new java.io.File("العملاء_" +
                java.time.LocalDate.now() + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // هنا يمكن إضافة كود التصدير الفعلي
            showMessage("سيتم إضافة خاصية التصدير في نسخة لاحقة",
                    "قيد التطوير", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            showMessage("⚠️ يرجى إدخال اسم العميل", "تحذير", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        if (phoneField.getText().trim().isEmpty()) {
            showMessage("⚠️ يرجى إدخال رقم الهاتف", "تحذير", JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return false;
        }

        return true;
    }

    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressArea.setText("");
        nameField.requestFocus();

        clearSelection();
        updateStatusBar("جاهز");
    }

    private void clearSelection() {
        customersTable.clearSelection();
        selectedCustomerId = -1;

        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        saveButton.setEnabled(true);
    }

    private void updateTitle() {
        setTitle("👥 إدارة العملاء (" + customerDAO.getCustomerCount() + " عميل) - نظام صيانة السيارات");
    }

    private void updateStatusBar(String message) {
        // في التطبيق الحقيقي، هنا يتم تحديث شريط الحالة
        System.out.println("📢 " + message);
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}