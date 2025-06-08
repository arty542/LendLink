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

@WebServlet("/browse-loans")
public class BrowseLoansServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(BrowseLoansServlet.class);
    private LoanDao loanDao;

    @Override
    public void init() throws ServletException {
        loanDao = new LoanDao();
        logger.info("BrowseLoansServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login");
            return;
        }

        // Get filter parameters
        String minAmountStr = request.getParameter("minAmount");
        String maxAmountStr = request.getParameter("maxAmount");
        String durationStr = request.getParameter("duration");
        String purpose = request.getParameter("purpose");
        String sortBy = request.getParameter("sortBy");

        // Parse parameters
        Double minAmount = null;
        Double maxAmount = null;
        Integer duration = null;

        try {
            if (minAmountStr != null && !minAmountStr.isEmpty()) {
                minAmount = Double.parseDouble(minAmountStr);
            }
            if (maxAmountStr != null && !maxAmountStr.isEmpty()) {
                maxAmount = Double.parseDouble(maxAmountStr);
            }
            if (durationStr != null && !durationStr.isEmpty()) {
                duration = Integer.parseInt(durationStr);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Invalid filter values");
            request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
            return;
        }

        // Get filtered loans
        List<LoanRequest> loans = loanDao.getAvailableLoanRequests(minAmount, maxAmount, duration, purpose, sortBy);
        request.setAttribute("loanRequests", loans);

        // Forward to JSP
        request.getRequestDispatcher("browse-loans.jsp").forward(request, response);
    }
} 