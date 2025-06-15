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
import com.lendlink.dao.UserDao;
import com.lendlink.model.LoanRequest;
import com.lendlink.model.User;
import org.apache.log4j.Logger;
import java.util.Calendar;
import java.sql.Date;
import com.lendlink.model.Repayment;

@WebServlet("/fund-loan")
public class FundLoanServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(FundLoanServlet.class);
    private LoanDao loanDao;
    private WalletDao walletDao;
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        loanDao = new LoanDao();
        walletDao = new WalletDao();
        userDao = new UserDao();
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

            // Get funding amount from request
            String fundingAmountStr = request.getParameter("fundingAmount");
            if (fundingAmountStr == null || fundingAmountStr.isEmpty()) {
                request.setAttribute("error", "Please specify the amount to fund");
                request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
                return;
            }

            double fundingAmount = Double.parseDouble(fundingAmountStr);
            // The loan.getAmount() now represents the remaining amount to be funded
            double remainingAmount = loan.getAmount();

            logger.debug("FundLoanServlet - Loan ID: " + loanId + ", loan.getAmount() (current remaining in DB): " + loan.getAmount() + ", calculated remainingAmount for validation: " + remainingAmount);

            // Validate funding amount
            if (fundingAmount <= 0 || fundingAmount > remainingAmount) {
                request.setAttribute("error", "Invalid funding amount. Please enter an amount between $1 and $" + remainingAmount);
                request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
                return;
            }

            // Check if user has sufficient balance
            double userBalance = walletDao.getBalance(userId);
            if (userBalance < fundingAmount) {
                request.setAttribute("error", "Insufficient balance to fund this amount");
                request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
                return;
            }

            // Process the funding transaction
            boolean success = walletDao.withdraw(userId, fundingAmount);
            if (!success) {
                request.setAttribute("error", "Failed to process the funding transaction");
                request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
                return;
            }

            // Record the funding in the Funding table
            success = loanDao.fundLoan(loanId, userId, fundingAmount);
            if (!success) {
                // If funding record fails, refund the user
                walletDao.deposit(userId, fundingAmount);
                request.setAttribute("error", "Failed to record the funding. Your balance has been refunded.");
                request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
                return;
            }
            
            // Re-fetch the loan to get its updated status and remaining amount after funding
            loan = loanDao.getLoanRequestById(loanId);
            if (loan != null) {
                logger.debug("FundLoanServlet: Loan ID: " + loanId + " status AFTER loanDao.fundLoan: " + loan.getStatus());
                logger.debug("FundLoanServlet: Loan ID: " + loanId + " amount AFTER loanDao.fundLoan: " + loan.getAmount());

                // If loan is fully funded, create repayment schedule
                if ("funded".equals(loan.getStatus())) {
                    logger.info("Creating repayment schedule for loan ID: " + loanId);
                    // Create repayment schedule
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(loan.getCreatedOn());
                    calendar.add(Calendar.MONTH, 1); // First payment due after 1 month

                    double totalFunded = loanDao.getTotalAmountFundedForLoan(loanId);
                    logger.info("Total funded amount: " + totalFunded + ", Duration months: " + loan.getDurationMonths());
                    double monthlyPayment = totalFunded / loan.getDurationMonths();
                    logger.info("Monthly payment amount: " + monthlyPayment);

                    for (int i = 0; i < loan.getDurationMonths(); i++) {
                        Repayment repayment = new Repayment();
                        repayment.setLoanId(loanId);
                        repayment.setDueDate(new Date(calendar.getTimeInMillis()));
                        repayment.setAmountDue(monthlyPayment);
                        repayment.setStatus("pending");

                        logger.info("Creating repayment entry " + (i + 1) + " of " + loan.getDurationMonths() + 
                                  " - Due date: " + repayment.getDueDate() + 
                                  ", Amount: " + repayment.getAmountDue());

                        boolean scheduleSuccess = loanDao.createRepayment(repayment);
                        if (!scheduleSuccess) {
                            logger.error("Failed to create repayment schedule for loan: " + loanId + 
                                       " - Entry " + (i + 1) + " of " + loan.getDurationMonths());
                        } else {
                            logger.info("Successfully created repayment entry " + (i + 1));
                        }

                        calendar.add(Calendar.MONTH, 1);
                    }
                }
            } else {
                logger.warn("FundLoanServlet: Loan object became null after funding for ID: " + loanId);
            }

            // Record the transaction and add funds to borrower's wallet in a single operation
            success = walletDao.processFundingTransaction(userId, loan.getUserId(), fundingAmount);
            if (!success) {
                logger.error("Failed to process funding transaction for loan ID: " + loanId);
            }

            // Get borrower's name
            User borrower = userDao.getUserById(loan.getUserId());
            String borrowerName = borrower != null ? borrower.getName() : "Unknown Borrower";

            // Set success message and redirect to success page
            request.setAttribute("loanAmount", fundingAmount);
            request.setAttribute("loanPurpose", loan.getPurpose());
            request.setAttribute("borrowerName", borrowerName);
            request.setAttribute("remainingAmount", remainingAmount - fundingAmount);
            request.getRequestDispatcher("funding-success.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid loan ID");
            request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
        }
    }
} 