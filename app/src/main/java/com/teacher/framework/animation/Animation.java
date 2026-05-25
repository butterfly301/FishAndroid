package com.teacher.framework.animation;

import com.teacher.framework.util.Painter;

public class Animation {
	
	private Frame frames[];
	private double []frameEndTimes;
	private int currentFrameIndex;
	
	private double currentTime;
	private double totalDuration;
	
	public Animation(Frame ... frames) {
		this.frames = frames;
		frameEndTimes = new double[frames.length];
		
		for (int i=0;i<frames.length;i++) {
			Frame f = frames[i];
			totalDuration += f.getDuration();
			frameEndTimes[i] = totalDuration;
		}		
	}
	
	public synchronized void update(double delta) {
		currentTime += delta;
		if (currentTime > totalDuration) {
			wrapAnimation();
		}
		while (currentTime > frameEndTimes[currentFrameIndex])
			currentFrameIndex++;
	}

	private synchronized void wrapAnimation() {
		currentFrameIndex = 0;
		currentTime %= totalDuration;
	}
	
	
	public synchronized void render(Painter g,int x, int y) {
		g.drawImage(frames[currentFrameIndex].getImage(), x, y);
	}
	
	public synchronized void render(Painter g,int x, int y,int width, int height) {
		g.drawImage(frames[currentFrameIndex].getImage(), x, y, width, height);
	}
	

}
