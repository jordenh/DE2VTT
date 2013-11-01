package org.ubc.de2vtt.comm;

import java.io.UnsupportedEncodingException;

import org.ubc.de2vtt.sendables.Sendable;

public class Received implements Sendable {
	private byte[] data;
	private Command cmd;
	
	public Received(Command c, byte[] b) {
		cmd = c;
		data = b;
	}
	
	@Override
	public byte[] ToByteArray() {
		return data;
	}
	
	public String DataToString() {
		String msg;
		try {
			msg = new String(data, 0, data.length, "US-ASCII");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			msg = "Unsuppored Encoding";
		}
		return msg;
	}
	
	// Something to use the request to decide to do something with the data
}
