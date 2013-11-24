package org.ubc.de2vtt.comm.sendables;

public class SendableRemoveToken implements Sendable {
	private int tokenID;
	
	public SendableRemoveToken(int tokenID) {
		this.tokenID = tokenID;
	}
	
	@Override
	public byte[] ToByteArray() {
		byte[] data = new byte[1];
		data[0] = (byte) tokenID;

		return data;
	}
}
