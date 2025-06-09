<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Loans - LendLink</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f4f7f6;
            margin: 0;
            padding: 0;
            display: flex;
            min-height: 100vh;
            color: #333;
        }
        .main-content {
            flex-grow: 1;
            padding: 20px;
            background-color: #ffffff;
            border-radius: 8px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05);
            margin: 20px;
        }
        .container {
            max-width: 1200px;
            margin: 0 auto;
        }
        h1 {
            color: #2c3e50;
            text-align: center;
            margin-bottom: 30px;
            font-size: 2.2rem;
            font-weight: 600;
        }
        .loan-section {
            margin-bottom: 40px;
            padding: 20px;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            background-color: #fdfdfd;
        }
        .loan-section h2 {
            color: #34495e;
            margin-bottom: 20px;
            font-size: 1.8rem;
            border-bottom: 2px solid #e0e0e0;
            padding-bottom: 10px;
        }
        .no-loans {
            text-align: center;
            color: #7f8c8d;
            font-style: italic;
            padding: 20px;
            background-color: #f0f3f5;
            border-radius: 5px;
        }
        .loan-list {
            display: grid;
            gap: 20px;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
        }
        .loan-card {
            background-color: #fff;
            border: 1px solid #ddd;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);
            overflow: hidden;
            transition: transform 0.2s ease-in-out, box-shadow 0.2s ease-in-out;
            display: flex;
            flex-direction: column;
        }
        .loan-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 6px 15px rgba(0, 0, 0, 0.1);
        }
        .loan-header {
            background-color: #3498db;
            color: white;
            padding: 15px;
            font-size: 1.2rem;
            font-weight: 500;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        .loan-details {
            padding: 15px;
            flex-grow: 1;
        }
        .loan-details p {
            margin: 8px 0;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .loan-details i {
            color: #3498db;
            width: 20px;
            text-align: center;
        }
        .loan-description {
            font-size: 0.9em;
            color: #555;
            padding: 0 15px 15px;
        }
        .loan-footer {
            padding: 15px;
            background-color: #f8f8f8;
            border-top: 1px solid #eee;
            display: flex;
            justify-content: flex-end; /* Align to the right */
            gap: 10px;
        }
        .loan-status {
            display: inline-block;
            padding: 5px 10px;
            border-radius: 4px;
            font-weight: bold;
            font-size: 0.9em;
            text-transform: capitalize;
            color: white;
        }
        .status-open { background-color: #f39c12; } /* Orange */
        .status-funded { background-color: #27ae60; } /* Green */
        .status-repaid { background-color: #2ecc71; } /* Lighter Green */
        .status-defaulted { background-color: #e74c3c; } /* Red */

        /* Action buttons */
        .action-button {
            padding: 8px 15px;
            border-radius: 5px;
            text-decoration: none;
            color: white;
            font-weight: 500;
            transition: background-color 0.2s ease;
            cursor: pointer;
            border: none;
        }
        .action-button.delete-button {
            background-color: #e74c3c;
        }
        .action-button.delete-button:hover {
            background-color: #c0392b;
        }
        .action-button.repay-button {
            background-color: #3498db;
        }
        .action-button.repay-button:hover {
            background-color: #2980b9;
        }
        .action-button.view-details {
            background-color: #7f8c8d; /* Grey */
        }
        .action-button.view-details:hover {
            background-color: #5d6d7e;
        }
    </style>
</head>
<body>
    <jsp:include page="./sidebar.jsp"/>
    <div class="main-content">
        <div class="container">
            <h1>My Loans</h1>

            <c:if test="${not empty error}">
                <div class="error-message">
                    <c:out value="${error}"/>
                </div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="success-message">
                    <c:out value="${success}"/>
                </div>
            </c:if>

            <c:if test="${userRole == 'borrower' || userRole == 'both'}">
                <div class="loan-section">
                    <h2>Loans You've Requested</h2>
                    <c:choose>
                        <c:when test="${not empty myRequestedLoans}">
                            <div class="loan-list">
                                <c:forEach items="${myRequestedLoans}" var="loan">
                                    <div class="loan-card">
                                        <div class="loan-header">
                                            <span>Requested: $<c:out value="${loan.amount}"/></span>
                                            <span class="loan-status status-${loan.status}"><c:out value="${loan.status}"/></span>
                                        </div>
                                        <div class="loan-details">
                                            <p><i class="fas fa-hand-holding-usd"></i> Purpose: <c:out value="${loan.purpose}"/></p>
                                            <p><i class="fas fa-clock"></i> Duration: <c:out value="${loan.durationMonths}"/> months</p>
                                            <p><i class="fas fa-calendar-alt"></i> Created On: <c:out value="${loan.createdOn}"/></p>
                                        </div>
                                        <div class="loan-description">
                                            <c:out value="${loan.description}"/>
                                        </div>
                                        <div class="loan-footer">
                                            <c:if test="${loan.status == 'open' || (loan.status == 'funded' && loan.amount > 0)}">
                                                <form action="delete-loan-request" method="post" onsubmit="return confirm('Are you sure you want to delete this loan request? Partial funds will be returned to lenders.');">
                                                    <input type="hidden" name="loanId" value="${loan.loanId}">
                                                    <button type="submit" class="action-button delete-button">Delete Loan</button>
                                                </form>
                                            </c:if>
                                            <c:if test="${loan.status == 'funded' && loan.amount <= 0}">
                                                <button class="action-button repay-button">Repay Loan</button>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="no-loans">You have not requested any loans yet.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

            <c:if test="${userRole == 'lender' || userRole == 'both'}">
                <div class="loan-section">
                    <h2>Loans You've Funded</h2>
                    <c:choose>
                        <c:when test="${not empty myFundedLoans}">
                            <div class="loan-list">
                                <c:forEach items="${myFundedLoans}" var="loan">
                                    <div class="loan-card">
                                        <div class="loan-header">
                                            <span>Funded: $<c:out value="${loan.amount}"/></span>
                                            <span class="loan-status status-${loan.status}"><c:out value="${loan.status}"/></span>
                                        </div>
                                        <div class="loan-details">
                                            <p><i class="fas fa-hand-holding-usd"></i> Purpose: <c:out value="${loan.purpose}"/></p>
                                            <p><i class="fas fa-clock"></i> Duration: <c:out value="${loan.durationMonths}"/> months</p>
                                            <p><i class="fas fa-calendar-alt"></i> Created On: <c:out value="${loan.createdOn}"/></p>
                                        </div>
                                        <div class="loan-description">
                                            <c:out value="${loan.description}"/>
                                        </div>
                                        <div class="loan-footer">
                                            <c:if test="${loan.status == 'funded'}">
                                                <button class="action-button view-details">View Repayment Plan</button>
                                            </c:if>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="no-loans">You have not funded any loans yet.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>
        </div>
    </div>
</body>
</html> 