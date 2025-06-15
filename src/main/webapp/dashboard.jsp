<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
            margin-left: 60px; 
            padding: 2rem 5%;
            flex-grow: 1;
            transition: margin-left 0.3s ease; 
        }
        
        body:has(.sidebar:hover) .main-content {
            margin-left: 250px; 
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
    <jsp:include page="./sidebar.jsp"/>
    <div class="main-content">
        <div class="dashboard-container">
            <h1 class="welcome-message">Your Dashboard</h1>
            <div id="error-message" class="error" style="display:none;"></div>
            <div class="dashboard-grid" id="dashboard-cards">
            </div>

            <div class="section-header">
                <h2>Quick Actions</h2>
            </div>
            <div id="quick-actions" style="margin-bottom: 2rem;">
            </div>

            <div class="section-header">
                <h2>Recent Activity</h2>
            </div>
            <div id="recent-activity" class="recent-activity">
            </div>
        </div>
    </div>

    <script>
        document.addEventListener("DOMContentLoaded", function () {
            // Show loading state
            document.getElementById("dashboard-cards").innerHTML = '<div class="dashboard-card"><p>Loading...</p></div>';
            
            fetch("${pageContext.request.contextPath}/api/dashboard", {
                method: 'GET',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                credentials: 'same-origin'
            })
            .then(response => {
                if (!response.ok) throw new Error("Failed to fetch dashboard data");
                return response.json();
            })
            .then(data => {
                renderDashboard(data);
            })
            .catch(error => {
                document.getElementById("error-message").textContent = error.message;
                document.getElementById("error-message").style.display = "block";
                document.getElementById("dashboard-cards").innerHTML = '<div class="dashboard-card"><p>Error loading dashboard data</p></div>';
            });

            function renderDashboard(data) {
                console.log('API data:', data);
                console.log('loansTaken:', data.loansTaken, typeof data.loansTaken);
                console.log('totalBorrowed:', data.totalBorrowed, typeof data.totalBorrowed);
                console.log('walletBalance:', data.walletBalance, typeof data.walletBalance);

                function safeMoney(val) {
                    return (typeof val === 'number' && !isNaN(val)) ? val : '0.00';
                }
                function safeCount(val) {
                    return (typeof val === 'number' && !isNaN(val)) ? val : 0;
                }

                const cards = document.getElementById("dashboard-cards");
                const quickActions = document.getElementById("quick-actions");
                const activity = document.getElementById("recent-activity");

                // Clear existing content
                cards.innerHTML = '';
                quickActions.innerHTML = '';
                activity.innerHTML = '';

                const userRole = data.userRole || 'both';

                if (userRole === 'borrower' || userRole === 'both') {
                    cards.innerHTML +=
                        '<div class="dashboard-card">' +
                            '<h3>Loans Taken Out</h3>' +
                            '<p class="count">' + safeCount(data.loansTaken) + '</p>' +
                            '<a href="my-loans" class="card-link">View My Loans</a>' +
                        '</div>' +
                        '<div class="dashboard-card">' +
                            '<h3>Amount Borrowed</h3>' +
                            '<p class="amount">$' + safeMoney(data.totalBorrowed) + '</p>' +
                            '<a href="my-loans" class="card-link">View Details</a>' +
                        '</div>';

                    quickActions.innerHTML +=
                        '<a href="create-loan-request" class="button">Create New Loan Request</a>';
                }

                if (userRole === 'lender' || userRole === 'both') {
                    cards.innerHTML +=
                        '<div class="dashboard-card">' +
                            '<h3>Loans Funded</h3>' +
                            '<p class="count">' + safeCount(data.loansFunded) + '</p>' +
                            '<a href="my-loans" class="card-link">View My Fundings</a>' +
                        '</div>' +
                        '<div class="dashboard-card">' +
                            '<h3>Total Amount Lent</h3>' +
                            '<p class="amount">$' + safeMoney(data.totalLent) + '</p>' +
                            '<a href="my-loans" class="card-link">View Details</a>' +
                        '</div>' +
                        '<div class="dashboard-card">' +
                            '<h3>Available Loans</h3>' +
                            '<p class="count">' + safeCount(data.availableLoans) + '</p>' +
                            '<a href="browse-loans" class="card-link">Browse Loans</a>' +
                        '</div>';

                    quickActions.innerHTML +=
                        '<a href="browse-loans" class="button">Browse Lending Opportunities</a>';
                }

                cards.innerHTML +=
                    '<div class="dashboard-card wallet-summary">' +
                        '<h3>Wallet Balance</h3>' +
                        '<p class="balance">$' + safeMoney(data.walletBalance) + '</p>' +
                        '<a href="wallet" class="card-link">View Wallet</a>' +
                    '</div>';

                if (!data.recentActivity || data.recentActivity.length === 0) {
                    activity.innerHTML = '<p>No recent activity to display.</p>';
                } else {
                    data.recentActivity.forEach(function(act) {
                        var description = '';
                        if (act.type === 'funding') {
                            description = 'Funding of $' + safeMoney(act.amount) + ' related to Transaction ID ' + act.transactionId + ' by ' + act.fromUserName;
                        } else if (act.type === 'repayment') {
                            description = 'Repayment of $' + safeMoney(act.amount) + ' related to Transaction ID ' + act.transactionId + ' to ' + act.toUserName;
                        } else if (act.type === 'withdrawal') {
                            description = 'Withdrawal of $' + safeMoney(act.amount);
                        } else if (act.type === 'deposit') {
                            description = 'Deposit of $' + safeMoney(act.amount);
                        } else {
                            description = 'Transaction: ' + act.type + ', Amount: $' + safeMoney(act.amount);
                        }

                        var amountClass = act.type === 'incoming' ? 'incoming' : 'outgoing';
                        var amountPrefix = act.type === 'incoming' ? '+' : '-';
                        var formattedDate = new Date(act.timestamp).toLocaleString();

                        activity.innerHTML +=
                            '<div class="activity-item">' +
                                '<span class="activity-icon">' +
                                    '<i class="fas fa-hand-holding-usd"></i>' +
                                '</span>' +
                                '<div class="activity-details">' +
                                    '<p class="activity-description">' + description + '</p>' +
                                    '<span class="activity-date">' + formattedDate + '</span>' +
                                '</div>' +
                                '<span class="activity-amount ' + amountClass + '">' +
                                    amountPrefix + '$' + safeMoney(act.amount) +
                                '</span>' +
                            '</div>';
                    });
                }
            }
        });
    </script>
</body>
</html>






