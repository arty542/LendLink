-- Drop existing tables if they exist
DROP TABLE IF EXISTS Repayment;
DROP TABLE IF EXISTS Funding;
DROP TABLE IF EXISTS Transaction;
DROP TABLE IF EXISTS LoanRequest;
DROP TABLE IF EXISTS Wallet;
DROP TABLE IF EXISTS User;

-- Create User table
CREATE TABLE User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('borrower', 'lender', 'both') NOT NULL,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create Wallet table
CREATE TABLE Wallet (
    wallet_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    UNIQUE KEY unique_user_wallet (user_id)
);

-- Create LoanRequest table
CREATE TABLE LoanRequest (
    loan_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    duration_months INT NOT NULL,
    purpose ENUM('personal', 'business', 'education', 'home', 'other') NOT NULL,
    description TEXT NOT NULL,
    status ENUM('open', 'funded', 'repaid', 'defaulted') DEFAULT 'open',
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Create Funding table
CREATE TABLE Funding (
    funding_id INT AUTO_INCREMENT PRIMARY KEY,
    loan_id INT NOT NULL,
    lender_id INT NOT NULL,
    amount_funded DECIMAL(10, 2) NOT NULL,
    funded_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (loan_id) REFERENCES LoanRequest(loan_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    FOREIGN KEY (lender_id) REFERENCES User(user_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Create Transaction table
CREATE TABLE Transaction (
    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
    from_user_id INT,
    to_user_id INT,
    amount DECIMAL(10, 2) NOT NULL,
    type ENUM('funding', 'repayment', 'withdrawal', 'deposit') NOT NULL,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_user_id) REFERENCES User(user_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE,
    FOREIGN KEY (to_user_id) REFERENCES User(user_id)
        ON DELETE SET NULL
        ON UPDATE CASCADE
);

-- Create Repayment table
CREATE TABLE Repayment (
    repayment_id INT AUTO_INCREMENT PRIMARY KEY,
    loan_id INT NOT NULL,
    due_date DATE NOT NULL,
    amount_due DECIMAL(10, 2) NOT NULL,
    amount_paid DECIMAL(10, 2) DEFAULT 0.00,
    paid_on DATE,
    status ENUM('pending', 'paid', 'late') DEFAULT 'pending',
    FOREIGN KEY (loan_id) REFERENCES LoanRequest(loan_id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

-- Mock data for User table
INSERT INTO User (user_id, name, email, password_hash, role, created_on) VALUES
(1, 'John Smith', 'john@example.com', 'password123', 'borrower', '2024-01-01 10:00:00'),
(2, 'Sarah Johnson', 'sarah@example.com', 'lender456', 'lender', '2024-01-02 11:30:00'),
(3, 'Michael Brown', 'michael@example.com', 'both789', 'both', '2024-01-03 09:15:00'),
(4, 'Emma Wilson', 'emma@example.com', 'test123', 'lender', '2024-01-04 14:20:00'),
(5, 'David Lee', 'david@example.com', 'pass123', 'borrower', '2024-01-05 16:45:00');

-- Mock data for Wallet table
INSERT INTO Wallet (wallet_id, user_id, balance, created_at) VALUES
(1, 1, 12050.00, '2024-01-01 10:00:00'),  -- John (borrower) has 2 loans: 5000 + 7500, paid 450
(2, 2, 15000.00, '2024-01-02 11:30:00'),  -- Sarah (lender)
(3, 3, 24325.00, '2024-01-03 09:15:00'),  -- Michael (both) has 2 loans: 10000 + 15000, paid 675
(4, 4, 20000.00, '2024-01-04 14:20:00'),  -- Emma (lender)
(5, 5, 3000.00, '2024-01-05 16:45:00');   -- David (borrower) has 1 loan: 3000

-- Mock data for LoanRequest table with new fields
INSERT INTO LoanRequest (loan_id, user_id, amount, duration_months, purpose, description, status, created_on) VALUES
(1, 1, 5000.00, 12, 'business', 'Starting a small online retail business selling handmade crafts', 'open', '2024-02-01 09:00:00'),
(2, 3, 10000.00, 24, 'education', 'Funding my master''s degree in Computer Science', 'funded', '2024-02-02 10:30:00'),
(3, 5, 3000.00, 6, 'personal', 'Emergency medical expenses and treatment', 'open', '2024-02-03 11:45:00'),
(4, 1, 7500.00, 18, 'home', 'Home renovation and repair work', 'funded', '2024-02-04 13:15:00'),
(5, 3, 15000.00, 36, 'business', 'Expanding my existing restaurant business with a new location', 'open', '2024-02-05 15:30:00');

-- Mock data for Funding table
INSERT INTO Funding (funding_id, loan_id, lender_id, amount_funded, funded_on) VALUES
(1, 2, 2, 5000.00, '2024-02-02 14:30:00'),
(2, 2, 4, 5000.00, '2024-02-02 16:45:00'),
(3, 4, 2, 7500.00, '2024-02-04 17:20:00');

-- Mock data for Transaction table
INSERT INTO Transaction (transaction_id, from_user_id, to_user_id, amount, type) VALUES
(1, 2, 1, 5000.00, 'funding'),
(2, 4, 1, 5000.00, 'funding'),
(3, 1, 2, 450.00, 'repayment'),
(4, 2, 3, 7500.00, 'funding'),
(5, 3, 2, 675.00, 'repayment'),
(6, 5, 2, 1000.00, 'deposit'),
(7, 4, 1, 500.00, 'withdrawal');

-- Mock data for Repayment table
INSERT INTO Repayment (repayment_id, loan_id, due_date, amount_due, amount_paid, paid_on, status) VALUES
(1, 2, '2024-03-02', 450.00, 450.00, '2024-03-02', 'paid'),
(2, 2, '2024-04-02', 450.00, 0.00, NULL, 'pending'),
(3, 2, '2024-05-02', 450.00, 0.00, NULL, 'pending'),
(4, 4, '2024-03-04', 675.00, 675.00, '2024-03-04', 'paid'),
(5, 4, '2024-04-04', 675.00, 0.00, NULL, 'pending'),
(6, 4, '2024-05-04', 675.00, 0.00, NULL, 'pending'); 