package com.carmaintenance;

import com.carmaintenance.dao.DatabaseCreator;

import java.util.Scanner;

public class DatabaseSetup {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  إعداد قاعدة بيانات نظام صيانة السيارات  ");
        System.out.println("==========================================");
        System.out.println();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n📋 قائمة الخيارات:");
            System.out.println("1️⃣  اختبار الاتصال بقاعدة البيانات");
            System.out.println("2️⃣  إنشاء قاعدة البيانات والجداول");
            System.out.println("3️⃣  إعادة إنشاء كل شيء من الصفر");
            System.out.println("4️⃣  تشغيل النظام الرئيسي");
            System.out.println("5️⃣  الخروج");
            System.out.print("\nاختر رقم الخيار: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ الرجاء إدخال رقم صحيح!");
                continue;
            }

            switch (choice) {
                case 1:
                    DatabaseCreator.testDatabaseConnection();
                    break;

                case 2:
                    DatabaseCreator.createDatabaseIfNotExists();
                    break;

                case 3:
                    System.out.print("⚠️ تحذير: هذا سيحذف جميع البيانات! هل أنت متأكد؟ (نعم/لا): ");
                    String confirm = scanner.nextLine();
                    if (confirm.equalsIgnoreCase("نعم") || confirm.equalsIgnoreCase("yes")) {
                        DatabaseCreator.dropAndRecreateDatabase();
                    } else {
                        System.out.println("✅ تم إلغاء العملية");
                    }
                    break;

                case 4:
                    runMainSystem();
                    break;

                case 5:
                    System.out.println("👋 مع السلامة!");
                    scanner.close();
                    return;

                default:
                    System.out.println("❌ خيار غير صحيح! الرجاء اختيار رقم من 1 إلى 5");
            }
        }
    }

    private static void runMainSystem() {
        System.out.println("\n🚀 بدء تشغيل النظام الرئيسي...");

        // هنا يمكنك استدعاء Main.main() أو فتح الواجهة مباشرة
        System.out.println("✅ تم تحميل النظام الرئيسي");
        System.out.println("💡 ملاحظة: افتح CustomerRegistrationForm مباشرة للتجربة");

        // أو تشغيل Main مباشرة
        // Main.main(new String[]{});
    }
}