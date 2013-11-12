package org.ubc.de2vtt.comm.sendables;

import java.nio.ByteBuffer;

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
		byte[] data = new byte[5];
		data[0] = (byte) tokenID;
		
		byte xBuf[] = ByteBuffer.allocate(4).putInt(x).array();
		System.arraycopy(xBuf, 0, data, 1, 2);
		
		byte yBuf[] = ByteBuffer.allocate(4).putInt(y).array();
		System.arraycopy(yBuf, 0, data, 3, 2);

		return data;
	}
}
