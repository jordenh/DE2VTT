package org.ubc.de2vtt.sendables;

public class SendableMove implements Sendable {
	private int tokenID;
	private int x,y;	
	
	public SendableMove(int tokenID, int x, int y) {
		this.tokenID = tokenID;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public byte[] ToByteArray() {
		byte[] data = new byte[3];
		data[0] = (byte) tokenID;
		data[1] = (byte) x;
		data[2] = (byte) y;
		return data;
	}
}
