-- Laundry Management System Database Setup
-- Run this script in MySQL to create the database and tables

-- Create database
CREATE DATABASE IF NOT EXISTS laundry_system;
USE laundry_system;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address TEXT NOT NULL,
    role ENUM('ADMIN', 'MEMBER') NOT NULL,
    points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    order_id VARCHAR(20) PRIMARY KEY,
    customer_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address TEXT NOT NULL,
    laundry_type VARCHAR(50) NOT NULL,
    service VARCHAR(50) NOT NULL,
    status VARCHAR(20) DEFAULT 'Pending',
    weight DECIMAL(5,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    pickup_time DATETIME,
    order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    customer_id INT
);

-- Insert default admin user
INSERT IGNORE INTO users (username, password, full_name, phone, address, role)
VALUES ('admin', 'admin', 'Administrator', '081234567890', 'Admin Office', 'ADMIN');

-- Insert sample member
INSERT IGNORE INTO users (username, password, full_name, phone, address, role)
VALUES ('john', '123', 'John Doe', '081234567891', 'Jl. Merdeka No. 1', 'MEMBER');

-- Insert sample order
INSERT IGNORE INTO orders (order_id, customer_name, phone, address, laundry_type, service, status, weight, total, pickup_time, customer_id)
VALUES ('ORD001', 'John Doe', '081234567891', 'Jl. Merdeka No. 1', 'Cuci Setrika', 'Regular', 'Processing', 2.5, 25000, DATE_ADD(NOW(), INTERVAL 2 HOUR), 1);

SELECT 'Database setup completed successfully!' as message;