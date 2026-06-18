package com.teacher.game.state;

import java.util.ArrayList;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;
import com.teacher.game.model.CollectionEntry;
import com.teacher.game.model.CollectionRepository;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class CollectionState extends State {

	private static final int PANEL_X = 60;
	private static final int PANEL_W = 1160;
	private static final int PANEL_Y = 14;
	private static final int PANEL_H = 692;

	private static final int TITLE_Y = 68;
	private static final int TAB_Y = 108;
	private static final int TAB_H = 46;
	private static final int TAB_GAP = 12;
	private static final int TAB_MIN_W = 140;

	private static final int CARD_X = PANEL_X + 24;
	private static final int CARD_W = PANEL_W - 48;
	private static final int CARD_H = 42;
	private static final int CARD_GAP = 4;
	private static final int ROW_H = CARD_H + CARD_GAP;
	private static final int FIRST_CARD_Y = TAB_Y + TAB_H + 12;

	private static final int BACK_BTN_Y = 675;
	private static final int BACK_BTN_W = 260;
	private static final int BACK_BTN_H = 40;

	private int mTabCount;
	private int[] mTabLeft;
	private int[] mTabWidth;
	private int mSelectedTab;

	private ArrayList<CollectionEntry> mCurrentEntries;

	@Override
	public void init() {
		mTabCount = 3;
		mSelectedTab = 0;
		computeTabPositions();
		updateCurrentEntries();
	}

	private void computeTabPositions() {
		mTabLeft = new int[mTabCount];
		mTabWidth = new int[mTabCount];
		String[] labels = {
			L10n.get("coll_section_fish"),
			L10n.get("coll_section_powerup"),
			L10n.get("coll_section_other"),
		};
		// Approximate text widths with a fallback measure
		// Use fixed widths based on text length
		int totalTextW = 0;
		for (int i = 0; i < mTabCount; i++) {
			mTabWidth[i] = Math.max(TAB_MIN_W, labels[i].length() * 22 + 32);
			totalTextW += mTabWidth[i];
		}
		int totalGap = TAB_GAP * (mTabCount - 1);
		int startX = PANEL_X + (PANEL_W - totalTextW - totalGap) / 2;
		for (int i = 0; i < mTabCount; i++) {
			mTabLeft[i] = startX;
			startX += mTabWidth[i] + TAB_GAP;
		}
	}

	private void updateCurrentEntries() {
		CollectionEntry.Category cat;
		switch (mSelectedTab) {
			case 0:  cat = CollectionEntry.Category.FISH; break;
			case 1:  cat = CollectionEntry.Category.POWERUP; break;
			case 2:  cat = CollectionEntry.Category.OTHER; break;
			default: cat = CollectionEntry.Category.FISH; break;
		}
		mCurrentEntries = CollectionRepository.getByCategory(cat);
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
		g.fillRoundRect(PANEL_X + 28, PANEL_Y + 30, PANEL_W - 56, 56, 22);
		g.setFont(Typeface.DEFAULT_BOLD, 34);
		g.setColor(Color.WHITE);
		drawCenteredText(g, L10n.get("coll_title"), PANEL_X, PANEL_W, TITLE_Y);

		// Tab bar
		String[] tabLabels = {
			L10n.get("coll_section_fish"),
			L10n.get("coll_section_powerup"),
			L10n.get("coll_section_other"),
		};
		for (int i = 0; i < mTabCount; i++) {
			boolean selected = (i == mSelectedTab);
			g.setColor(selected
					? Color.rgb(255, 198, 84)
					: Color.argb(160, 200, 220, 240));
			g.fillRoundRect(mTabLeft[i], TAB_Y, mTabWidth[i], TAB_H, 14);
			g.setFont(Typeface.DEFAULT_BOLD, 22);
			g.setColor(selected ? Color.rgb(12, 58, 93) : Color.rgb(200, 220, 240));
			drawCenteredText(g, tabLabels[i], mTabLeft[i], mTabWidth[i], TAB_Y + 32);
		}

		// Section header count
		int discovered = countDiscovered(mCurrentEntries);
		String countLabel = discovered + "/" + mCurrentEntries.size();
		g.setFont(Typeface.SANS_SERIF, 18);
		g.setColor(Color.argb(160, 200, 220, 240));
		float countW = g.measureText(countLabel);
		g.drawString(countLabel, PANEL_X + PANEL_W - 36 - (int)countW, TAB_Y + 34);

		// Cards
		for (int i = 0; i < mCurrentEntries.size(); i++) {
			drawEntryCard(g, mCurrentEntries.get(i), i);
		}

		// Back button
		int backBtnX = (GameMainActivity.GAME_WIDTH - BACK_BTN_W) / 2;
		g.setColor(Color.rgb(106, 191, 245));
		g.fillRoundRect(backBtnX, BACK_BTN_Y, BACK_BTN_W, BACK_BTN_H, 16);
		g.setFont(Typeface.DEFAULT_BOLD, 22);
		g.setColor(Color.rgb(16, 56, 90));
		drawCenteredText(g, L10n.get("coll_back"), backBtnX, BACK_BTN_W, BACK_BTN_Y + 28);
	}

	private void drawEntryCard(Painter g, CollectionEntry entry, int index) {
		int y = FIRST_CARD_Y + index * ROW_H;
		boolean found = GameMainActivity.isCollectionDiscovered(entry.id);

		// Card background
		g.setColor(found
				? Color.argb(90, 255, 215, 0)
				: Color.argb(55, 255, 255, 255));
		g.fillRoundRect(CARD_X, y, CARD_W, CARD_H, 10);

		// Icon
		g.setFont(Typeface.DEFAULT_BOLD, 20);
		g.setColor(found ? Color.rgb(255, 215, 0) : Color.argb(100, 200, 200, 200));
		String icon = found ? "\u2605" : "\u2606";
		g.drawString(icon, CARD_X + 12, y + 28);

		// Name
		g.setFont(Typeface.DEFAULT_BOLD, 20);
		g.setColor(found ? Color.rgb(255, 215, 0) : Color.argb(180, 220, 220, 220));
		String name = entry.getName();
		if (!found) {
			int nameLen = name.length();
			StringBuilder masked = new StringBuilder(nameLen);
			for (int si = 0; si < nameLen; si++) {
				masked.append('*');
			}
			name = masked.toString();
		}
		g.drawString(name, CARD_X + 40, y + 20);

		// Description (only shown if found)
		if (found) {
			g.setFont(Typeface.SANS_SERIF, 15);
			g.setColor(Color.argb(200, 200, 220, 240));
			String desc = entry.getDesc();
			// Truncate description if too long
			int maxChars = (CARD_W - 40) / 9;
			if (desc.length() > maxChars) {
				desc = desc.substring(0, maxChars - 2) + "..";
			}
			g.drawString(desc, CARD_X + 40, y + 38);
		} else {
			// "未发现" label
			g.setFont(Typeface.SANS_SERIF, 15);
			g.setColor(Color.argb(120, 180, 180, 180));
			g.drawString(L10n.get("coll_not_found"), CARD_X + 40, y + 38);
		}
	}

	private static int countDiscovered(ArrayList<CollectionEntry> entries) {
		int count = 0;
		for (CollectionEntry e : entries) {
			if (GameMainActivity.isCollectionDiscovered(e.id)) {
				count++;
			}
		}
		return count;
	}

	private void drawCenteredText(Painter g, String text, int left, int width, int baselineY) {
		float textW = g.measureText(text);
		int textX = left + (int)((width - textW) / 2f);
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
				Assets.playSound(Assets.selectedID);
				mSelectedTab = i;
				updateCurrentEntries();
				return true;
			}
		}

		// Back button
		int backBtnX = (GameMainActivity.GAME_WIDTH - BACK_BTN_W) / 2;
		if (scaleX >= backBtnX && scaleX <= backBtnX + BACK_BTN_W
				&& scaleY >= BACK_BTN_Y && scaleY <= BACK_BTN_Y + BACK_BTN_H) {
			setCurrentState(new MenuState());
			return true;
		}

		return true;
	}
}
