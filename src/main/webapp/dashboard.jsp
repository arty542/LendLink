<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Dashboard - LendLink</title>
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
            min-height: 100vh;
            display: flex;
        }

        .main-content {
            margin-left: 60px; /* Initial collapsed sidebar width */
            padding: 2rem 5%;
            flex-grow: 1;
            transition: margin-left 0.3s ease; /* Smooth transition */
        }

        /* Adjust main content margin when sidebar is hovered */
        body:has(.sidebar:hover) .main-content {
            margin-left: 250px; /* Expanded sidebar width */
        }

        .dashboard-container {
            max-width: 1200px;
            margin: 0 auto;
            width: 100%;
        }

        .welcome-message {
            margin-bottom: 2rem;
            color: #2c3e50;
        }

        .dashboard-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 2rem;
            margin-bottom: 2rem;
        }

        .dashboard-card {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            display: flex;
            flex-direction: column;
            justify-content: space-between;
        }

        .dashboard-card h3 {
            color: #2c3e50;
            margin-bottom: 0.5rem;
        }

        .dashboard-card p {
            font-size: 1.8rem;
            font-weight: bold;
            color: #3498db;
            margin-bottom: 1rem;
        }

        .card-link {
            color: #3498db;
            text-decoration: none;
            font-weight: 500;
            align-self: flex-start;
            transition: color 0.3s ease;
        }

        .card-link:hover {
            text-decoration: underline;
        }

        .section-header {
            border-bottom: 1px solid #e1e1e1;
            padding-bottom: 1rem;
            margin-bottom: 1.5rem;
            color: #2c3e50;
        }

        .recent-activity .activity-item {
            background: white;
            padding: 1rem;
            border-radius: 4px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            margin-bottom: 1rem;
            display: flex;
            align-items: center;
            gap: 1rem;
        }

        .activity-icon {
            color: #3498db;
            font-size: 1.5rem;
        }

        .activity-details {
            flex-grow: 1;
        }

        .activity-description {
            margin: 0;
            font-weight: 500;
        }

        .activity-date {
            font-size: 0.9rem;
            color: #666;
        }

        .activity-amount {
            font-size: 1.1rem;
            font-weight: bold;
        }

        .activity-amount.incoming {
            color: #28a745;
        }

        .activity-amount.outgoing {
            color: #dc3545;
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
            margin-top: 1rem;
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
    </style>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>
    <jsp:include page="/WEB-INF/jspf/sidebar.jsp"/>
    <div class="main-content">
        <div class="dashboard-container">
            <h1 class="welcome-message">Your Dashboard</h1>

            <c:if test="${not empty error}">
                <div class="error">
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <div class="dashboard-grid">
                 <c:if test="${sessionScope.userRole == 'borrower' || sessionScope.userRole == 'both'}">
                    <div class="dashboard-card">
                        <h3>Loans Taken Out</h3>
                        <p class="count"><c:out value="${dashboardData.loansTaken}"/></p>
                        <a href="my-loans" class="card-link">View My Loans</a>
                    </div>

                    <div class="dashboard-card">
                        <h3>Amount Borrowed</h3>
                        <p class="amount">$<fmt:formatNumber value="${dashboardData.totalBorrowed}" pattern="#,##0.00"/></p>
                        <a href="my-loans" class="card-link">View Details</a>
                    </div>
                </c:if>

                <c:if test="${sessionScope.userRole == 'lender' || sessionScope.userRole == 'both'}">
                     <div class="dashboard-card">
                        <h3>Loans Funded</h3>
                        <p class="count"><c:out value="${dashboardData.loansFunded}"/></p>
                        <a href="my-fundings" class="card-link">View My Fundings</a>
                    </div>

                    <div class="dashboard-card">
                        <h3>Amount Lent</h3>
                        <p class="amount">$<fmt:formatNumber value="${dashboardData.totalLent}" pattern="#,##0.00"/></p>
                        <a href="my-fundings" class="card-link">View Details</a>
                    </div>
                </c:if>

                 <div class="dashboard-card wallet-summary">
                     <h3>Wallet Balance</h3>
                     <p class="balance">$<fmt:formatNumber value="${dashboardData.walletBalance}" pattern="#,##0.00"/></p>
                     <a href="wallet" class="card-link">View Wallet</a>
                 </div>
            </div>

            <div class="section-header">
                <h2>Quick Actions</h2>
            </div>

            <div style="margin-bottom: 2rem;">
                 <c:if test="${sessionScope.userRole == 'borrower' || sessionScope.userRole == 'both'}">
                    <a href="create-loan-request" class="button">Create New Loan Request</a>
                </c:if>

                <c:if test="${sessionScope.userRole == 'lender' || sessionScope.userRole == 'both'}">
                    <a href="browse-loans" class="button">Browse Lending Opportunities</a>
                </c:if>
            </div>

             <div class="section-header">
                 <h2>Recent Activity</h2>
             </div>

             <div class="recent-activity">
                 <c:choose>
                     <c:when test="${empty dashboardData.recentActivity}">
                         <p>No recent activity to display.</p>
                     </c:when>
                     <c:otherwise>
                         <c:forEach items="${dashboardData.recentActivity}" var="activity">
                             <div class="activity-item">
                                 <span class="activity-icon">
                                     <i class="fas fa-hand-holding-usd"></i>
                                 </span>
                                 <div class="activity-details">
                                     <p class="activity-description">
                                         <c:choose>
                                             <c:when test="${activity.type == 'funding'}">
                                                 Funding of $<fmt:formatNumber value="${activity.amount}" pattern="#,##0.00"/> related to Transaction ID ${activity.transactionId} by ${activity.fromUserName}
                                             </c:when>
                                             <c:when test="${activity.type == 'repayment'}">
                                                 Repayment of $<fmt:formatNumber value="${activity.amount}" pattern="#,##0.00"/> related to Transaction ID ${activity.transactionId} to ${activity.toUserName}
                                             </c:when>
                                             <c:when test="${activity.type == 'withdrawal'}">
                                                 Withdrawal of $<fmt:formatNumber value="${activity.amount}" pattern="#,##0.00"/>
                                             </c:when>
                                             <c:when test="${activity.type == 'deposit'}">
                                                 Deposit of $<fmt:formatNumber value="${activity.amount}" pattern="#,##0.00"/>
                                             </c:when>
                                             <c:otherwise>
                                                 Transaction: ${activity.type}, Amount: $<fmt:formatNumber value="${activity.amount}" pattern="#,##0.00"/>
                                             </c:otherwise>
                                         </c:choose>
                                     </p>
                                     <span class="activity-date"><fmt:formatDate value="${activity.timestamp}" pattern="yyyy-MM-dd HH:mm"/></span>
                                 </div>
                                 <span class="activity-amount ${activity.type}">
                                     ${activity.type == 'incoming' ? '+' : '-'}$<fmt:formatNumber value="${activity.amount}" pattern="#,##0.00"/>
                                 </span>
                             </div>
                         </c:forEach>
                     </c:otherwise>
                 </c:choose>
             </div>
        </div>
    </div>
</body>
</html> 