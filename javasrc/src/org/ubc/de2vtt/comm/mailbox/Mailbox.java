package org.ubc.de2vtt.comm.mailbox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.ubc.de2vtt.MainActivity;
import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.ReceiveTask;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.comm.receivers.RepeatingReceiver;
import org.ubc.de2vtt.exceptions.InvalidCommandException;

import android.os.AsyncTask;

public class Mailbox extends AsyncTask<Void, Void, Void> {
	private final Map<Command, Queue<Received>> data;	
	private MainActivity activity;
	static boolean waiting = false;
	
	public Mailbox(MainActivity m) {
		// Initialize map
		data = new ConcurrentHashMap<Command, Queue<Received>>();
		
		// Initialize queues
		Command[] commands = getCommands();
		for (int i = 0; i < commands.length; i++) {
			data.put(commands[i], new ConcurrentLinkedQueue<Received>());
		}
		
		// Set reference for callbacks
		activity = m;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		while (true) {
			waiting = true;
			RepeatingReceiver r = new RepeatingReceiver(new MailboxReceiveTask(), 500);
			while(waiting) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					Thread.interrupted();
				}
			}
			r.cancel();	
		}
	}
	
	private void callback() {
		Command[] commands = getCommands();
		for (int i = 0; i < commands.length; i++) {
			Received r = getNext(commands[i]);
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

	private Command[] getCommands() {
		ArrayList<Command> commands = new ArrayList<Command>();
		for (int i = 0;; i++) {
			try {
				Command c = Command.Convert((byte) i);
				commands.add(c);
			} catch (InvalidCommandException e) {
				break;
			}
		}
		return (Command[]) commands.toArray();
	}
	
	private class MailboxReceiveTask extends ReceiveTask {
		@Override
		protected void performAction(Received rcv) {
			add(rcv);	
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
}
