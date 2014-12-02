package com.github.mcfeod.robots4droid;

public class SettingsParser {
	public static final char NORMAL_MODE = '0';
	public static final char EXTRA_FAST_BOTS = '1';

	private static boolean isMusicOn = true;
	private static boolean areSuicidesPermitted = true;
	private static char gameMode = NORMAL_MODE;
	private static boolean areMinesOn = true;
	private static boolean areBombsOn = true;
	private static boolean isLSDOn = false;

	public static void setMusicMode(boolean on){
		isMusicOn = on;
	}
	
	public static void setSuicidePermission(boolean permitted){
		areSuicidesPermitted = permitted;
	}
	
	public static void setGameComplexity(char complexity){
		gameMode = complexity;
	}
	
	public static void setMineMode(boolean on){
		areMinesOn = on;
	}
	
	public static void setBombMode(boolean on){
		areBombsOn = on;
	}
	
	public static void setLSDMode(boolean on){
		isLSDOn = on;
	}
	
	public static boolean areMinesOn(){
		return areMinesOn;
	}
	
	public static boolean areBombsOn(){
		return areBombsOn;
	}
	
	public static boolean isMusicOn(){
		return isMusicOn;
	}
	
	public static boolean areSuicidesOn(){
		return areSuicidesPermitted;
	}
	
	public static boolean needExtraFastBots(){
		return  gameMode == EXTRA_FAST_BOTS;
	}
	
	public static boolean isLSDOn(){
		return isLSDOn;
	}
}
