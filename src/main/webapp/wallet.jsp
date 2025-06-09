<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Wallet - LendLink</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        .wallet-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 2rem;
        }

        .wallet-summary {
            background-color: #f8f9fa;
            border-radius: 8px;
            padding: 2rem;
            margin-bottom: 2rem;
            text-align: center;
        }

        .balance {
            font-size: 2.5rem;
            font-weight: bold;
            color: #28a745;
            margin: 1rem 0;
        }

        .transaction-form {
            background-color: white;
            border-radius: 8px;
            padding: 2rem;
            margin-bottom: 2rem;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .form-group {
            margin-bottom: 1rem;
        }

        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: bold;
        }

        .form-group input {
            width: 100%;
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
        }

        .transaction-history {
            background-color: white;
            border-radius: 8px;
            padding: 2rem;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }

        .transaction-list {
            list-style: none;
            padding: 0;
        }

        .transaction-item {
            padding: 1rem;
            border-bottom: 1px solid #eee;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .transaction-item:last-child {
            border-bottom: none;
        }

        .transaction-type {
            font-weight: bold;
        }

        .transaction-amount {
            font-weight: bold;
        }

        .amount-positive {
            color: #28a745;
        }

        .amount-negative {
            color: #dc3545;
        }

        .error {
            background-color: #dc3545;
            color: white;
            padding: 1rem;
            border-radius: 4px;
            margin-bottom: 1rem;
        }

        .success {
            background-color: #28a745;
            color: white;
            padding: 1rem;
            border-radius: 4px;
            margin-bottom: 1rem;
        }
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <jsp:include page="./sidebar.jsp"/>
    <div class="main-content">
        <div class="wallet-container">
            <h1>My Wallet</h1>

            <c:if test="${not empty error}">
                <div class="error">
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <c:if test="${not empty success}">
                <div class="success">
                    <c:out value="${success}"/>
                </div>
            </c:if>

            <div class="wallet-summary">
                <h2>Current Balance</h2>
                <p class="balance">$<fmt:formatNumber value="${walletBalance}" pattern="#,##0.00"/></p>
            </div>

            <div class="transaction-form">
                <h2>Add Funds</h2>
                <form action="wallet" method="post">
                    <input type="hidden" name="action" value="deposit">
                    <div class="form-group">
                        <label for="amount">Amount ($)</label>
                        <input type="number" id="amount" name="amount" min="1" step="0.01" required>
                    </div>
                    <button type="submit" class="button">Add Funds</button>
                </form>
            </div>

            <div class="transaction-form">
                <h2>Withdraw Funds</h2>
                <form action="wallet" method="post">
                    <input type="hidden" name="action" value="withdraw">
                    <div class="form-group">
                        <label for="withdrawAmount">Amount ($)</label>
                        <input type="number" id="withdrawAmount" name="amount" min="1" step="0.01" max="${walletBalance}" required>
                    </div>
                    <button type="submit" class="button">Withdraw Funds</button>
                </form>
            </div>

            <div class="transaction-history">
                <h2>Transaction History</h2>
                <ul class="transaction-list">
                    <c:forEach items="${recentTransactions}" var="transaction">
                        <li class="transaction-item">
                            <div>
                                <span class="transaction-type">
                                    <c:choose>
                                        <c:when test="${transaction.type == 'deposit'}">
                                            Deposit
                                        </c:when>
                                        <c:when test="${transaction.type == 'withdrawal'}">
                                            Withdrawal
                                        </c:when>
                                        <c:when test="${transaction.type == 'funding'}">
                                            Loan Funding
                                        </c:when>
                                        <c:when test="${transaction.type == 'repayment'}">
                                            Loan Repayment
                                        </c:when>
                                        <c:otherwise>
                                            ${transaction.type}
                                        </c:otherwise>
                                    </c:choose>
                                </span>
                                <br>
                                <small><fmt:formatDate value="${transaction.createdOn}" pattern="MMM dd, yyyy HH:mm"/></small>
                            </div>
                            <span class="transaction-amount ${transaction.toUserId == sessionScope.userId ? 'amount-positive' : 'amount-negative'}">
                                <c:choose>
                                    <c:when test="${transaction.toUserId == sessionScope.userId}">
                                        +$<fmt:formatNumber value="${transaction.amount}" pattern="#,##0.00"/>
                                    </c:when>
                                    <c:otherwise>
                                        -$<fmt:formatNumber value="${transaction.amount}" pattern="#,##0.00"/>
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
    </div>
</body>
</html> 