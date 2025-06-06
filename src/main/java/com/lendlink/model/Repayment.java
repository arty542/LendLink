package com.lendlink.model;

import java.sql.Date;

public class Repayment {
    private int repaymentId;
    private int loanId;
    private Date dueDate;
    private double amountDue;
    private double amountPaid;
    private Date paidOn;
    private String status;

    public Repayment() {
    }

    public Repayment(int loanId, Date dueDate, double amountDue, String status) {
        this.loanId = loanId;
        this.dueDate = dueDate;
        this.amountDue = amountDue;
        this.status = status;
    }

    public int getRepaymentId() {
        return repaymentId;
    }

    public void setRepaymentId(int repaymentId) {
        this.repaymentId = repaymentId;
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public Date getPaidOn() {
        return paidOn;
    }

    public void setPaidOn(Date paidOn) {
        this.paidOn = paidOn;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 