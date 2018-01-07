package com.nearur.payrec;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

/**
 * Created by mrdis on 1/2/2018.
 */

public class SharedPrefManager  {

    private static final String name="Payrec";
    private static final String key="KeyToken";
    private static Context context;
    private static SharedPrefManager instance;

    public SharedPrefManager(Context context) {
        this.context = context;
    }

    public static SharedPrefManager getInstance(Context context){
        if(instance == null){
            instance=new SharedPrefManager(context);
        }
        return instance;
    }

    public  boolean storeToken(String token){
        SharedPreferences sharedPreferences=context.getSharedPreferences(name,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(key,token);
        editor.commit();
        return true;
    }

    public  String getToken(){
        SharedPreferences sharedPreferences=context.getSharedPreferences(name,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,null);
    }
}
