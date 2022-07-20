package com.antitheft.donttouchmyphone;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class PinDataBase {
    public static Context context;
   public static void setPin(String PIN){
       SharedPreferences.Editor editor = context.getSharedPreferences("MyDataBase", MODE_PRIVATE).edit();
       editor.putString("PIN", PIN);
       editor.putBoolean("isPinActivated", true);
       editor.apply();
   }
    public static Boolean pinConnexion(String userPin){
        SharedPreferences prefs = context.getSharedPreferences("MyDataBase", MODE_PRIVATE);
        String PIN = prefs.getString("PIN", "No name defined");//"No name defined" is the default value.
        //int idName = prefs.getInt("idName", 0);
        if (userPin.equals(PIN)){
            return true;
        }else {
            return false;
        }
    }
    public static Boolean isPinActivated(){
        SharedPreferences prefs = context.getSharedPreferences("MyDataBase", MODE_PRIVATE);
        return prefs.getBoolean("isPinActivated", false);
    }
    public static void setIsPinActivated(Boolean PIN){
        SharedPreferences.Editor editor = context.getSharedPreferences("MyDataBase", MODE_PRIVATE).edit();
        editor.putBoolean("isPinActivated", PIN);
        editor.apply();
    }
    public static String getPin(){
        SharedPreferences prefs = context.getSharedPreferences("MyDataBase", MODE_PRIVATE);
        String PIN = prefs.getString("PIN", "0000");//"No name defined" is the default value.
        return PIN;
    }

}
