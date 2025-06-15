package com.lendlink.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.lendlink.dao.LoanDao;
import com.lendlink.model.LoanRequest;
import org.apache.log4j.Logger;

@WebServlet("/my-loans")
public class MyLoansServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(MyLoansServlet.class);
    private LoanDao loanDao;

    @Override
    public void init() throws ServletException {
        loanDao = new LoanDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String userRole = (String) session.getAttribute("userRole");

        try {
            // Get loans requested by the user (for borrowers)
            if ("borrower".equals(userRole) || "both".equals(userRole)) {
                List<LoanRequest> myRequestedLoans = loanDao.getLoansByUserId(userId);
                request.setAttribute("myRequestedLoans", myRequestedLoans);
            }

            // Get loans funded by the user (for lenders)
            if ("lender".equals(userRole) || "both".equals(userRole)) {
                List<LoanRequest> myFundedLoans = loanDao.getLoansFundedByUser(userId);
                for (LoanRequest loan : myFundedLoans) {
                    double fundedAmount = loanDao.getTotalAmountFundedForLoan(loan.getLoanId());
                    loan.setFundedAmount(fundedAmount);
                }
                request.setAttribute("myFundedLoans", myFundedLoans);
            }

            // Set user role for the view
            request.setAttribute("userRole", userRole);

            // Forward to the JSP
            request.getRequestDispatcher("my-loans.jsp").forward(request, response);

        } catch (Exception e) {
            logger.error("Error loading loans for user: " + userId, e);
            request.setAttribute("error", "An error occurred while loading your loans");
            request.getRequestDispatcher("my-loans.jsp").forward(request, response);
        }
    }
} 