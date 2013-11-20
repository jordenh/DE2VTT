package org.ubc.de2vtt.fragments;

import java.util.LinkedList;
import java.util.List;

import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Received;

import android.app.Fragment;

public abstract class WINGFragment extends Fragment {
	private Command[] accept;
	
	public abstract boolean passReceived(Received r);
	
	public Command[] commandsAccepted() {
		if (accept != null) {
			return accept.clone();
		} else {
			return new Command[0];
		}
	}
	
	/**
	 * This method must be called before commandAccepted
	 */
	protected List<Command> setAcceptedCommands(Command... cmds) {
		List<Command> l = new LinkedList<Command>();
		for (Command c : cmds) {
			l.add(c);
		}
		return l;
	}
}
