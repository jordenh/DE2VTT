package org.ubc.de2vtt.comm;

import java.util.TimerTask;

public class PerpetualReceiveTask extends TimerTask {
	Mailbox mailbox;
	
	public PerpetualReceiveTask(Mailbox m) {
		mailbox = m;
	}
	
	public void run() {
		Messenger messenger = Messenger.GetSharedInstance();
		if (messenger.isConnected()) {
			getMessage(messenger);
		}
	}
	
	private void getMessage(Messenger messenger) {
        Received rcv = messenger.receive();
        if (rcv != null) {
            mailbox.add(rcv);
        }
    }
}
