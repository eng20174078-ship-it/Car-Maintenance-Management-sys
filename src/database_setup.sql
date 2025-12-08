-- Car Maintenance Management System Database Setup
-- Omar Al-Mukhtar University - PGCS653 Course Project

-- 1. إنشاء قاعدة البيانات
DROP DATABASE IF EXISTS car_maintenance_db;
CREATE DATABASE car_maintenance_db;
USE car_maintenance_db;

-- 2. جدول العملاء
CREATE TABLE customers (
                           id INT AUTO_INCREMENT PRIMARY KEY,
                           name VARCHAR(100) NOT NULL,
                           phone VARCHAR(20) UNIQUE NOT NULL,
                           email VARCHAR(100),
                           address TEXT,
                           created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. جدول السيارات
CREATE TABLE vehicles (
                          plate_number VARCHAR(20) PRIMARY KEY,
                          model VARCHAR(50) NOT NULL,
                          year INT,
                          owner_id INT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          FOREIGN KEY (owner_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- 4. جدول الفنيين
CREATE TABLE technicians (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(100) NOT NULL,
                             phone VARCHAR(20) UNIQUE NOT NULL,
                             specialization VARCHAR(100),
                             hire_date DATE,
                             salary DECIMAL(10, 2),
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. جدول طلبات الصيانة
CREATE TABLE maintenance_orders (
                                    id INT AUTO_INCREMENT PRIMARY KEY,
                                    vehicle_plate VARCHAR(20) NOT NULL,
                                    technician_id INT NOT NULL,
                                    description TEXT,
                                    status ENUM('Pending', 'In Progress', 'Waiting for Parts', 'Completed') DEFAULT 'Pending',
                                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                    completed_at TIMESTAMP NULL,
                                    FOREIGN KEY (vehicle_plate) REFERENCES vehicles(plate_number) ON DELETE CASCADE,
                                    FOREIGN KEY (technician_id) REFERENCES technicians(id) ON DELETE CASCADE
);

-- 6. جدول قطع الغيار
CREATE TABLE spare_parts (
                             id INT AUTO_INCREMENT PRIMARY KEY,
                             name VARCHAR(100) NOT NULL,
                             price DECIMAL(10, 2) NOT NULL,
                             quantity INT DEFAULT 0,
                             min_threshold INT DEFAULT 5,
                             created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 7. جدول الفواتير
CREATE TABLE invoices (
                          id INT AUTO_INCREMENT PRIMARY KEY,
                          order_id INT NOT NULL,
                          total_amount DECIMAL(10, 2) NOT NULL,
                          issued_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          paid BOOLEAN DEFAULT FALSE,
                          FOREIGN KEY (order_id) REFERENCES maintenance_orders(id) ON DELETE CASCADE
);

-- 8. جدول العلاقة بين الطلبات وقطع الغيار
CREATE TABLE order_parts (
                             order_id INT,
                             part_id INT,
                             quantity_used INT NOT NULL,
                             PRIMARY KEY (order_id, part_id),
                             FOREIGN KEY (order_id) REFERENCES maintenance_orders(id) ON DELETE CASCADE,
                             FOREIGN KEY (part_id) REFERENCES spare_parts(id) ON DELETE CASCADE
);

-- 9. إضافة بيانات تجريبية
INSERT INTO customers (name, phone, email, address) VALUES
                                                        ('أحمد محمد', '0912345678', 'ahmed@example.com', 'بنغازي - الحي الأول'),
                                                        ('فاطمة علي', '0923456789', 'fatima@example.com', 'طرابلس - حي الأندلس'),
                                                        ('خالد حسين', '0934567890', NULL, 'درنة - وسط المدينة');

INSERT INTO technicians (name, phone, specialization, hire_date, salary) VALUES
                                                                             ('محمود سالم', '0945678901', 'ميكانيكا محركات', '2023-01-15', 2500.00),
                                                                             ('سالم الكيومي', '0956789012', 'كهرباء سيارات', '2023-03-20', 2200.00);

INSERT INTO spare_parts (name, price, quantity) VALUES
                                                    ('فلتر زيت', 15.50, 25),
                                                    ('شمعة احتراق', 8.75, 40),
                                                    ('مكابح أمامية', 45.00, 12),
                                                    ('بطارية', 120.00, 8);

-- 10. عرض الرسالة
SELECT '✅ تم إنشاء قاعدة البيانات والجداول بنجاح!' as message;
SELECT '📊 عدد العملاء: ' || COUNT(*) FROM customers;
SELECT '👨‍🔧 عدد الفنيين: ' || COUNT(*) FROM technicians;
SELECT '🔩 عدد قطع الغيار: ' || COUNT(*) FROM spare_parts;