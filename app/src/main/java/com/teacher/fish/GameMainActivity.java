package com.teacher.fish;



import com.teacher.fish.Assets;
import com.teacher.game.state.L10n;

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
	private static final String KEY_SOUND_ENABLED = "sound_enabled";
	private static final String KEY_MUSIC_ENABLED = "music_enabled";
	private static final String KEY_UNLOCKED_LEVEL = "unlocked_level";
	private static final String KEY_ACHIEVE_FISH_EATEN = "achieve_fish_eaten";
	private static final String KEY_ACHIEVE_POWERUPS = "achieve_powerups";
	private static final String KEY_ACHIEVE_COMBO_PEAK = "achieve_combo_peak";
	public static GameView sGame;
	public static AssetManager assets;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		assets = getAssets();
		L10n.init(this);
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

	public static boolean isSoundEnabled() {
		if (sGame == null) {
			return true;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return prefs.getBoolean(KEY_SOUND_ENABLED, true);
	}

	public static void setSoundEnabled(boolean enabled) {
		if (sGame == null) {
			return;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply();
		Assets.sSoundEnabled = enabled;
	}

	public static boolean isMusicEnabled() {
		if (sGame == null) {
			return true;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return prefs.getBoolean(KEY_MUSIC_ENABLED, true);
	}

	public static void setMusicEnabled(boolean enabled) {
		if (sGame == null) {
			return;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit().putBoolean(KEY_MUSIC_ENABLED, enabled).apply();
		Assets.setMusicEnabled(enabled);
	}

	public static void resetAllScores() {
		if (sGame == null) {
			return;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit()
				.putInt(KEY_HIGH_SCORE, 0)
				.putInt(KEY_ENDLESS_HIGH_SCORE, 0)
				.apply();
	}

	// ================================================================
	//  Level progress unlock
	// ================================================================

	public static int getUnlockedLevel() {
		if (sGame == null) {
			return 0;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return prefs.getInt(KEY_UNLOCKED_LEVEL, 0);
	}

	public static void setUnlockedLevel(int levelIndex) {
		if (sGame == null) {
			return;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		int current = prefs.getInt(KEY_UNLOCKED_LEVEL, 0);
		if (levelIndex > current) {
			prefs.edit().putInt(KEY_UNLOCKED_LEVEL, levelIndex).apply();
		}
	}

	/** Call when any round ends to update cumulative stats. */
	public static void addFishEaten(int count) {
		if (sGame == null || count <= 0) {
			return;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		int total = prefs.getInt(KEY_ACHIEVE_FISH_EATEN, 0) + count;
		prefs.edit().putInt(KEY_ACHIEVE_FISH_EATEN, total).apply();
	}

	public static int getFishEaten() {
		if (sGame == null) {
			return 0;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return prefs.getInt(KEY_ACHIEVE_FISH_EATEN, 0);
	}

	public static void addPowerUpsCollected(int count) {
		if (sGame == null || count <= 0) {
			return;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		int total = prefs.getInt(KEY_ACHIEVE_POWERUPS, 0) + count;
		prefs.edit().putInt(KEY_ACHIEVE_POWERUPS, total).apply();
	}

	public static int getPowerUpsCollected() {
		if (sGame == null) {
			return 0;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return prefs.getInt(KEY_ACHIEVE_POWERUPS, 0);
	}

	public static void updateComboPeak(int peak) {
		if (sGame == null) {
			return;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		int current = prefs.getInt(KEY_ACHIEVE_COMBO_PEAK, 0);
		if (peak > current) {
			prefs.edit().putInt(KEY_ACHIEVE_COMBO_PEAK, peak).apply();
		}
	}

	public static int getComboPeak() {
		if (sGame == null) {
			return 0;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return prefs.getInt(KEY_ACHIEVE_COMBO_PEAK, 0);
	}

	public static void resetAchievementStats() {
		if (sGame == null) {
			return;
		}
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit()
				.putInt(KEY_ACHIEVE_FISH_EATEN, 0)
				.putInt(KEY_ACHIEVE_POWERUPS, 0)
				.putInt(KEY_ACHIEVE_COMBO_PEAK, 0)
				.apply();
	}

	// ================================================================
	//  Collection (encyclopedia) progress
	// ================================================================

	private static final String KEY_COLL_PREFIX = "coll_";

	public static boolean isCollectionDiscovered(String entryId) {
		if (sGame == null) return false;
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		return prefs.getBoolean(KEY_COLL_PREFIX + entryId, false);
	}

	public static void markCollectionDiscovered(String entryId) {
		if (sGame == null) return;
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		String key = KEY_COLL_PREFIX + entryId;
		if (!prefs.getBoolean(key, false)) {
			prefs.edit().putBoolean(key, true).apply();
		}
	}

	public static int getCollectionCount() {
		if (sGame == null) return 0;
		// Count entries: 8 fish + 5 powerups + 1 companion = 14
		return 14;
	}

	public static int getCollectionDiscoveredCount() {
		if (sGame == null) return 0;
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		int count = 0;
		// Fish (8)
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_fish_SURGEON", false)) count++;
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_fish_GLOW_SURGEON", false)) count++;
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_fish_TUNA", false)) count++;
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_fish_SUN_TUNA", false)) count++;
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_fish_LION", false)) count++;
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_fish_ROYAL_LION", false)) count++;
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_fish_SHARK", false)) count++;
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_fish_REEF_SHARK", false)) count++;
		// Power-ups (5)
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_power_SPEED", false)) count++;
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_power_SHIELD", false)) count++;
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_power_FREEZE", false)) count++;
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_power_BOMB", false)) count++;
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_power_LURE", false)) count++;
		// Companion
		if (prefs.getBoolean(KEY_COLL_PREFIX + "coll_companion", false)) count++;
		return count;
	}

	public static void resetCollectionData() {
		if (sGame == null) return;
		SharedPreferences prefs = sGame.getContext()
				.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
		prefs.edit()
				.putBoolean(KEY_COLL_PREFIX + "coll_fish_SURGEON", false)
				.putBoolean(KEY_COLL_PREFIX + "coll_fish_GLOW_SURGEON", false)
				.putBoolean(KEY_COLL_PREFIX + "coll_fish_TUNA", false)
				.putBoolean(KEY_COLL_PREFIX + "coll_fish_SUN_TUNA", false)
				.putBoolean(KEY_COLL_PREFIX + "coll_fish_LION", false)
				.putBoolean(KEY_COLL_PREFIX + "coll_fish_ROYAL_LION", false)
				.putBoolean(KEY_COLL_PREFIX + "coll_fish_SHARK", false)
				.putBoolean(KEY_COLL_PREFIX + "coll_fish_REEF_SHARK", false)
				.putBoolean(KEY_COLL_PREFIX + "coll_power_SPEED", false)
				.putBoolean(KEY_COLL_PREFIX + "coll_power_SHIELD", false)
				.putBoolean(KEY_COLL_PREFIX + "coll_power_FREEZE", false)
				.putBoolean(KEY_COLL_PREFIX + "coll_power_BOMB", false)
				.putBoolean(KEY_COLL_PREFIX + "coll_power_LURE", false)
				.putBoolean(KEY_COLL_PREFIX + "coll_companion", false)
				.apply();
	}

	public static int getPlayTop() {
		return HUD_HEIGHT + 6;
	}

	public static int getPlayBottom() {
		return GAME_HEIGHT - FLOOR_MARGIN;
	}
}
