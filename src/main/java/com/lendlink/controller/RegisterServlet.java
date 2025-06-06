package com.lendlink.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.lendlink.util.DatabaseConnection;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String role = request.getParameter("role");

        if (name == null || name.trim().isEmpty() || email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty() || role == null || role.trim().isEmpty()) {
            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("register.jsp").forward(request, response);
            return;
        }


        String passwordHash = password; 

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO User (name, email, password_hash, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name.trim());
                pstmt.setString(2, email.trim());
                pstmt.setString(3, passwordHash);
                pstmt.setString(4, role);

                int rowsAffected = pstmt.executeUpdate();

                if (rowsAffected > 0) {
                    response.sendRedirect("login.jsp");
                } else {
                    request.setAttribute("error", "Registration failed. Please try again.");
                    request.getRequestDispatcher("register.jsp").forward(request, response);
                }
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("for key 'email'")) {
                 request.setAttribute("error", "Email address already registered.");
            } else {
                 request.setAttribute("error", "Database error occurred during registration.");
                 e.printStackTrace();
            }
            request.getRequestDispatcher("register.jsp").forward(request, response);
        }
    }
}