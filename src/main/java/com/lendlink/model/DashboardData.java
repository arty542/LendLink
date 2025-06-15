package com.lendlink.model;

import java.util.List;
import java.sql.Timestamp;
import java.util.ArrayList;

public class DashboardData {
    private int loansTaken;
    private double totalBorrowed;
    private int loansFunded;
    private double totalFunded;
    private double walletBalance;
    private int availableLoans;
    private List<Activity> recentActivity;
    private double totalLent;
    private String userRole;

    public DashboardData() {
        this.recentActivity = new ArrayList<>();
    }

    // Getters and Setters
    public int getLoansTaken() {
        return loansTaken;
    }

    public void setLoansTaken(int loansTaken) {
        this.loansTaken = loansTaken;
    }

    public double getTotalBorrowed() {
        return totalBorrowed;
    }

    public void setTotalBorrowed(double totalBorrowed) {
        this.totalBorrowed = totalBorrowed;
    }

    public int getLoansFunded() {
        return loansFunded;
    }

    public void setLoansFunded(int loansFunded) {
        this.loansFunded = loansFunded;
    }

    public double getTotalFunded() {
        return totalFunded;
    }

    public void setTotalFunded(double totalFunded) {
        this.totalFunded = totalFunded;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public int getAvailableLoans() {
        return availableLoans;
    }

    public void setAvailableLoans(int availableLoans) {
        this.availableLoans = availableLoans;
    }

    public List<Activity> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<Activity> recentActivity) {
        this.recentActivity = recentActivity;
    }

    public double getTotalLent() {
        return totalLent;
    }

    public void setTotalLent(double totalLent) {
        this.totalLent = totalLent;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    // Inner class for activity items
    public static class Activity {
        private int transactionId;
        private double amount;
        private Timestamp timestamp;
        private String type;
        private String fromUserName;
        private String toUserName;

        public Activity(int transactionId, double amount, Timestamp timestamp, String type, String fromUserName, String toUserName) {
            this.transactionId = transactionId;
            this.amount = amount;
            this.timestamp = timestamp;
            this.type = type;
            this.fromUserName = fromUserName;
            this.toUserName = toUserName;
        }

        public int getTransactionId() {
            return transactionId;
        }

        public double getAmount() {
            return amount;
        }

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public String getType() {
            return type;
        }

        public String getFromUserName() {
            return fromUserName;
        }

        public String getToUserName() {
            return toUserName;
        }
    }
} 