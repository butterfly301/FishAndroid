package com.teacher.game.state;

import java.util.ArrayList;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;
import com.teacher.game.model.AchievementConfig;
import com.teacher.game.model.AchievementRepository;
import com.teacher.game.model.AchievementTracker;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class AchievementState extends State {

	private static final int PANEL_X = 140;
	private static final int PANEL_W = 1000;
	private static final int PANEL_Y = 20;
	private static final int PANEL_H = 660;

	private static final int TITLE_Y = 85;
	private static final int TAB_Y = 118;
	private static final int TAB_H = 42;
	private static final int TAB_MIN_W = 110;

	private static final int CARD_X = PANEL_X + 28;
	private static final int CARD_W = PANEL_W - 56;
	private static final int CARD_H = 50;
	private static final int CARD_GAP = 6;
	private static final int ROW_H = CARD_H + CARD_GAP;
	private static final int FIRST_CARD_Y = TAB_Y + TAB_H + 14;

	private static final int BACK_BTN_Y = 655;
	private static final int BACK_BTN_W = 260;
	private static final int BACK_BTN_H = 42;

	// ---- Tab state ----
	private String[] mCategories;
	private int mTabCount;
	private int[] mTabLeft;
	private int[] mTabWidth;
	private int mSelectedTab;

	@Override
	public void init() {
		buildCategories();
		mSelectedTab = 0;
	}

	private void buildCategories() {
		// Collect unique category keys from repository
		ArrayList<String> cats = new ArrayList<>();
		for (int i = 0; i < AchievementRepository.getCount(); i++) {
			String cat = AchievementRepository.get(i).category;
			if (!cats.contains(cat)) {
				cats.add(cat);
			}
		}
		mCategories = cats.toArray(new String[0]);
		mTabCount = mCategories.length;
		computeTabPositions();
	}

	private void computeTabPositions() {
		mTabLeft = new int[mTabCount];
		mTabWidth = new int[mTabCount];
		int totalTextW = 0;
		for (int i = 0; i < mTabCount; i++) {
			String label = L10n.get(mCategories[i]);
			mTabWidth[i] = Math.max(TAB_MIN_W, label.length() * 20 + 28);
			totalTextW += mTabWidth[i];
		}
		int totalGap = 10 * (mTabCount - 1);
		int startX = PANEL_X + (PANEL_W - totalTextW - totalGap) / 2;
		for (int i = 0; i < mTabCount; i++) {
			mTabLeft[i] = startX;
			startX += mTabWidth[i] + 10;
		}
	}

	private ArrayList<AchievementConfig> getEntriesForTab() {
		ArrayList<AchievementConfig> result = new ArrayList<>();
		String cat = mCategories[mSelectedTab];
		for (int i = 0; i < AchievementRepository.getCount(); i++) {
			AchievementConfig c = AchievementRepository.get(i);
			if (c.category.equals(cat)) {
				result.add(c);
			}
		}
		return result;
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void render(Painter g) {
		g.drawImage(Assets.menu, 0, 0);

		// Background panel
		g.setColor(Color.argb(180, 6, 32, 64));
		g.fillRoundRect(PANEL_X, PANEL_Y, PANEL_W, PANEL_H, 28);

		// Title
		g.setColor(Color.argb(130, 255, 255, 255));
		g.fillRoundRect(PANEL_X + 28, PANEL_Y + 26, PANEL_W - 56, 64, 22);
		g.setFont(Typeface.DEFAULT_BOLD, 36);
		g.setColor(Color.WHITE);
		drawCenteredText(g, L10n.get("ach_title"), PANEL_X, PANEL_W, TITLE_Y);

		// Tab bar
		for (int i = 0; i < mTabCount; i++) {
			String label = L10n.get(mCategories[i]);
			boolean selected = (i == mSelectedTab);
			g.setColor(selected
					? Color.rgb(255, 198, 84)
					: Color.argb(160, 200, 220, 240));
			g.fillRoundRect(mTabLeft[i], TAB_Y, mTabWidth[i], TAB_H, 14);
			g.setFont(Typeface.DEFAULT_BOLD, 20);
			g.setColor(selected ? Color.rgb(12, 58, 93) : Color.rgb(200, 220, 240));
			drawCenteredText(g, label, mTabLeft[i], mTabWidth[i], TAB_Y + 29);
		}

		// Achievement rows for selected tab
		ArrayList<AchievementConfig> entries = getEntriesForTab();
		for (int i = 0; i < entries.size(); i++) {
			drawAchievementRow(g, entries.get(i), i);
		}

		// Back button
		int backBtnX = (GameMainActivity.GAME_WIDTH - BACK_BTN_W) / 2;
		g.setColor(Color.rgb(106, 191, 245));
		g.fillRoundRect(backBtnX, BACK_BTN_Y, BACK_BTN_W, BACK_BTN_H, 16);
		g.setFont(Typeface.DEFAULT_BOLD, 22);
		g.setColor(Color.rgb(16, 56, 90));
		drawCenteredText(g, L10n.get("ach_back"), backBtnX, BACK_BTN_W, BACK_BTN_Y + 28);
	}

	private void drawAchievementRow(Painter g, AchievementConfig config, int index) {
		int y = FIRST_CARD_Y + index * ROW_H;
		boolean earned = isAchievementEarned(config);
		int progress = getAchievementProgress(config);
		int threshold = config.threshold;
		String name = config.getName();
		String desc = config.getDesc();

		// Card background
		g.setColor(earned
				? Color.argb(90, 255, 215, 0)
				: Color.argb(55, 255, 255, 255));
		g.fillRoundRect(CARD_X, y, CARD_W, CARD_H, 12);

		// Achievement icon (✓ / ○)
		g.setFont(Typeface.DEFAULT_BOLD, 24);
		g.setColor(earned ? Color.rgb(255, 215, 0) : Color.argb(120, 200, 200, 200));
		g.drawString(earned ? "\u2713" : "\u25CB", CARD_X + 14, y + 34);

		// Name
		g.setFont(Typeface.DEFAULT_BOLD, 22);
		g.setColor(earned ? Color.rgb(255, 215, 0) : Color.argb(220, 255, 255, 255));
		g.drawString(name, CARD_X + 48, y + 24);

		// Description
		g.setFont(Typeface.SANS_SERIF, 17);
		g.setColor(earned ? Color.argb(255, 255, 240, 190) : Color.argb(180, 200, 220, 240));
		g.drawString(desc, CARD_X + 48, y + 44);

		// Right side: progress bar (unearned) or badge (earned)
		int rightX = CARD_X + CARD_W - 20;

		if (!earned) {
			int barX = rightX - 200;
			int barY = y + 12;
			int barW = 200;
			int barH = 12;
			int fillW = (int)(barW * Math.min((float)progress / threshold, 1.0f));

			g.setColor(Color.argb(80, 0, 0, 0));
			g.fillRoundRect(barX, barY, barW, barH, 6);
			if (fillW > 0) {
				g.setColor(Color.rgb(255, 198, 84));
				g.fillRoundRect(barX, barY, fillW, barH, 6);
			}

			g.setFont(Typeface.SANS_SERIF, 15);
			g.setColor(Color.argb(180, 200, 220, 240));
			g.drawString(progress + "/" + threshold, barX + barW / 2 - 20, barY + 26);
		} else {
			g.setFont(Typeface.DEFAULT_BOLD, 18);
			g.setColor(Color.rgb(255, 215, 0));
			String badge = L10n.get("ach_unlocked");
			float badgeW = g.measureText(badge);
			g.drawString(badge, rightX - (int)badgeW, y + 34);
		}
	}

	private static boolean isAchievementEarned(AchievementConfig config) {
		return getAchievementProgress(config) >= config.threshold;
	}

	private static int getAchievementProgress(AchievementConfig config) {
		switch (config.tracker) {
			case FISH_EATEN:           return GameMainActivity.getFishEaten();
			case COMBO_PEAK:           return Math.min(GameMainActivity.getComboPeak(), config.threshold);
			case POWERUPS_COLLECTED:   return GameMainActivity.getPowerUpsCollected();
			case UNLOCKED_LEVEL_BOOL:  return GameMainActivity.getUnlockedLevel() >= config.threshold ? 1 : 0;
			case UNLOCKED_LEVEL:       return Math.min(GameMainActivity.getUnlockedLevel(), config.threshold);
			case HIGH_SCORE:           return Math.min(GameMainActivity.getHighScore(), config.threshold);
			case ENDLESS_HIGH_SCORE:   return Math.min(GameMainActivity.getEndlessHighScore(), config.threshold);
			default:                   return 0;
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

		// Tab selection
		for (int i = 0; i < mTabCount; i++) {
			if (scaleX >= mTabLeft[i] && scaleX <= mTabLeft[i] + mTabWidth[i]
					&& scaleY >= TAB_Y && scaleY <= TAB_Y + TAB_H) {
				Assets.playTab();
				mSelectedTab = i;
				return true;
			}
		}

		// Back button
		int backBtnX = (GameMainActivity.GAME_WIDTH - BACK_BTN_W) / 2;
		if (scaleX >= backBtnX && scaleX <= backBtnX + BACK_BTN_W
				&& scaleY >= BACK_BTN_Y && scaleY <= BACK_BTN_Y + BACK_BTN_H) {
			Assets.playBack();
			setCurrentState(new MenuState());
			return true;
		}

		return true;
	}
}
