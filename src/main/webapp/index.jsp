<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>LendLink - Peer-to-Peer Lending Platform</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            line-height: 1.6;
            color: #333;
            background-color: #f8f9fa;
        }

        .nav {
            background-color: #fff;
            padding: 1rem 5%;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            position: fixed;
            width: 100%;
            top: 0;
            z-index: 1000;
        }

        .logo {
            font-size: 1.5rem;
            font-weight: bold;
            color: #2c3e50;
            text-decoration: none;
        }

        .nav-links {
            display: flex;
            gap: 2rem;
            align-items: center;
        }

        .nav-links a {
            color: #2c3e50;
            text-decoration: none;
            font-weight: 500;
            transition: color 0.3s ease;
            padding: 0.5rem 1rem;
        }

        .nav-links a:hover {
            color: #3498db;
        }

        .hero {
            background: linear-gradient(135deg, #3498db, #2c3e50);
            color: white;
            padding: 8rem 5% 4rem;
            text-align: center;
        }

        .hero h1 {
            font-size: 3rem;
            margin-bottom: 1rem;
        }

        .hero p {
            font-size: 1.2rem;
            max-width: 600px;
            margin: 0 auto 2rem;
        }

        .content {
            padding: 4rem 5%;
            max-width: 1200px;
            margin: 0 auto;
        }

        .features {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
            gap: 2rem;
            margin-top: 3rem;
        }

        .feature-card {
            background: white;
            padding: 2rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            transition: transform 0.3s ease;
        }

        .feature-card:hover {
            transform: translateY(-5px);
        }

        .feature-card h3 {
            color: #2c3e50;
            margin-bottom: 1rem;
        }

        .button {
            background-color: #3498db;
            color: white;
            padding: 0.8rem 2rem;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            font-weight: 500;
            transition: background-color 0.3s ease;
        }

        .button:hover {
            background-color: #2980b9;
        }

        .cta-section {
            text-align: center;
            margin-top: 3rem;
        }
    </style>
</head>
<body>
    <nav class="nav">
        <a href="index.jsp" class="logo">LendLink</a>
        <div class="nav-links">
            <a href="login.jsp">Login</a>
            <a href="register.jsp" class="button">Register</a>
        </div>
    </nav>

    <section class="hero">
        <h1>Welcome to LendLink</h1>
        <p>Connect with borrowers and lenders in a secure peer-to-peer lending platform</p>
        <a href="register.jsp" class="button">Get Started</a>
    </section>

    <div class="content">
        <div class="features">
            <div class="feature-card">
                <h3>For Borrowers</h3>
                <p>Create a loan request, set your terms, and connect with potential lenders. Get the funds you need with competitive rates.</p>
            </div>
            
            <div class="feature-card">
                <h3>For Lenders</h3>
                <p>Browse available loans, choose your investments, and earn returns. Diversify your portfolio with peer-to-peer lending.</p>
            </div>
            
            <div class="feature-card">
                <h3>Secure Platform</h3>
                <p>Our platform ensures secure transactions and protects both borrowers and lenders with advanced security measures.</p>
            </div>
        </div>

        <div class="cta-section">
            <a href="register.jsp" class="button">Start Lending Today</a>
        </div>
    </div>
</body>
</html> 