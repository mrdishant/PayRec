package com.nearur.payrec;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mrdis on 12/26/2017.
 */

public class DailyWaged extends Worker implements Serializable {

    public DailyWaged() {
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
