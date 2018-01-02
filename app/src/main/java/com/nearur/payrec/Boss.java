package com.nearur.payrec;

import java.util.ArrayList;

/**
 * Created by mrdis on 12/26/2017.
 */

public class Boss {

    String name;
    long total_payment_due;
    long total_advance;

    public Boss() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotal_payment_due() {
        return total_payment_due;
    }

    public void setTotal_payment_due(long total_payment_due) {
        this.total_payment_due = total_payment_due;
    }


    public long getTotal_advance() {
        return total_advance;
    }

    public void setTotal_advance(long total_advance) {
        this.total_advance = total_advance;
    }
}
