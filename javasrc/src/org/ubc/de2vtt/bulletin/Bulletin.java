package org.ubc.de2vtt.bulletin;

import java.nio.charset.Charset;

import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.exceptions.IncorrectCommandDatumExpression;

public class Bulletin {
	private int senderID;
	private String text;
	
	public Bulletin(Received rcv) {
		if (rcv.getCommand() != Command.PASS_MSG) {
			throw new IncorrectCommandDatumExpression();
		}
		
		byte[] data = rcv.getData();
		senderID = data[0];
		text = new String(data, 1, data.length - 1, Charset.forName("US-ASCII"));
	}
	
	public String getText() {
		return text;
	}
	
	public int getSenderID() {
		return senderID;
	}
}
