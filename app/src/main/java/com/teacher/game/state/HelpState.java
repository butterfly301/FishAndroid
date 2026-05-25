package com.teacher.game.state;

import com.teacher.fish.Assets;
import com.teacher.framework.util.Painter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.MotionEvent;

public class HelpState extends State {

	private String[] lines;

	@Override
	public void init() {
		lines = new String[] {
				"杨世杰",
				"周欣琦",
				"吴凯东",
		};
	}

	@Override
	public void update(float delta) {
	}

	@Override
	public void render(Painter g) {
		g.drawImage(Assets.bgimg7, 0, 0);

		g.setColor(Color.argb(150, 5, 29, 58));
		g.fillRoundRect(152, 92, 976, 454, 34);
		g.setFont(Typeface.SANS_SERIF, 54);
		g.setColor(Color.WHITE);
		float titleWidth = g.measureText("制作成员");
		g.drawString("制作成员", (1280 - (int)titleWidth) / 2, 170);
		g.setFont(Typeface.SANS_SERIF, 28);
		g.setColor(Color.argb(255, 219, 246, 255));
		for (int i = 0; i < lines.length; i++) {
			g.drawString(lines[i], 250, 260 + i * 54);
		}
		g.setFont(Typeface.SANS_SERIF, 24);
		g.setColor(Color.argb(255, 255, 211, 111));
		float tipWidth = g.measureText("点击任意位置返回");
		g.drawString("点击任意位置返回", (1280 - (int)tipWidth) / 2, 500);
	}

	@Override
	public boolean onTouch(MotionEvent e, int scaleX, int scaleY) {
		if (e.getAction() == MotionEvent.ACTION_UP) {
			setCurrentState(new MenuState());
			return true;
		}
		return true;
	}

}
