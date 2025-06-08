<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Funding Success - LendLink</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: #333;
            background-color: #f8f9fa;
            min-height: 100vh;
            display: flex;
        }

        .main-content {
            margin-left: 60px;
            padding: 2rem;
            flex-grow: 1;
            transition: margin-left 0.3s ease;
        }

        body:has(.sidebar:hover) .main-content {
            margin-left: 250px;
        }

        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 2rem;
            text-align: center;
        }

        .success-card {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }

        .success-icon {
            color: #28a745;
            font-size: 4rem;
            margin-bottom: 1rem;
        }

        .success-title {
            color: #28a745;
            font-size: 2rem;
            margin-bottom: 1rem;
        }

        .success-message {
            color: #6c757d;
            font-size: 1.1rem;
            margin-bottom: 2rem;
        }

        .button {
            display: inline-block;
            padding: 0.8rem 1.5rem;
            background-color: #007bff;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            transition: background-color 0.3s ease;
        }

        .button:hover {
            background-color: #0056b3;
        }

        .button.secondary {
            background-color: #6c757d;
            margin-left: 1rem;
        }

        .button.secondary:hover {
            background-color: #5a6268;
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <div class="main-content">
        <div class="container">
            <div class="success-card">
                <i class="fas fa-check-circle success-icon"></i>
                <h1 class="success-title">Funding Successful!</h1>
                <p class="success-message">
                    Thank you for funding this loan request. Your contribution will help make a difference in someone's life.
                </p>
                <div>
                    <a href="dashboard" class="button">Go to Dashboard</a>
                    <a href="browse-loans" class="button secondary">Browse More Loans</a>
                </div>
            </div>
        </div>
    </div>
</body>
</html> 