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
import com.lendlink.model.Repayment;
import com.google.gson.Gson;
import org.apache.log4j.Logger;

@WebServlet("/get-repayment-schedule")
public class GetRepaymentScheduleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(GetRepaymentScheduleServlet.class);
    private LoanDao loanDao;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        loanDao = new LoanDao();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            logger.warn("Unauthorized access attempt to repayment schedule");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String loanIdStr = request.getParameter("loanId");
        if (loanIdStr == null || loanIdStr.isEmpty()) {
            logger.warn("Missing loan ID parameter in repayment schedule request");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Loan ID is required");
            return;
        }

        try {
            int loanId = Integer.parseInt(loanIdStr);
            logger.info("Fetching repayment schedule for loan ID: " + loanId);
            
            List<Repayment> repayments = loanDao.getRepaymentsForLoan(loanId);
            logger.info("Found " + repayments.size() + " repayment entries for loan ID: " + loanId);
            
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(repayments));

        } catch (NumberFormatException e) {
            logger.error("Invalid loan ID format: " + loanIdStr);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid loan ID");
        } catch (Exception e) {
            logger.error("Error getting repayment schedule for loan ID: " + loanIdStr, e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
} 