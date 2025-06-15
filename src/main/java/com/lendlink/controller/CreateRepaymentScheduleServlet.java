package com.lendlink.controller;

import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.lendlink.dao.LoanDao;
import com.lendlink.model.LoanRequest;
import com.lendlink.model.Repayment;
import org.apache.log4j.Logger;

@WebServlet("/create-repayment-schedule")
public class CreateRepaymentScheduleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(CreateRepaymentScheduleServlet.class);
    private LoanDao loanDao;

    @Override
    public void init() throws ServletException {
        loanDao = new LoanDao();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login");
            return;
        }

        String loanIdStr = request.getParameter("loanId");
        if (loanIdStr == null || loanIdStr.isEmpty()) {
            request.setAttribute("error", "Invalid loan request");
            request.getRequestDispatcher("my-loans.jsp").forward(request, response);
            return;
        }

        try {
            int loanId = Integer.parseInt(loanIdStr);
            LoanRequest loan = loanDao.getLoanRequestById(loanId);

            if (loan == null) {
                request.setAttribute("error", "Loan request not found");
                request.getRequestDispatcher("my-loans.jsp").forward(request, response);
                return;
            }

            // Calculate monthly payment
            double totalAmount = loan.getAmount();
            int durationMonths = loan.getDurationMonths();
            double monthlyPayment = totalAmount / durationMonths;

            // Create repayment schedule
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(loan.getCreatedOn());
            calendar.add(Calendar.MONTH, 1); // First payment due after 1 month

            for (int i = 0; i < durationMonths; i++) {
                Repayment repayment = new Repayment();
                repayment.setLoanId(loanId);
                repayment.setDueDate(new Date(calendar.getTimeInMillis()));
                repayment.setAmountDue(monthlyPayment);
                repayment.setStatus("pending");

                boolean success = loanDao.createRepayment(repayment);
                if (!success) {
                    request.setAttribute("error", "Failed to create repayment schedule");
                    request.getRequestDispatcher("my-loans.jsp").forward(request, response);
                    return;
                }

                calendar.add(Calendar.MONTH, 1);
            }

            response.sendRedirect("my-loans");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid loan ID");
            request.getRequestDispatcher("my-loans.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error creating repayment schedule", e);
            request.setAttribute("error", "An error occurred while creating the repayment schedule");
            request.getRequestDispatcher("my-loans.jsp").forward(request, response);
        }
    }
} 