package org.ubc.de2vtt.comm.mailbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.ubc.de2vtt.MainActivity;
import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.ReceiveTask;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.comm.receivers.RepeatingReceiver;
import org.ubc.de2vtt.exceptions.InvalidCommandException;
import org.ubc.de2vtt.exceptions.MailboxNotInitializeException;

import android.os.AsyncTask;
import android.util.Log;

public class Mailbox extends AsyncTask<Void, Void, Void> {
	private static final String TAG = Mailbox.class.getSimpleName();
	
	private final Map<Command, Queue<Received>> data;	
	private MainActivity activity;
	static boolean waiting = false;
	private static Mailbox sharedInstance;
	private static boolean active = false;
	
	public static Mailbox getSharedInstance(MainActivity m) {
		if (sharedInstance == null) {
			if (m != null) {
				sharedInstance = new Mailbox(m);
			} else {
				Log.e(TAG, "Mailbox never setup with activity reference.");
				throw new MailboxNotInitializeException();
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
		while (true) {
			waiting = true;
			RepeatingReceiver r = new RepeatingReceiver(new MailboxReceiveTask(), 100);
			while(waiting) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					Thread.interrupted();
				}
			}
			//r.cancel();	
		}
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
			Log.v(TAG, "Received a message.");
			add(rcv);	
			
			// Notify activity
			MailboxCallback callBack = new MailboxCallback();
			callBack.execute(sharedInstance);
		}

		@Override
		protected void onFinishRun() {
			waiting = false;
		}
	}
	
	public void add(Received rcv) {
		Queue<Received> q = data.get(rcv.getCommand());
		q.add(rcv);
	}

	public Void execute() {
		if (!active) {
			active = true;
			super.execute();
		}
		return null;
	}
}
