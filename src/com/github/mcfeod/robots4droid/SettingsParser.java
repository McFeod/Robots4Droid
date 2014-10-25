package com.github.mcfeod.robots4droid;

public class SettingsParser {
    public static final char NORMAL_MODE = '0';
    public static final char EXTRA_FAST_BOTS = '1';

    private static final int SETTINGS_SIZE = 3;

    public static String settings = "100";

    private static StringBuilder mBuilder = new StringBuilder(settings);
    /* Семантика параметров:
    *  1 символ
    *    0 -- музыка выключена
    *    1 -- музыка включена
    *  2 символ
    *    0 -- самоубийственные ходы заблокированы
    *    1 -- самоубиственные ходы разрешены
    *  3 символ
    *    0 -- обычные пропорции роботов
    *    1 -- повышенное количество быстрых роботов, пониженное -- обычных
    * */
     public static String getSettings(){
         return settings;
     }
     public static String getSettings(boolean isMusicOn, boolean areSuicidesOn, char complexity){
         mBuilder.delete(0, SETTINGS_SIZE);
        if(isMusicOn){
            mBuilder.append('1');
        }else{
            mBuilder.append('0');
        }
        if(areSuicidesOn){
            mBuilder.append('1');
        }else{
            mBuilder.append('0');
        }
        mBuilder.append(complexity);
        settings = mBuilder.toString();
        return settings;
    }

    public static void setMusicMode(boolean on){
        if(on){
            mBuilder.setCharAt(0,'1');
        }else{
            mBuilder.setCharAt(0,'0');
        }
        settings = mBuilder.toString();
    }
    public static void setSuicidePermission(boolean permitted){
        if(permitted){
            mBuilder.setCharAt(1,'1');
        }else{
            mBuilder.setCharAt(1,'0');
        }
        settings = mBuilder.toString();
    }
    public static void setGameComplexity(char complexity){
        mBuilder.setCharAt(2, complexity);
        settings = mBuilder.toString();
    }

    public static boolean isMusicOn(String settings){
        return settings.charAt(0) == '1';
    }

    public static boolean isMusicOn(){
        return settings.charAt(0) == '1';
    }

    public static boolean areSuicidesOn(String settings){
        return settings.charAt(1) == '1';
    }

    public static boolean areSuicidesOn(){
        return settings.charAt(1) == '1';
    }

    public static boolean needExtraFastBots(String settings){
        return settings.charAt(2) == EXTRA_FAST_BOTS;
    }

    public static boolean needExtraFastBots(){
        return settings.charAt(2) == EXTRA_FAST_BOTS;
    }
}