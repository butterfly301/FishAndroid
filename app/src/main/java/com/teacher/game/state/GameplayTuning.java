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

	// Enemy fish AI
	static final int TRACK_DISTANCE = 420;
	static final int FLEE_DISTANCE = 350;
	static final float TRACK_MULTIPLIER = 2.5f;
	static final float FLEE_MULTIPLIER = 1.8f;

	// Auto-pilot core tuning
	static final float AUTOPILOT_DECISION_INTERVAL = 0.10f;
	static final float AUTOPILOT_TARGET_LOCK_SECONDS = 0.8f;
	static final float AUTOPILOT_HARDRAM_MAX_SPEED = 160f;
	static final float AUTOPILOT_HARDRAM_STOP_RADIUS = 18f;
	static final int AUTOPILOT_HARDRAM_MAX_X = 160;
	static final int AUTOPILOT_HARDRAM_MAX_Y = 140;

	private GameplayTuning() {
		// no instance
	}
}
