package com.carmaintenance;

import com.carmaintenance.dao.DatabaseCreator;
import com.carmaintenance.gui.CustomerRegistrationForm;
import javax.swing.*;

public class QuickStart {
    public static void main(String[] args) {
        System.out.println("⚡ === بدء سريع لنظام صيانة السيارات === ⚡");

        // 1. إنشاء قاعدة البيانات تلقائياً
        System.out.println("🔄 إنشاء قاعدة البيانات...");
        DatabaseCreator.createDatabaseIfNotExists();

        // 2. تحميل واجهة النظام
        System.out.println("🎨 تحميل واجهة المستخدم...");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.out.println("⚠️ لا يمكن تحميل واجهة النظام: " + e.getMessage());
            }

            CustomerRegistrationForm form = new CustomerRegistrationForm();
            form.setVisible(true);
            System.out.println("✅ النظام جاهز للاستخدام!");
        });
    }
}