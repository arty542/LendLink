package com.lendlink.dao;

import com.lendlink.model.LoanRequest;
import com.lendlink.model.DashboardData.Activity;
import com.lendlink.model.Repayment;
import com.lendlink.util.DatabaseConnection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

public class LoanDao {
    private static final Logger logger = Logger.getLogger(LoanDao.class);
    
    public List<LoanRequest> getLoanRequestsByUserId(int userId) {
        logger.info("Attempting to get loan requests for user ID: " + userId);
        List<LoanRequest> loanRequests = new ArrayList<>();
        String sql = "SELECT * FROM LoanRequest WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LoanRequest loanRequest = new LoanRequest();
                    loanRequest.setLoanId(rs.getInt("loan_id"));
                    loanRequest.setUserId(rs.getInt("user_id"));
                    loanRequest.setAmount(rs.getDouble("amount"));
                    loanRequest.setDurationMonths(rs.getInt("duration_months"));
                    loanRequest.setStatus(rs.getString("status"));
                    loanRequest.setCreatedOn(rs.getTimestamp("created_on"));
                    loanRequests.add(loanRequest);
                }
                logger.info("Found " + loanRequests.size() + " loan requests for user ID: " + userId);
            }
        } catch (SQLException e) {
            logger.error("Error getting loan requests for user ID: " + userId, e);
        }
        return loanRequests;
    }

    public boolean createLoanRequest(LoanRequest loanRequest) {
        logger.info("Attempting to create loan request - User ID: " + loanRequest.getUserId() + 
                   ", Amount: " + loanRequest.getAmount());
        String sql = "INSERT INTO LoanRequest (user_id, amount, duration_months, purpose, description, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, loanRequest.getUserId());
            pstmt.setDouble(2, loanRequest.getAmount());
            pstmt.setInt(3, loanRequest.getDurationMonths());
            pstmt.setString(4, loanRequest.getPurpose());
            pstmt.setString(5, loanRequest.getDescription());
            pstmt.setString(6, loanRequest.getStatus());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully created loan request");
                return true;
            } else {
                logger.warn("Failed to create loan request - no rows affected");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error creating loan request", e);
            return false;
        }
    }

    public boolean updateLoanRequestStatus(int loanId, String status) {
        logger.info("Attempting to update loan request status - Loan ID: " + loanId + ", New Status: " + status);
        String sql = "UPDATE LoanRequest SET status = ? WHERE loan_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);
            pstmt.setInt(2, loanId);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully updated loan request status");
                return true;
            } else {
                logger.warn("Failed to update loan request status - no rows affected");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error updating loan request status", e);
            return false;
        }
    }

    public LoanRequest getLoanRequestById(int loanId) {
        logger.info("Attempting to get loan request by ID: " + loanId);
        String sql = "SELECT * FROM LoanRequest WHERE loan_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, loanId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    LoanRequest loanRequest = new LoanRequest();
                    loanRequest.setLoanId(rs.getInt("loan_id"));
                    loanRequest.setUserId(rs.getInt("user_id"));
                    loanRequest.setAmount(rs.getDouble("amount"));
                    loanRequest.setDurationMonths(rs.getInt("duration_months"));
                    loanRequest.setStatus(rs.getString("status"));
                    loanRequest.setCreatedOn(rs.getTimestamp("created_on"));
                    logger.info("Found loan request with ID: " + loanId);
                    return loanRequest;
                } else {
                    logger.info("No loan request found with ID: " + loanId);
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting loan request by ID: " + loanId, e);
            return null;
        }
    }

    public int getLoansTakenCount(int userId) {
        logger.info("Getting loans taken count for user ID: " + userId);
        String sql = "SELECT COUNT(*) FROM LoanRequest WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.info("Found " + count + " loans taken for user ID: " + userId);
                    return count;
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting loans taken count for user ID: " + userId, e);
            e.printStackTrace();
        }
        logger.warn("No loans found or error occurred for user ID: " + userId);
        return 0;
    }

    public double getTotalAmountBorrowed(int userId) {
        logger.info("Getting total amount borrowed for user ID: " + userId);
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM LoanRequest WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double amount = rs.getDouble(1);
                    logger.info("Total amount borrowed for user ID " + userId + ": " + amount);
                    return amount;
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting total amount borrowed for user ID: " + userId, e);
            e.printStackTrace();
        }
        logger.warn("No amount found or error occurred for user ID: " + userId);
        return 0.0;
    }

    public List<LoanRequest> getAvailableLoanRequests(int userId, Double minAmount, Double maxAmount, 
            Integer duration, String purpose, String sortBy) {
        logger.info("Getting available loan requests with filters");
        List<LoanRequest> loanRequests = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM LoanRequest WHERE status = 'open' AND user_id != ?");
        
        List<Object> params = new ArrayList<>();
        params.add(userId); // Add userId as first parameter
        
        if (minAmount != null) {
            sql.append(" AND amount >= ?");
            params.add(minAmount);
        }
        
        if (maxAmount != null) {
            sql.append(" AND amount <= ?");
            params.add(maxAmount);
        }
        
        if (duration != null) {
            sql.append(" AND duration_months = ?");
            params.add(duration);
        }
        
        if (purpose != null && !purpose.isEmpty()) {
            sql.append(" AND purpose = ?");
            params.add(purpose);
        }
        
        // Add sorting
        if (sortBy != null) {
            switch (sortBy) {
                case "oldest":
                    sql.append(" ORDER BY created_on ASC");
                    break;
                case "amount_asc":
                    sql.append(" ORDER BY amount ASC");
                    break;
                case "amount_desc":
                    sql.append(" ORDER BY amount DESC");
                    break;
                default: // newest
                    sql.append(" ORDER BY created_on DESC");
            }
        } else {
            sql.append(" ORDER BY created_on DESC");
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {

            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LoanRequest loanRequest = new LoanRequest();
                    loanRequest.setLoanId(rs.getInt("loan_id"));
                    loanRequest.setUserId(rs.getInt("user_id"));
                    loanRequest.setAmount(rs.getDouble("amount"));
                    loanRequest.setDurationMonths(rs.getInt("duration_months"));
                    loanRequest.setPurpose(rs.getString("purpose"));
                    loanRequest.setDescription(rs.getString("description"));
                    loanRequest.setStatus(rs.getString("status"));
                    loanRequest.setCreatedOn(rs.getTimestamp("created_on"));
                    loanRequests.add(loanRequest);
                }
            }
            
            logger.info("Found " + loanRequests.size() + " available loan requests");
        } catch (SQLException e) {
            logger.error("Error getting available loan requests", e);
        }
        
        return loanRequests;
    }

    public boolean fundLoan(int loanId, int lenderId, double amount) {
        logger.info("Attempting to fund loan - Loan ID: " + loanId + ", Lender ID: " + lenderId + ", Amount: " + amount);
        
        // Start transaction
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            
            try {
                // Get current loan details
                LoanRequest loan = getLoanRequestById(loanId);
                if (loan == null) {
                    logger.error("Loan not found: " + loanId);
                    return false;
                }
                logger.debug("Original loan amount: " + loan.getAmount());

                // Get total amount funded so far
                double totalFunded = getTotalAmountFundedForLoan(loanId);
                logger.debug("Total amount funded so far: " + totalFunded);
                double remainingAmount = loan.getAmount();
                logger.debug("Calculated remaining amount: " + remainingAmount);

                // Validate funding amount
                if (amount <= 0 || amount > remainingAmount) {
                    logger.error("Invalid funding amount: " + amount + " for loan: " + loanId + ". Remaining amount: " + remainingAmount);
                    return false;
                }
                // Log the status before update
                logger.debug("Loan ID: " + loanId + " status BEFORE update: " + loan.getStatus());

                // Record the funding
                String fundingSql = "INSERT INTO Funding (loan_id, lender_id, amount_funded) VALUES (?, ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(fundingSql)) {
            pstmt.setInt(1, loanId);
            pstmt.setInt(2, lenderId);
            pstmt.setDouble(3, amount);
                    pstmt.executeUpdate();
                }

                // Update loan amount and status in LoanRequest table
                String updateLoanRequestSql = "UPDATE LoanRequest SET status = CASE WHEN (amount - ?) <= 0 THEN 'funded' ELSE status END, amount = amount - ? WHERE loan_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateLoanRequestSql)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setDouble(2, amount);
                    pstmt.setInt(3, loanId);
                    pstmt.executeUpdate();
                    // Log the status after update
                    LoanRequest updatedLoan = getLoanRequestById(loanId);
                    if (updatedLoan != null) {
                        logger.debug("Loan ID: " + loanId + " status after update: " + updatedLoan.getStatus());
                    }
                }

                conn.commit();
                logger.info("Successfully funded loan - Loan ID: " + loanId + ", Amount: " + amount);
                return true;

            } catch (SQLException e) {
                conn.rollback();
                logger.error("Error funding loan", e);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error in transaction", e);
            return false;
        }
    }

    public double getTotalAmountFundedForLoan(int loanId) {
        String sql = "SELECT COALESCE(SUM(amount_funded), 0) FROM Funding WHERE loan_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, loanId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting total amount funded for loan: " + loanId, e);
        }
        return 0.0;
    }

    public boolean isLoanFullyFunded(int loanId) {
        LoanRequest loan = getLoanRequestById(loanId);
        if (loan == null) {
            return false;
        }
        double totalFunded = getTotalAmountFundedForLoan(loanId);
        return totalFunded >= loan.getAmount();
    }

    public int getAvailableLoansCount(int userId) {
        String sql = "SELECT COUNT(*) as count FROM LoanRequest WHERE status = 'open' AND user_id != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            logger.error("Error getting available loans count", e);
        }
        return 0;
    }

    public double getTotalAmountFunded(int userId) {
        return getTotalAmountLent(userId);
    }

    public double getTotalAmountLent(int userId) {
        logger.info("Getting total amount lent for user ID: " + userId);
        String sql = "SELECT COALESCE(SUM(amount_funded), 0) FROM Funding WHERE lender_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    double amount = rs.getDouble(1);
                    logger.info("Total amount lent for user ID " + userId + ": " + amount);
                    return amount;
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting total amount lent for user ID: " + userId, e);
        }
        logger.warn("No amount found or error occurred for user ID: " + userId);
        return 0.0;
    }

    public int getLoansFundedCount(int userId) {
        logger.info("Getting loans funded count for user ID: " + userId);
        String sql = "SELECT COUNT(DISTINCT loan_id) FROM Funding WHERE lender_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    logger.info("Found " + count + " loans funded for user ID: " + userId);
                    return count;
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting loans funded count for user ID: " + userId, e);
        }
        logger.warn("No loans funded found or error occurred for user ID: " + userId);
        return 0;
    }

    public List<Activity> getRecentActivity(int userId) {
        logger.info("Getting recent activity for user ID: " + userId);
        List<Activity> activities = new ArrayList<>();
        String sql = "SELECT t.transaction_id, t.amount, t.created_on, t.type, " +
                    "u1.name as from_user_name, u2.name as to_user_name " +
                    "FROM Transaction t " +
                    "JOIN User u1 ON t.from_user_id = u1.user_id " +
                    "JOIN User u2 ON t.to_user_id = u2.user_id " +
                    "WHERE t.from_user_id = ? OR t.to_user_id = ? " +
                    "ORDER BY t.created_on DESC LIMIT 10";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    activities.add(new Activity(
                        rs.getInt("transaction_id"),
                        rs.getDouble("amount"),
                        rs.getTimestamp("created_on"),
                        rs.getString("type"),
                        rs.getString("from_user_name"),
                        rs.getString("to_user_name")
                    ));
                }
                logger.info("Found " + activities.size() + " recent activities for user ID: " + userId);
            }
        } catch (SQLException e) {
            logger.error("Error getting recent activity for user ID: " + userId, e);
        }
        if (activities.isEmpty()) {
            logger.warn("No recent activities found for user ID: " + userId);
        }
        return activities;
    }

    public Repayment getRepaymentById(int repaymentId) {
        String sql = "SELECT * FROM Repayment WHERE repayment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, repaymentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Repayment repayment = new Repayment();
                    repayment.setRepaymentId(rs.getInt("repayment_id"));
                    repayment.setLoanId(rs.getInt("loan_id"));
                    repayment.setDueDate(rs.getDate("due_date"));
                    repayment.setAmountDue(rs.getDouble("amount_due"));
                    repayment.setAmountPaid(rs.getDouble("amount_paid"));
                    repayment.setPaidOn(rs.getDate("paid_on"));
                    repayment.setStatus(rs.getString("status"));
                    return repayment;
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting repayment by ID: " + repaymentId, e);
        }
        return null;
    }

    public boolean updateRepayment(int repaymentId, double amountPaid, Date paidOn) {
        String sql = "UPDATE Repayment SET amount_paid = ?, paid_on = ?, status = 'paid' WHERE repayment_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, amountPaid);
            pstmt.setDate(2, paidOn);
            pstmt.setInt(3, repaymentId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Error updating repayment: " + repaymentId, e);
            return false;
        }
    }

    public List<Integer> getLoanFunders(int loanId) {
        List<Integer> funderIds = new ArrayList<>();
        String sql = "SELECT DISTINCT lender_id FROM Funding WHERE loan_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, loanId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    funderIds.add(rs.getInt("lender_id"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting loan funders for loan: " + loanId, e);
        }
        return funderIds;
    }

    public double getFunderAmountForLoan(int loanId, int funderId) {
        String sql = "SELECT amount_funded FROM Funding WHERE loan_id = ? AND lender_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, loanId);
            pstmt.setInt(2, funderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("amount_funded");
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting funder amount for loan: " + loanId + ", funder: " + funderId, e);
        }
        return 0.0;
    }

    public boolean areAllRepaymentsComplete(int loanId) {
        String sql = "SELECT COUNT(*) as total, SUM(CASE WHEN status = 'paid' THEN 1 ELSE 0 END) as paid " +
                    "FROM Repayment WHERE loan_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, loanId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int paid = rs.getInt("paid");
                    return total > 0 && total == paid;
                }
            }
        } catch (SQLException e) {
            logger.error("Error checking repayment completion for loan: " + loanId, e);
        }
        return false;
    }

    public boolean updateLoanStatus(int loanId, String status) {
        String sql = "UPDATE LoanRequest SET status = ? WHERE loan_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, loanId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Error updating loan status for loan: " + loanId, e);
            return false;
        }
    }

    public boolean deleteLoanRequest(int loanId, int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Get loan details first
                LoanRequest loan = getLoanRequestById(loanId);
                if (loan == null || loan.getUserId() != userId) {
                    return false;
                }

                // Get total amount funded
                double totalFunded = getTotalAmountFundedForLoan(loanId);
                
                // Get all funders and their amounts
                List<Integer> funderIds = getLoanFunders(loanId);
                
                // Return funds to lenders if any
                if (totalFunded > 0) {
                    for (int funderId : funderIds) {
                        double funderAmount = getFunderAmountForLoan(loanId, funderId);
                        // Add funds back to lender's wallet
                        WalletDao walletDao = new WalletDao();
                        walletDao.deposit(funderId, funderAmount);
                        // Record transaction
                        walletDao.recordTransaction(userId, funderId, funderAmount, "refund");
                    }
                }

                // Delete the loan request (this will cascade delete related records)
                String sql = "DELETE FROM LoanRequest WHERE loan_id = ? AND user_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, loanId);
                    pstmt.setInt(2, userId);
                    pstmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Error deleting loan request: " + loanId, e);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error in transaction for deleting loan request: " + loanId, e);
            return false;
        }
    }

    public List<Repayment> getRepaymentsForLoan(int loanId) {
        List<Repayment> repayments = new ArrayList<>();
        String sql = "SELECT * FROM Repayment WHERE loan_id = ? ORDER BY due_date";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, loanId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Repayment repayment = new Repayment();
                    repayment.setRepaymentId(rs.getInt("repayment_id"));
                    repayment.setLoanId(rs.getInt("loan_id"));
                    repayment.setDueDate(rs.getDate("due_date"));
                    repayment.setAmountDue(rs.getDouble("amount_due"));
                    repayment.setAmountPaid(rs.getDouble("amount_paid"));
                    repayment.setPaidOn(rs.getDate("paid_on"));
                    repayment.setStatus(rs.getString("status"));
                    repayments.add(repayment);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting repayments for loan: " + loanId, e);
        }
        return repayments;
    }

    public boolean createRepayment(Repayment repayment) {
        String sql = "INSERT INTO Repayment (loan_id, due_date, amount_due, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, repayment.getLoanId());
            pstmt.setDate(2, repayment.getDueDate());
            pstmt.setDouble(3, repayment.getAmountDue());
            pstmt.setString(4, repayment.getStatus());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            logger.error("Error creating repayment for loan: " + repayment.getLoanId(), e);
            return false;
        }
    }

    public List<LoanRequest> getLoansByUserId(int userId) {
        List<LoanRequest> loans = new ArrayList<>();
        String sql = "SELECT * FROM LoanRequest WHERE user_id = ? ORDER BY created_on DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LoanRequest loan = new LoanRequest();
                    loan.setLoanId(rs.getInt("loan_id"));
                    loan.setUserId(rs.getInt("user_id"));
                    loan.setAmount(rs.getDouble("amount"));
                    loan.setDurationMonths(rs.getInt("duration_months"));
                    loan.setPurpose(rs.getString("purpose"));
                    loan.setDescription(rs.getString("description"));
                    loan.setStatus(rs.getString("status"));
                    loan.setCreatedOn(rs.getTimestamp("created_on"));
                    // Set fundedAmount
                    double fundedAmount = getTotalAmountFundedForLoan(loan.getLoanId());
                    loan.setFundedAmount(fundedAmount);
                    loans.add(loan);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting loans for user: " + userId, e);
        }
        return loans;
    }

    public List<LoanRequest> getLoansFundedByUser(int userId) {
        List<LoanRequest> loans = new ArrayList<>();
        String sql = "SELECT DISTINCT l.* FROM LoanRequest l " +
                    "JOIN Funding f ON l.loan_id = f.loan_id " +
                    "WHERE f.lender_id = ? " +
                    "ORDER BY l.created_on DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LoanRequest loan = new LoanRequest();
                    loan.setLoanId(rs.getInt("loan_id"));
                    loan.setUserId(rs.getInt("user_id"));
                    loan.setAmount(rs.getDouble("amount"));
                    loan.setDurationMonths(rs.getInt("duration_months"));
                    loan.setPurpose(rs.getString("purpose"));
                    loan.setDescription(rs.getString("description"));
                    loan.setStatus(rs.getString("status"));
                    loan.setCreatedOn(rs.getTimestamp("created_on"));
                    // Set fundedAmount
                    double fundedAmount = getTotalAmountFundedForLoan(loan.getLoanId());
                    loan.setFundedAmount(fundedAmount);
                    loans.add(loan);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting loans funded by user: " + userId, e);
        }
        return loans;
    }

    public double getTotalAmountFundedForUser(int userId) {
        String sql = "SELECT COALESCE(SUM(f.amount_funded), 0) " +
                     "FROM Funding f " +
                     "JOIN LoanRequest l ON f.loan_id = l.loan_id " +
                     "WHERE l.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting total amount funded for user: " + userId, e);
        }
        return 0.0;
    }
} 