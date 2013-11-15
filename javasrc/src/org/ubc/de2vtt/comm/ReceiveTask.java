package org.ubc.de2vtt.comm;

import java.util.TimerTask;

public abstract class ReceiveTask extends TimerTask {	
	private static final String TAG = ReceiveTask.class.getSimpleName();
	
	public void run() {
		//Log.v(TAG, "run");
		Messenger messenger = Messenger.GetSharedInstance();
		if (messenger.isConnected()) {
			//Log.v(TAG, "Messenger connected.");
			getMessage(messenger);
		}
	}
	
	private void getMessage(Messenger messenger) {
        Received rcv = messenger.receive();
        if (rcv != null) {
            performAction(rcv);
        }
    }
	
	abstract protected void performAction(Received rcv);
}
