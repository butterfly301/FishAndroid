package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;
import com.teacher.game.model.LevelRepository;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class LevelSelectState extends State {

	private static final int CARD_X = 180;
	private static final int CARD_Y = 96;
	private static final int CARD_W = 920;
	private static final int CARD_H = 520;
	private static final int LEVEL_BTN_W = 220;
	private static final int LEVEL_BTN_H = 120;
	private static final int LEVEL_BTN_GAP = 36;
	private static final int LEVEL_BTN_TOP = 254;

	@Override
	public void init() {
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void render(Painter g) {
		g.drawImage(Assets.menu, 0, 0);

		g.setColor(Color.argb(180, 6, 32, 64));
		g.fillRoundRect(CARD_X, CARD_Y, CARD_W, CARD_H, 34);

		g.setColor(Color.argb(130, 255, 255, 255));
		g.fillRoundRect(CARD_X + 28, CARD_Y + 24, CARD_W - 56, 96, 28);

		g.setFont(Typeface.DEFAULT_BOLD, 48);
		g.setColor(Color.WHITE);
		drawCenteredText(g, "选择关卡", CARD_X, CARD_W, 164);

		g.setFont(Typeface.SANS_SERIF, 24);
		g.setColor(Color.argb(255, 220, 244, 255));
		drawCenteredText(g, "每一关的敌鱼数量、速度和目标分数都会提升", CARD_X, CARD_W, 214);

		for (int i = 0; i < LevelRepository.getLevelCount(); i++) {
			int left = getLevelButtonLeft(i);
			int top = LEVEL_BTN_TOP;

			g.setColor(Color.argb(90, 0, 0, 0));
			g.fillRoundRect(left + 4, top + 4, LEVEL_BTN_W, LEVEL_BTN_H, 24);
			g.setColor(Color.rgb(255, 199, 84));
			g.fillRoundRect(left, top, LEVEL_BTN_W, LEVEL_BTN_H, 24);

			g.setFont(Typeface.DEFAULT_BOLD, 34);
			g.setColor(Color.rgb(16, 56, 90));
			drawCenteredText(g, "第 " + (i + 1) + " 关", left, LEVEL_BTN_W, top + 52);

			g.setFont(Typeface.SANS_SERIF, 22);
			g.setColor(Color.rgb(35, 83, 126));
			drawCenteredText(g, "点击开始挑战", left, LEVEL_BTN_W, top + 88);
		}

		g.setColor(Color.rgb(106, 191, 245));
		g.fillRoundRect(494, 534, 292, 66, 18);
		g.setFont(Typeface.DEFAULT_BOLD, 28);
		g.setColor(Color.rgb(16, 56, 90));
		drawCenteredText(g, "返回主菜单", 494, 292, 576);
	}

	private int getLevelButtonLeft(int index) {
		int totalWidth = LevelRepository.getLevelCount() * LEVEL_BTN_W
				+ (LevelRepository.getLevelCount() - 1) * LEVEL_BTN_GAP;
		return (GameMainActivity.GAME_WIDTH - totalWidth) / 2 + index * (LEVEL_BTN_W + LEVEL_BTN_GAP);
	}

	private void drawCenteredText(Painter g, String text, int left, int width, int baselineY) {
		float textWidth = g.measureText(text);
		int textX = left + (int)((width - textWidth) / 2f);
		g.drawString(text, textX, baselineY);
	}

	private boolean isPointInside(int x, int y, int left, int top, int width, int height) {
		return x >= left && x <= left + width && y >= top && y <= top + height;
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		if (e.getAction() != MotionEvent.ACTION_UP) {
			return true;
		}

		for (int i = 0; i < LevelRepository.getLevelCount(); i++) {
			int left = getLevelButtonLeft(i);
			if (isPointInside(scaleX, scaleY, left, LEVEL_BTN_TOP, LEVEL_BTN_W, LEVEL_BTN_H)) {
				Assets.playSound(Assets.selectedID);
				setCurrentState(new PlayState(i));
				return true;
			}
		}

		if (isPointInside(scaleX, scaleY, 494, 534, 292, 66)) {
			Assets.playSound(Assets.selectedID);
			setCurrentState(new MenuState());
			return true;
		}

		return true;
	}
}
