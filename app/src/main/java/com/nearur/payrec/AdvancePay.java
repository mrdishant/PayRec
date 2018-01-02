package com.nearur.payrec;

/**
 * Created by mrdis on 12/31/2017.
 */

public class AdvancePay {

    String date;
    String approver;
    float amount;

    public AdvancePay() {
    }

    @Override
    public String toString() {
        return "AdvancePay{" +
                "date='" + date + '\'' +
                ", approver='" + approver + '\'' +
                ", amount=" + amount +
                '}';
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
