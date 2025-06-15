package com.lendlink.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.lendlink.dao.LoanDao;
import com.lendlink.dao.WalletDao;
import com.lendlink.model.LoanRequest;
import com.lendlink.model.Repayment;
import org.apache.log4j.Logger;

@WebServlet("/repay-loan")
public class RepayLoanServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(RepayLoanServlet.class);
    private LoanDao loanDao;
    private WalletDao walletDao;

    @Override
    public void init() throws ServletException {
        loanDao = new LoanDao();
        walletDao = new WalletDao();
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
        String repaymentIdStr = request.getParameter("repaymentId");
        String amountStr = request.getParameter("amount");

        if (loanIdStr == null || repaymentIdStr == null || amountStr == null) {
            request.setAttribute("error", "Missing required parameters");
            request.getRequestDispatcher("my-loans.jsp").forward(request, response);
            return;
        }

        try {
            int loanId = Integer.parseInt(loanIdStr);
            int repaymentId = Integer.parseInt(repaymentIdStr);
            double amount = Double.parseDouble(amountStr);

            // Get loan details
            LoanRequest loan = loanDao.getLoanRequestById(loanId);
            if (loan == null || loan.getUserId() != userId) {
                request.setAttribute("error", "Invalid loan request");
                request.getRequestDispatcher("my-loans.jsp").forward(request, response);
                return;
            }

            // Get repayment details
            Repayment repayment = loanDao.getRepaymentById(repaymentId);
            if (repayment == null || repayment.getLoanId() != loanId) {
                request.setAttribute("error", "Invalid repayment request");
                request.getRequestDispatcher("my-loans.jsp").forward(request, response);
                return;
            }

            // Validate amount
            if (amount <= 0 || amount > repayment.getAmountDue()) {
                request.setAttribute("error", "Invalid repayment amount");
                request.getRequestDispatcher("my-loans.jsp").forward(request, response);
                return;
            }

            // Check if user has sufficient balance
            double userBalance = walletDao.getBalance(userId);
            if (userBalance < amount) {
                request.setAttribute("error", "Insufficient balance to make this payment");
                request.getRequestDispatcher("my-loans.jsp").forward(request, response);
                return;
            }

            // Process the repayment
            boolean success = walletDao.withdraw(userId, amount);
            if (!success) {
                request.setAttribute("error", "Failed to process the payment");
                request.getRequestDispatcher("my-loans.jsp").forward(request, response);
                return;
            }

            // Update repayment record
            success = loanDao.updateRepayment(repaymentId, amount, new Date(System.currentTimeMillis()));
            if (!success) {
                // If repayment record fails, refund the user
                walletDao.deposit(userId, amount);
                request.setAttribute("error", "Failed to record the payment. Your balance has been refunded.");
                request.getRequestDispatcher("my-loans.jsp").forward(request, response);
                return;
            }

            // Get all funders for this loan
            List<Integer> funderIds = loanDao.getLoanFunders(loanId);
            
            // Calculate and distribute payment to each funder proportionally
            double totalFunded = loanDao.getTotalAmountFundedForLoan(loanId);
            for (int funderId : funderIds) {
                double funderAmount = loanDao.getFunderAmountForLoan(loanId, funderId);
                double share = (funderAmount / totalFunded) * amount;
                
                // Add funds to funder's wallet
                walletDao.deposit(funderId, share);
                
                // Record transaction
                walletDao.recordTransaction(userId, funderId, share, "repayment");
            }

            // Check if all repayments are complete
            if (loanDao.areAllRepaymentsComplete(loanId)) {
                loanDao.updateLoanStatus(loanId, "repaid");
            }

            request.setAttribute("success", "Payment processed successfully");
            response.sendRedirect("my-loans");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid input parameters");
            request.getRequestDispatcher("my-loans.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error processing loan repayment", e);
            request.setAttribute("error", "An error occurred while processing your payment");
            request.getRequestDispatcher("my-loans.jsp").forward(request, response);
        }
    }
} 