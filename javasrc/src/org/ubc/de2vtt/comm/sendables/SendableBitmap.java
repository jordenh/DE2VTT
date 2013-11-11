package org.ubc.de2vtt.comm.sendables;

import java.nio.ByteBuffer;

import android.graphics.*;

public class SendableBitmap implements Sendable {
	private Bitmap bmp;
	
	public SendableBitmap(Bitmap bmp) {
		if (bmp == null) {
			throw new NullPointerException();
		}
		this.bmp = bmp;
	}
	
	@Override
	public byte[] ToByteArray() {
		int size = bmp.getRowBytes() * bmp.getHeight();
		ByteBuffer b = ByteBuffer.allocate(size);

		bmp.copyPixelsToBuffer(b);
		//ByteArrayOutputStream stream = new ByteArrayOutputStream();
		//bmp.compress(Bitmap.CompressFormat., quality, stream)
		
		return b.array();
	}
}
