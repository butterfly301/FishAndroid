package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

/**
 * Decodes raw touch events into game actions.
 * Owns touch-tracking fields (mTouchX/Y, mTouchDown, mDX/mDY)
 * and delegates to PlayState for game-state mutation.
 */
public class TouchHandler {

    // --- Overlay constants shared with OverlayRenderer ---
    // Keep local copies to avoid circular intra-package dependency on OverlayRenderer
    private static final int OVERLAY_BUTTON_W = 180;
    private static final int OVERLAY_BUTTON_H = 64;
    private static final int OVERLAY_BUTTON_GAP = 18;
    private static final int OVERLAY_BUTTON_Y = 560;

    // --- Pause button ---
    private static final int PAUSE_BTN_X = 1182;
    private static final int PAUSE_BTN_Y = 26;
    private static final int PAUSE_BTN_SIZE = 48;

    // --- Debug panel ---
    private static final int DEBUG_BTN_SIZE = 66;
    private static final int DEBUG_BTN_MARGIN = 22;
    private static final int DEBUG_PANEL_W = 640;
	private static final int DEBUG_PANEL_H = 420;
    private static final int DEBUG_PANEL_X = GameMainActivity.GAME_WIDTH - DEBUG_PANEL_W - 24;
    private static final int DEBUG_PANEL_Y = GameMainActivity.GAME_HEIGHT - DEBUG_PANEL_H - 24;
    private static final int DEBUG_CLOSE_W = 56;
    private static final int DEBUG_CLOSE_H = 56;
    private static final int DEBUG_SPEED_DEC_W = 84;
    private static final int DEBUG_SPEED_DEC_H = 64;
    private static final int DEBUG_SPEED_INC_W = 84;
    private static final int DEBUG_SPEED_INC_H = 64;
    private static final int DEBUG_SPAWN_W = 400;
    private static final int DEBUG_SPAWN_H = 72;

    // --- Touch state (public so PlayState.render() can read them) ---

    public int mTouchX, mTouchY;
    public boolean mTouchDown;
    public int mDX, mDY;

    // --- Back-reference to owning PlayState ---

    private final PlayState mState;

    public TouchHandler(PlayState state) {
        mState = state;
    }

    // ================================================================
    //  Main entry — called from PlayState.onTouch()
    // ================================================================

    public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            if (handleDebugTap(scaleX, scaleY)) {
                return true;
            }
            if (handlePauseButtonTap(scaleX, scaleY) || handleOverlayTap(scaleX, scaleY)) {
                return true;
            }
            if (mState.mAutoMode) {
                return true;
            }
            mTouchX = scaleX;
            mTouchY = scaleY;
            mTouchDown = true;
        } else if (e.getAction() == MotionEvent.ACTION_MOVE) {
            if (mState.mAutoMode) {
                return true;
            }
            if (scaleX - mTouchX > 10)
                mDX = 10;
            else if (scaleX - mTouchX < -10)
                mDX = -10;
            else
                mDX = 0;
            if (scaleY - mTouchY > 10)
                mDY = 10;
            else if (scaleY - mTouchY < -10)
                mDY = -10;
            else
                mDY = 0;
        } else if (e.getAction() == MotionEvent.ACTION_UP) {
            if (mState.mDebugPanelVisible || mState.mGamePaused || mState.isRoundFinished()) {
                return true;
            }
            if (mState.mAutoMode) {
                return true;
            }
            mTouchDown = false;
        }
        return true;
    }

    // ================================================================
    //  Debug panel
    // ================================================================

    private boolean handleDebugTap(int scaleX, int scaleY) {
        int btnLeft = GameMainActivity.GAME_WIDTH - DEBUG_BTN_SIZE - DEBUG_BTN_MARGIN;
        int btnTop = GameMainActivity.GAME_HEIGHT - DEBUG_BTN_SIZE - DEBUG_BTN_MARGIN;
        if (!mState.mDebugPanelVisible) {
            if (isPointInside(scaleX, scaleY, btnLeft, btnTop, DEBUG_BTN_SIZE, DEBUG_BTN_SIZE)) {
                mState.mDebugPanelVisible = true;
                mTouchDown = false;
                mDX = 0;
                mDY = 0;
                return true;
            }
            return false;
        }

        // Panel is visible — hit-test inner controls
        int closeX = DEBUG_PANEL_X + DEBUG_PANEL_W - DEBUG_CLOSE_W - 18;
        int closeY = DEBUG_PANEL_Y + DEBUG_PANEL_H - DEBUG_CLOSE_H - 16;
        if (isPointInside(scaleX, scaleY, closeX, closeY, DEBUG_CLOSE_W, DEBUG_CLOSE_H)) {
            mState.mDebugPanelVisible = false;
            return true;
        }

        int decX = DEBUG_PANEL_X + 26;
        int decY = DEBUG_PANEL_Y + 154;
        int incX = decX + DEBUG_SPEED_DEC_W + 12;
        int incY = decY;
        if (isPointInside(scaleX, scaleY, decX, decY, DEBUG_SPEED_DEC_W, DEBUG_SPEED_DEC_H)) {
            mState.mDebugGameSpeed = Math.max(0.25f, mState.mDebugGameSpeed - 0.25f);
            return true;
        }
        if (isPointInside(scaleX, scaleY, incX, incY, DEBUG_SPEED_INC_W, DEBUG_SPEED_INC_H)) {
            mState.mDebugGameSpeed = Math.min(5.0f, mState.mDebugGameSpeed + 0.25f);
            return true;
        }

        int spawnX = DEBUG_PANEL_X + 26;
        int spawnBtnH = 54;
        int spawnGap = 8;

        // "生成1条基础鱼"
        int fishY = DEBUG_PANEL_Y + 230;
        if (isPointInside(scaleX, scaleY, spawnX, fishY, DEBUG_SPAWN_W, spawnBtnH)) {
            mState.spawnDebugSmallFish();
            return true;
        }

        // "生成一个泡泡"
        int powerUpY = fishY + spawnBtnH + spawnGap;
        if (isPointInside(scaleX, scaleY, spawnX, powerUpY, DEBUG_SPAWN_W, spawnBtnH)) {
            mState.spawnPowerUp();
            return true;
        }

        // "生成一个同伴"
        int companionY = powerUpY + spawnBtnH + spawnGap;
        if (isPointInside(scaleX, scaleY, spawnX, companionY, DEBUG_SPAWN_W, spawnBtnH)) {
            mState.spawnDebugCompanion();
            return true;
        }

        return true; // panel consumes touch
    }

    // ================================================================
    //  Pause button
    // ================================================================

    private boolean handlePauseButtonTap(int scaleX, int scaleY) {
        if (!mState.mGamePaused && !mState.isRoundFinished()
                && isPointInside(scaleX, scaleY, PAUSE_BTN_X, PAUSE_BTN_Y, PAUSE_BTN_SIZE, PAUSE_BTN_SIZE)) {
            mState.mGamePaused = true;
            mTouchDown = false;
            mDX = 0;
            mDY = 0;
            Assets.stopMusic();
            return true;
        }
        return false;
    }

    // ================================================================
    //  Overlay tap routing (pause overlay + round-end overlay)
    // ================================================================

    private boolean handleOverlayTap(int scaleX, int scaleY) {
        if (mState.mGamePaused) {
            // Resume
            if (isPointInside(scaleX, scaleY,
                    getOverlayButtonX(3, 0), OVERLAY_BUTTON_Y, OVERLAY_BUTTON_W, OVERLAY_BUTTON_H)) {
                mState.mGamePaused = false;
                Assets.playMusic("backgoundsound.mid", true);
                return true;
            }
            // Restart
            if (isPointInside(scaleX, scaleY,
                    getOverlayButtonX(3, 1), OVERLAY_BUTTON_Y, OVERLAY_BUTTON_W, OVERLAY_BUTTON_H)) {
                mState.restartGame();
                return true;
            }
            // Return menu
            if (isPointInside(scaleX, scaleY,
                    getOverlayButtonX(3, 2), OVERLAY_BUTTON_Y, OVERLAY_BUTTON_W, OVERLAY_BUTTON_H)) {
                mState.returnToMenu();
                return true;
            }
            return true;
        }

        if (mState.isRoundFinished()) {
            boolean cleared = mState.didClearLevel();
            boolean hasNext = mState.hasNextLevel();
            String[] labels = mState.getRoundEndButtonLabels();
            for (int i = 0; i < labels.length; i++) {
                if (isPointInside(scaleX, scaleY,
                        getOverlayButtonX(labels.length, i), OVERLAY_BUTTON_Y, OVERLAY_BUTTON_W, OVERLAY_BUTTON_H)) {
                    ModeRules.RoundEndAction action = mState.mModeRules.resolveRoundEndAction(i, cleared, hasNext);
                    mState.handleRoundEndAction(action);
                    return true;
                }
            }
            return true;
        }

        return false;
    }

    // ================================================================
    //  Utility
    // ================================================================

    private boolean isPointInside(int x, int y, int left, int top, int width, int height) {
        return x >= left && x <= left + width && y >= top && y <= top + height;
    }

    /** Mirror of OverlayRenderer.getOverlayButtonX so TouchHandler doesn't depend on it. */
    private int getOverlayButtonX(int buttonCount, int index) {
        int totalWidth = buttonCount * OVERLAY_BUTTON_W + (buttonCount - 1) * OVERLAY_BUTTON_GAP;
        return (GameMainActivity.GAME_WIDTH - totalWidth) / 2 + index * (OVERLAY_BUTTON_W + OVERLAY_BUTTON_GAP);
    }
}
