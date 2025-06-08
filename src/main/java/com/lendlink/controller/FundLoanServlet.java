package com.lendlink.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.lendlink.dao.LoanDao;
import com.lendlink.dao.WalletDao;
import com.lendlink.model.LoanRequest;
import org.apache.log4j.Logger;

@WebServlet("/fund-loan")
public class FundLoanServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(FundLoanServlet.class);
    private LoanDao loanDao;
    private WalletDao walletDao;

    @Override
    public void init() throws ServletException {
        loanDao = new LoanDao();
        walletDao = new WalletDao();
        logger.info("FundLoanServlet initialized");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        String loanIdStr = request.getParameter("loanId");

        if (loanIdStr == null || loanIdStr.isEmpty()) {
            request.setAttribute("error", "Invalid loan request");
            request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
            return;
        }

        try {
            int loanId = Integer.parseInt(loanIdStr);
            LoanRequest loan = loanDao.getLoanRequestById(loanId);

            if (loan == null) {
                request.setAttribute("error", "Loan request not found");
                request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
                return;
            }

            if (!"open".equals(loan.getStatus())) {
                request.setAttribute("error", "This loan is no longer available for funding");
                request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
                return;
            }

            // Check if user has sufficient balance
            double userBalance = walletDao.getBalance(userId);
            if (userBalance < loan.getAmount()) {
                request.setAttribute("error", "Insufficient balance to fund this loan");
                request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
                return;
            }

            // Process the funding transaction
            boolean success = walletDao.withdraw(userId, loan.getAmount());
            if (!success) {
                request.setAttribute("error", "Failed to process the funding transaction");
                request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
                return;
            }

            // Record the funding in the Funding table
            success = loanDao.fundLoan(loanId, userId, loan.getAmount());
            if (!success) {
                // If funding record fails, refund the user
                walletDao.deposit(userId, loan.getAmount());
                request.setAttribute("error", "Failed to record the funding. Your balance has been refunded.");
                request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
                return;
            }

            // Update loan status to funded
            success = loanDao.updateLoanStatus(loanId, "funded");
            if (!success) {
                // If status update fails, refund the user
                walletDao.deposit(userId, loan.getAmount());
                request.setAttribute("error", "Failed to update loan status. Your balance has been refunded.");
                request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
                return;
            }

            // Record the transaction
            walletDao.recordTransaction(userId, loan.getUserId(), loan.getAmount(), "funding");

            // Add funds to borrower's wallet
            success = walletDao.deposit(loan.getUserId(), loan.getAmount());
            if (!success) {
                logger.error("Failed to add funds to borrower's wallet. Loan ID: " + loanId);
            }

            response.sendRedirect("browse-loans");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid loan ID");
            request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
        }
    }
} 