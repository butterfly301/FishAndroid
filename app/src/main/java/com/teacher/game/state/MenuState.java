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
	private static final String[] MENU_ITEMS = {"关卡模式", "无尽模式", "制作成员", "游戏设置", "成就", "退出游戏"};
	private static final int BUTTON_WIDTH = 320;
	private static final int BUTTON_HEIGHT = 70;
	private static final int BUTTON_LEFT = (GameMainActivity.GAME_WIDTH - BUTTON_WIDTH) / 2;
	private static final int BUTTON_TOP = 190;
	private static final int BUTTON_GAP = 78;
	private static final int CONTROL_PANEL_X = 36;
	private static final int CONTROL_PANEL_Y = 666;
	private static final int CONTROL_PANEL_W = 340;
	private static final int CONTROL_PANEL_H = 48;
	private static final int CONTROL_LABEL_X = CONTROL_PANEL_X + 12;
	private static final int CONTROL_OPTIONS_LEFT = CONTROL_PANEL_X + 116;
	private static final int CONTROL_OPTION_W = 96;
	private static final int CONTROL_OPTION_H = 36;
	private static final int CONTROL_OPTION_GAP = 16;
	private static final int CONTROL_OPTION_Y = 672;

	@Override
	public void init() {
		Assets.stopMusic();
		Assets.playMusic("MainMenuBGM.mp3", true);
	}

	@Override
	public void update(float delta) {
		// No delayed transition in main menu. Keep immediate response.
	}

	@Override
	public void render(Painter g) {
		g.drawImage(Assets.menu, 0, 0);

		g.setColor(Color.argb(132, 6, 30, 60));
		g.fillRoundRect(340, 54, 600, 150, 34);
		g.setFont(Typeface.SANS_SERIF, 62);
		g.setColor(Color.WHITE);
		float titleWidth = g.measureText("深海大作战");
		g.drawString("深海大作战", (GameMainActivity.GAME_WIDTH - (int)titleWidth) / 2, 116);
		g.setFont(Typeface.SANS_SERIF, 24);
		g.setColor(Color.argb(255, 205, 244, 255));
		float subtitleWidth = g.measureText("选择模式开始挑战");
		g.drawString("选择模式开始挑战", (GameMainActivity.GAME_WIDTH - (int)subtitleWidth) / 2, 170);

		for (int i = 0; i < MENU_ITEMS.length; i++) {
			int top = BUTTON_TOP + i * BUTTON_GAP;
			int color = Color.argb(210, 255, 255, 255);
			int shadow = Color.argb(78, 3, 26, 54);

			g.setColor(shadow);
			g.fillRoundRect(BUTTON_LEFT + 4, top + 4, BUTTON_WIDTH, BUTTON_HEIGHT, 18);
			g.setColor(color);
			g.fillRoundRect(BUTTON_LEFT, top, BUTTON_WIDTH, BUTTON_HEIGHT, 18);

			g.setFont(Typeface.SANS_SERIF, 30);
			g.setColor(Color.rgb(12, 58, 93));
			float textWidth = g.measureText(MENU_ITEMS[i]);
			int textX = BUTTON_LEFT + (int)((BUTTON_WIDTH - textWidth) / 2);
			g.drawString(MENU_ITEMS[i], textX, top + 46);

			if (i == 1) {
				g.setFont(Typeface.SANS_SERIF, 20);
				g.setColor(Color.argb(255, 230, 244, 255));
				g.drawString("最高分：" + GameMainActivity.getEndlessHighScore(), BUTTON_LEFT + BUTTON_WIDTH + 28, top + 44);
			}
		}

		drawControlToggle(g);
	}

	private void drawControlToggle(Painter g) {
		boolean autoMode = GameMainActivity.isAutoMode();
		g.setColor(Color.argb(156, 6, 30, 60));
		g.fillRoundRect(CONTROL_PANEL_X, CONTROL_PANEL_Y, CONTROL_PANEL_W, CONTROL_PANEL_H, 18);

		g.setFont(Typeface.SANS_SERIF, 18);
		g.setColor(Color.WHITE);
		g.drawString("操控模式", CONTROL_LABEL_X, CONTROL_PANEL_Y + 31);

		int manualX = getControlOptionLeft(false);
		int autoX = getControlOptionLeft(true);
		drawControlOption(g, manualX, "手动", !autoMode);
		drawControlOption(g, autoX, "自动", autoMode);
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

		int left = BUTTON_LEFT;
		int right = left + BUTTON_WIDTH;
		if (scaleX > left && scaleX < right) {
			for (int i = 0; i < MENU_ITEMS.length; i++) {
				int top = BUTTON_TOP + i * BUTTON_GAP;
				int bottom = top + BUTTON_HEIGHT;
				if (scaleY > top && scaleY < bottom) {
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
						setCurrentState(new HelpState());
						break;
					case 3:
						setCurrentState(new SettingsState());
						break;
					case 4:
						setCurrentState(new AchievementState());
						break;
					case 5:
						GameMainActivity.sGame.exit();
						break;
					default:
						break;
					}
					return true;
				}					
			}			
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
