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
		int width = bmp.getWidth();
		int height = bmp.getHeight();
		
		byte widthBuf[] = ByteBuffer.allocate(4).putInt(width).array();
		byte heightBuf[] = ByteBuffer.allocate(4).putInt(height).array();
		
		int size = bmp.getRowBytes() * bmp.getHeight();
		ByteBuffer b = ByteBuffer.allocate(size);

		bmp.copyPixelsToBuffer(b);
		byte pixelBuf[] = b.array();
		
		byte ret[] = new byte[widthBuf.length + heightBuf.length + pixelBuf.length];
		int cursor = 0;
		putInt(widthBuf, ret, cursor);
		putInt(heightBuf, ret, cursor);
		System.arraycopy(pixelBuf, 0, ret, cursor, pixelBuf.length);
		
		return ret;
	}

	private void putInt(byte[] intBuf, byte[] dest, Integer cursor) {
		System.arraycopy(intBuf, 0, dest, cursor, intBuf.length);
		cursor += intBuf.length;
	}
}
