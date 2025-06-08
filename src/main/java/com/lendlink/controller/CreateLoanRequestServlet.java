package com.lendlink.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.lendlink.dao.LoanDao;
import com.lendlink.model.LoanRequest;
import org.apache.log4j.Logger;

@WebServlet("/create-loan-request")
public class CreateLoanRequestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(CreateLoanRequestServlet.class);
    private LoanDao loanDao;

    @Override
    public void init() throws ServletException {
        loanDao = new LoanDao();
        logger.info("CreateLoanRequestServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String userRole = (String) session.getAttribute("userRole");
        
        if (userRole == null || (!userRole.equals("borrower") && !userRole.equals("both"))) {
            logger.warn("Unauthorized access attempt to create loan request");
            response.sendRedirect("dashboard");
            return;
        }
        
        request.getRequestDispatcher("create-loan-request.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        if (userId == null || userRole == null || (!userRole.equals("borrower") && !userRole.equals("both"))) {
            logger.warn("Unauthorized loan request creation attempt");
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Get and validate input parameters
            double amount = validateAmount(request.getParameter("amount"));
            int duration = validateDuration(request.getParameter("duration"));
            String purpose = validatePurpose(request.getParameter("purpose"));
            String description = validateDescription(request.getParameter("description"));

            // Create loan request
            LoanRequest loanRequest = new LoanRequest();
            loanRequest.setUserId(userId);
            loanRequest.setAmount(amount);
            loanRequest.setDurationMonths(duration);
            loanRequest.setPurpose(purpose);
            loanRequest.setDescription(description);
            loanRequest.setStatus("open");

            // Save loan request
            boolean success = loanDao.createLoanRequest(loanRequest);

            if (success) {
                logger.info("Loan request created successfully for user ID: " + userId);
                response.sendRedirect("dashboard");
            } else {
                logger.error("Failed to create loan request for user ID: " + userId);
                request.setAttribute("error", "Failed to create loan request. Please try again.");
                request.getRequestDispatcher("create-loan-request.jsp").forward(request, response);
            }

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid loan request parameters: " + e.getMessage());
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("create-loan-request.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error creating loan request", e);
            request.setAttribute("error", "An unexpected error occurred. Please try again later.");
            request.getRequestDispatcher("create-loan-request.jsp").forward(request, response);
        }
    }

    private double validateAmount(String amountStr) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Loan amount is required.");
        }

        try {
            double amount = Double.parseDouble(amountStr);
            if (amount < 100 || amount > 10000) {
                throw new IllegalArgumentException("Loan amount must be between $100 and $10,000.");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid loan amount format.");
        }
    }

    private int validateDuration(String durationStr) {
        if (durationStr == null || durationStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Loan duration is required.");
        }

        try {
            int duration = Integer.parseInt(durationStr);
            if (duration != 3 && duration != 6 && duration != 12 && duration != 24) {
                throw new IllegalArgumentException("Invalid loan duration. Please select from available options.");
            }
            return duration;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid loan duration format.");
        }
    }

    private String validatePurpose(String purpose) {
        if (purpose == null || purpose.trim().isEmpty()) {
            throw new IllegalArgumentException("Loan purpose is required.");
        }

        String[] validPurposes = {"personal", "business", "education", "home", "other"};
        for (String validPurpose : validPurposes) {
            if (validPurpose.equals(purpose)) {
                return purpose;
            }
        }
        throw new IllegalArgumentException("Invalid loan purpose.");
    }

    private String validateDescription(String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Loan description is required.");
        }

        if (description.length() < 10 || description.length() > 500) {
            throw new IllegalArgumentException("Description must be between 10 and 500 characters.");
        }

        return description.trim();
    }
} 