package com.teacher.game.state;

import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;
import com.teacher.game.state.L10n;

import android.graphics.Color;
import android.graphics.Typeface;

/**
 * Pure-rendering helpers for pause and round-end overlays.
 * All data is passed in — no state kept here.
 */
final class OverlayRenderer {

    // --- Overlay card geometry ---

    // --- Stats panel geometry (inside card) ---

    private static final int STATS_PANEL_X = OverlayLayout.CARD_X + 40;
    private static final int STATS_PANEL_Y = 300;
    private static final int STATS_PANEL_W = OverlayLayout.CARD_W - 80;
    private static final int STATS_ROW_H = 44;
    private static final int STATS_ROW_START_Y = STATS_PANEL_Y + 28;

    private OverlayRenderer() {
        // utility class
    }

    // ================================================================
    //  Entry points
    // ================================================================

    static void drawPauseOverlay(Painter g) {
        g.setColor(Color.argb(168, 0, 0, 0));
        g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);
        drawOverlayCard(g, L10n.get("pause_title"), L10n.get("pause_desc"));
        drawOverlayButtons(g, new String[]{L10n.get("pause_resume"), L10n.get("pause_restart"), L10n.get("pause_menu")});
    }

	static void drawRoundEndOverlay(Painter g, String title, String subtitle,
	                                String[] statsData, String[] buttonLabels) {
		drawRoundEndOverlay(g, title, subtitle, statsData, buttonLabels, 0);
	}

	static void drawRoundEndOverlay(Painter g, String title, String subtitle,
	                                String[] statsData, String[] buttonLabels, int stars) {
		g.setColor(Color.argb(168, 0, 0, 0));
		g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);
		drawOverlayCard(g, title, subtitle);
		drawStars(g, stars);
		drawStatsPanel(g, statsData);
		drawOverlayButtons(g, buttonLabels);
	}

    // ================================================================
    //  Card
    // ================================================================

    static void drawOverlayCard(Painter g, String title, String subtitle) {
        g.setColor(Color.argb(228, 8, 37, 74));
        g.fillRoundRect(OverlayLayout.CARD_X, OverlayLayout.CARD_Y, OverlayLayout.CARD_W, OverlayLayout.CARD_H, 34);
        g.setColor(Color.argb(120, 255, 255, 255));
        g.fillRoundRect(OverlayLayout.CARD_X + 20, OverlayLayout.CARD_Y + 16, OverlayLayout.CARD_W - 40, 96, 28);

        g.setFont(Typeface.DEFAULT_BOLD, 44);
        g.setColor(Color.WHITE);
        drawCenteredText(g, title, OverlayLayout.CARD_X, OverlayLayout.CARD_W, OverlayLayout.CARD_Y + 96);

        g.setFont(Typeface.SANS_SERIF, 24);
        g.setColor(Color.argb(255, 223, 241, 255));
        drawCenteredText(g, subtitle, OverlayLayout.CARD_X, OverlayLayout.CARD_W, OverlayLayout.CARD_Y + 148);
    }

	// ================================================================
	//  Star rating (1-3)
	// ================================================================

	private static void drawStars(Painter g, int starCount) {
		if (starCount <= 0) return;
		int starSize = 36;
		int gap = 20;
		int totalW = starCount * starSize + (starCount - 1) * gap;
		int startX = (GameMainActivity.GAME_WIDTH - totalW) / 2;
		int y = OverlayLayout.CARD_Y + 178;

		for (int i = 0; i < 3; i++) {
			int cx = startX + i * (starSize + gap) + starSize / 2;
			boolean filled = i < starCount;
			drawStarShape(g, cx, y, starSize / 2, filled);
		}
	}

	/**
	 * Draw a single five-pointed star.
	 */
	private static void drawStarShape(Painter g, int cx, int cy, int r, boolean filled) {
		android.graphics.Path path = new android.graphics.Path();
		for (int i = 0; i < 10; i++) {
			float angle = (float) (i * Math.PI / 5 - Math.PI / 2);
			float radius = (i % 2 == 0) ? r : r * 0.45f;
			float px = cx + (float) Math.cos(angle) * radius;
			float py = cy + (float) Math.sin(angle) * radius;
			if (i == 0) path.moveTo(px, py);
			else path.lineTo(px, py);
		}
		path.close();

		android.graphics.Paint p = new android.graphics.Paint(
				android.graphics.Paint.ANTI_ALIAS_FLAG);
		if (filled) {
			p.setStyle(android.graphics.Paint.Style.FILL);
			p.setColor(Color.rgb(255, 215, 0));
		} else {
			p.setStyle(android.graphics.Paint.Style.STROKE);
			p.setStrokeWidth(2.5f);
			p.setColor(Color.argb(120, 255, 255, 255));
		}
		g.getCanvas().drawPath(path, p);
	}

	// ================================================================
	//  Stats panel
	// ================================================================

    private static void drawStatsPanel(Painter g, String[] stats) {
        if (stats == null || stats.length == 0) {
            return;
        }

        // Panel background
        int panelH = stats.length * STATS_ROW_H + 36;
        g.setColor(Color.argb(80, 0, 0, 0));
        g.fillRoundRect(STATS_PANEL_X, STATS_PANEL_Y, STATS_PANEL_W, panelH, 20);

        // Each stat on its own row, left-aligned
        g.setFont(Typeface.SANS_SERIF, 26);
        for (int i = 0; i < stats.length; i++) {
            int y = STATS_ROW_START_Y + i * STATS_ROW_H;

            // Alternating row tint
            if (i % 2 == 0) {
                g.setColor(Color.argb(25, 255, 255, 255));
                g.fillRoundRect(STATS_PANEL_X + 12, y - 6,
                        STATS_PANEL_W - 24, STATS_ROW_H - 4, 8);
            }

            g.setColor(Color.argb(255, 229, 245, 255));
            g.drawString(stats[i], STATS_PANEL_X + 28, y + 22);
        }
    }

    // ================================================================
    //  Buttons
    // ================================================================

    static void drawOverlayButtons(Painter g, String[] labels) {
        for (int i = 0; i < labels.length; i++) {
            int x = getOverlayButtonX(labels.length, i);
            int color = i == labels.length - 1 && labels.length == 3
                    ? Color.rgb(106, 191, 245)
                    : Color.rgb(255, 197, 81);
            if (labels.length == 2 && i == 1) {
                color = Color.rgb(106, 191, 245);
            }
            if (labels.length == 3 && i == 1) {
                color = Color.rgb(255, 197, 81);
            }

            g.setColor(Color.argb(100, 0, 0, 0));
            g.fillRoundRect(x + 4, OverlayLayout.BUTTON_Y + 4, OverlayLayout.BUTTON_W, OverlayLayout.BUTTON_H, 18);
            g.setColor(color);
            g.fillRoundRect(x, OverlayLayout.BUTTON_Y, OverlayLayout.BUTTON_W, OverlayLayout.BUTTON_H, 18);

            g.setFont(Typeface.DEFAULT_BOLD, 26);
            g.setColor(Color.rgb(14, 52, 88));
            drawCenteredText(g, labels[i], x, OverlayLayout.BUTTON_W, OverlayLayout.BUTTON_Y + 41);
        }
    }

    static int getOverlayButtonX(int buttonCount, int index) {
        int totalWidth = buttonCount * OverlayLayout.BUTTON_W + (buttonCount - 1) * OverlayLayout.BUTTON_GAP;
        return (GameMainActivity.GAME_WIDTH - totalWidth) / 2 + index * (OverlayLayout.BUTTON_W + OverlayLayout.BUTTON_GAP);
    }

    // ================================================================
    //  Text
    // ================================================================

    static void drawCenteredText(Painter g, String text, int left, int width, int baselineY) {
        float textWidth = g.measureText(text);
        int textX = left + (int) ((width - textWidth) / 2f);
        g.drawString(text, textX, baselineY);
    }
}
