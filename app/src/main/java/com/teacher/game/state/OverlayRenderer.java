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

    static final int OVERLAY_CARD_X = 300;
    static final int OVERLAY_CARD_Y = 180;
    static final int OVERLAY_CARD_W = 680;
    static final int OVERLAY_CARD_H = 320;
    static final int OVERLAY_BUTTON_W = 180;
    static final int OVERLAY_BUTTON_H = 64;
    static final int OVERLAY_BUTTON_GAP = 18;
    static final int OVERLAY_BUTTON_Y = 376;

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

    static void drawRoundEndOverlay(Painter g, String title, String subtitle, String[] buttonLabels) {
        g.setColor(Color.argb(168, 0, 0, 0));
        g.fillRect(0, 0, GameMainActivity.GAME_WIDTH, GameMainActivity.GAME_HEIGHT);
        drawOverlayCard(g, title, subtitle);
        drawOverlayButtons(g, buttonLabels);
    }

    // ================================================================
    //  Card
    // ================================================================

    static void drawOverlayCard(Painter g, String title, String subtitle) {
        g.setColor(Color.argb(228, 8, 37, 74));
        g.fillRoundRect(OVERLAY_CARD_X, OVERLAY_CARD_Y, OVERLAY_CARD_W, OVERLAY_CARD_H, 34);
        g.setColor(Color.argb(120, 255, 255, 255));
        g.fillRoundRect(OVERLAY_CARD_X + 16, OVERLAY_CARD_Y + 16, OVERLAY_CARD_W - 32, 96, 28);

        g.setFont(Typeface.DEFAULT_BOLD, 44);
        g.setColor(Color.WHITE);
        drawCenteredText(g, title, OVERLAY_CARD_X, OVERLAY_CARD_W, OVERLAY_CARD_Y + 96);

        g.setFont(Typeface.SANS_SERIF, 24);
        g.setColor(Color.argb(255, 223, 241, 255));
        drawCenteredText(g, subtitle, OVERLAY_CARD_X, OVERLAY_CARD_W, OVERLAY_CARD_Y + 156);
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
            g.fillRoundRect(x + 4, OVERLAY_BUTTON_Y + 4, OVERLAY_BUTTON_W, OVERLAY_BUTTON_H, 18);
            g.setColor(color);
            g.fillRoundRect(x, OVERLAY_BUTTON_Y, OVERLAY_BUTTON_W, OVERLAY_BUTTON_H, 18);

            g.setFont(Typeface.DEFAULT_BOLD, 26);
            g.setColor(Color.rgb(14, 52, 88));
            drawCenteredText(g, labels[i], x, OVERLAY_BUTTON_W, OVERLAY_BUTTON_Y + 41);
        }
    }

    static int getOverlayButtonX(int buttonCount, int index) {
        int totalWidth = buttonCount * OVERLAY_BUTTON_W + (buttonCount - 1) * OVERLAY_BUTTON_GAP;
        return (GameMainActivity.GAME_WIDTH - totalWidth) / 2 + index * (OVERLAY_BUTTON_W + OVERLAY_BUTTON_GAP);
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
