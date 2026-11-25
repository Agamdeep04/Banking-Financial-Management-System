
CREATE DATABASE IF NOT EXISTS BankingManagementSystem;

USE BankingManagementSystem;

-- 1. Customers Table
CREATE TABLE IF NOT EXISTS Customers (
    id INT PRIMARY KEY,
    customer_code VARCHAR(30) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    phone VARCHAR(15) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    registered_date DATETIME
) ENGINE=InnoDB;

-- 2. Admins Table
CREATE TABLE IF NOT EXISTS Admins (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    admin_code VARCHAR(30) UNIQUE NOT NULL
) ENGINE=InnoDB;

-- 3. Bank Accounts Table
CREATE TABLE IF NOT EXISTS BankAccounts (
    account_number VARCHAR(40) PRIMARY KEY,
    customer_id INT NOT NULL,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    account_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    FOREIGN KEY (customer_id)
    REFERENCES Customers(id),

    CHECK (balance >= -1000000)
) ENGINE=InnoDB;

-- 4. Transactions Table
CREATE TABLE IF NOT EXISTS Transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(40) NOT NULL,
    transaction_type VARCHAR(30) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    description VARCHAR(255),
    transaction_date DATETIME NOT NULL,

    FOREIGN KEY (account_number)
    REFERENCES BankAccounts(account_number),

    CHECK (amount > 0)
) ENGINE=InnoDB;

-- 5. Fixed Deposits Table
CREATE TABLE IF NOT EXISTS FixedDeposits (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(40) NOT NULL,
    principal DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(6,2) NOT NULL,
    tenure_months INT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',

    FOREIGN KEY (account_number)
    REFERENCES BankAccounts(account_number),

    CHECK (principal > 0),
    CHECK (interest_rate >= 0),
    CHECK (tenure_months > 0)
) ENGINE=InnoDB;

-- 6. Loans Table
CREATE TABLE IF NOT EXISTS Loans (
    id INT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(40) NOT NULL,
    principal DECIMAL(15,2) NOT NULL,
    interest_rate DECIMAL(6,2) NOT NULL,
    tenure_months INT NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',

    FOREIGN KEY (account_number)
    REFERENCES BankAccounts(account_number),

    CHECK (principal > 0),
    CHECK (interest_rate >= 0),
    CHECK (tenure_months > 0)
) ENGINE=InnoDB;

-- 7. Loan Repayments Table
CREATE TABLE IF NOT EXISTS LoanRepayments (
    id INT AUTO_INCREMENT PRIMARY KEY,
    loan_id INT NOT NULL,
    repayment_amount DECIMAL(15,2) NOT NULL,
    repayment_date DATETIME NOT NULL,
    remaining_after DECIMAL(15,2) NOT NULL,

    FOREIGN KEY (loan_id)
    REFERENCES Loans(id),

    CHECK (repayment_amount > 0),
    CHECK (remaining_after >= 0)
) ENGINE=InnoDB;

-- Default Admin Account
INSERT INTO Admins
(name, email, password_hash, admin_code)
VALUES
(
    'Agamdeep Singh',
    'agam@bank.com',
    '5278727e514243444546',
    'AGAM2026'
)
ON DUPLICATE KEY UPDATE
    name = 'Agamdeep Singh',
    password_hash = '5278727e514243444546',
    admin_code = 'AGAM2026';
    
-- Display created tables
SHOW TABLES;
