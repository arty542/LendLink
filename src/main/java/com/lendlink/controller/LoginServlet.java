package com.lendlink.controller;

import com.lendlink.dao.UserDao;
import com.lendlink.model.User;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(LoginServlet.class);
    private UserDao userDao;

    @Override
    public void init() throws ServletException {
        userDao = new UserDao();
        logger.info("LoginServlet initialized");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        logger.info("Login attempt received");
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        logger.debug("Attempting login for email: " + email);

        User user = userDao.getUserByEmailAndPassword(email, password);

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userName", user.getName());
            session.setAttribute("userRole", user.getRole());
            
            logger.info("Login successful for user: " + user.getName());
            response.sendRedirect("dashboard");
        } else {
            logger.warn("Login failed for email: " + email);
            request.setAttribute("error", "Invalid email or password");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
        System.out.println("Test log message - " + new java.util.Date());
    }
} 