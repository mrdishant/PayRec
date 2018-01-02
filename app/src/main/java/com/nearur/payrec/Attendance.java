package com.nearur.payrec;

/**
 * Created by mrdis on 12/26/2017.
 */

public class Attendance {

    String date;
    boolean present;
    String approver;

    public Attendance() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    @Override
    public String toString() {
        String value;
        if(present){
            value="Present";
        }else {
            value="Absent";
        }
        return "Attendance{" +
                "date='" + date + '\'' +
                ", status=" + value +
                ", approver='" + approver + '\'' +
                '}';
    }
}
