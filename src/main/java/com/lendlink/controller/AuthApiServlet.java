package com.lendlink.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lendlink.dao.UserDao;
import com.lendlink.model.User;
import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/api/auth/*")
public class AuthApiServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(AuthApiServlet.class);
    private UserDao userDao;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        userDao = new UserDao();
        gson = new Gson();
        logger.info("AuthApiServlet initialized");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        if ("/login".equals(pathInfo)) {
            handleLogin(request, response);
        } else if ("/register".equals(pathInfo)) {
            handleRegister(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }
        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> requestData = gson.fromJson(jsonBuilder.toString(), mapType);
        
        String email = requestData.get("email");
        String password = requestData.get("password");

        if (email == null || password == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "Email and password are required");
            return;
        }
        User user = userDao.getUserByEmailAndPassword(email, password);
        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("userName", user.getName());
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("userRole", user.getRole());

            Map<String, Object> responseData = new HashMap<>();
            responseData.put("user", user);
            responseData.put("message", "Login successful");

            sendJsonResponse(response, HttpServletResponse.SC_OK, responseData);
            logger.info("API Login successful for user: " + user.getEmail());
        } else {
            sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Invalid email or password");
            logger.warn("API Login failed for email: " + email);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        StringBuilder jsonBuilder = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }
        }

        Type mapType = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> requestData = gson.fromJson(jsonBuilder.toString(), mapType);
        
        String name = requestData.get("name");
        String email = requestData.get("email");
        String password = requestData.get("password");
        String role = requestData.get("role");

        if (name == null || email == null || password == null || role == null) {
            sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, "All fields are required");
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPasswordHash(password);
        user.setRole(role);

        try {
            boolean success = userDao.createUser(user);
            if (success) {
                sendJsonResponse(response, HttpServletResponse.SC_CREATED, 
                    Map.of("message", "User registered successfully"));
                logger.info("API Registration successful for user: " + email);
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "Failed to register user");
                logger.error("API Registration failed for user: " + email);
            }
        } catch (Exception e) {
            if (e.getMessage().contains("Duplicate entry")) {
                sendErrorResponse(response, HttpServletResponse.SC_CONFLICT, 
                    "Email address already registered");
            } else {
                sendErrorResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                    "An error occurred during registration");
            }
            logger.error("API Registration error for user: " + email, e);
        }
    }

    private void sendJsonResponse(HttpServletResponse response, int status, Object data) 
            throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(status);
        response.getWriter().write(gson.toJson(data));
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) 
            throws IOException {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        sendJsonResponse(response, status, error);
    }
} 