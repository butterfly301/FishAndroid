package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class AchievementState extends State {

	private static final String[][] ACHIEVEMENTS = {
		{"大鱼吃小鱼", "累计吃掉 100 条鱼"},
		{"连击大师",    "单局连击达到 5"},
		{"道具收藏家",  "累计收集 30 个道具"},
		{"初出茅庐",    "通关第 1 关"},
		{"关卡征服者",  "解锁全部 100 关"},
	};

	/** Thresholds for each achievement (index 0-4). */
	private static final int[] THRESHOLDS = {100, 5, 30, 1, 99};

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
		g.fillRoundRect(240, 50, 800, 620, 34);

		g.setColor(Color.argb(130, 255, 255, 255));
		g.fillRoundRect(268, 74, 744, 80, 28);

		g.setFont(Typeface.DEFAULT_BOLD, 40);
		g.setColor(Color.WHITE);
		drawCenteredText(g, "成就列表", 240, 800, 128);

		int startY = 190;
		for (int i = 0; i < ACHIEVEMENTS.length; i++) {
			drawAchievementRow(g, i, startY + i * 84);
		}

		// Back button
		int btnW = 300;
		int btnH = 56;
		int btnX = (GameMainActivity.GAME_WIDTH - btnW) / 2;
		int btnY = 634;
		g.setColor(Color.rgb(106, 191, 245));
		g.fillRoundRect(btnX, btnY, btnW, btnH, 18);
		g.setFont(Typeface.DEFAULT_BOLD, 28);
		g.setColor(Color.rgb(16, 56, 90));
		drawCenteredText(g, "返回主菜单", btnX, btnW, btnY + 38);
	}

	private void drawAchievementRow(Painter g, int index, int y) {
		boolean earned = isAchievementEarned(index);
		int progress = getAchievementProgress(index);
		int threshold = THRESHOLDS[index];
		String name = ACHIEVEMENTS[index][0];
		String desc = ACHIEVEMENTS[index][1];

		// Card background
		int cardX = 268;
		int cardW = 744;

		g.setColor(earned
				? Color.argb(90, 255, 215, 0)
				: Color.argb(55, 255, 255, 255));
		g.fillRoundRect(cardX, y, cardW, 74, 16);

		// Achievement icon placeholder
		g.setFont(Typeface.DEFAULT_BOLD, 30);
		g.setColor(earned ? Color.rgb(255, 215, 0) : Color.argb(120, 200, 200, 200));
		g.drawString(earned ? "✓" : "○", cardX + 18, y + 48);

		// Name
		g.setFont(Typeface.DEFAULT_BOLD, 26);
		g.setColor(earned ? Color.rgb(255, 215, 0) : Color.argb(220, 255, 255, 255));
		g.drawString(name, cardX + 60, y + 32);

		// Description
		g.setFont(Typeface.SANS_SERIF, 20);
		g.setColor(earned ? Color.argb(255, 255, 240, 190) : Color.argb(180, 200, 220, 240));
		g.drawString(desc, cardX + 60, y + 60);

		// Progress bar (only if not earned)
		if (!earned) {
			int barX = cardX + 480;
			int barY = y + 24;
			int barW = 220;
			int barH = 14;
			int fillW = (int)(barW * Math.min((float)progress / threshold, 1.0f));

			g.setColor(Color.argb(80, 0, 0, 0));
			g.fillRoundRect(barX, barY, barW, barH, 7);
			g.setColor(Color.rgb(255, 198, 84));
			g.fillRoundRect(barX, barY, fillW, barH, 7);

			g.setFont(Typeface.SANS_SERIF, 18);
			g.setColor(Color.argb(180, 200, 220, 240));
			g.drawString(progress + "/" + threshold, barX + barW / 2 - 24, barY + 34);
		} else {
			// Badge: "已达成"
			g.setFont(Typeface.DEFAULT_BOLD, 22);
			g.setColor(Color.rgb(255, 215, 0));
			int badgeX = cardX + 720;
			g.drawString("已达成", badgeX - 40, y + 48);
		}
	}

	private boolean isAchievementEarned(int index) {
		return getAchievementProgress(index) >= THRESHOLDS[index];
	}

	private int getAchievementProgress(int index) {
		switch (index) {
			case 0: return GameMainActivity.getFishEaten();
			case 1: return Math.min(GameMainActivity.getComboPeak(), THRESHOLDS[1]);
			case 2: return GameMainActivity.getPowerUpsCollected();
			case 3: return GameMainActivity.getUnlockedLevel() >= 1 ? 1 : 0;
			case 4: return Math.min(GameMainActivity.getUnlockedLevel(), THRESHOLDS[4]);
			default: return 0;
		}
	}

	private void drawCenteredText(Painter g, String text, int left, int width, int baselineY) {
		float textWidth = g.measureText(text);
		int textX = left + (int)((width - textWidth) / 2f);
		g.drawString(text, textX, baselineY);
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		if (e.getAction() != MotionEvent.ACTION_UP) {
			return true;
		}
		int btnW = 300;
		int btnH = 56;
		int btnX = (GameMainActivity.GAME_WIDTH - btnW) / 2;
		int btnY = 634;
		if (scaleX >= btnX && scaleX <= btnX + btnW
				&& scaleY >= btnY && scaleY <= btnY + btnH) {
			setCurrentState(new MenuState());
			return true;
		}
		return true;
	}
}
