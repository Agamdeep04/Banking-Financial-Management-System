-- ============================================================
-- Banking and Financial Management System
-- MySQL schema script
-- Run this script in MySQL before starting the application.
-- ============================================================

CREATE DATABASE IF NOT EXISTS BankingManagementSystem;
USE BankingManagementSystem;

-- ------------------------------------------------------------
-- Admins table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Admins (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    admin_code VARCHAR(50) DEFAULT 'ADMIN',
    registered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Initial admin record
INSERT INTO Admins (name, email, password_hash, admin_code)
VALUES ('System Admin', 'admin@bank.com', 'admin123', 'ADMIN');

-- ------------------------------------------------------------
-- Customers table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Customers (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    password_hash VARCHAR(255) NOT NULL,
    registered_date DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ------------------------------------------------------------
-- BankAccounts table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS BankAccounts (
    id INT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    customer_id INT NOT NULL,
    balance DECIMAL(19,4) DEFAULT 0.0000,
    account_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    CONSTRAINT fk_account_customer FOREIGN KEY (customer_id) REFERENCES Customers(id)
);

-- ------------------------------------------------------------
-- Transactions table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Transactions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(20) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(19,4) NOT NULL,
    description VARCHAR(255),
    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_txn_account FOREIGN KEY (account_number) REFERENCES BankAccounts(account_number)
);

-- ------------------------------------------------------------
-- FixedDeposits table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS FixedDeposits (
    id INT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(20) NOT NULL,
    principal DECIMAL(19,4) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    tenure_months INT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    CONSTRAINT fk_fd_account FOREIGN KEY (account_number) REFERENCES BankAccounts(account_number)
);

-- ------------------------------------------------------------
-- Loans table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS Loans (
    id INT PRIMARY KEY AUTO_INCREMENT,
    account_number VARCHAR(20) NOT NULL,
    principal DECIMAL(19,4) NOT NULL,
    interest_rate DECIMAL(5,2) NOT NULL,
    tenure_months INT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    CONSTRAINT fk_loan_account FOREIGN KEY (account_number) REFERENCES BankAccounts(account_number)
);

-- ------------------------------------------------------------
-- LoanRepayments table
-- ------------------------------------------------------------
CREATE TABLE IF NOT EXISTS LoanRepayments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    loan_id INT NOT NULL,
    repayment_amount DECIMAL(19,4) NOT NULL,
    repayment_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    remaining_after DECIMAL(19,4) NOT NULL,
    CONSTRAINT fk_repayment_loan FOREIGN KEY (loan_id) REFERENCES Loans(id)
);

-- ------------------------------------------------------------
-- Sample data (optional - demonstrates working records)
-- ------------------------------------------------------------
INSERT INTO Customers (customer_code, name, email, phone, password_hash, registered_date)
VALUES
('C001', 'John Doe', 'john@example.com', '1234567890', 'demo-hash-1', NOW()),
('C002', 'Jane Smith', 'jane@example.com', '0987654321', 'demo-hash-2', NOW());

INSERT INTO BankAccounts (account_number, customer_id, balance, account_type, status)
VALUES
('ACC1001', 1, 5000.0000, 'SAVINGS', 'ACTIVE'),
('ACC1002', 1, 2000.0000, 'CURRENT', 'ACTIVE'),
('ACC1003', 2, 3000.0000, 'SAVINGS', 'ACTIVE');

INSERT INTO Transactions (account_number, transaction_type, amount, description, transaction_date)
VALUES
('ACC1001', 'DEPOSIT', 5000.0000, 'Initial deposit', NOW()),
('ACC1002', 'DEPOSIT', 2000.0000, 'Initial deposit', NOW());
