package com.lendlink.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.lendlink.dao.WalletDao;
import com.lendlink.model.Transaction;
import org.apache.log4j.Logger;
import java.util.List;

@WebServlet("/wallet")
public class WalletServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(WalletServlet.class);
    private WalletDao walletDao;

    @Override
    public void init() throws ServletException {
        walletDao = new WalletDao();
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

        try {
            // Get wallet balance
            double balance = walletDao.getBalance(userId);
            request.setAttribute("walletBalance", balance);

            // Get recent transactions
            List<Transaction> recentTransactions = walletDao.getRecentTransactions(userId, 10);
            request.setAttribute("recentTransactions", recentTransactions);

            // Forward to wallet.jsp
            request.getRequestDispatcher("wallet.jsp").forward(request, response);
        } catch (Exception e) {
            logger.error("Error loading wallet page", e);
            request.setAttribute("error", "An error occurred while loading your wallet. Please try again later.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
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
        String action = request.getParameter("action");

        if ("deposit".equals(action)) {
            try {
                double amount = Double.parseDouble(request.getParameter("amount"));
                if (amount <= 0) {
                    request.setAttribute("error", "Please enter a valid amount.");
                    doGet(request, response);
                    return;
                }

                boolean success = walletDao.deposit(userId, amount);
                if (success) {
                    request.setAttribute("success", "Successfully added funds to your wallet.");
                } else {
                    request.setAttribute("error", "Failed to add funds. Please try again.");
                }
            } catch (NumberFormatException e) {
                request.setAttribute("error", "Please enter a valid amount.");
            } catch (Exception e) {
                logger.error("Error processing deposit", e);
                request.setAttribute("error", "An error occurred while processing your deposit. Please try again later.");
            }
        }

        doGet(request, response);
    }
} 