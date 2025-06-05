package com.lendlink.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.lendlink.util.DatabaseConnection;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM User WHERE email = ? AND password_hash = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, email);
                pstmt.setString(2, password); // Note: In a real application, use proper password hashing
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Login successful
                        HttpSession session = request.getSession();
                        session.setAttribute("userId", rs.getInt("user_id"));
                        session.setAttribute("userName", rs.getString("name"));
                        session.setAttribute("userRole", rs.getString("role"));
                        
                        response.sendRedirect("dashboard.jsp");
                    } else {
                        // Login failed
                        request.setAttribute("error", "Invalid email or password");
                        request.getRequestDispatcher("login.jsp").forward(request, response);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Database error occurred");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
} 