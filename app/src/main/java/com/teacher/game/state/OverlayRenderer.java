package com.teacher.game.state;

import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;

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
        drawOverlayCard(g, "游戏已暂停", "可以继续挑战，也可以重新开始");
        drawOverlayButtons(g, new String[]{"继续游戏", "重新开始", "返回菜单"});
    }

    static void drawRoundEndOverlay(Painter g, String title, String subtitle,
                                    String[] statsData, String[] buttonLabels) {
        g.setColor(Color.argb(168, 0, 0, 0));
        g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);
        drawOverlayCard(g, title, subtitle);
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
