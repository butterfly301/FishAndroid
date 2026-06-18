package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;
import com.teacher.game.model.MyFish;
import com.teacher.game.state.L10n;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;

/**
 * Pure-rendering helpers for the HUD.
 * All data is read from the PlayState reference — no state kept here.
 */
final class HudRenderer {

	private HudRenderer() {}

	// ================================================================
	//  Companion marker (above fish heads)
	// ================================================================

	static void drawCompanionMarker(Painter g, PlayState s) {
		if (s.mCompanionFishList == null || s.mCompanionFishList.isEmpty()) {
			return;
		}
		for (com.teacher.game.model.CompanionFish companion : s.mCompanionFishList) {
			int centerX = companion.getX() + companion.getWidth() / 2;
			int baseY = companion.getY() - 10;

			g.setColor(Color.argb(228, 26, 220, 114));
			g.fillRect(centerX - 6, baseY, 12, 3);
			g.fillRect(centerX - 4, baseY + 3, 8, 3);
			g.fillRect(centerX - 2, baseY + 6, 4, 3);

			g.setColor(Color.argb(180, 8, 62, 40));
			g.fillRect(centerX - 7, baseY - 1, 1, 10);
			g.fillRect(centerX + 6, baseY - 1, 1, 10);
		}
	}

	// ================================================================
	//  Main HUD (score bar, life, pause button, sub-indicators)
	// ================================================================

	static void drawHud(Painter g, PlayState s) {
		g.setColor(Color.argb(150, 4, 29, 58));
		g.fillRoundRect(20, 18, 1240, 74, 28);

		g.setFont(Typeface.SANS_SERIF, 28);
		g.setColor(Color.WHITE);
		if (s.mEndlessMode) {
			g.drawString(s.getModeLabel(), 48, 65);
			g.drawString(L10n.get("hud_high_score", s.mEndlessHighScore), 220, 65);
			g.drawString(L10n.get("hud_life", s.mLife), 1010, 65);

			g.setFont(Typeface.SANS_SERIF, 24);
			g.setColor(Color.rgb(12, 58, 93));
			float scoreWidth = g.measureText(s.getScoreLabel());
			g.drawString(s.getScoreLabel(),
					(GameMainActivity.GAME_WIDTH - (int) scoreWidth) / 2, 56);
		} else {
			g.drawString(s.getModeLabel(), 48, 65);
			g.drawString(L10n.get("hud_life", s.mLife), 1010, 65);

			g.setColor(Color.argb(120, 255, 255, 255));
			g.fillRoundRect(430, 34, 420, 24, 12);
			int cappedScore = Math.min(s.mScore, s.mLevelConfig.targetScore);
			int progressWidth = s.mLevelConfig.targetScore > 0
					? (int) (420f * cappedScore / s.mLevelConfig.targetScore)
					: 0;
			g.setColor(Color.rgb(255, 195, 82));
			g.fillRoundRect(430, 34, progressWidth, 24, 12);

			g.setFont(Typeface.SANS_SERIF, 24);
			g.setColor(Color.rgb(12, 58, 93));
			g.drawString(s.getScoreLabel(), 500, 56);
		}

		// Pause button
		g.setColor(Color.argb(220, 255, 255, 255));
		g.fillRoundRect(1182, 26, 48, 48, 16);
		g.setColor(Color.rgb(20, 72, 116));
		g.fillRect(1182 + 14, 26 + 10, 6, 28);
		g.fillRect(1182 + 28, 26 + 10, 6, 28);

		drawTimeLimit(g, s);
		drawPowerUpIndicators(g, s);
		drawCompanionIndicators(g, s);
	}

	// ================================================================
	//  Time limit bar
	// ================================================================

	private static void drawTimeLimit(Painter g, PlayState s) {
		if (s.mLevelConfig.timeLimit <= 0 || s.mTimeLimitTimer <= 0) {
			return;
		}
		int barX = 430;
		int barY = 64;
		int barW = 420;
		int barH = 18;
		float ratio = s.mTimeLimitTimer / s.mLevelConfig.timeLimit;
		int fillW = (int) (barW * ratio);

		g.setColor(Color.argb(120, 0, 0, 0));
		g.fillRoundRect(barX, barY, barW, barH, 9);

		int color = ratio < 0.25f ? Color.rgb(255, 50, 50)
				: ratio < 0.5f ? Color.rgb(255, 200, 50)
				: Color.rgb(100, 200, 255);
		g.setColor(color);
		g.fillRoundRect(barX + 1, barY + 1, Math.max(fillW - 2, 0), barH - 2, 8);

		g.setFont(Typeface.SANS_SERIF, 20);
		g.setColor(Color.WHITE);
		g.drawString((int) s.mTimeLimitTimer + "s", barX + barW / 2 - 14, barY + 22);
	}

	// ================================================================
	//  Companion charge indicator
	// ================================================================

	private static void drawCompanionIndicators(Painter g, PlayState s) {
		int cardX = 40;
		int cardY = 88;
		g.setColor(Color.argb(168, 6, 34, 66));
		g.fillRoundRect(cardX, cardY, 320, 54, 16);

		g.setFont(Typeface.SANS_SERIF, 22);
		g.setColor(Color.WHITE);
		g.drawString(L10n.get("hud_companion_count", s.mCompanionFishList.size()), cardX + 14, cardY + 35);
		g.setColor(Color.argb(120, 255, 255, 255));
		g.fillRoundRect(cardX + 168, cardY + 20, 136, 14, 8);
		int w = (int) (136f * s.mCompanionCharge / PlayState.COMPANION_CHARGE_TARGET);
		g.setColor(Color.rgb(255, 198, 84));
		g.fillRoundRect(cardX + 168, cardY + 20, w, 14, 8);
	}

	// ================================================================
	//  Power-up indicators (speed / freeze / shield / lure)
	// ================================================================

	private static void drawPowerUpIndicators(Painter g, PlayState s) {
		int x = 460;
		int y = 20;
		int barH = 32;

		if (s.mSpeedTimer > 0) {
			float ratio = s.mSpeedTimer / com.teacher.game.model.PowerUpType.SPEED.duration;
			int w = (int) (140 * ratio);
			g.setColor(Color.argb(180, 255, 215, 0));
			g.fillRoundRect(x, y, w, barH, 6);
			g.setColor(Color.argb(220, 255, 255, 255));
			g.setFont(Typeface.SANS_SERIF, 22);
			g.drawString(L10n.get("hud_speed"), x + 8, y + 24);
			x += 158;
		}

		if (s.mFreezeTimer > 0) {
			float ratio = s.mFreezeTimer / com.teacher.game.model.PowerUpType.FREEZE.duration;
			int w = (int) (140 * ratio);
			g.setColor(Color.argb(180, 0, 200, 255));
			g.fillRoundRect(x, y, w, barH, 6);
			g.setColor(Color.argb(220, 255, 255, 255));
			g.setFont(Typeface.SANS_SERIF, 22);
			g.drawString(L10n.get("hud_freeze"), x + 8, y + 24);
			x += 158;
		}

		if (s.mMyFish.mHasShield) {
			g.setColor(Color.argb(180, 40, 130, 255));
			g.fillRoundRect(x, y, 100, barH, 6);
			g.setColor(Color.argb(220, 255, 255, 255));
			g.setFont(Typeface.SANS_SERIF, 22);
			g.drawString(L10n.get("hud_shield"), x + 8, y + 24);
			x += 118;
		}

		if (s.mLureTimer > 0) {
			float ratio = s.mLureTimer / com.teacher.game.model.PowerUpType.LURE.duration;
			int w = (int) (140 * ratio);
			g.setColor(Color.argb(180, 255, 105, 180));
			g.fillRoundRect(x, y, w, barH, 6);
			g.setColor(Color.argb(220, 255, 255, 255));
			g.setFont(Typeface.SANS_SERIF, 22);
			g.drawString(L10n.get("hud_lure"), x + 8, y + 24);
		}
	}

	// ================================================================
	//  Combo display
	// ================================================================

	static void drawCombo(Painter g, PlayState s) {
		if (s.mCombo < 2) return;

		int[] comboColors = {
				Color.rgb(255, 215, 0),
				Color.rgb(255, 165, 0),
				Color.rgb(255, 69, 0),
				Color.rgb(200, 50, 255)
		};
		int ci = Math.min(s.mCombo - 2, comboColors.length - 1);

		int comboX = 800;
		int comboY = 65;

		int fontSize = (int) (28 * s.mComboScale);
		int offset = (int) (2 * s.mComboScale);
		g.setFont(Typeface.DEFAULT_BOLD, fontSize);
		g.setColor(Color.argb(120, 0, 0, 0));
		g.drawString(L10n.get("hud_combo", s.mCombo), comboX + offset, comboY + offset);
		g.setColor(Color.argb(80, 255, 255, 255));
		g.drawString(L10n.get("hud_combo", s.mCombo), comboX + offset / 2, comboY + offset / 2);
		g.setColor(comboColors[ci]);
		g.drawString(L10n.get("hud_combo", s.mCombo), comboX, comboY);
	}

	// ================================================================
	//  Element overlays (die / gameover / pass animation)
	// ================================================================

	static void drawElement(Painter g, PlayState s) {
		if (s.mMyFish.mNonceState == com.teacher.game.model.Fish.DIE)
			g.drawImage(Assets.sorry, 555, 310);
		if (s.mLife <= 0)
			g.drawImage(Assets.gameover, 520, 310);
		if (s.didClearLevel()) {
			for (int i = 0; i < 4; i++) {
				if (s.mMyFish.mStartTime % 4 != i)
					g.drawImage(Assets.pass, i * 25, 0, 554 + 45 * i, 310, 25, 25);
				else
					g.drawImage(Assets.pass, i * 25, 0, 554 + 45 * i, 320, 25, 25);
			}
		}
	}

	// ================================================================
	//  Lure aura (pulsing pink circles)
	// ================================================================

	static void drawLureAura(Painter g, PlayState s) {
		int cx = s.mMyFish.getX() + s.mMyFish.getWidth() / 2;
		int cy = s.mMyFish.getY() + s.mMyFish.getHeight() / 2;
		float pulse = (float) (Math.sin(System.nanoTime() / 200_000_000.0) * 0.25 + 0.75);
		int alpha = (int) (60 * pulse);
		int baseR = 90 + (int) (20 * pulse);
		Canvas canvas = g.getCanvas();
		android.graphics.Paint p = new android.graphics.Paint(
				android.graphics.Paint.ANTI_ALIAS_FLAG);
		p.setStyle(android.graphics.Paint.Style.STROKE);
		p.setStrokeWidth(2.5f);
		for (int i = 0; i < 3; i++) {
			p.setColor(Color.argb(alpha - i * 12, 255, 105, 180));
			canvas.drawCircle(cx, cy, baseR + i * 25, p);
		}
	}

	// ================================================================
	//  Debug button (floating "调" in corner)
	// ================================================================

	static void drawDebugButton(Painter g) {
		int size = 66;
		int margin = 22;
		int left = GameMainActivity.GAME_WIDTH - size - margin;
		int top = GameMainActivity.GAME_HEIGHT - size - margin;
		g.setColor(Color.argb(220, 15, 44, 72));
		g.fillRoundRect(left, top, size, size, 14);
		g.setColor(Color.rgb(255, 214, 96));
		g.fillRoundRect(left + 4, top + 4, size - 8, size - 8, 12);
		g.setFont(Typeface.DEFAULT_BOLD, 18);
		g.setColor(Color.rgb(12, 58, 93));
		g.drawString(L10n.get("hud_debug_btn"), left + 20, top + 37);
	}
}
