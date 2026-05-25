package com.teacher.game.state;

import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;

import android.view.MotionEvent;

public abstract class State {
	
	public abstract void init();
	
	public abstract void update(float delta);
	
	public abstract void render(Painter g);	

	public abstract boolean onTouch(MotionEvent e,int scaleX,int scaleY);
	
	public void setCurrentState(State newState) {
		GameMainActivity.sGame.setCurrentState(newState);
	}
	
	public void onPause()	{}
	
	public void onResume()	{}
}
