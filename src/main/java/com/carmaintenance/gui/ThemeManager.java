package com.carmaintenance.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class ThemeManager {

    // ألوان ثابتة للموضوع - ألوان أكثر جاذبية
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185);      // أزرق أساسي جذاب
    public static final Color SECONDARY_COLOR = new Color(39, 174, 96);     // أخضر نابض بالحياة
    public static final Color ACCENT_COLOR = new Color(241, 196, 15);       // أصفر ذهبي
    public static final Color DANGER_COLOR = new Color(231, 76, 60);        // أحمر جريء
    public static final Color WARNING_COLOR = new Color(230, 126, 34);      // برتقالي دافئ
    public static final Color INFO_COLOR = new Color(52, 152, 219);         // أزرق سماوي
    public static final Color DARK_COLOR = new Color(44, 62, 80);           // أزرق داكن متميز
    public static final Color LIGHT_COLOR = new Color(236, 240, 241);       // رمادي فاتح ناعم
    public static final Color SUCCESS_COLOR = new Color(46, 204, 113);      // أخضر فاتح
    public static final Color PANEL_BG = new Color(249, 249, 249);          // خلفية اللوحة

    // خطوط محسنة لدعم اللغة العربية
    public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 26);
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    public static final Font SUBTITLE_FONT = new Font("Arial", Font.PLAIN, 16);
    public static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 14);
    public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    public static final Font ARABIC_FONT = new Font("Tahoma", Font.BOLD, 14);

    // هوامش محسنة
    public static final Insets STANDARD_MARGIN = new Insets(12, 12, 12, 12);
    public static final Insets SMALL_MARGIN = new Insets(8, 8, 8, 8);
    public static final Insets LARGE_MARGIN = new Insets(20, 20, 20, 20);

    // أحجام محسنة
    public static final Dimension BUTTON_SIZE = new Dimension(180, 45);
    public static final Dimension LARGE_BUTTON = new Dimension(200, 50);
    public static final Dimension FIELD_SIZE = new Dimension(280, 40);
    public static final Dimension TABLE_SIZE = new Dimension(700, 450);
    public static final Dimension CARD_SIZE = new Dimension(180, 100);

    // تطبيق سمة عالمية محسنة
    public static void applyTheme() {
        try {
            // استخدام واجهة Nimbus للشكل الحديث
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");

            // تخصيص ألوان العناصر
            UIManager.put("nimbusBase", PRIMARY_COLOR);
            UIManager.put("nimbusBlueGrey", DARK_COLOR);
            UIManager.put("control", LIGHT_COLOR);

            UIManager.put("Panel.background", PANEL_BG);
            UIManager.put("Button.background", PRIMARY_COLOR);
            UIManager.put("Button.foreground", Color.WHITE);
            UIManager.put("Button.font", BUTTON_FONT);
            UIManager.put("Button.focus", ACCENT_COLOR);
            UIManager.put("Button.select", PRIMARY_COLOR.darker());

            UIManager.put("TextField.background", Color.WHITE);
            UIManager.put("TextField.font", BODY_FONT);
            UIManager.put("TextField.border", BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

            UIManager.put("TextArea.font", BODY_FONT);
            UIManager.put("TextArea.background", Color.WHITE);

            UIManager.put("Label.font", BODY_FONT);
            UIManager.put("Label.foreground", DARK_COLOR);

            UIManager.put("Table.font", BODY_FONT);
            UIManager.put("Table.background", Color.WHITE);
            UIManager.put("Table.gridColor", new Color(230, 230, 230));
            UIManager.put("TableHeader.font", BUTTON_FONT);
            UIManager.put("TableHeader.background", DARK_COLOR);
            UIManager.put("TableHeader.foreground", Color.WHITE);

            UIManager.put("ComboBox.font", BODY_FONT);
            UIManager.put("ComboBox.background", Color.WHITE);

            UIManager.put("MenuBar.background", DARK_COLOR);
            UIManager.put("MenuBar.foreground", Color.WHITE);

            UIManager.put("TabbedPane.background", LIGHT_COLOR);
            UIManager.put("TabbedPane.foreground", DARK_COLOR);

        } catch (Exception e) {
            try {
                // إذا فشل Nimbus، استخدم النظام
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                System.err.println("⚠️ خطأ في تطبيق السمة: " + ex.getMessage());
            }
        }
    }
    // الدالة الأصلية + تحسينات
    public static JPanel createInfoCard(String title, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(180, 100));

        // إضافة ظل وتصميم حديث
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(3, 3, 3, 3),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
        ));

        // إضافة تأثير زجاجي
        JPanel glassEffect = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // خلفية متدرجة
                GradientPaint gradient = new GradientPaint(0, 0, color.brighter(),
                        0, getHeight(), color);
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.dispose();
            }
        };
        glassEffect.setOpaque(false);
        glassEffect.setPreferredSize(new Dimension(50, 50));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        iconLabel.setForeground(Color.WHITE);
        glassEffect.setLayout(new BorderLayout());
        glassEffect.add(iconLabel, BorderLayout.CENTER);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(DARK_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 22));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // لوحة للأيقونة
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setBackground(Color.WHITE);
        iconPanel.add(glassEffect, BorderLayout.CENTER);

        // لوحة للنصوص
        JPanel textPanel = new JPanel(new BorderLayout(5, 5));
        textPanel.setBackground(Color.WHITE);
        textPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(valueLabel, BorderLayout.CENTER);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        // إضافة خط ملون في الأسفل
        JPanel bottomLine = new JPanel();
        bottomLine.setBackground(color);
        bottomLine.setPreferredSize(new Dimension(0, 4));
        card.add(bottomLine, BorderLayout.SOUTH);

        // تأثير عند المرور
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color.brighter(), 2),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)
                ));
                card.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createEmptyBorder(3, 3, 3, 3),
                                BorderFactory.createEmptyBorder(15, 15, 15, 15)
                        )
                ));
            }
        });

        return card;
    }

    // إنشاء زر محسن بشكل كبير مع تصميم حديث
    public static JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // خلفية دائرية
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // ظل خفيف
                g2.setColor(color.darker());
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        button.setFont(BUTTON_FONT);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(BUTTON_SIZE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(false);

        // تأثيرات تفاعلية محسنة
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(color.brighter().brighter(), 2));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                button.setForeground(Color.WHITE);
                button.setBorder(null);
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
            }
        });

        return button;
    }

    // إنشاء زر كبير للوحة التحكم
    public static JButton createDashboardButton(String text, Color color, String icon) {
        JButton button = new JButton("<html><center><font size='+1'>" + icon + "<br>" + text + "</font></center></html>");
        button.setFont(ARABIC_FONT);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setPreferredSize(LARGE_BUTTON);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // تأثيرات
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.brighter());
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color.brighter(), 2),
                        BorderFactory.createEmptyBorder(15, 10, 15, 10)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color.darker(), 2),
                        BorderFactory.createEmptyBorder(15, 10, 15, 10)
                ));
            }
        });

        return button;
    }

    // إنشاء حقل نصي محسن مع حدود دائرية
    public static JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                super.paintComponent(g2);
                g2.dispose();
            }
        };

        field.setFont(BODY_FONT);
        field.setPreferredSize(FIELD_SIZE);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setOpaque(false);

        return field;
    }

    // إنشاء تسمية محسنة مع دعم للنص العربي
    public static JLabel createStyledLabel(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        return label;
    }

    // إنشاء لوحة محسنة مع ظل وحدود دائرية
    public static JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // ظل خفيف
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);

                g2.dispose();
            }
        };

        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ),
                title,
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                TITLE_FONT,
                PRIMARY_COLOR
        ));

        return panel;
    }

    // إنشاء جدول محسن مع تناوب الألوان
    public static JTable createStyledTable(Object[][] data, String[] columns) {
        JTable table = new JTable(data, columns);
        table.setFont(BODY_FONT);
        table.setRowHeight(35);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getTableHeader().setFont(BUTTON_FONT);
        table.getTableHeader().setBackground(DARK_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setReorderingAllowed(false);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(250, 250, 250));
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }

                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                setHorizontalAlignment(SwingConstants.CENTER);

                return c;
            }
        });

        table.setGridColor(new Color(230, 230, 230));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(INFO_COLOR);
        table.setSelectionForeground(Color.WHITE);

        return table;
    }

    // إنشاء بطاقة إحصائية للوحة التحكم
    public static JPanel createDashboardCard(String title, String value, Color color, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(CARD_SIZE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // إضافة ظل
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(3, 3, 3, 3),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
        ));

        JLabel titleLabel = new JLabel(icon + "  " + title);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(DARK_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        // إضافة خط ملون في الأسفل
        JPanel bottomLine = new JPanel();
        bottomLine.setBackground(color);
        bottomLine.setPreferredSize(new Dimension(0, 5));
        card.add(bottomLine, BorderLayout.SOUTH);

        return card;
    }

    // إنشاء قائمة جانبية محسنة
    public static JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(DARK_COLOR);
        sidebar.setPreferredSize(new Dimension(280, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        // شعار محسن
        JLabel logo = new JLabel("<html><center><font size='5'>🚗</font><br><font size='4' color='white'>نظام إدارة<br>صيانة السيارات</font></center></html>");
        logo.setFont(new Font("Arial", Font.BOLD, 16));
        logo.setForeground(Color.WHITE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 40)));

        return sidebar;
    }

    // إنشاء شريط حالة (Status Bar)
    public static JPanel createStatusBar(String message) {
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(DARK_COLOR);
        statusBar.setPreferredSize(new Dimension(0, 30));
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, PRIMARY_COLOR));

        JLabel statusLabel = new JLabel("  " + message);
        statusLabel.setFont(BODY_FONT);
        statusLabel.setForeground(Color.WHITE);

        JLabel timeLabel = new JLabel(new java.util.Date().toString() + "  ");
        timeLabel.setFont(BODY_FONT);
        timeLabel.setForeground(Color.WHITE);

        statusBar.add(statusLabel, BorderLayout.WEST);
        statusBar.add(timeLabel, BorderLayout.EAST);

        return statusBar;
    }

    // إنشاء رأس الصفحة
    public static JPanel createHeader(String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(PRIMARY_COLOR);
        header.setPreferredSize(new Dimension(0, 80));
        header.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JLabel userLabel = new JLabel("👤 المستخدم: مدير النظام");
        userLabel.setFont(SUBTITLE_FONT);
        userLabel.setForeground(Color.WHITE);

        header.add(titleLabel, BorderLayout.CENTER);
        header.add(userLabel, BorderLayout.EAST);

        return header;
    }
}