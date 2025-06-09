package com.lendlink.controller;

import com.lendlink.dao.LoanDao;
import com.lendlink.model.LoanRequest;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/my-loans")
public class MyLoansServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(MyLoansServlet.class);
    private LoanDao loanDao;

    @Override
    public void init() throws ServletException {
        loanDao = new LoanDao();
        logger.info("MyLoansServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        try {
            List<LoanRequest> myRequestedLoans = null;
            List<LoanRequest> myFundedLoans = null;

            if ("borrower".equals(userRole) || "both".equals(userRole)) {
                myRequestedLoans = loanDao.getLoanRequestsByUserId(userId);
                logger.info("Fetched requested loans for user ID " + userId + ": " + myRequestedLoans.size());
            }

            if ("lender".equals(userRole) || "both".equals(userRole)) {
                // This method will be added in LoanDao in a subsequent step
                // For now, we'll initialize as null or empty list
                // myFundedLoans = loanDao.getFundedLoanRequestsByLenderId(userId);
                // logger.info("Fetched funded loans for user ID " + userId + ": " + myFundedLoans.size());
            }
            
            request.setAttribute("myRequestedLoans", myRequestedLoans);
            request.setAttribute("myFundedLoans", myFundedLoans);
            request.setAttribute("userRole", userRole); // Pass user role to JSP
            request.getRequestDispatcher("my-loans.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading my loans data for user ID: " + userId, e);
            request.setAttribute("error", "An error occurred while loading your loans. Please try again later.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
} 