package com.lendlink.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.lendlink.dao.UserDao;
import com.lendlink.dao.LoanDao;
import com.lendlink.dao.WalletDao;
import com.lendlink.model.User;
import com.lendlink.model.DashboardData;
import org.apache.log4j.Logger;
import java.util.List;
import com.lendlink.model.Transaction;
import java.util.ArrayList;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(DashboardServlet.class);
    private UserDao userDao;
    private LoanDao loanDao;
    private WalletDao walletDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDao();
        loanDao = new LoanDao();
        walletDao = new WalletDao();
        logger.info("DashboardServlet initialized");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        logger.info("--- Entering DashboardServlet doGet method ---");
        HttpSession session = request.getSession(false);
        
        logger.info("Accessing DashboardServlet");

        // Check if user is logged in
        if (session == null || session.getAttribute("userId") == null) {
            logger.warn("User not logged in, redirecting to login.jsp");
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        logger.info("User logged in. User ID: " + userId + ", Role: " + userRole);

        try {
            // Get user data
            User user = userDao.getUserById(userId);
            if (user == null) {
                logger.warn("User object is null for userId: " + userId);
                response.sendRedirect("login.jsp");
                return;
            }
            logger.info("User object retrieved successfully");

            // Get dashboard data based on user role
            DashboardData dashboardData = new DashboardData();
            
            if ("borrower".equals(userRole) || "both".equals(userRole)) {
                int loansTaken = loanDao.getLoansTakenCount(userId);
                double totalBorrowed = loanDao.getTotalAmountBorrowed(userId);
                dashboardData.setLoansTaken(loansTaken);
                dashboardData.setTotalBorrowed(totalBorrowed);
                logger.info("Borrower data fetched - Loans Taken: " + loansTaken + ", Total Borrowed: " + totalBorrowed);
            }
            
            if ("lender".equals(userRole) || "both".equals(userRole)) {
                int loansFunded = loanDao.getLoansFundedCount(userId);
                double totalLent = loanDao.getTotalAmountLent(userId);
                dashboardData.setLoansFunded(loansFunded);
                dashboardData.setTotalLent(totalLent);
                dashboardData.setTotalFunded(totalLent);
                logger.info("Lender data fetched - Loans Funded: " + loansFunded + ", Total Lent: " + totalLent);
            }
            
            // Get wallet balance
            double walletBalance = walletDao.getBalance(userId);
            dashboardData.setWalletBalance(walletBalance);
            logger.info("Wallet Balance fetched: " + walletBalance);
            
            try {
                // Get available loans count
                int availableLoans = loanDao.getAvailableLoansCount();
                dashboardData.setAvailableLoans(availableLoans);
            } catch (Exception e) {
                logger.warn("Error getting available loans count: " + e.getMessage());
                dashboardData.setAvailableLoans(0);
            }

            try {
                // Get recent transactions
                List<Transaction> recentTransactions = walletDao.getRecentTransactions(userId, 5);
                request.setAttribute("recentTransactions", recentTransactions);
            } catch (Exception e) {
                logger.warn("Error getting recent transactions: " + e.getMessage());
                request.setAttribute("recentTransactions", new ArrayList<>());
            }
            
            // Get recent activity
            java.util.List<DashboardData.Activity> recentActivity = loanDao.getRecentActivity(userId);
            dashboardData.setRecentActivity(recentActivity);
            logger.info("Recent Activity fetched. Count: " + (recentActivity != null ? recentActivity.size() : 0));

            // Set attributes for JSP
            request.setAttribute("dashboardData", dashboardData);
            request.setAttribute("user", user);
            logger.info("Dashboard data set as request attribute, forwarding to dashboard.jsp");
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.error("Error loading dashboard data", e);
            request.setAttribute("error", "An error occurred while loading your dashboard. Please try again later.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
} 