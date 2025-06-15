package com.lendlink.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.lendlink.dao.LoanDao;
import org.apache.log4j.Logger;

@WebServlet("/delete-loan-request")
public class DeleteLoanServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(DeleteLoanServlet.class);
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

        int userId = (int) session.getAttribute("userId");
        String loanIdStr = request.getParameter("loanId");

        if (loanIdStr == null || loanIdStr.isEmpty()) {
            request.setAttribute("error", "Invalid loan request");
            request.getRequestDispatcher("my-loans.jsp").forward(request, response);
            return;
        }

        try {
            int loanId = Integer.parseInt(loanIdStr);
            
            // Attempt to delete the loan
            boolean success = loanDao.deleteLoanRequest(loanId, userId);
            
            if (success) {
                request.setAttribute("success", "Loan request deleted successfully");
            } else {
                request.setAttribute("error", "Failed to delete loan request");
            }
            
            response.sendRedirect("my-loans");

        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid loan ID");
            request.getRequestDispatcher("my-loans.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error deleting loan request", e);
            request.setAttribute("error", "An error occurred while deleting the loan request");
            request.getRequestDispatcher("my-loans.jsp").forward(request, response);
        }
    }
} 