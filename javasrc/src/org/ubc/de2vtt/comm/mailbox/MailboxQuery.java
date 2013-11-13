package org.ubc.de2vtt.comm.mailbox;

import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Received;

import android.os.AsyncTask;

// For the main activity to make requests to Mailbox
public class MailboxQuery extends AsyncTask<Command, Void, Received[]> {
	Mailbox mail;
	
	public MailboxQuery(Mailbox m) {
		mail = m;
	}
	
	@Override
	protected Received[] doInBackground(Command... params) {
		// I WANT COMMAND
		if (mail.hasNext(params[0])) {
			// HERE YOU GO
			return mail.getArray(params[0]);
		}
		// NO COMMAND
		return null;
	}

}
