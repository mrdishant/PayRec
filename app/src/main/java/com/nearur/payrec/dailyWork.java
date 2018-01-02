package com.nearur.payrec;

/**
 * Created by mrdis on 12/26/2017.
 */

public class dailyWork {

    String date;
    String approver;
    int units;
    float price_per_unit;
    float total;

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

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }



    public void setPrice_per_unit(int price_per_unit) {
        this.price_per_unit = price_per_unit;
    }

    public float getPrice_per_unit() {
        return price_per_unit;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "dailyWork{" +
                "date='" + date + '\'' +
                ", approver='" + approver + '\'' +
                ", units=" + units +
                ", price_per_unit=" + price_per_unit +
                ", total=" + total +
                '}';
    }
}
