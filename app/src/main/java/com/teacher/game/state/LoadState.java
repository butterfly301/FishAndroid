package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.fish.GameMainActivity;
import com.teacher.framework.util.Painter;
import com.teacher.game.state.L10n;

import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;

public class LoadState extends State {

	private float logoTime;
	private float loadingProgress;


	@Override
	public void init() {
		Log.d("LoadState", "开始读取资源");
		Assets.load();
		Log.d("LoadState", "资源读取完成");

		// Apply persisted settings
		Assets.sSoundEnabled = GameMainActivity.isSoundEnabled();
		Assets.sMusicEnabled = GameMainActivity.isMusicEnabled();

		logoTime = 0;
		loadingProgress = 0;
	}

	@Override
	public void update(float delta) {
		logoTime += delta;
		loadingProgress = Math.min(logoTime / 2.0f, 1.0f);
		if (logoTime > 2)
			setCurrentState(new MenuState());
	}

	@Override
	public void render(Painter g) {
		g.drawImage(Assets.logo, 0, 0);

		// Progress bar
		int barW = 480;
		int barH = 20;
		int barX = (GameMainActivity.GAME_WIDTH - barW) / 2;
		int barY = GameMainActivity.GAME_HEIGHT - 80;
		int fillW = (int)(barW * loadingProgress);

		g.setColor(Color.argb(160, 4, 29, 58));
		g.fillRoundRect(barX, barY, barW, barH, 10);
		g.setColor(Color.rgb(255, 198, 84));
		g.fillRoundRect(barX + 2, barY + 2, Math.max(fillW - 4, 0), barH - 4, 8);

		// Loading text
		g.setFont(Typeface.SANS_SERIF, 22);
		g.setColor(Color.argb(220, 255, 255, 255));
		g.drawString(L10n.get("loading"), barX + barW / 2 - 48, barY - 10);
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		return true;
	}

}
