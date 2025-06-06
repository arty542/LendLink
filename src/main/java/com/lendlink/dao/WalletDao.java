package com.lendlink.dao;

import com.lendlink.model.Wallet;
import com.lendlink.util.DatabaseConnection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class WalletDao {
    private static final Logger logger = Logger.getLogger(WalletDao.class);

    public Wallet getWalletByUserId(int userId) {
        logger.info("Attempting to get wallet for user ID: " + userId);
        Wallet wallet = null;
        String sql = "SELECT wallet_id, user_id, balance FROM Wallet WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    wallet = new Wallet();
                    wallet.setWalletId(rs.getInt("wallet_id"));
                    wallet.setUserId(rs.getInt("user_id"));
                    wallet.setBalance(rs.getDouble("balance"));
                    logger.info("Wallet found for user ID: " + userId + " with balance: " + wallet.getBalance());
                } else {
                    logger.info("No wallet found for user ID: " + userId);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting wallet for user ID: " + userId, e);
        }
        return wallet;
    }

    public boolean createWallet(Wallet wallet) {
        logger.info("Attempting to create wallet for user ID: " + wallet.getUserId());
        String sql = "INSERT INTO Wallet (user_id, balance) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, wallet.getUserId());
            pstmt.setDouble(2, wallet.getBalance());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully created wallet for user ID: " + wallet.getUserId());
                return true;
            } else {
                logger.warn("Failed to create wallet for user ID: " + wallet.getUserId());
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error creating wallet for user ID: " + wallet.getUserId(), e);
            return false;
        }
    }

    public boolean updateBalance(int walletId, double newBalance) {
        logger.info("Attempting to update balance for wallet ID: " + walletId + " to: " + newBalance);
        String sql = "UPDATE Wallet SET balance = ? WHERE wallet_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, newBalance);
            pstmt.setInt(2, walletId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully updated balance for wallet ID: " + walletId);
                return true;
            } else {
                logger.warn("Failed to update balance for wallet ID: " + walletId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error updating balance for wallet ID: " + walletId, e);
            return false;
        }
    }

    public double getWalletBalance(int userId) {
        logger.info("Getting wallet balance for user ID: " + userId);
        String sql = "SELECT " +
                    "COALESCE(SUM(CASE WHEN to_user_id = ? THEN amount ELSE -amount END), 0) as balance " +
                    "FROM Transaction " +
                    "WHERE from_user_id = ? OR to_user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);
            pstmt.setInt(3, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double balance = rs.getDouble("balance");
                    logger.info("Wallet balance for user ID " + userId + ": " + balance);
                    return balance;
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting wallet balance for user ID: " + userId, e);
        }
        logger.warn("No wallet balance found or error occurred for user ID: " + userId);
        return 0.0;
    }

    public boolean recordTransaction(int fromUserId, int toUserId, double amount, String type) {
        logger.info("Recording transaction - From: " + fromUserId + ", To: " + toUserId + 
                   ", Amount: " + amount + ", Type: " + type);
        String sql = "INSERT INTO Transaction (from_user_id, to_user_id, amount, type) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, fromUserId);
            pstmt.setInt(2, toUserId);
            pstmt.setDouble(3, amount);
            pstmt.setString(4, type);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully recorded transaction");
                return true;
            } else {
                logger.warn("Failed to record transaction - no rows affected");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error recording transaction", e);
            return false;
        }
    }

    public boolean recordFunding(int loanId, int lenderId, double amount) {
        logger.info("Recording funding - Loan ID: " + loanId + ", Lender ID: " + lenderId + 
                   ", Amount: " + amount);
        String sql = "INSERT INTO Funding (loan_id, lender_id, amount_funded) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, loanId);
            pstmt.setInt(2, lenderId);
            pstmt.setDouble(3, amount);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully recorded funding");
                return true;
            } else {
                logger.warn("Failed to record funding - no rows affected");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error recording funding", e);
            return false;
        }
    }

    public boolean recordRepayment(int loanId, double amountDue, double amountPaid) {
        logger.info("Recording repayment - Loan ID: " + loanId + 
                   ", Amount Due: " + amountDue + ", Amount Paid: " + amountPaid);
        String sql = "INSERT INTO Repayment (loan_id, due_date, amount_due, amount_paid, status) " +
                    "VALUES (?, CURRENT_DATE, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, loanId);
            pstmt.setDouble(2, amountDue);
            pstmt.setDouble(3, amountPaid);
            pstmt.setString(4, amountPaid >= amountDue ? "paid" : "pending");

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully recorded repayment");
                return true;
            } else {
                logger.warn("Failed to record repayment - no rows affected");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error recording repayment", e);
            return false;
        }
    }
} 