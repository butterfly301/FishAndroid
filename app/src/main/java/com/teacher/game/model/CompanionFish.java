package com.teacher.game.model;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;

public class CompanionFish extends Fish {

	private static final float FOLLOW_BLEND = 0.14f;
	private static final int MAX_SPEED_X = 8;
	private static final int MAX_SPEED_Y = 5;
	private int mLevel;
	private int mAssistEatCount;

	public CompanionFish() {
		super(Assets.angelfishnormal, 34, 28, NORMAL, SWIMR);
		setPosition(220, 320);
		mLevel = 1;
		mAssistEatCount = 0;
	}

	public int getLevel() {
		return mLevel;
	}

	public void recordAssistEat() {
		mAssistEatCount++;
		int targetLevel = 1 + (mAssistEatCount / 6);
		if (targetLevel > 3) {
			targetLevel = 3;
		}
		if (targetLevel != mLevel) {
			mLevel = targetLevel;
			refreshVisualByLevel();
		}
	}

	public void follow(MyFish owner) {
		int ownerCenterX = owner.getX() + owner.getWidth() / 2;
		int ownerCenterY = owner.getY() + owner.getHeight() / 2;
		int selfCenterX = getX() + getWidth() / 2;
		int selfCenterY = getY() + getHeight() / 2;

		int targetX = ownerCenterX - 96;
		int targetY = ownerCenterY + 48;

		int diffX = targetX - selfCenterX;
		int diffY = targetY - selfCenterY;

		mMoveX += (int) (diffX * FOLLOW_BLEND);
		mMoveY += (int) (diffY * FOLLOW_BLEND);
		int speedX = MAX_SPEED_X + (mLevel - 1) * 2;
		int speedY = MAX_SPEED_Y + (mLevel - 1);
		if (mMoveX > speedX) {
			mMoveX = speedX;
		} else if (mMoveX < -speedX) {
			mMoveX = -speedX;
		}
		if (mMoveY > speedY) {
			mMoveY = speedY;
		} else if (mMoveY < -speedY) {
			mMoveY = -speedY;
		}

		if (mMoveX >= 0) {
			setNonceState(SWIMR);
		} else {
			setNonceState(SWIML);
		}
		move(mMoveX, mMoveY);
		nextFrame();

		clampToPlayArea();
	}

	public void dashToward(Fish target) {
		int tx = target.getX() + target.getWidth() / 2;
		int ty = target.getY() + target.getHeight() / 2;
		int sx = getX() + getWidth() / 2;
		int sy = getY() + getHeight() / 2;
		int dx = tx - sx;
		int dy = ty - sy;
		int dashX = MAX_SPEED_X + 2 + (mLevel - 1) * 2;
		int dashY = MAX_SPEED_Y + (mLevel - 1);
		mMoveX = dx > 0 ? dashX : -dashX;
		if (Math.abs(dy) < 18) {
			mMoveY = 0;
		} else {
			mMoveY = dy > 0 ? dashY : -dashY;
		}
		if (mMoveX >= 0) {
			setNonceState(SWIMR);
		} else {
			setNonceState(SWIML);
		}
		move(mMoveX, mMoveY);
		nextFrame();
		clampToPlayArea();
	}

	private void clampToPlayArea() {
		int x = getX();
		int y = getY();
		if (x < 0) {
			x = 0;
		}
		if (x + getWidth() > GameMainActivity.GAME_WIDTH) {
			x = GameMainActivity.GAME_WIDTH - getWidth();
		}
		if (y < GameMainActivity.getPlayTop()) {
			y = GameMainActivity.getPlayTop();
		}
		if (y + getHeight() > GameMainActivity.getPlayBottom()) {
			y = GameMainActivity.getPlayBottom() - getHeight();
		}
		setPosition(x, y);
	}

	private void refreshVisualByLevel() {
		int cx = getX() + getWidth() / 2;
		int cy = getY() + getHeight() / 2;
		if (mLevel == 1) {
			setImage(Assets.angelfishnormal, 34, 28);
		} else if (mLevel == 2) {
			setImage(Assets.angelfishbig, 84, 68);
		} else {
			setImage(Assets.angelfishsuper, 118, 94);
		}
		setPosition(cx - getWidth() / 2, cy - getHeight() / 2);
		setCollisionRectangle();
	}
}
