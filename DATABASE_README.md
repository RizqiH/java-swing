# Database Setup Guide for Laundry Management System

This guide will help you set up the MySQL database for the Laundry Management System.

## Prerequisites

1. **MySQL Server** - Install MySQL Server 8.0 or later
2. **MySQL Workbench** (optional) - For easier database management
3. **Java 11+** - Required for the application
4. **Maven** - For building and running the application

## Database Configuration

The application is configured to connect to MySQL with the following default settings:

- **Host**: localhost
- **Port**: 3306
- **Database**: laundry_system
- **Username**: root
- **Password**: (empty)

### Changing Database Configuration

If you need to change these settings, modify the `DatabaseConfig.java` file:

```java
// Located in: src/main/java/com/laundry/config/DatabaseConfig.java
private static final String DB_URL = "jdbc:mysql://localhost:3306/laundry_system";
private static final String DB_USERNAME = "root";
private static final String DB_PASSWORD = "";
```

## Setup Instructions

### Option 1: Automatic Setup (Recommended)

1. **Start MySQL Server**
   - Make sure MySQL is running on your system
   - Default port should be 3306

2. **Create the Database**
   ```sql
   CREATE DATABASE laundry_system;
   ```

3. **Run the Application**
   ```bash
   mvn compile exec:java
   ```
   
   The application will automatically:
   - Create the required tables
   - Insert default admin and sample data
   - Set up the connection pool

### Option 2: Manual Setup

1. **Run the SQL Script**
   ```bash
   mysql -u root -p < database_setup.sql
   ```
   
   Or execute the script in MySQL Workbench:
   - Open `database_setup.sql`
   - Execute the entire script

2. **Verify Setup**
   ```sql
   USE laundry_system;
   SHOW TABLES;
   SELECT * FROM users;
   SELECT * FROM orders;
   ```

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    address TEXT NOT NULL,
    role ENUM('ADMIN', 'MEMBER') NOT NULL,
    points INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Orders Table
```sql
CREATE TABLE orders (
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
```

## Default Data

The system comes with pre-configured data:

### Default Admin Account
- **Username**: admin
- **Password**: admin
- **Role**: ADMIN

### Sample Member Account
- **Username**: john
- **Password**: 123
- **Role**: MEMBER

## Features

### Database Features Implemented

1. **Connection Pooling** - Using HikariCP for efficient database connections
2. **Prepared Statements** - All queries use prepared statements for security
3. **Transaction Safety** - Proper error handling and connection management
4. **Dynamic Queries** - All data operations use SQL queries instead of in-memory storage

### Repository Methods Available

#### UserRepository
- `addUser(User user)` - Add new user
- `getUser(String username)` - Get user by username
- `userExists(String username)` - Check if user exists
- `getAllMembers()` - Get all members
- `updateUser(User user)` - Update user information
- `deleteUser(String username)` - Delete user

#### OrderRepository
- `addOrder(Order order)` - Add new order
- `save(Order order)` - Save order
- `getAllOrders()` - Get all orders
- `findById(String orderId)` - Find order by ID
- `findByCustomerName(String name)` - Find orders by customer
- `findByStatus(String status)` - Find orders by status
- `updateOrder(Order order)` - Update order
- `deleteOrder(String orderId)` - Delete order
- `generateOrderId()` - Generate unique order ID

## Troubleshooting

### Common Issues

1. **Connection Refused**
   - Make sure MySQL server is running
   - Check if port 3306 is available
   - Verify username/password in DatabaseConfig.java

2. **Database Not Found**
   - Create the database manually: `CREATE DATABASE laundry_system;`
   - Make sure you have proper permissions

3. **Access Denied**
   - Check MySQL user permissions
   - Update username/password in DatabaseConfig.java
   - Grant necessary privileges to the user

4. **Driver Not Found**
   - Run `mvn clean compile` to download dependencies
   - Check if MySQL connector is in pom.xml

### Performance Tips

1. **Connection Pool Settings**
   - Adjust pool size based on your needs in DatabaseConfig.java
   - Monitor connection usage

2. **Database Indexing**
   - Consider adding indexes for frequently queried columns
   - Monitor query performance

## Migration from In-Memory

The application has been successfully migrated from in-memory storage to MySQL database:

- ✅ User management now uses database
- ✅ Order management now uses database
- ✅ All data persists between application restarts
- ✅ Improved data integrity and concurrent access
- ✅ Better scalability and performance

## Next Steps

1. **Security Enhancements**
   - Implement password hashing
   - Add input validation
   - Use environment variables for database credentials

2. **Advanced Features**
   - Add database migrations
   - Implement audit logging
   - Add backup and restore functionality

3. **Performance Optimization**
   - Add database indexes
   - Implement caching
   - Optimize queries

For any issues or questions, please refer to the application logs or contact the development team.