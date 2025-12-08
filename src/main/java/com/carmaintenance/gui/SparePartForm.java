package com.carmaintenance.gui;

import com.carmaintenance.dao.SparePartDAO;
import com.carmaintenance.model.SparePart;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SparePartForm extends JFrame {

    // الحقول
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<String> categoryComboBox;
    private JTextField brandField;
    private JTextField partNumberField;
    private JTextField priceField;
    private JTextField quantityField;
    private JTextField minThresholdField;
    private JTextField locationField;

    // الأزرار
    private JButton saveButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton searchButton;
    private JButton viewAllButton;
    private JButton statsButton;
    private JButton lowStockButton;
    private JButton restockButton;

    // الجدول
    private JTable sparePartsTable;
    private DefaultTableModel tableModel;

    // DAO
    private SparePartDAO sparePartDAO;

    // ID المحدد حاليًا
    private int selectedSparePartId = -1;

    public SparePartForm() {
        // تهيئة DAO
        sparePartDAO = new SparePartDAO();

        // إعداد النافذة
        setupWindow();
        initComponents();
        layoutComponents();

        // تحميل البيانات
        loadAllSpareParts();
        updateTitle();
    }

    private void setupWindow() {
        setTitle("نظام صيانة السيارات - إدارة قطع الغيار");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        // الحقول
        nameField = createTextField();
        descriptionArea = new JTextArea(4, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        // ComboBox للفئات
        categoryComboBox = new JComboBox<>(new String[]{
                "اختر الفئة", "محرك", "كهرباء", "فرامل", "تعليق",
                "عجلات وإطارات", "نظام تبريد", "نظام عادم",
                "إكسسوارات", "زيت ومواد تشحيم", "أخرى"
        });

        brandField = createTextField();
        partNumberField = createTextField();
        priceField = createTextField();
        priceField.setText("0.0");
        quantityField = createTextField();
        quantityField.setText("0");
        minThresholdField = createTextField();
        minThresholdField.setText("5");
        locationField = createTextField();

        // الأزرار
        saveButton = createButton("💾 حفظ القطعة", new Color(40, 167, 69));
        updateButton = createButton("✏️ تحديث البيانات", new Color(255, 193, 7));
        deleteButton = createButton("🗑️ حذف القطعة", new Color(220, 53, 69));
        clearButton = createButton("🧹 مسح الحقول", new Color(108, 117, 125));
        searchButton = createButton("🔍 بحث بالاسم", new Color(0, 123, 255));
        viewAllButton = createButton("🔩 عرض الجميع", new Color(111, 66, 193));
        statsButton = createButton("📊 الإحصائيات", new Color(32, 201, 151));
        lowStockButton = createButton("⚠️ المخزون المنخفض", new Color(253, 126, 20));
        restockButton = createButton("📦 إضافة مخزون", new Color(23, 162, 184));

        // إضافة المستمعين
        saveButton.addActionListener(e -> saveSparePart());
        updateButton.addActionListener(e -> updateSparePart());
        deleteButton.addActionListener(e -> deleteSparePart());
        clearButton.addActionListener(e -> clearFields());
        searchButton.addActionListener(e -> searchSpareParts());
        viewAllButton.addActionListener(e -> loadAllSpareParts());
        statsButton.addActionListener(e -> showStatistics());
        lowStockButton.addActionListener(e -> showLowStock());
        restockButton.addActionListener(e -> restockSparePart());

        // إعداد الجدول
        String[] columns = {"ID", "الاسم", "الفئة", "الماركة", "رقم القطعة",
                "السعر", "الكمية", "الحد الأدنى", "القيمة", "المكان"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        sparePartsTable = new JTable(tableModel);
        sparePartsTable.setRowHeight(25);
        sparePartsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        sparePartsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));

        // تلوين الصفوف حسب حالة المخزون
        sparePartsTable.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    // الحصول على الكمية والحد الأدنى
                    int quantity = 0;
                    int minThreshold = 5;

                    try {
                        quantity = Integer.parseInt(table.getValueAt(row, 6).toString());
                        minThreshold = Integer.parseInt(table.getValueAt(row, 7).toString());
                    } catch (Exception e) {
                        // تجاهل الأخطاء
                    }

                    // تلوين حسب حالة المخزون
                    if (quantity == 0) {
                        c.setBackground(new Color(255, 220, 220)); // أحمر فاتح للمنتهي
                    } else if (quantity <= minThreshold) {
                        c.setBackground(new Color(255, 255, 200)); // أصفر للمنخفض
                    } else {
                        c.setBackground(Color.WHITE); // أبيض للجيد
                    }
                }

                return c;
            }
        });

        // إضافة اختيار الصف
        sparePartsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = sparePartsTable.getSelectedRow();
                if (selectedRow != -1) {
                    loadSparePartFromTable(selectedRow);
                }
            }
        });

        // تهيئة زر التحديث والحذف
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        restockButton.setEnabled(false);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // لوحة العنوان
        JPanel titlePanel = new JPanel();
        JLabel titleLabel = new JLabel("🔩 إدارة قطع الغيار");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        // لوحة الحقول (اليسار)
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218), 2),
                "بيانات قطعة الغيار"
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // الصف 0: الاسم
        gbc.gridx = 0; gbc.gridy = 0; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("🔩 اسم القطعة:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(nameField, gbc);

        // الصف 1: الوصف
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.NORTHWEST;
        fieldsPanel.add(createLabel("📝 الوصف:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(300, 80));
        fieldsPanel.add(descScroll, gbc);

        // الصف 2: الفئة
        gbc.gridx = 0; gbc.gridy = 2; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("🏷️ الفئة:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(categoryComboBox, gbc);

        // الصف 3: الماركة
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("🏭 الماركة:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(brandField, gbc);

        // الصف 4: رقم القطعة
        gbc.gridx = 0; gbc.gridy = 4; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("🔢 رقم القطعة:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(partNumberField, gbc);

        // الصف 5: السعر
        gbc.gridx = 0; gbc.gridy = 5; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("💰 السعر:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(priceField, gbc);

        // الصف 6: الكمية
        gbc.gridx = 0; gbc.gridy = 6; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("📦 الكمية المتاحة:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(quantityField, gbc);

        // الصف 7: الحد الأدنى
        gbc.gridx = 0; gbc.gridy = 7; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("⚠️ الحد الأدنى للمخزون:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(minThresholdField, gbc);

        // الصف 8: مكان التخزين
        gbc.gridx = 0; gbc.gridy = 8; gbc.fill = GridBagConstraints.NONE;
        fieldsPanel.add(createLabel("🏠 مكان التخزين:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        fieldsPanel.add(locationField, gbc);

        add(fieldsPanel, BorderLayout.WEST);

        // لوحة الأزرار (الوسط)
        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        buttonPanel.add(saveButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(viewAllButton);
        buttonPanel.add(statsButton);
        buttonPanel.add(lowStockButton);
        buttonPanel.add(restockButton);

        add(buttonPanel, BorderLayout.CENTER);

        // لوحة الجدول (اليمين)
        JScrollPane tableScroll = new JScrollPane(sparePartsTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("قائمة قطع الغيار"));
        add(tableScroll, BorderLayout.EAST);
        tableScroll.setPreferredSize(new Dimension(600, 0));
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

    private void loadAllSpareParts() {
        // مسح الجدول الحالي
        tableModel.setRowCount(0);

        List<SparePart> spareParts = sparePartDAO.getAllSpareParts();

        for (SparePart sparePart : spareParts) {
            Object[] row = {
                    sparePart.getId(),
                    sparePart.getName(),
                    sparePart.getCategory(),
                    sparePart.getBrand(),
                    sparePart.getPartNumber(),
                    String.format("%.2f", sparePart.getPrice()),
                    sparePart.getQuantity(),
                    sparePart.getMinThreshold(),
                    String.format("%.2f", sparePart.getTotalValue()),
                    sparePart.getLocation()
            };
            tableModel.addRow(row);
        }

        updateTitle();
        clearSelection();
    }

    private void loadSparePartFromTable(int rowIndex) {
        int id = (int) tableModel.getValueAt(rowIndex, 0);
        SparePart sparePart = sparePartDAO.getSparePartById(id);

        if (sparePart != null) {
            loadSparePartToForm(sparePart);
            selectedSparePartId = id;

            // تفعيل أزرار التحديث والحذف والتزويد
            updateButton.setEnabled(true);
            deleteButton.setEnabled(true);
            restockButton.setEnabled(true);
            saveButton.setEnabled(false);
        }
    }

    private void loadSparePartToForm(SparePart sparePart) {
        nameField.setText(sparePart.getName());
        descriptionArea.setText(sparePart.getDescription() != null ? sparePart.getDescription() : "");

        // اختيار الفئة
        categoryComboBox.setSelectedItem(sparePart.getCategory());

        brandField.setText(sparePart.getBrand() != null ? sparePart.getBrand() : "");
        partNumberField.setText(sparePart.getPartNumber() != null ? sparePart.getPartNumber() : "");
        priceField.setText(String.valueOf(sparePart.getPrice()));
        quantityField.setText(String.valueOf(sparePart.getQuantity()));
        minThresholdField.setText(String.valueOf(sparePart.getMinThreshold()));
        locationField.setText(sparePart.getLocation() != null ? sparePart.getLocation() : "");

        nameField.requestFocus();
    }

    private void saveSparePart() {
        System.out.println("\n🔩 === محاولة حفظ قطعة غيار ===");

        // التحقق من البيانات
        if (!validateInput()) {
            return;
        }

        // إنشاء كائن قطعة الغيار
        SparePart sparePart = createSparePartFromForm();
        if (sparePart == null) {
            return;
        }

        try {
            // حفظ في قاعدة البيانات
            boolean success = sparePartDAO.addSparePart(sparePart);

            if (success) {
                showSuccess("✅ تم حفظ قطعة الغيار بنجاح!\n" +
                        "🆔 رقم القطعة: " + sparePart.getId() + "\n" +
                        "🔩 الاسم: " + sparePart.getName() + "\n" +
                        "🏷️ الفئة: " + sparePart.getCategory());

                clearFields();
                loadAllSpareParts();

            } else {
                showError("❌ فشل في حفظ قطعة الغيار!");
            }

        } catch (Exception e) {
            showError("❌ حدث خطأ غير متوقع: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateSparePart() {
        if (selectedSparePartId == -1) {
            showError("⚠️ يرجى اختيار قطعة غيار للتحديث");
            return;
        }

        System.out.println("\n✏️ === محاولة تحديث قطعة غيار ===");

        // التحقق من البيانات
        if (!validateInput()) {
            return;
        }

        // إنشاء كائن قطعة الغيار
        SparePart sparePart = createSparePartFromForm();
        if (sparePart == null) {
            return;
        }

        sparePart.setId(selectedSparePartId);

        try {
            // تحديث في قاعدة البيانات
            boolean success = sparePartDAO.updateSparePart(sparePart);

            if (success) {
                showSuccess("✅ تم تحديث بيانات قطعة الغيار بنجاح!\n" +
                        "🆔 رقم القطعة: " + sparePart.getId() + "\n" +
                        "🔩 الاسم: " + sparePart.getName());

                clearFields();
                loadAllSpareParts();

            } else {
                showError("❌ فشل في تحديث قطعة الغيار!");
            }

        } catch (Exception e) {
            showError("❌ حدث خطأ غير متوقع: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteSparePart() {
        if (selectedSparePartId == -1) {
            showError("⚠️ يرجى اختيار قطعة غيار للحذف");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "هل أنت متأكد من حذف قطعة الغيار؟\nهذا الإجراء لا يمكن التراجع عنه.",
                "تأكيد الحذف", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            System.out.println("\n🗑️ === محاولة حذف قطعة غيار ===");

            boolean success = sparePartDAO.deleteSparePart(selectedSparePartId);

            if (success) {
                showSuccess("✅ تم حذف قطعة الغيار بنجاح!");
                clearFields();
                loadAllSpareParts();
            } else {
                showError("❌ فشل في حذف قطعة الغيار!");
            }
        }
    }

    private void restockSparePart() {
        if (selectedSparePartId == -1) {
            showError("⚠️ يرجى اختيار قطعة غيار للتزويد");
            return;
        }

        String quantityStr = JOptionPane.showInputDialog(this,
                "أدخل كمية التزويد:", "تزويد المخزون", JOptionPane.QUESTION_MESSAGE);

        if (quantityStr != null && !quantityStr.trim().isEmpty()) {
            try {
                int quantity = Integer.parseInt(quantityStr.trim());
                if (quantity <= 0) {
                    showError("⚠️ الكمية يجب أن تكون رقم موجب");
                    return;
                }

                boolean success = sparePartDAO.updateQuantity(selectedSparePartId, quantity);

                if (success) {
                    showSuccess("✅ تم تزويد المخزون بنجاح!\n" +
                            "➕ كمية التزويد: " + quantity);
                    loadAllSpareParts();
                    loadSparePartToForm(sparePartDAO.getSparePartById(selectedSparePartId));
                } else {
                    showError("❌ فشل في تزويد المخزون!");
                }

            } catch (NumberFormatException e) {
                showError("⚠️ الكمية يجب أن تكون رقم صحيح");
            }
        }
    }

    private boolean validateInput() {
        // التحقق من الاسم
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            showError("⚠️ يرجى إدخال اسم قطعة الغيار");
            nameField.requestFocus();
            return false;
        }

        // التحقق من الفئة
        String category = (String) categoryComboBox.getSelectedItem();
        if (category == null || "اختر الفئة".equals(category)) {
            showError("⚠️ يرجى اختيار فئة قطعة الغيار");
            categoryComboBox.requestFocus();
            return false;
        }

        // التحقق من السعر
        String priceText = priceField.getText().trim();
        try {
            double price = Double.parseDouble(priceText);
            if (price < 0) {
                showError("⚠️ السعر يجب أن يكون رقم موجب");
                priceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("⚠️ السعر يجب أن يكون رقم صحيح");
            priceField.requestFocus();
            return false;
        }

        // التحقق من الكمية
        String quantityText = quantityField.getText().trim();
        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity < 0) {
                showError("⚠️ الكمية يجب أن تكون رقم موجب");
                quantityField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("⚠️ الكمية يجب أن تكون رقم صحيح");
            quantityField.requestFocus();
            return false;
        }

        // التحقق من الحد الأدنى
        String thresholdText = minThresholdField.getText().trim();
        try {
            int threshold = Integer.parseInt(thresholdText);
            if (threshold < 0) {
                showError("⚠️ الحد الأدنى يجب أن يكون رقم موجب");
                minThresholdField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showError("⚠️ الحد الأدنى يجب أن يكون رقم صحيح");
            minThresholdField.requestFocus();
            return false;
        }

        return true;
    }

    private SparePart createSparePartFromForm() {
        try {
            SparePart sparePart = new SparePart();

            sparePart.setName(nameField.getText().trim());
            sparePart.setDescription(descriptionArea.getText().trim());
            sparePart.setCategory((String) categoryComboBox.getSelectedItem());
            sparePart.setBrand(brandField.getText().trim());
            sparePart.setPartNumber(partNumberField.getText().trim());

            // السعر
            try {
                sparePart.setPrice(Double.parseDouble(priceField.getText().trim()));
            } catch (NumberFormatException e) {
                sparePart.setPrice(0.0);
            }

            // الكمية
            try {
                sparePart.setQuantity(Integer.parseInt(quantityField.getText().trim()));
            } catch (NumberFormatException e) {
                sparePart.setQuantity(0);
            }

            // الحد الأدنى
            try {
                sparePart.setMinThreshold(Integer.parseInt(minThresholdField.getText().trim()));
            } catch (NumberFormatException e) {
                sparePart.setMinThreshold(5);
            }

            sparePart.setLocation(locationField.getText().trim());

            return sparePart;

        } catch (Exception e) {
            showError("❌ خطأ في إنشاء كائن قطعة الغيار: " + e.getMessage());
            return null;
        }
    }

    private void searchSpareParts() {
        String name = JOptionPane.showInputDialog(this,
                "أدخل اسم قطعة الغيار للبحث:", "بحث عن قطع غيار", JOptionPane.QUESTION_MESSAGE);

        if (name != null && !name.trim().isEmpty()) {
            // مسح الجدول الحالي
            tableModel.setRowCount(0);

            List<SparePart> spareParts = sparePartDAO.searchSparePartsByName(name.trim());

            if (spareParts.isEmpty()) {
                showInfo("🔍 لم يتم العثور على قطع غيار بهذا الاسم");
                return;
            }

            for (SparePart sparePart : spareParts) {
                Object[] row = {
                        sparePart.getId(),
                        sparePart.getName(),
                        sparePart.getCategory(),
                        sparePart.getBrand(),
                        sparePart.getPartNumber(),
                        String.format("%.2f", sparePart.getPrice()),
                        sparePart.getQuantity(),
                        sparePart.getMinThreshold(),
                        String.format("%.2f", sparePart.getTotalValue()),
                        sparePart.getLocation()
                };
                tableModel.addRow(row);
            }

            showInfo("🔍 تم العثور على " + spareParts.size() + " قطعة غيار");
        }
    }

    private void showStatistics() {
        String stats = sparePartDAO.getStatistics();

        JTextArea textArea = new JTextArea(stats, 20, 50);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(248, 249, 250));

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane,
                "📊 إحصائيات قطع الغيار", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showLowStock() {
        List<SparePart> lowStockParts = sparePartDAO.getLowStockParts();
        List<SparePart> outOfStockParts = sparePartDAO.getOutOfStockParts();

        StringBuilder report = new StringBuilder();
        report.append("🚨 تقرير المخزون الحرج\n");
        report.append("===================\n\n");

        if (outOfStockParts.isEmpty() && lowStockParts.isEmpty()) {
            report.append("✅ جميع قطع الغيار في حالة جيدة!\n");
        } else {
            if (!outOfStockParts.isEmpty()) {
                report.append("🔴 قطع الغيار المنتهية:\n");
                report.append("---------------------\n");
                for (SparePart part : outOfStockParts) {
                    report.append("   • ").append(part.getName())
                            .append(" (").append(part.getCategory()).append(")\n");
                }
                report.append("\n");
            }

            if (!lowStockParts.isEmpty()) {
                report.append("🟡 قطع الغيار المنخفضة:\n");
                report.append("--------------------\n");
                for (SparePart part : lowStockParts) {
                    report.append("   • ").append(part.getName())
                            .append(" (").append(part.getCategory()).append(")")
                            .append(" - متوفر: ").append(part.getQuantity())
                            .append(" / الحد الأدنى: ").append(part.getMinThreshold()).append("\n");
                }
            }
        }

        JTextArea textArea = new JTextArea(report.toString(), 20, 50);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(255, 248, 248));

        JScrollPane scrollPane = new JScrollPane(textArea);
        JOptionPane.showMessageDialog(this, scrollPane,
                "⚠️ المخزون المنخفض والمنتهي", JOptionPane.WARNING_MESSAGE);
    }

    private void clearFields() {
        nameField.setText("");
        descriptionArea.setText("");
        categoryComboBox.setSelectedIndex(0);
        brandField.setText("");
        partNumberField.setText("");
        priceField.setText("0.0");
        quantityField.setText("0");
        minThresholdField.setText("5");
        locationField.setText("");
        nameField.requestFocus();

        clearSelection();
    }

    private void clearSelection() {
        sparePartsTable.clearSelection();
        selectedSparePartId = -1;

        // تعطيل أزرار التحديث والحذف والتزويد
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        restockButton.setEnabled(false);
        saveButton.setEnabled(true);
    }

    private void updateTitle() {
        int total = sparePartDAO.getSparePartCount();
        double value = sparePartDAO.getTotalInventoryValue();
        setTitle("إدارة قطع الغيار - العدد: " + total + " | القيمة: " + String.format("%.2f", value));
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