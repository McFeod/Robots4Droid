package com.github.mcfeod.robots4droid;

public class SettingsParser {
    public static String getSettingsString(boolean isMusicOn){
        // StringBuilder setting = ...
        if(isMusicOn){
            return "1";
        }else{
            return "0";
        }
    }
    public static boolean isMusicOn(String settings){
        return settings.charAt(0) == '1';
    }
}
