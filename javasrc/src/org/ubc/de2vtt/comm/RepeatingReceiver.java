package org.ubc.de2vtt.comm;

public class RepeatingReceiver extends Receiver {

	public RepeatingReceiver(ReceiveTask task, int interval) {
		super();
		timer.schedule(task, 1000, interval);
	}
}
