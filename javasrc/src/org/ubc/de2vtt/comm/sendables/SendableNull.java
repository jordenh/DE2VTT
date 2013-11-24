package org.ubc.de2vtt.comm.sendables;

public class SendableNull implements Sendable {
	// This is a sendable object for commands that do not require arguments
	
	private static SendableNull instance;
	
	protected SendableNull() {	
	}
	
	public static SendableNull GetSharedInstance() {
		if (instance == null) {
			instance = new SendableNull();
		}
		return instance;
	}
	
	@Override
	public byte[] ToByteArray() {
		return new byte[0];
	}
}
