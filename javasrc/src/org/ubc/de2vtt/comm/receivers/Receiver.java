package org.ubc.de2vtt.comm.receivers;

import java.util.Timer;

import org.ubc.de2vtt.comm.ReceiveTask;

public abstract class Receiver {
	protected Timer timer;
	protected ReceiveTask task;
	
	public Receiver() {
		timer = new Timer();
	}
	
	public boolean isTaskNull() {
		return task == null;
	}
	
	public void cancel() {
		if (task != null) {
			timer.cancel();
			task.cancel();
			timer.purge();
		}
	}
}
