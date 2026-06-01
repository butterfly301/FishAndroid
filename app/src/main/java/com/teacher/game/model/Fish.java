package com.teacher.game.model;

import javax.microedition.lcdui.game.Sprite;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.RandomNumberGenerator;

import android.graphics.Bitmap;

public class Fish extends Sprite {

	public static final byte SMALL = 0 , NORMAL = 1 , BIG =  2, SUPER = 3;

	public static final byte SWIMR = 0, SWIML = 1, EATR = 2, EATL = 3,
			SWERVE_R = 4, SWERVE_L = 5, DIE = 6;

    public enum Behavior {
        PATROL    // Default: swim left-right
    }

	public byte mSize;

	public byte mNonceState;

	public int mMoveX, mMoveY;

	public Behavior mBehavior = Behavior.PATROL;
	
	private int mSpeedBonus;

	public Fish(Bitmap image, int frameWidth, int frameHeight,byte size, byte state) {
		super(image, frameWidth, frameHeight);
		mSize = size;
		setNonceState(state);
	}

	public void setSize(byte size) {
		switch(size) {
			case NORMAL:
				this.setImage(Assets.tuna, 78, 40);
				mSize = size;
				break;
			case BIG:
				this.setImage(Assets.lion, 110, 86);
				mSize = size;
				break;
			case SUPER:
				this.setImage(Assets.shark, 220, 96);
				mSize = size;
				break;
			default:
				this.setImage(Assets.suergeonfish, 42, 24);
				mSize = SMALL;
				break;
		}
		mNonceState = DIE;
		mMoveX = 0;
		mMoveY = 0;
	}

	public void setSpeedBonus(int speedBonus) {
		mSpeedBonus = Math.max(0, speedBonus);
	}

	public void setNonceState(byte state) {
		switch(state) {
			case SWIML:
				if (mNonceState != SWIML) {
					mNonceState = SWIML;
					mMoveX = -(mSize + RandomNumberGenerator.getRandIntBetween(1, 5) + mSpeedBonus);
					if (mSize == SUPER)
						mMoveY = RandomNumberGenerator.getRandIntBetween(-8, 9);
					else
						mMoveY = 0;
					this.setFrameSequence(new int[] {3, 4, 5, 4});
				}
				break;
			case SWIMR:
				if (mNonceState != SWIMR) {
					mNonceState = SWIMR;
					mMoveX = (mSize + RandomNumberGenerator.getRandIntBetween(1, 5) + mSpeedBonus);
					if (mSize == SUPER)
						mMoveY = RandomNumberGenerator.getRandIntBetween(-8, 9);
					else
						mMoveY = 0;
					this.setFrameSequence(new int[] {0, 1, 2, 1});
				}
				break;
			case SWERVE_L:
				if (mNonceState != SWIML && mNonceState != EATL
						&& mNonceState != SWERVE_L) {
					mNonceState = SWERVE_L;
					this.setFrameSequence(new int[] {0, 7, 3, 4});
				}
				break;
			case SWERVE_R:
				if (mNonceState != SWIMR && mNonceState != EATR
						&& mNonceState != SWERVE_R) {
					mNonceState = SWERVE_R;
					this.setFrameSequence(new int[] {3, 6, 0, 1});
				}
				break;
			case EATL:
				if (mNonceState != EATL) {
					mNonceState = EATL;
					this.setFrameSequence(new int[] {10, 11});
				}
				break;
			case EATR:
				if (mNonceState != EATR) {
					mNonceState = EATR;
					this.setFrameSequence(new int[] {8, 9});
				}
				break;
			case DIE:
				if (mNonceState != DIE) {
					mNonceState = DIE;
					this.setFrameSequence(new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 1});
				}
				break;
		}
		setCollisionRectangle();
	}

	public void setCollisionRectangle() {
		int width = Math.max(12, (int)(getWidth() * 0.42f));
		switch(mNonceState) {
			case SWIML:
			case SWERVE_L:
			case EATL:
				defineCollisionRectangle(0, 0, width, getHeight());
				break;
			case SWIMR:
			case SWERVE_R:
			case EATR:
				defineCollisionRectangle(getWidth() - width, 0, width, getHeight());
				break;
		}
	}

	public void update() {
		move(mMoveX, mMoveY);
		nextFrame();
		if (getY() < GameMainActivity.getPlayTop()) { //超过上边界
			mMoveY = -mMoveY;
			setPosition(getX(), GameMainActivity.getPlayTop());
		}
		else if (getY() + getHeight() > GameMainActivity.getPlayBottom()) {
			mMoveY = -mMoveY;
			setPosition(getX(), GameMainActivity.getPlayBottom() - getHeight());
		}

		changeState();
	}

	public void changeState() {
		if (mNonceState == SWERVE_L) {
			if (getFrame() == 3)
				setNonceState(SWIML);
		}
		else if (mNonceState == SWERVE_R) {
			if (getFrame() == 3)
				setNonceState(SWIMR);
		}
		else if (mNonceState == EATL) {
			if (getFrame() == 1)
				setNonceState(SWIML);
		}
		else if (mNonceState == EATR) {
			if (getFrame() == 1)
				setNonceState(SWIMR);
		}
	}

}
