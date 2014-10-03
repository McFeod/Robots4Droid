package com.github.mcfeod.robots4droid;

public class Object{
	private int mPower;
	private int mKind;
	
	public static final byte JUNK=1;
	public static final byte BOT=2;
	public static final byte FASTBOT=3;	
	
	public Object(int kind){
		mKind = kind;
		switch (mKind){
			case JUNK: mPower = 4; break;
			case BOT: mPower = 2; break;
			case FASTBOT: mPower = 3; break;
			default: mPower = 0;
		}
	}
	
	public int GetPower(){
		return mPower;	
	}
	
	public void SetPower(int power){
		mPower = power;	
		if (mPower > 4)
			mPower = 4;
	}
	
	public void ChPower(int diff){
		mPower += diff;	
		if (mPower > 4)
			mPower = 4;
	}
	
	public int GetKind(){
		return mKind;
	}
	
	public void SetKind(int kind){
		mKind = kind;
		switch (mKind){
			case JUNK: mPower = 4; break;
			case BOT: mPower = 2; break;
			case FASTBOT: mPower = 3; break;
			default: mPower = 0;
		}
	}
	
	public Object CopyObject(){
		return new Object(mKind);
	}
	
}