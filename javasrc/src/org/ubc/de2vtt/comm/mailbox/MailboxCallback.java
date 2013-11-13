package org.ubc.de2vtt.comm.mailbox;

import org.ubc.de2vtt.MainActivity;
import org.ubc.de2vtt.comm.Command;

import android.os.AsyncTask;

// For mailbox to make requests to MainActivity
public class MailboxCallback extends AsyncTask<Command, Void, Boolean> {
	MainActivity activity;
	
	public MailboxCallback(MainActivity m) {
		activity = m;
	}
	
	@Override
	protected Boolean doInBackground(Command... params) {
		// DO YOU WANT COMMAND?
		return activity.acceptCommand(params[0]);
	}
}
