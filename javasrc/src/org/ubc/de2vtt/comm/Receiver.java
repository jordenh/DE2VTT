package org.ubc.de2vtt.comm;

import java.util.Timer;

public class Receiver {
	private Timer timer;
	private ReceiveTask task;
	
	public Receiver(ReceiveTask task) {
		timer = new Timer();
		timer.schedule(task, 3000, 500);
	}
	
	public boolean isTaskNull() {
		return task == null;
	}
	
	public void cancel() {
		timer.cancel();
	}
}
