package org.ubc.de2vtt.comm.mailbox;

import android.os.AsyncTask;

// For mailbox to make requests to MainActivity
public class MailboxCallback extends AsyncTask<Mailbox, Void, Void> {
	
	@Override
	protected Void doInBackground(Mailbox... params) {
		// HERE HAVE SOME DATA
		(params[0]).callbackAll();
		return null;
	}
}
