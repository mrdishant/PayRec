package com.nearur.payrec;

import java.util.ArrayList;

/**
 * Created by mrdis on 12/26/2017.
 */

public class Salaried extends Worker {

    long salary;

    public Salaried() {
    }



    public long getSalary() {
        return salary;
    }

    public void setSalary(long salary) {
        this.salary = salary;
    }


    @Override
    public String toString() {
        return "Salaried{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                ", payment_due=" + payment_due +
                ", advance_payment=" + advance_payment +
                ", job='" + job + '\'' +
                ", date_join='" + date_join + '\'' +
                ", picture='" + picture + '\'' +
                ", mobile=" + mobile +
                ", address='" + address + '\'' +
                '}';
    }
}

