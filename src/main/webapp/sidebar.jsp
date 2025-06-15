<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<style>
    .sidebar {
        width: 60px; /* Collapsed width */
        background-color: #2c3e50;
        color: white;
        padding-top: 20px;
        min-height: 100vh;
        position: fixed;
        left: 0;
        top: 0;
        box-shadow: 2px 0 5px rgba(0,0,0,0.1);
        overflow-x: hidden; /* Hide overflow content */
        transition: width 0.3s ease; /* Smooth transition for width */
        z-index: 100;
    }

    .sidebar:hover {
        width: 250px; /* Expanded width on hover */
    }

    .sidebar .logo {
        font-weight: bold;
        margin-bottom: 30px;
        color: white;
        text-decoration: none;
        height: 60px;
        position: relative; /* Container for absolutely positioned children */
        overflow: hidden; /* Crucial for hiding overflowing text */
        display: flex; /* Use flexbox for centering */
        align-items: center;
        justify-content: center;
    }

    .sidebar .logo .icon {
        position: absolute;
        top: 50%;
        left: 50%; /* Center the icon */
        transform: translate(-50%, -50%); /* Center the icon */
        font-size: 1.2rem;
        opacity: 1; /* Visible when collapsed */
        transition: opacity 0.3s ease, left 0.3s ease; /* Smooth transition */
    }

    .sidebar .logo .text {
        position: absolute;
        top: 50%;
        left: -9999px; /* Ensure text is completely off-screen when collapsed */
        transform: translate(0, -50%); /* Vertically center when off-screen */
        font-size: 1.8rem;
        white-space: nowrap;
        opacity: 0; /* Hidden when collapsed */
        line-height: 1; /* Normalize line height for better vertical centering */
        transition: opacity 0.3s ease, left 0.3s ease, transform 0.3s ease; /* Smooth transition */
    }

    .sidebar:hover .logo .icon {
        opacity: 0; /* Icon fades out on hover */
        left: -100%; /* Move completely off-screen to the left */
    }

    .sidebar:hover .logo .text {
        opacity: 1; /* Text fades in on hover */
        left: 50%; /* Move to center */
        transform: translate(-50%, -50%); /* To align its center with `left` position AND vertically center */
    }

    .sidebar .user-info {
        text-align: center;
        padding: 20px;
        border-bottom: 1px solid rgba(255,255,255,0.1);
        margin-bottom: 20px;
        white-space: nowrap;
        opacity: 0; /* Hide initially */
        transition: opacity 0.3s ease;
    }

    .sidebar:hover .user-info {
        opacity: 1; /* Show on hover */
    }

    .sidebar .user-info p {
        margin: 5px 0;
        font-weight: 500;
    }

    .sidebar .user-info .role {
        font-size: 0.9em;
        color: #bdc3c7;
    }

    .sidebar ul {
        list-style: none;
        padding: 0;
    }

    .sidebar ul li a {
        display: flex;
        align-items: center;
        padding: 15px 20px;
        color: white;
        text-decoration: none;
        transition: background-color 0.3s ease, color 0.3s ease;
        white-space: nowrap;
    }

    .sidebar ul li a i {
        margin-right: 10px;
        font-size: 1.2rem;
    }

    .sidebar ul li a span {
        display: inline-block;
        opacity: 0;
        transition: opacity 0.3s ease;
    }

    .sidebar:hover ul li a span {
        opacity: 1;
    }

    .sidebar ul li a:hover {
        background-color: #34495e;
        color: #3498db;
    }

    .sidebar .logout-btn {
        background-color: #e74c3c;
        color: white;
        padding: 10px 20px;
        border-radius: 5px;
        text-align: center;
        margin: 20px auto;
        display: block;
        text-decoration: none;
        transition: background-color 0.3s ease;
        white-space: nowrap;
        opacity: 0;
    }

    .sidebar:hover .logout-btn {
        opacity: 1;
    }

    .sidebar .logout-btn:hover {
        background-color: #c0392b;
    }
</style>

<c:if test="${not empty sessionScope.userId}">
    <div class="sidebar">
        <a href="dashboard" class="logo">
            <i class="fas fa-link icon"></i>
            <span class="text">LendLink</span>
        </a>
        <div class="user-info">
            <p>Welcome, <c:out value="${sessionScope.userName}"/></p>
            <p class="role">Role: <c:out value="${sessionScope.userRole}"/></p>
        </div>
        <ul>
            <li><a href="dashboard"><i class="fas fa-tachometer-alt"></i> <span>Dashboard</span></a></li>
            <c:if test="${sessionScope.userRole == 'borrower' || sessionScope.userRole == 'both'}">
                <li><a href="create-loan-request"><i class="fas fa-plus-circle"></i> <span>Create Loan Request</span></a></li>
            </c:if>
            <c:if test="${sessionScope.userRole == 'lender' || sessionScope.userRole == 'both'}">
                <li><a href="browse-loans"><i class="fas fa-search-dollar"></i> <span>Browse Loans</span></a></li>
            </c:if>
            <li><a href="my-loans"><i class="fas fa-money-check-alt"></i> <span>My Loans</span></a></li>
            <li><a href="wallet"><i class="fas fa-wallet"></i> <span>Wallet</span></a></li>
            <li><a href="profile"><i class="fas fa-user"></i> <span>Profile</span></a></li>
            <li><a href="logout" class="logout-btn"><i class="fas fa-sign-out-alt"></i> <span>Logout</span></a></li>
        </ul>
    </div>
</c:if> 