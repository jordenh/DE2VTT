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
		byte sendPixBuf[] = new byte[pixelBuf.length / 2]; // use half the bytes in transmission.
		
		int j;
		byte red,green,blue;
		//TBD - make pixelBuff 2 byte arr.
		for(int i = 0; i < pixelBuf.length; i += 4) {
			j = i / 2;
			red = (byte) ((pixelBuf[i] & 0xF1) >> 3);
			green = (byte) ((pixelBuf[i + 1] & 0xFC) >> 2);
			blue = (byte) ((pixelBuf[i + 2] & 0xF1) >> 3);
			
			int d = (red << 11) | (green << 5) | blue;
			
			sendPixBuf[j] = (byte) (d >> 8);
			sendPixBuf[j + 1] = (byte) d;
		}
		
		byte ret[] = new byte[widthBuf.length + heightBuf.length + sendPixBuf.length];
		//Integer cursor = new Integer(0);
		//putInt(widthBuf, ret, cursor);
		//putInt(heightBuf, ret, cursor);
		System.arraycopy(widthBuf, 0, ret, 0, widthBuf.length);
		System.arraycopy(heightBuf, 0, ret, 4, heightBuf.length);
		System.arraycopy(sendPixBuf, 0, ret, 8, sendPixBuf.length);
		
		return ret;
	}
}
