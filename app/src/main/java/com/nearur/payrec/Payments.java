package com.nearur.payrec;

/**
 * Created by mrdis on 12/26/2017.
 */

public class Payments {

    String date;
    long amount_paid;
    String approver;

    public Payments() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getAmount_paid() {
        return amount_paid;
    }

    public void setAmount_paid(long amount_paid) {
        this.amount_paid = amount_paid;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    @Override
    public String toString() {
        return "Payments{" +
                "date='" + date + '\'' +
                ", amount_paid=" + amount_paid +
                ", approver='" + approver + '\'' +
                '}';
    }
}
