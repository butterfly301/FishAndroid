package com.teacher.framework.util;

import com.teacher.fish.GameMainActivity;
import com.teacher.game.state.State;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class InputHandler implements OnTouchListener {
	
private State currentState;
	
	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		int scaleX = (int)(e.getX()/v.getWidth() *  GameMainActivity.GAME_WIDTH);
		int scaleY = (int)(e.getY()/v.getHeight() *  GameMainActivity.GAME_HEIGHT);		
		return currentState.onTouch(e, scaleX, scaleY);
	}

}
