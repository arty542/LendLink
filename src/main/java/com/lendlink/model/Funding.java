package com.lendlink.model;

import java.sql.Timestamp;

public class Funding {
    private int fundingId;
    private int loanId;
    private int lenderId;
    private double amountFunded;
    private Timestamp fundedOn;

    public Funding() {
    }

    public Funding(int loanId, int lenderId, double amountFunded) {
        this.loanId = loanId;
        this.lenderId = lenderId;
        this.amountFunded = amountFunded;
    }

    // Getters and Setters
    public int getFundingId() {
        return fundingId;
    }

    public void setFundingId(int fundingId) {
        this.fundingId = fundingId;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getLenderId() {
        return lenderId;
    }

    public void setLenderId(int lenderId) {
        this.lenderId = lenderId;
    }

    public double getAmountFunded() {
        return amountFunded;
    }

    public void setAmountFunded(double amountFunded) {
        this.amountFunded = amountFunded;
    }

    public Timestamp getFundedOn() {
        return fundedOn;
    }

    public void setFundedOn(Timestamp fundedOn) {
        this.fundedOn = fundedOn;
    }
} 