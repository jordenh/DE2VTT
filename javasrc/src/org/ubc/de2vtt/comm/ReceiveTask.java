package org.ubc.de2vtt.comm;

import java.util.TimerTask;

public abstract class ReceiveTask extends TimerTask {	
	public void run() {
		Messenger messenger = Messenger.GetSharedInstance();
		if (messenger.isConnected()) {
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
