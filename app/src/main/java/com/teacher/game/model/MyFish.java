package com.teacher.game.model;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;

import android.graphics.Bitmap;

public class MyFish extends Fish {
	
	public int mStartTime;

	public float mSpeedMultiplier = 1.0f;

	public boolean mHasShield = false;

	public MyFish() {
		super(Assets.angelfishnormal, 34, 28, NORMAL, DIE);
		setPosition(260, 300);
		setNonceState(SWIML);
		mStartTime = 0;
		mSpeedMultiplier = 1.0f;
		mHasShield = false;
	}
	
	public void setSize(byte size) {
		switch(size) {
		case BIG:
			this.setImage(Assets.angelfishbig, 84, 68);
			mSize = size;
			break;
		case SUPER:
			this.setImage(Assets.angelfishsuper, 118, 94);
			mSize = size;
			break;
		}
	}
	
	public void update() {
		mStartTime++;
		if (mStartTime < 10)
			move(0, 10);
		else {
			move(mMoveX, mMoveY);
			if (mMoveX > 0)
				mMoveX--;
			else if (mMoveX < 0)
				mMoveX++;
			if (mMoveY > 0)
				mMoveY--;
			else if (mMoveY < 0)
				mMoveY++;
		}
		changeState();
		nextFrame();
	}
	
	public void movePress(int mx,int my) {
		if (mNonceState == DIE)
			return;		
		mx = (int)(mx / 10 * mSpeedMultiplier);
		my = (int)(my / 10 * mSpeedMultiplier);
		if (mx < 0) {
			setNonceState(SWERVE_L);
			if (mMoveX > -8 * mSpeedMultiplier)
				mMoveX += mx;
		}
		if (mx > 0) {
			setNonceState(SWERVE_R);
			if (mMoveX < 8 * mSpeedMultiplier)
				mMoveX += mx;
		}
		if (my < 0) {
			if (mMoveY > -4 * mSpeedMultiplier)
				mMoveY += my;
		}
		if (my > 0) {
			if (mMoveY < 4 * mSpeedMultiplier)
				mMoveY += my;
		}
	}
	
	@Override
	public void move(int dx, int dy) {
		if (getX() + dx > 0 && 
			getX() + getWidth() + dx < GameMainActivity.GAME_WIDTH && 	
			getY() + dy > GameMainActivity.getPlayTop() &&
			getY() + getHeight() + dy < GameMainActivity.getPlayBottom()
			) {			
			super.move(dx, dy);
		}
	}
	
	@Override
	public void changeState() {
		super.changeState();
		if (mNonceState == DIE && getFrame() == 9) {
			setPosition(260, 300);
			setNonceState(SWIML);
			mStartTime = 0;
		}
	}

}
