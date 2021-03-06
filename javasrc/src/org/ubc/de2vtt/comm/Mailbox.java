package org.ubc.de2vtt.comm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.ubc.de2vtt.Disconnect;
import org.ubc.de2vtt.MainActivity;
import org.ubc.de2vtt.comm.receivers.RepeatingReceiver;
import org.ubc.de2vtt.exceptions.InvalidCommandException;
import org.ubc.de2vtt.exceptions.MailboxNotInitializedException;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class Mailbox extends AsyncTask<Void, Void, Void> {
	private static final String TAG = Mailbox.class.getSimpleName();
	private static MainActivity activityHandle;
	
	private final Map<Command, Queue<Received>> data;	
	private MainActivity activity;
	private static Mailbox sharedInstance;
	private static boolean active = false;
	private RepeatingReceiver timer;
	private ReceiveTask task;
	
	public static Mailbox getSharedInstance(MainActivity m) {
		if (sharedInstance == null) {
			if (m != null) {
				sharedInstance = new Mailbox(m);
			} 
			else if(activityHandle != null) {
				sharedInstance = new Mailbox(activityHandle);
			} else {
				Log.e(TAG, "Mailbox never setup with activity reference.");
				throw new MailboxNotInitializedException();
			}
		}
		return sharedInstance;
	}
	
	protected Mailbox(MainActivity m) {
		// Initialize map
		data = new ConcurrentHashMap<Command, Queue<Received>>();
		
		// Initialize queues
		List<Command> commands = getCommands();
		for (Command c : commands) {
			data.put(c, new ConcurrentLinkedQueue<Received>());
		}
		
		// Set reference for callbacks
		activity = m;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		if (timer != null) {
			task.cancel();
			task = null;
			timer.cancel();
			timer = null;
		}
		Log.v(TAG, "Starting new receiver.");
		task = new MailboxReceiveTask();
		timer = new RepeatingReceiver(task, 100);
		return null;
	}
	
	public void kill(Activity a) {
		kill();
		Disconnect.removeSessionData(); 
		Log.v(TAG, "Disconnect code hit");
		activityHandle = (MainActivity)a;
	}
	
	public void kill() {
		task.cancel();
		task = null;
		timer.cancel();
		timer = null;
		sharedInstance = null;
		active = false;
	}
	
	public void callbackAll() {
		List<Command> commands = getCommands();
		for (Command c : commands) {
			if (activity.acceptCommand(c)) {
				callbackSingle(c);
			}
		}
	}

	private void callbackSingle(Command cmd) {
		while (hasNext(cmd)) {
			Received r = getNext(cmd);
			if (r != null) {
				activity.onReceiveData(r);
			}
		}
	}
	
	public Received[] getArray(Command cmd) {
		Queue<Received> q = data.get(cmd);
		return (Received[]) q.toArray();
	}
	
	/**
	 * Gets the next received corresponding to the given command
	 * and null otherwise.
	 * Removes this element from the queue
	 * @param cmd
	 * @return
	 */
	public Received getNext(Command cmd) {
		Queue<Received> q = data.get(cmd);
		if (hasNext(cmd)) {
			return q.remove();
		} else {
			return null;
		}
	}
	
	public boolean hasNext(Command cmd) {
		Queue<Received> q = data.get(cmd);
		return q.peek() != null;
	}

	private List<Command> getCommands() {
		List<Command> commands = new ArrayList<Command>();
		for (int i = 0;; i++) {
			try {
				Command c = Command.Convert((byte) i);
				commands.add(c);
			} catch (InvalidCommandException e) {
				break;
			}
		}
		return commands;
	}
	
	private class MailboxReceiveTask extends ReceiveTask {
		@Override
		protected void performAction(Received rcv) {
			// only runs if rcv is not null
			add(rcv);	
			
			// Notify activity
			//MailboxCallback callBack = new MailboxCallback();
			//callBack.execute(sharedInstance);
			Log.v(TAG, "performAction");
			// TODO: remove
			
			final Received r = rcv.copy();
			
			activity.runOnUiThread(new Runnable() {
				public void run() {
					activity.onReceiveData(r);
				}
			});
		}
	}
	
	public void add(Received rcv) {
		Queue<Received> q = data.get(rcv.getCommand());
		q.add(rcv);
	}

	public Void execute() {
		if (!active) {
			active = true;
			Log.v(TAG, "Executing");
			super.execute();
		}
		return null;
	}
}
