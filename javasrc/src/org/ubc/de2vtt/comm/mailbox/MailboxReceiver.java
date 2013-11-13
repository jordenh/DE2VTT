package org.ubc.de2vtt.comm.mailbox;

import org.ubc.de2vtt.comm.ReceiveTask;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.comm.receivers.RepeatingReceiver;

import android.os.AsyncTask;

public class MailboxReceiver extends AsyncTask<Void, Void, Void> {
	Mailbox m;
	
	public MailboxReceiver(Mailbox m) {
		this.m = m;
	}
	
	
	@Override
	protected Void doInBackground(Void... arg0) {
		new RepeatingReceiver(new MBReceiveTask(), 500);
		// need to check that this keeps going
		return null;
	}

	private class MBReceiveTask extends ReceiveTask {
		@Override
		protected void performAction(Received rcv) {
			if (rcv != null) {
				m.add(rcv);
				// notify mailbox?
			}
		}

		@Override
		protected void onFinishRun() {
		}
	}
}
