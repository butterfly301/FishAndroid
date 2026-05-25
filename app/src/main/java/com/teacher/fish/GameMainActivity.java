package com.teacher.fish;



import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;

public class GameMainActivity extends Activity {
	
	public static final int GAME_WIDTH = 1280;
	public static final int GAME_HEIGHT = 720;
	public static final int HUD_HEIGHT = 104;
	public static final int FLOOR_MARGIN = 24;
	private static final String PREFS_NAME = "fish_prefs";
	private static final String KEY_HIGH_SCORE = "high_score";
	private static final String KEY_ENDLESS_HIGH_SCORE = "endless_high_score";
	private static final String KEY_AUTO_MODE = "auto_mode";
	public static GameView sGame;
	public static AssetManager assets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		assets = getAssets();
		sGame = new GameView(this, GAME_WIDTH, GAME_HEIGHT);
		setContentView(sGame);		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		sGame.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		sGame.onResume();
	}

	public static int getHighScore() {
		if (sGame == null) {
			return 0;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return prefs.getInt(KEY_HIGH_SCORE, 0);
	}

	public static void saveHighScore(int score) {
		if (sGame == null || score <= getHighScore()) {
			return;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit().putInt(KEY_HIGH_SCORE, score).apply();
	}

	public static int getEndlessHighScore() {
		if (sGame == null) {
			return 0;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return prefs.getInt(KEY_ENDLESS_HIGH_SCORE, 0);
	}

	public static void saveEndlessHighScore(int score) {
		if (sGame == null || score <= getEndlessHighScore()) {
			return;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit().putInt(KEY_ENDLESS_HIGH_SCORE, score).apply();
	}

	public static boolean isAutoMode() {
		if (sGame == null) {
			return false;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return prefs.getBoolean(KEY_AUTO_MODE, false);
	}

	public static void setAutoMode(boolean autoMode) {
		if (sGame == null) {
			return;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit().putBoolean(KEY_AUTO_MODE, autoMode).apply();
	}

	public static int getPlayTop() {
		return HUD_HEIGHT + 6;
	}

	public static int getPlayBottom() {
		return GAME_HEIGHT - FLOOR_MARGIN;
	}
}
