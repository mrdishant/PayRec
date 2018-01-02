package com.nearur.payrec;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mrdis on 12/26/2017.
 */

public class Worker implements Serializable {

    String id,name;
    float payment_due;
    float advance_payment;
    String job;
    String date_join;
    String picture;
    long mobile;
    String address;

    private ArrayList<Payments> previous_payments;



    public Worker() {
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPayment_due() {
        return payment_due;
    }

    public void setPayment_due(float payment_due) {
        this.payment_due = payment_due;
    }

    public float getAdvance_payment() {
        return advance_payment;
    }

    public void setAdvance_payment(float advance_payment) {
        this.advance_payment = advance_payment;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getDate_join() {
        return date_join;
    }

    public void setDate_join(String date_join) {
        this.date_join = date_join;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public long getMobile() {
        return mobile;
    }

    public void setMobile(long mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<Payments> getPrevious_payments() {
        return previous_payments;
    }

    public void setPrevious_payments(ArrayList<Payments> previous_payments) {
        this.previous_payments = previous_payments;
    }

    @Override
    public String toString() {
        return
                "Id              : " + id + '\n'+
                        "Name            : " + name + '\n'+
                        "Payment_Due     : " + payment_due+'\n'+
                        "Advance Payment : " + advance_payment + '\n'+
                        "Job             : " + job + '\n'+
                        "Date of Join    : " + date_join + '\n'+
                        "Mobile          : " + mobile + '\n'+
                        "Address         : " + address ;
    }

}
