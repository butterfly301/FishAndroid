package com.teacher.game.model;

import com.teacher.game.state.L10n;

public enum PowerUpType {

	SPEED("powerup_speed", 5f),
	SHIELD("powerup_shield", 0f),
	FREEZE("powerup_freeze", 3f),
	BOMB("powerup_bomb", 0f),
	LURE("powerup_lure", 4f);

	public final String l10nKey;
	public final float duration;  // 持续秒数，0=瞬间效果

	public String getName() {
		return L10n.get(l10nKey);
	}

	PowerUpType(String l10nKey, float duration) {
		this.l10nKey = l10nKey;
		this.duration = duration;
	}
}
