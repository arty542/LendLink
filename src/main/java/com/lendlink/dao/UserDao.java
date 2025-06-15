package com.lendlink.dao;

import com.lendlink.model.User;
import com.lendlink.util.DatabaseConnection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    private static final Logger logger = Logger.getLogger(UserDao.class);

    public User getUserByEmailAndPassword(String email, String password) {
        logger.info("Attempting to get user by email: " + email);
        User user = null;
        String sql = "SELECT user_id, name, email, role, created_on FROM User WHERE email = ? AND password_hash = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setCreatedOn(rs.getString("created_on"));
                    logger.info("User found: " + user.getName());
                } else {
                    logger.info("No user found with email: " + email);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting user by email: " + email, e);
        }
        return user;
    }

    public User getUserById(int userId) {
        logger.info("Attempting to get user by ID: " + userId);
        User user = null;
        String sql = "SELECT user_id, name, email, role, created_on FROM User WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setCreatedOn(rs.getString("created_on"));
                    logger.info("User found: " + user.getName());
                } else {
                    logger.info("No user found with ID: " + userId);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting user by ID: " + userId, e);
        }
        return user;
    }

    public boolean createUser(User user) {
        logger.info("Attempting to create new user: " + user.getEmail());
        String sql = "INSERT INTO User (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPasswordHash());
            pstmt.setString(4, user.getRole());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                // Get the generated user ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        // Create wallet for the new user
                        WalletDao walletDao = new WalletDao();
                        boolean walletCreated = walletDao.createWallet(userId);
                        if (walletCreated) {
                            logger.info("Successfully created user and wallet: " + user.getEmail());
                            return true;
                        } else {
                            logger.error("Failed to create wallet for user: " + user.getEmail());
                            return false;
                        }
                    }
                }
                logger.info("Successfully created user: " + user.getEmail());
                return true;
            } else {
                logger.warn("Failed to create user: " + user.getEmail());
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error creating user: " + user.getEmail(), e);
            return false;
        }
    }
} 