package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.framework.util.Painter;

import android.util.Log;
import android.view.MotionEvent;

public class LoadState extends State {

	private float logoTime;


	@Override
	public void init() {
		Log.d("LoadState", "开始读取资源");
		Assets.load();
		Log.d("LoadState", "资源读取完成");

		logoTime = 0;
	}

	@Override
	public void update(float delta) {
		logoTime += delta;
		if (logoTime > 2)
			setCurrentState(new MenuState());
	}

	@Override
	public void render(Painter g) {
		g.drawImage(Assets.logo, 0, 0);
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		return true;
	}

}
