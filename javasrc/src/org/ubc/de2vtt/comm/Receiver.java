package org.ubc.de2vtt.comm;

import java.util.Timer;

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
		timer.cancel();
	}
}
