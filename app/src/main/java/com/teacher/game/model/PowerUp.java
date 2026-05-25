package com.teacher.game.model;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;

import javax.microedition.lcdui.game.Sprite;

public class PowerUp extends Sprite {

	public final PowerUpType type;

	private float mFloatTime;

	private static final int SIZE = 36;

	public PowerUp(PowerUpType type) {
		super(Assets.createPowerUpBitmap(type, SIZE), SIZE, SIZE);
		this.type = type;
		mFloatTime = 0;
		defineCollisionRectangle(4, 4, SIZE - 8, SIZE - 8);
	}

	public void update() {
		mFloatTime += 0.1f;
		int dx = (int) (Math.sin(mFloatTime) * 1);
		move(dx, -2);
	}

	public boolean isOutOfScreen() {
		return getY() + getHeight() < GameMainActivity.getPlayTop()
				|| getX() + getWidth() < 0
				|| getX() > GameMainActivity.GAME_WIDTH;
	}
}
