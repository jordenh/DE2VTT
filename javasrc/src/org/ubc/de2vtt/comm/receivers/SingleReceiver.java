package org.ubc.de2vtt.comm.receivers;

import org.ubc.de2vtt.comm.ReceiveTask;

public class SingleReceiver extends Receiver {
	
	public SingleReceiver(ReceiveTask task) {
		super();
		timer.schedule(task, 100);
	}
}
