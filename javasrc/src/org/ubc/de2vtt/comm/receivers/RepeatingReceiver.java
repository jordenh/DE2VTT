package org.ubc.de2vtt.comm.receivers;

import org.ubc.de2vtt.comm.ReceiveTask;

public class RepeatingReceiver extends Receiver {

	public RepeatingReceiver(ReceiveTask task, int interval) {
		super();
		timer.schedule(task, 1000, interval);
	}
}
