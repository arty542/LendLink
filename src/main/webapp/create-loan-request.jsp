<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Create Loan Request - LendLink</title>
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
            margin-left: 60px; /* Initial collapsed sidebar width */
            padding: 2rem;
            flex-grow: 1;
            transition: margin-left 0.3s ease; /* Smooth transition */
        }

        /* Adjust main content margin when sidebar is hovered */
        body:has(.sidebar:hover) .main-content {
            margin-left: 250px; /* Expanded sidebar width */
        }

        .container {
            max-width: 800px;
            margin: 0 auto;
            padding: 2rem;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .form-header {
            margin-bottom: 2rem;
            text-align: center;
        }

        .form-header h1 {
            color: #2c3e50;
            margin-bottom: 0.5rem;
        }

        .form-group {
            margin-bottom: 1.5rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            color: #2c3e50;
            font-weight: 500;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            width: 100%;
            padding: 0.8rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1rem;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #3498db;
            box-shadow: 0 0 0 2px rgba(52,152,219,0.2);
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
            width: 100%;
            font-size: 1rem;
        }

        .button:hover {
            background-color: #2980b9;
        }

        .error {
            background-color: #dc3545;
            color: white;
            padding: 1rem;
            border-radius: 4px;
            margin-bottom: 1rem;
        }

        .info-text {
            color: #666;
            font-size: 0.9rem;
            margin-top: 0.5rem;
        }

        .loan-summary {
            margin-top: 2rem;
            padding: 1rem;
            background-color: #f8f9fa;
            border-radius: 4px;
            display: none;
        }

        .loan-summary h3 {
            color: #2c3e50;
            margin-bottom: 1rem;
        }

        .loan-summary p {
            margin-bottom: 0.5rem;
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <jsp:include page="./sidebar.jsp"/>
    <div class="main-content">
        <div class="container">
            <div class="form-header">
                <h1>Create Loan Request</h1>
                <p>Fill in the details below to create your loan request</p>
            </div>

            <c:if test="${not empty error}">
                <div class="error">
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <form action="create-loan-request" method="post" id="loanRequestForm">
                <div class="form-group">
                    <label for="amount">Loan Amount ($)</label>
                    <input type="number" id="amount" name="amount" min="100" max="10000" step="100" required>
                    <p class="info-text">Minimum amount: $100, Maximum amount: $10,000</p>
                </div>

                <div class="form-group">
                    <label for="duration">Loan Duration (months)</label>
                    <select id="duration" name="duration" required>
                        <option value="">Select duration</option>
                        <option value="3">3 months</option>
                        <option value="6">6 months</option>
                        <option value="12">12 months</option>
                        <option value="24">24 months</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="purpose">Loan Purpose</label>
                    <select id="purpose" name="purpose" required>
                        <option value="">Select purpose</option>
                        <option value="personal">Personal</option>
                        <option value="business">Business</option>
                        <option value="education">Education</option>
                        <option value="home">Home</option>
                        <option value="other">Other</option>
                    </select>
                </div>

                <div class="form-group">
                    <label for="description">Description</label>
                    <textarea id="description" name="description" rows="5" placeholder="Please provide details about how you plan to use the loan and your repayment strategy." required></textarea>
                </div>

                <button type="submit" class="button">Submit Loan Request</button>
            </form>

            <div class="loan-summary" id="loanSummary">
                <h3>Loan Summary</h3>
                <p><strong>Loan Amount:</strong> $<span id="summaryAmount">0.00</span></p>
                <p><strong>Loan Duration:</strong> <span id="summaryDuration">0</span> months</p>
                <p><strong>Total Repayment:</strong> $<span id="summaryTotalRepayment">0.00</span></p>
            </div>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const amountInput = document.getElementById('amount');
            const durationSelect = document.getElementById('duration');
            const loanSummaryDiv = document.getElementById('loanSummary');
            const summaryAmount = document.getElementById('summaryAmount');
            const summaryDuration = document.getElementById('summaryDuration');
            const summaryTotalRepayment = document.getElementById('summaryTotalRepayment');

            function calculateLoanSummary() {
                const amount = parseFloat(amountInput.value);
                const duration = parseInt(durationSelect.value);

                if (!isNaN(amount) && amount > 0 && !isNaN(duration) && duration > 0) {
                    const totalRepayment = amount;

                    summaryAmount.textContent = amount.toFixed(2);
                    summaryDuration.textContent = duration;
                    summaryTotalRepayment.textContent = totalRepayment.toFixed(2);

                    loanSummaryDiv.style.display = 'block';
                } else {
                    loanSummaryDiv.style.display = 'none';
                }
            }

            amountInput.addEventListener('input', calculateLoanSummary);
            durationSelect.addEventListener('change', calculateLoanSummary);

            calculateLoanSummary(); // Calculate on page load if values are pre-filled
        });
    </script>
</body>
</html> 