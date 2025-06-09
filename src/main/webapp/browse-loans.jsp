<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Browse Loans - LendLink</title>
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
            max-width: 1200px;
            margin: 0 auto;
            padding: 2rem;
        }

        .filters {
            background: white;
            padding: 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            margin-bottom: 2rem;
        }

        .filter-group {
            display: flex;
            gap: 1rem;
            margin-bottom: 1rem;
        }

        .filter-group label {
            display: block;
            margin-bottom: 0.5rem;
            color: #2c3e50;
            font-weight: 500;
        }

        .filter-group select,
        .filter-group input {
            padding: 0.5rem;
            border: 1px solid #ddd;
            border-radius: 4px;
            font-size: 1rem;
        }

        .loans-grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
            gap: 1.5rem;
        }

        .loan-card {
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            padding: 1.5rem;
            transition: transform 0.2s ease;
        }

        .loan-card:hover {
            transform: translateY(-5px);
        }

        .loan-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 1rem;
        }

        .loan-amount {
            font-size: 1.5rem;
            font-weight: bold;
            color: #2c3e50;
        }

        .loan-purpose {
            background: #e9ecef;
            padding: 0.25rem 0.75rem;
            border-radius: 20px;
            font-size: 0.9rem;
            color: #495057;
        }

        .loan-details {
            margin-bottom: 1rem;
        }

        .loan-details p {
            margin: 0.5rem 0;
            color: #6c757d;
        }

        .loan-description {
            margin: 1rem 0;
            color: #495057;
            font-size: 0.95rem;
        }

        .fund-button {
            background-color: #28a745;
            color: white;
            padding: 0.8rem 1.5rem;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            width: 100%;
            font-size: 1rem;
            transition: background-color 0.3s ease;
        }

        .fund-button:hover {
            background-color: #218838;
        }

        .fund-button:disabled {
            background-color: #6c757d;
            cursor: not-allowed;
        }

        .no-loans {
            text-align: center;
            padding: 2rem;
            background: white;
            border-radius: 8px;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
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
    <jsp:include page="./sidebar.jsp"/>
    <div class="main-content">
        <div class="container">
            <h1>Browse Available Loans</h1>
            
            <c:if test="${not empty error}">
                <div class="error">
                    <c:out value="${error}"/>
                </div>
            </c:if>

            <div class="filters">
                <form action="browse-loans" method="get" id="filterForm">
                    <div class="filter-group">
                        <div>
                            <label for="minAmount">Min Amount ($)</label>
                            <input type="number" id="minAmount" name="minAmount" min="100" step="100" value="${param.minAmount}">
                        </div>
                        <div>
                            <label for="maxAmount">Max Amount ($)</label>
                            <input type="number" id="maxAmount" name="maxAmount" max="10000" step="100" value="${param.maxAmount}">
                        </div>
                        <div>
                            <label for="duration">Duration</label>
                            <select id="duration" name="duration">
                                <option value="">Any</option>
                                <option value="3" ${param.duration == '3' ? 'selected' : ''}>3 months</option>
                                <option value="6" ${param.duration == '6' ? 'selected' : ''}>6 months</option>
                                <option value="12" ${param.duration == '12' ? 'selected' : ''}>12 months</option>
                                <option value="24" ${param.duration == '24' ? 'selected' : ''}>24 months</option>
                            </select>
                        </div>
                        <div>
                            <label for="purpose">Purpose</label>
                            <select id="purpose" name="purpose">
                                <option value="">Any</option>
                                <option value="personal" ${param.purpose == 'personal' ? 'selected' : ''}>Personal</option>
                                <option value="business" ${param.purpose == 'business' ? 'selected' : ''}>Business</option>
                                <option value="education" ${param.purpose == 'education' ? 'selected' : ''}>Education</option>
                                <option value="home" ${param.purpose == 'home' ? 'selected' : ''}>Home</option>
                                <option value="other" ${param.purpose == 'other' ? 'selected' : ''}>Other</option>
                            </select>
                        </div>
                        <div>
                            <label for="sortBy">Sort By</label>
                            <select id="sortBy" name="sortBy">
                                <option value="newest" ${param.sortBy == 'newest' ? 'selected' : ''}>Newest First</option>
                                <option value="oldest" ${param.sortBy == 'oldest' ? 'selected' : ''}>Oldest First</option>
                                <option value="amount_asc" ${param.sortBy == 'amount_asc' ? 'selected' : ''}>Amount (Low to High)</option>
                                <option value="amount_desc" ${param.sortBy == 'amount_desc' ? 'selected' : ''}>Amount (High to Low)</option>
                            </select>
                        </div>
                    </div>
                    <button type="submit" class="fund-button">Apply Filters</button>
                </form>
            </div>

            <div class="loans-grid">
                <c:forEach items="${loanRequests}" var="loan">
                    <div class="loan-card">
                        <div class="loan-header">
                            <span class="loan-amount">$${loan.amount}</span>
                            <span class="loan-purpose">${loan.purpose}</span>
                        </div>
                        <div class="loan-details">
                            <p><i class="fas fa-clock"></i> Duration: ${loan.durationMonths} months</p>
                            <p><i class="fas fa-calendar"></i> Posted: ${loan.createdOn}</p>
                        </div>
                        <div class="loan-description">
                            ${loan.description}
                        </div>
                        <form action="fund-loan" method="post">
                            <input type="hidden" name="loanId" value="${loan.loanId}">
                            <div class="form-group" style="margin-bottom: 1rem;">
                                <label for="fundingAmount">Amount to Fund ($)</label>
                                <input type="number" id="fundingAmount" name="fundingAmount" 
                                       min="1" step="0.01" max="${loan.amount}" 
                                       value="${loan.amount}" required
                                       style="width: 100%; padding: 0.5rem; border: 1px solid #ddd; border-radius: 4px;">
                            </div>
                            <button type="submit" class="fund-button">Fund This Loan</button>
                        </form>
                    </div>
                </c:forEach>
            </div>

            <c:if test="${empty loanRequests}">
                <div class="no-loans">
                    <h2>No loans available</h2>
                    <p>There are currently no loan requests matching your criteria.</p>
                </div>
            </c:if>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            const minAmount = document.getElementById('minAmount');
            const maxAmount = document.getElementById('maxAmount');

            function validateAmounts() {
                const min = parseInt(minAmount.value);
                const max = parseInt(maxAmount.value);

                if (min && max && min > max) {
                    maxAmount.value = min;
                }
            }

            minAmount.addEventListener('change', validateAmounts);
            maxAmount.addEventListener('change', validateAmounts);
        });
    </script>
</body>
</html> 