package com.teacher.game.state;

final class GameplayTuning {
	// Joystick visual scale / offsets
	static final int JOYSTICK_INNER_RADIUS = 36;
	static final int JOYSTICK_INNER_DIAGONAL = 25;
	static final int JOYSTICK_OUTER_RADIUS = 72;
	static final int JOYSTICK_INNER_HALF = 36;

	// Combo
	static final float COMBO_WINDOW_SECONDS = 2.0f;
	static final int MAX_COMBO = 5;

	// Auto-pilot core tuning
	static final float AUTOPILOT_DECISION_INTERVAL = 0.10f;
	static final float AUTOPILOT_TARGET_LOCK_SECONDS = 0.8f;
	private GameplayTuning() {
		// no instance
	}
}
