package com.teacher.game.state;

import com.teacher.framework.util.Painter;

import android.graphics.Color;
import android.graphics.Typeface;

/**
 * Floating score text that rises upward and fades out.
 */
public class ScorePopup {

	private static final int RISE_SPEED = -2;

	public String text;
	public int x, y;
	public float life;
	public int color; // text color

	public ScorePopup(String text, int x, int y) {
		this(text, x, y, Color.rgb(255, 220, 50));
	}

	public ScorePopup(String text, int x, int y, int color) {
		this.text = text;
		this.x = x;
		this.y = y;
		this.life = 1.0f;
		this.color = color;
	}

	public boolean isDead() {
		return life <= 0;
	}

	public void update() {
		life -= 0.028f;
		y += RISE_SPEED;
	}

	public void render(Painter g) {
		if (life <= 0) {
			return;
		}
		int alpha = Math.min(255, Math.max(0, (int) (255 * life)));
		g.setFont(Typeface.DEFAULT_BOLD, 30);
		// Shadow
		g.setColor(Color.argb(alpha / 2, 0, 0, 0));
		g.drawString(text, x - (int) (g.measureText(text) / 2) + 2, y + 2);
		// Text
		g.setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
		g.drawString(text, x - (int) (g.measureText(text) / 2), y);
	}
}
