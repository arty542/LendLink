package com.lendlink.model;

import java.sql.Timestamp;

public class LoanRequest {
    private int loanId;
    private int userId;
    private double amount;
    private int durationMonths;
    private String purpose;
    private String description;
    private String status;
    private Timestamp createdOn;
    private double fundedAmount;

    public LoanRequest() {
    }

    public LoanRequest(int userId, double amount, int durationMonths, String purpose, String description, String status) {
        this.userId = userId;
        this.amount = amount;
        this.durationMonths = durationMonths;
        this.purpose = purpose;
        this.description = description;
        this.status = status;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Timestamp createdOn) {
        this.createdOn = createdOn;
    }

    public double getFundedAmount() {
        return fundedAmount;
    }

    public void setFundedAmount(double fundedAmount) {
        this.fundedAmount = fundedAmount;
    }
} 