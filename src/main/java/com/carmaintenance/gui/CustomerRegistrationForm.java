package com.carmaintenance.gui;

import com.carmaintenance.dao.CustomerDAO;
import com.carmaintenance.dao.DatabaseConnection;
import com.carmaintenance.dao.DatabaseCreator;
import com.carmaintenance.model.Customer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.List;

public class CustomerRegistrationForm extends JFrame {

    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea addressArea;
    private JButton saveButton;
    private JButton clearButton;
    private JButton testButton;
    private JButton viewAllButton;

    private CustomerDAO customerDAO;

    public CustomerRegistrationForm() {
        // تهيئة DAO
        customerDAO = new CustomerDAO();

        // التأكد من وجود الجدول
        customerDAO.createTableIfNotExists();
        DatabaseCreator.createDatabaseIfNotExists();

        // إعداد النافذة
        setupWindow();
        initComponents();
        layoutComponents();

        // تحديث العنوان بعد العد
        updateTitle();
    }

    private void setupWindow() {
        setTitle("نظام إدارة صيانة السيارات - تسجيل عميل جديد");
        setSize(550, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // أيقونة النافذة
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage(
                    getClass().getResource("/icon.png")));
        } catch (Exception e) {
            // تجاهل الخطأ إذا لم توجد الأيقونة
        }
    }

    private void initComponents() {
        // الحقول
        nameField = createTextField();
        phoneField = createTextField();
        emailField = createTextField();
        addressArea = new JTextArea(5, 25);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        // الأزرار
        saveButton = createButton("💾 حفظ العميل", new Color(40, 167, 69));
        clearButton = createButton("🗑️ مسح الحقول", new Color(220, 53, 69));
        testButton = createButton("🔗 اختبار الاتصال", new Color(0, 123, 255));
        viewAllButton = createButton("👥 عرض العملاء", new Color(108, 117, 125));

        // إضافة المستمعين
        saveButton.addActionListener(e -> saveCustomer());
        clearButton.addActionListener(e -> clearFields());
        testButton.addActionListener(e -> testConnection());
        viewAllButton.addActionListener(e -> viewAllCustomers());

        // إضافة اختصار Enter للحفظ
        getRootPane().setDefaultButton(saveButton);
    }

    private JTextField createTextField() {
        JTextField field = new JTextField(25);
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

    private void layoutComponents() {
        // استخدام BorderLayout مع هوامش
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // لوحة العنوان
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("📝 تسجيل بيانات عميل جديد");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(new Color(33, 37, 41));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // لوحة الحقول الرئيسية
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 2),
                "معلومات العميل"
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // الصف 0: الاسم
        gbc.gridx = 0; gbc.gridy = 0;
        fieldsPanel.add(createLabel("👤 الاسم الكامل:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(nameField, gbc);

        // الصف 1: الهاتف
        gbc.gridx = 0; gbc.gridy = 1;
        fieldsPanel.add(createLabel("📱 رقم الهاتف:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(phoneField, gbc);

        // الصف 2: البريد الإلكتروني
        gbc.gridx = 0; gbc.gridy = 2;
        fieldsPanel.add(createLabel("📧 البريد الإلكتروني:"), gbc);
        gbc.gridx = 1;
        fieldsPanel.add(emailField, gbc);

        // الصف 3: العنوان
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        fieldsPanel.add(createLabel("🏠 العنوان:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane addressScroll = new JScrollPane(addressArea);
        addressScroll.setPreferredSize(new Dimension(300, 100));
        fieldsPanel.add(addressScroll, gbc);

        add(fieldsPanel, BorderLayout.CENTER);

        // لوحة الأزرار السفلية
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(testButton);
        buttonPanel.add(viewAllButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(new Color(73, 80, 87));
        return label;
    }

    private void saveCustomer() {
        System.out.println("\n💾 === محاولة حفظ عميل ===");

        // جمع البيانات
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressArea.getText().trim();

        // التحقق من البيانات الإجبارية
        if (name.isEmpty()) {
            showError("⚠️ يرجى إدخال اسم العميل");
            nameField.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            showError("⚠️ يرجى إدخال رقم الهاتف");
            phoneField.requestFocus();
            return;
        }

        // التحقق من صحة البريد الإلكتروني
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("⚠️ البريد الإلكتروني غير صالح");
            emailField.requestFocus();
            return;
        }

        // التحقق من تكرار رقم الهاتف
        if (customerDAO.isPhoneExists(phone)) {
            showError("❌ رقم الهاتف مسجل مسبقاً!");
            phoneField.selectAll();
            phoneField.requestFocus();
            return;
        }

        // إنشاء كائن العميل
        Customer customer = new Customer(name, phone, email, address);

        try {
            // حفظ في قاعدة البيانات
            boolean success = customerDAO.addCustomer(customer);

            if (success) {
                showSuccess("✅ تم حفظ العميل بنجاح!\n" +
                        "🆔 رقم العميل: " + customer.getId() + "\n" +
                        "👤 الاسم: " + customer.getName());

                clearFields();
                updateTitle();

            } else {
                showError("❌ فشل في حفظ العميل!");
            }

        } catch (Exception e) {
            showError("❌ حدث خطأ غير متوقع: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearFields() {
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressArea.setText("");
        nameField.requestFocus();
        System.out.println("🗑️ تم مسح جميع الحقول");
    }

    private void testConnection() {
        System.out.println("\n🔗 === اختبار الاتصال بقاعدة البيانات ===");

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() {
                try {
                    Connection conn = DatabaseConnection.getConnection();
                    if (conn != null && !conn.isClosed()) {
                        int count = customerDAO.getCustomerCount();
                        System.out.println("✅ الاتصال ناجح - عدد العملاء: " + count);
                        return true;
                    }
                } catch (Exception e) {
                    System.err.println("❌ فشل الاتصال: " + e.getMessage());
                }
                return false;
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        int count = customerDAO.getCustomerCount();
                        showSuccess("🎉 الاتصال ناجح!\n" +
                                "📊 عدد العملاء المسجلين: " + count);
                    } else {
                        showError("💔 فشل الاتصال بقاعدة البيانات!");
                    }
                } catch (Exception e) {
                    showError("❌ خطأ في اختبار الاتصال: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void viewAllCustomers() {
        List<Customer> customers = customerDAO.getAllCustomers();

        if (customers.isEmpty()) {
            showInfo("📭 لا يوجد عملاء مسجلين بعد");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("📋 قائمة العملاء المسجلين:\n");
        sb.append("========================\n\n");

        for (Customer customer : customers) {
            sb.append("🆔 الرقم: ").append(customer.getId()).append("\n");
            sb.append("👤 الاسم: ").append(customer.getName()).append("\n");
            sb.append("📱 الهاتف: ").append(customer.getPhone()).append("\n");
            sb.append("📧 البريد: ").append(
                    customer.getEmail().isEmpty() ? "غير محدد" : customer.getEmail()
            ).append("\n");
            sb.append("🏠 العنوان: ").append(
                    customer.getAddress().isEmpty() ? "غير محدد" : customer.getAddress()
            ).append("\n");
            sb.append("────────────────────\n");
        }

        sb.append("\n📊 العدد الإجمالي: ").append(customers.size()).append(" عميل");

        // عرض في TextArea داخل ScrollPane
        JTextArea textArea = new JTextArea(20, 50);
        textArea.setText(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(textArea);

        JOptionPane.showMessageDialog(this, scrollPane,
                "👥 قائمة العملاء", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateTitle() {
        int count = customerDAO.getCustomerCount();
        setTitle("نظام صيانة السيارات - تسجيل عميل جديد (العملاء: " + count + ")");
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