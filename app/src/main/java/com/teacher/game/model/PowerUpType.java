package com.teacher.game.model;

public enum PowerUpType {

	SPEED("加速", 5f),
	SHIELD("护盾", 0f),
	FREEZE("冰冻", 3f),
	BOMB("炸弹", 0f);

	public final String label;
	public final float duration;  // 持续秒数，0=瞬间效果

	PowerUpType(String label, float duration) {
		this.label = label;
		this.duration = duration;
	}
}
