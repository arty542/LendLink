CREATE DATABASE IF NOT EXISTS lendlink;
USE lendlink;

CREATE TABLE User (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('borrower', 'lender', 'both') NOT NULL,
    created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

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

CREATE TABLE Transaction (
    transaction_id INT PRIMARY KEY AUTO_INCREMENT,
    from_user_id INT,
    to_user_id INT,
    amount DECIMAL(10,2),
    type ENUM('funding', 'repayment', 'withdrawal', 'deposit') NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_user_id) REFERENCES User(user_id),
    FOREIGN KEY (to_user_id) REFERENCES User(user_id)
);

CREATE TABLE Wallet (
    wallet_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    balance DECIMAL(10,2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(user_id),
    UNIQUE KEY unique_user_wallet (user_id)
); 