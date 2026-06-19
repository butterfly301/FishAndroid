package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;
import com.teacher.game.state.SettingsState;
import com.teacher.game.state.AchievementState;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class MenuState extends State {

	// Item order: 0-1 row0, 2-3 row1, 4-5 row2, 6 bottom
	private static final int ITEM_COUNT = 7;
	private static final int GRID_COUNT = 6; // first 6 items in 3x2 grid

	private String[] getMenuItems() {
		return new String[]{
			L10n.get("menu_level_mode"),      // 0  col0 row0
			L10n.get("menu_endless_mode"),    // 1  col1 row0
			L10n.get("menu_shop"),            // 2  col0 row1
			L10n.get("menu_settings"),        // 3  col1 row1
			L10n.get("menu_achievements"),    // 4  col0 row2
			L10n.get("menu_collection"),      // 5  col1 row2
			L10n.get("menu_exit")             // 6  centered bottom
		};
	}

	// Two-column grid constants
	private static final int BUTTON_W = 300;
	private static final int BUTTON_H = 64;
	private static final int GRID_GAP_X = 60;
	private static final int GRID_LEFT = (GameMainActivity.GAME_WIDTH - (BUTTON_W * 2 + GRID_GAP_X)) / 2;
	private static final int COL0_X = GRID_LEFT;
	private static final int COL1_X = GRID_LEFT + BUTTON_W + GRID_GAP_X;
	private static final int GRID_TOP = 215;
	private static final int GRID_GAP_Y = 72;

	// Exit button (bottom center)
	private static final int EXIT_W = 240;
	private static final int EXIT_H = 56;
	private static final int EXIT_X = (GameMainActivity.GAME_WIDTH - EXIT_W) / 2;
	private static final int EXIT_Y = GRID_TOP + 3 * GRID_GAP_Y + 6;

	// High score label for endless mode (next to col1 row0)
	private static final int HS_X = COL1_X + BUTTON_W + 18;
	private static final int HS_Y = GRID_TOP + 44;

	// Control mode toggle (bottom-left)
	private static final int CONTROL_PANEL_X = 36;
	private static final int CONTROL_PANEL_Y = 666;
	private static final int CONTROL_PANEL_W = 340;
	private static final int CONTROL_PANEL_H = 48;

	// Help / info button (bottom-right)
	private static final int HELP_BTN_SIZE = 52;
	private static final int HELP_BTN_X = GameMainActivity.GAME_WIDTH - HELP_BTN_SIZE - 20;
	private static final int HELP_BTN_Y = CONTROL_PANEL_Y;

	// Stats button (bottom-right, left of help)
	private static final int STATS_BTN_SIZE = 52;
	private static final int STATS_BTN_X = HELP_BTN_X - STATS_BTN_SIZE - 8;
	private static final int STATS_BTN_Y = CONTROL_PANEL_Y;
	private static final int CONTROL_LABEL_X = CONTROL_PANEL_X + 12;
	private static final int CONTROL_OPTIONS_LEFT = CONTROL_PANEL_X + 116;
	private static final int CONTROL_OPTION_W = 96;
	private static final int CONTROL_OPTION_H = 36;
	private static final int CONTROL_OPTION_GAP = 16;
	private static final int CONTROL_OPTION_Y = 672;

	private String[] mMenuItems;

	@Override
	public void init() {
		Assets.stopMusic();
		Assets.playMusic("MainMenuBGM.mp3", true);
		mMenuItems = getMenuItems();
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void render(Painter g) {
		g.drawImage(Assets.menu, 0, 0);

		// Title card
		String title = L10n.get("menu_title");
		String subtitle = L10n.get("menu_subtitle");

		g.setColor(Color.argb(132, 6, 30, 60));
		g.fillRoundRect(340, 54, 600, 150, 34);
		g.setFont(Typeface.SANS_SERIF, 62);
		g.setColor(Color.WHITE);
		float titleWidth = g.measureText(title);
		g.drawString(title, (GameMainActivity.GAME_WIDTH - (int)titleWidth) / 2, 116);
		g.setFont(Typeface.SANS_SERIF, 24);
		g.setColor(Color.argb(255, 205, 244, 255));
		float subtitleWidth = g.measureText(subtitle);
		g.drawString(subtitle, (GameMainActivity.GAME_WIDTH - (int)subtitleWidth) / 2, 170);

		// Draw 2-column grid buttons (indices 0-5)
		for (int i = 0; i < GRID_COUNT; i++) {
			int col = i % 2;
			int row = i / 2;
			int left = (col == 0) ? COL0_X : COL1_X;
			int top = GRID_TOP + row * GRID_GAP_Y;
			drawButton(g, left, top, BUTTON_W, BUTTON_H, mMenuItems[i]);
		}

		// High score label for endless mode
		g.setFont(Typeface.SANS_SERIF, 20);
		g.setColor(Color.argb(255, 230, 244, 255));
		g.drawString(L10n.get("menu_high_score", GameMainActivity.getEndlessHighScore()), HS_X, HS_Y);

		// Exit button (bottom center)
		drawButton(g, EXIT_X, EXIT_Y, EXIT_W, EXIT_H, mMenuItems[6]);

		// Control toggle
		drawControlToggle(g);

		// Stats button (bottom-right, left of help)
		g.setColor(Color.argb(120, 6, 32, 64));
		g.fillRoundRect(STATS_BTN_X, STATS_BTN_Y, STATS_BTN_SIZE, STATS_BTN_SIZE, 14);
		g.setFont(Typeface.SANS_SERIF, 16);
		g.setColor(Color.argb(180, 255, 215, 0));
		g.drawString(L10n.get("menu_stats"), STATS_BTN_X + 6, STATS_BTN_Y + 32);

		// Help / info button (bottom-right)
		g.setColor(Color.argb(120, 6, 32, 64));
		g.fillRoundRect(HELP_BTN_X, HELP_BTN_Y, HELP_BTN_SIZE, HELP_BTN_SIZE, 14);
		g.setFont(Typeface.DEFAULT_BOLD, 28);
		g.setColor(Color.argb(200, 255, 255, 255));
		g.drawString("?", HELP_BTN_X + 18, HELP_BTN_Y + 36);
	}

	private void drawButton(Painter g, int left, int top, int w, int h, String label) {
		int shadow = Color.argb(78, 3, 26, 54);
		int color = Color.argb(210, 255, 255, 255);
		g.setColor(shadow);
		g.fillRoundRect(left + 3, top + 3, w, h, 16);
		g.setColor(color);
		g.fillRoundRect(left, top, w, h, 16);
		g.setFont(Typeface.SANS_SERIF, 28);
		g.setColor(Color.rgb(12, 58, 93));
		float textWidth = g.measureText(label);
		int textX = left + (int)((w - textWidth) / 2);
		g.drawString(label, textX, top + h - 24);
	}

	private void drawControlToggle(Painter g) {
		boolean autoMode = GameMainActivity.isAutoMode();
		g.setColor(Color.argb(156, 6, 30, 60));
		g.fillRoundRect(CONTROL_PANEL_X, CONTROL_PANEL_Y, CONTROL_PANEL_W, CONTROL_PANEL_H, 18);

		g.setFont(Typeface.SANS_SERIF, 18);
		g.setColor(Color.WHITE);
		g.drawString(L10n.get("menu_control_mode"), CONTROL_LABEL_X, CONTROL_PANEL_Y + 31);

		int manualX = getControlOptionLeft(false);
		int autoX = getControlOptionLeft(true);
		drawControlOption(g, manualX, L10n.get("menu_manual"), !autoMode);
		drawControlOption(g, autoX, L10n.get("menu_auto"), autoMode);
	}

	private void drawControlOption(Painter g, int left, String label, boolean active) {
		g.setColor(active ? Color.rgb(255, 198, 84) : Color.argb(210, 255, 255, 255));
		g.fillRoundRect(left, CONTROL_OPTION_Y, CONTROL_OPTION_W, CONTROL_OPTION_H, 14);
		g.setFont(Typeface.DEFAULT_BOLD, 20);
		g.setColor(Color.rgb(12, 58, 93));
		float textWidth = g.measureText(label);
		g.drawString(label, left + (int)((CONTROL_OPTION_W - textWidth) / 2), CONTROL_OPTION_Y + 25);
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		if (e.getAction() == MotionEvent.ACTION_UP && handleControlToggleTap(scaleX, scaleY)) {
			return true;
		}
		if (e.getAction() != MotionEvent.ACTION_UP) {
			return true;
		}

		// Check grid buttons (indices 0-5)
		for (int i = 0; i < GRID_COUNT; i++) {
			int col = i % 2;
			int row = i / 2;
			int left = (col == 0) ? COL0_X : COL1_X;
			int top = GRID_TOP + row * GRID_GAP_Y;
			if (isInside(scaleX, scaleY, left, top, BUTTON_W, BUTTON_H)) {
				Assets.playSound(Assets.selectedID);
				Assets.stopMusic();
				switch (i) {
				case 0:
					setCurrentState(new LevelSelectState());
					break;
				case 1:
					setCurrentState(new PlayState(true));
					break;
				case 2:
					setCurrentState(new ShopState());
					break;
				case 3:
					setCurrentState(new SettingsState());
					break;
				case 4:
					setCurrentState(new AchievementState());
					break;
				case 5:
					setCurrentState(new CollectionState());
					break;
				}
				return true;
			}
		}

		// Stats button
		if (isInside(scaleX, scaleY, STATS_BTN_X, STATS_BTN_Y, STATS_BTN_SIZE, STATS_BTN_SIZE)) {
			Assets.playClick();
			setCurrentState(new StatsState());
			return true;
		}

		// Help / info button
		if (isInside(scaleX, scaleY, HELP_BTN_X, HELP_BTN_Y, HELP_BTN_SIZE, HELP_BTN_SIZE)) {
			Assets.playClick();
			setCurrentState(new HelpState());
			return true;
		}

		// Exit button (index 6)
		if (isInside(scaleX, scaleY, EXIT_X, EXIT_Y, EXIT_W, EXIT_H)) {
			Assets.playSound(Assets.selectedID);
			Assets.stopMusic();
			GameMainActivity.sGame.exit();
			return true;
		}

		return true;
	}

	private boolean isInside(int x, int y, int left, int top, int width, int height) {
		return x >= left && x <= left + width && y >= top && y <= top + height;
	}

	private int getControlOptionLeft(boolean autoOption) {
		if (autoOption) {
			return CONTROL_OPTIONS_LEFT + CONTROL_OPTION_W + CONTROL_OPTION_GAP;
		}
		return CONTROL_OPTIONS_LEFT;
	}

	private boolean handleControlToggleTap(int scaleX, int scaleY) {
		int manualX = getControlOptionLeft(false);
		int autoX = getControlOptionLeft(true);
		if (isInside(scaleX, scaleY, manualX, CONTROL_OPTION_Y, CONTROL_OPTION_W, CONTROL_OPTION_H)) {
			GameMainActivity.setAutoMode(false);
			return true;
		}
		if (isInside(scaleX, scaleY, autoX, CONTROL_OPTION_Y, CONTROL_OPTION_W, CONTROL_OPTION_H)) {
			GameMainActivity.setAutoMode(true);
			return true;
		}
		return false;
	}
}
