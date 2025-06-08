package com.lendlink.dao;

import com.lendlink.util.DatabaseConnection;
import com.lendlink.model.Transaction;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Types;

public class WalletDao {
    private static final Logger logger = Logger.getLogger(WalletDao.class);

    public double getBalance(int userId) {
        logger.info("Getting balance for user ID: " + userId);
        String sql = "SELECT balance FROM Wallet WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double balance = rs.getDouble("balance");
                    logger.info("Balance for user ID " + userId + ": " + balance);
                    return balance;
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting balance for user ID: " + userId, e);
        }
        return 0.0;
    }

    public boolean createWallet(int userId) {
        logger.info("Creating wallet for user ID: " + userId);
        String sql = "INSERT INTO Wallet (user_id, balance) VALUES (?, 0.00)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Successfully created wallet for user ID: " + userId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error creating wallet for user ID: " + userId, e);
        }
        return false;
    }

    public boolean deposit(int userId, double amount) {
        if (amount <= 0) {
            logger.warn("Invalid deposit amount: " + amount);
            return false;
        }

        logger.info("Processing deposit for user ID: " + userId + ", amount: " + amount);
        String sql = "UPDATE Wallet SET balance = balance + ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // Record the deposit transaction
                recordTransaction(0, userId, amount, "deposit");
                logger.info("Successfully processed deposit for user ID: " + userId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error processing deposit for user ID: " + userId, e);
        }
        return false;
    }

    public boolean withdraw(int userId, double amount) {
        if (amount <= 0) {
            logger.warn("Invalid withdrawal amount: " + amount);
            return false;
        }

        logger.info("Processing withdrawal for user ID: " + userId + ", amount: " + amount);
        String sql = "UPDATE Wallet SET balance = balance - ? WHERE user_id = ? AND balance >= ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setInt(2, userId);
            pstmt.setDouble(3, amount);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // Record the withdrawal transaction
                recordTransaction(userId, 0, amount, "withdrawal");
                logger.info("Successfully processed withdrawal for user ID: " + userId);
                return true;
            }
        } catch (SQLException e) {
            logger.error("Error processing withdrawal for user ID: " + userId, e);
        }
        return false;
    }

    public void recordTransaction(int fromUserId, int toUserId, double amount, String type) {
        String sql = "INSERT INTO Transaction (from_user_id, to_user_id, amount, type, created_on) VALUES (?, ?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Handle system transactions (deposits/withdrawals)
            if (fromUserId == 0) {
                stmt.setNull(1, Types.INTEGER);  // from_user_id is NULL for deposits
            } else {
                stmt.setInt(1, fromUserId);
            }
            
            if (toUserId == 0) {
                stmt.setNull(2, Types.INTEGER);  // to_user_id is NULL for withdrawals
            } else {
                stmt.setInt(2, toUserId);
            }
            
            stmt.setDouble(3, amount);
            stmt.setString(4, type);
            stmt.executeUpdate();
            logger.info("Transaction recorded successfully - From: " + fromUserId + ", To: " + toUserId + ", Amount: " + amount + ", Type: " + type);
        } catch (SQLException e) {
            logger.error("Error recording transaction", e);
            throw new RuntimeException("Failed to record transaction", e);
        }
    }

    public List<Transaction> getRecentTransactions(int userId, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transaction WHERE from_user_id = ? OR to_user_id = ? ORDER BY created_on DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Transaction t = new Transaction();
                t.setTransactionId(rs.getInt("transaction_id"));
                t.setFromUserId(rs.getInt("from_user_id"));
                t.setToUserId(rs.getInt("to_user_id"));
                t.setAmount(rs.getDouble("amount"));
                t.setType(rs.getString("type"));
                t.setCreatedOn(rs.getTimestamp("created_on"));
                transactions.add(t);
            }
        } catch (SQLException e) {
            logger.error("Error getting recent transactions", e);
        }
        return transactions;
    }
} 