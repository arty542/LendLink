package com.lendlink.model;

import java.util.List;
import java.sql.Timestamp;
import java.util.ArrayList;

public class DashboardData {
    private int loansTaken;
    private double totalBorrowed;
    private int loansFunded;
    private double totalLent;
    private double walletBalance;
    private List<Activity> recentActivity;

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

    public double getTotalLent() {
        return totalLent;
    }

    public void setTotalLent(double totalLent) {
        this.totalLent = totalLent;
    }

    public double getWalletBalance() {
        return walletBalance;
    }

    public void setWalletBalance(double walletBalance) {
        this.walletBalance = walletBalance;
    }

    public List<Activity> getRecentActivity() {
        return recentActivity;
    }

    public void setRecentActivity(List<Activity> recentActivity) {
        this.recentActivity = recentActivity;
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