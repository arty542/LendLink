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
import com.google.gson.Gson;
import java.util.Map;


@WebServlet({"/dashboard", "/api/dashboard"})
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(DashboardServlet.class);
    private UserDao userDao;
    private LoanDao loanDao;
    private WalletDao walletDao;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        userDao = new UserDao();
        loanDao = new LoanDao();
        walletDao = new WalletDao();
        gson = new Gson();
        logger.info("DashboardServlet initialized");
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        logger.info("--- Entering DashboardServlet doGet method ---");

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            logger.warn("Session is null or user not logged in");
            if (request.getRequestURI().contains("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(gson.toJson(Map.of("error", "User not logged in")));
                return;
            }
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");
        logger.info("Session valid - userId: " + userId + ", userRole: " + userRole);

        try {
            User user = userDao.getUserById(userId);
            if (user == null) {
                logger.warn("User not found for userId: " + userId);
                if (request.getRequestURI().contains("/api/")) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write(gson.toJson(Map.of("error", "User not found")));
                    return;
                }
                response.sendRedirect("login.jsp");
                return;
            }

            logger.info("User found: " + user.getName());
            DashboardData dashboardData = getDashboardData(userId, userRole);
            logger.info("Dashboard data successfully assembled for userId: " + userId);

            if (request.getRequestURI().contains("/api/")) {
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                String jsonResponse = gson.toJson(dashboardData);
                logger.info("Sending dashboard JSON response: " + jsonResponse);
                response.getWriter().write(jsonResponse);
                response.getWriter().flush();
            } else {
                logger.info("Forwarding to dashboard.jsp");
                request.setAttribute("dashboardData", dashboardData);
                request.setAttribute("user", user);
                request.getRequestDispatcher("dashboard.jsp").forward(request, response);
            }

        } catch (Exception e) {
            logger.error("Error loading dashboard data", e);
            if (request.getRequestURI().contains("/api/")) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(gson.toJson(Map.of("error", "An error occurred while loading dashboard data")));
            } else {
                request.setAttribute("error", "An error occurred while loading your dashboard. Please try again later.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
        }
    }

    private DashboardData getDashboardData(int userId, String userRole) throws Exception {
        logger.info("Fetching dashboard data for userId: " + userId + ", role: " + userRole);
        DashboardData dashboardData = new DashboardData();
        dashboardData.setUserRole(userRole);

        if ("borrower".equals(userRole) || "both".equals(userRole)) {
            int loansTaken = loanDao.getLoansTakenCount(userId);
            double totalBorrowed = loanDao.getTotalAmountFundedForUser(userId);
            logger.info("Borrower stats - LoansTaken: " + loansTaken + ", TotalBorrowed: " + totalBorrowed);

            dashboardData.setLoansTaken(loansTaken);
            dashboardData.setTotalBorrowed(totalBorrowed);
        }

        if ("lender".equals(userRole) || "both".equals(userRole)) {
            int loansFunded = loanDao.getLoansFundedCount(userId);
            double totalLent = loanDao.getTotalAmountLent(userId);
            logger.info("Lender stats - LoansFunded: " + loansFunded + ", TotalLent: " + totalLent);

            dashboardData.setLoansFunded(loansFunded);
            dashboardData.setTotalLent(totalLent);
            dashboardData.setTotalFunded(totalLent);  // Redundant but kept for consistency

            try {
                int availableLoans = loanDao.getAvailableLoansCount(userId);
                logger.info("Available loans: " + availableLoans);
                dashboardData.setAvailableLoans(availableLoans);
            } catch (Exception e) {
                logger.warn("Error getting available loans count: " + e.getMessage());
                dashboardData.setAvailableLoans(0);
            }
        }

        double walletBalance = walletDao.getBalance(userId);
        logger.info("Wallet balance: " + walletBalance);
        dashboardData.setWalletBalance(walletBalance);

        List<DashboardData.Activity> recentActivity = loanDao.getRecentActivity(userId);
        logger.info("Recent activity count: " + (recentActivity != null ? recentActivity.size() : 0));
        dashboardData.setRecentActivity(recentActivity);

        return dashboardData;
    }
}