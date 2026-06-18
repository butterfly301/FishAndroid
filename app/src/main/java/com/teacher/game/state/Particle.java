package com.teacher.game.state;

import com.teacher.framework.util.Painter;

import android.graphics.Color;

/**
 * Single particle: a colored dot with velocity, gravity, and fade-out.
 */
public class Particle {

	public float x, y, vx, vy;
	public float life, maxLife;
	public int color;
	public int size;

	public Particle(float x, float y, float vx, float vy, int color, float life) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.color = color;
		this.life = life;
		this.maxLife = life;
		this.size = 4 + (int) (life * 6);
	}

	public boolean isDead() {
		return life <= 0;
	}

	public void update() {
		x += vx;
		y += vy;
		vy += 0.25f; // gentle gravity
		life -= 0.035f;
	}

	public void render(Painter g) {
		if (life <= 0) {
			return;
		}
		int alpha = Math.min(255, Math.max(0, (int) (255 * life / maxLife)));
		int r = Color.red(color);
		int gr = Color.green(color);
		int b = Color.blue(color);
		g.setColor(Color.argb(alpha, r, gr, b));
		g.fillOval((int) x - size / 2, (int) y - size / 2, size, size);
	}
}
