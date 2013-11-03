package org.ubc.de2vtt.sendables;

import java.nio.ByteBuffer;

import android.graphics.*;

public class SendableBitmap implements Sendable {
	private Bitmap bmp;
	
	public SendableBitmap(Bitmap bmp) {
		this.bmp = bmp;
	}
	
	@Override
	public byte[] ToByteArray() {
		int size = bmp.getRowBytes() * bmp.getHeight();
		ByteBuffer b = ByteBuffer.allocate(size);

		bmp.copyPixelsToBuffer(b);
		
		return b.array();
	}
}
