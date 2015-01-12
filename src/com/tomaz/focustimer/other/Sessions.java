package com.tomaz.focustimer.other;

public enum Sessions {
	WORKING(25*60),LONG_REST(15*60),SHORT_REST(5*60);
	
	private int sec = 0;
	private Sessions(int sec){
		this.sec = sec;
	}
	
	public int getSec(){
		return this.sec;
	}
}
