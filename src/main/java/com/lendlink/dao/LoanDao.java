package com.lendlink.dao;

import com.lendlink.model.LoanRequest;
import com.lendlink.model.DashboardData.Activity;
import com.lendlink.model.Loan;
import com.lendlink.util.DatabaseConnection;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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
            e.printStackTrace();
        }
        logger.warn("No loans funded found or error occurred for user ID: " + userId);
        return 0;
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

    public List<Loan> getLoansByUserId(int userId) {
        logger.info("Attempting to get loans for user ID: " + userId);
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM Loan WHERE borrower_id = ? OR lender_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            pstmt.setInt(2, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Loan loan = new Loan();
                    loan.setLoanId(rs.getInt("loan_id"));
                    loan.setBorrowerId(rs.getInt("borrower_id"));
                    loan.setLenderId(rs.getInt("lender_id"));
                    loan.setAmount(rs.getDouble("amount"));
                    loan.setInterestRate(rs.getDouble("interest_rate"));
                    loan.setTerm(rs.getInt("term"));
                    loan.setStatus(rs.getString("status"));
                    loan.setCreatedAt(rs.getTimestamp("created_at"));
                    loan.setUpdatedAt(rs.getTimestamp("updated_at"));
                    loans.add(loan);
                }
                logger.info("Found " + loans.size() + " loans for user ID: " + userId);
            }
        } catch (SQLException e) {
            logger.error("Error getting loans for user ID: " + userId, e);
        }
        return loans;
    }

    public boolean createLoan(Loan loan) {
        logger.info("Attempting to create loan - Borrower: " + loan.getBorrowerId() + 
                   ", Lender: " + loan.getLenderId() + 
                   ", Amount: " + loan.getAmount());
        String sql = "INSERT INTO Loan (borrower_id, lender_id, amount, interest_rate, term, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, loan.getBorrowerId());
            pstmt.setInt(2, loan.getLenderId());
            pstmt.setDouble(3, loan.getAmount());
            pstmt.setDouble(4, loan.getInterestRate());
            pstmt.setInt(5, loan.getTerm());
            pstmt.setString(6, loan.getStatus());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully created loan");
                return true;
            } else {
                logger.warn("Failed to create loan - no rows affected");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error creating loan", e);
            return false;
        }
    }

    public boolean updateLoanStatus(int loanId, String status) {
        logger.info("Attempting to update loan status - Loan ID: " + loanId + ", New Status: " + status);
        String sql = "UPDATE LoanRequest SET status = ? WHERE loan_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, loanId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully updated loan status to: " + status);
                return true;
            } else {
                logger.warn("No loan found with ID: " + loanId);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error updating loan status", e);
            throw new RuntimeException("Failed to update loan status", e);
        }
    }

    public Loan getLoanById(int loanId) {
        logger.info("Attempting to get loan by ID: " + loanId);
        String sql = "SELECT * FROM Loan WHERE loan_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, loanId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Loan loan = new Loan();
                    loan.setLoanId(rs.getInt("loan_id"));
                    loan.setBorrowerId(rs.getInt("borrower_id"));
                    loan.setLenderId(rs.getInt("lender_id"));
                    loan.setAmount(rs.getDouble("amount"));
                    loan.setInterestRate(rs.getDouble("interest_rate"));
                    loan.setTerm(rs.getInt("term"));
                    loan.setStatus(rs.getString("status"));
                    loan.setCreatedAt(rs.getTimestamp("created_at"));
                    loan.setUpdatedAt(rs.getTimestamp("updated_at"));
                    logger.info("Found loan with ID: " + loanId);
                    return loan;
                } else {
                    logger.info("No loan found with ID: " + loanId);
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting loan by ID: " + loanId, e);
            return null;
        }
    }

    public List<LoanRequest> getAvailableLoanRequests(Double minAmount, Double maxAmount, 
            Integer duration, String purpose, String sortBy) {
        logger.info("Getting available loan requests with filters");
        List<LoanRequest> loanRequests = new ArrayList<>();
        
        StringBuilder sql = new StringBuilder(
            "SELECT * FROM LoanRequest WHERE status = 'open'");
        
        List<Object> params = new ArrayList<>();
        
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
        logger.info("Attempting to fund loan - Loan ID: " + loanId + ", Lender ID: " + lenderId);
        String sql = "INSERT INTO Funding (loan_id, lender_id, amount_funded) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, loanId);
            pstmt.setInt(2, lenderId);
            pstmt.setDouble(3, amount);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully funded loan");
                return true;
            } else {
                logger.warn("Failed to fund loan - no rows affected");
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error funding loan", e);
            return false;
        }
    }

    public List<LoanRequest> getAvailableLoans(Double minAmount, Double maxAmount, Integer duration, String purpose, String sortBy) {
        List<LoanRequest> loans = new ArrayList<>();
        String sql = "SELECT * FROM LoanRequest WHERE status = 'open'";
        List<Object> params = new ArrayList<>();
        
        if (minAmount != null) {
            sql += " AND amount >= ?";
            params.add(minAmount);
        }
        if (maxAmount != null) {
            sql += " AND amount <= ?";
            params.add(maxAmount);
        }
        if (duration != null) {
            sql += " AND duration_months = ?";
            params.add(duration);
        }
        if (purpose != null && !purpose.isEmpty()) {
            sql += " AND purpose LIKE ?";
            params.add("%" + purpose + "%");
        }
        
        if (sortBy != null && !sortBy.isEmpty()) {
            sql += " ORDER BY " + sortBy;
        } else {
            sql += " ORDER BY created_on DESC";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LoanRequest loan = new LoanRequest();
                loan.setLoanId(rs.getInt("loan_id"));
                loan.setUserId(rs.getInt("user_id"));
                loan.setAmount(rs.getDouble("amount"));
                loan.setDurationMonths(rs.getInt("duration_months"));
                loan.setPurpose(rs.getString("purpose"));
                loan.setStatus(rs.getString("status"));
                loan.setCreatedOn(rs.getTimestamp("created_on"));
                loans.add(loan);
            }
        } catch (SQLException e) {
            logger.error("Error getting available loans", e);
        }
        return loans;
    }

    public int getAvailableLoansCount() {
        String sql = "SELECT COUNT(*) as count FROM LoanRequest WHERE status = 'open'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            logger.error("Error getting available loans count", e);
        }
        return 0;
    }

    public double getTotalAmountFunded(int userId) {
        // This method is redundant with getTotalAmountLent
        // Using getTotalAmountLent instead
        return getTotalAmountLent(userId);
    }
} 