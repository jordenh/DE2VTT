package org.ubc.de2vtt.comm;

public class SingleReceiver extends Receiver {
	
	public SingleReceiver(ReceiveTask task) {
		super();
		timer.schedule(task, 3000, 500);
	}
}
