package com.tomaz.focustimer.other;

public enum Sessions {
	
	// TODO setting secToCount
	WORKING(30),LONG_REST(5),SHORT_REST(15);
	
	private int sec = 0;
	private Sessions(int sec){
		this.sec = sec;
	}
	
	public int getSec(){
		return this.sec;
	}
}
