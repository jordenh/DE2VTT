package org.ubc.de2vtt.comm.sendables;

public class SendableString implements Sendable {
	private String str;

	public SendableString(String str) {
		this.str = str;
	}
	
	public String GetString() {
		return str;
	}
	
	public void SetString(String str) {
		this.str = str;
	}
	
	@Override
	public byte[] ToByteArray() {
		return str.getBytes();
	}
}
