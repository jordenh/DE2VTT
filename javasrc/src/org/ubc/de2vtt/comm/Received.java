package org.ubc.de2vtt.comm;

import java.io.UnsupportedEncodingException;
import java.security.InvalidParameterException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.ubc.de2vtt.sendables.Sendable;

public class Received implements Sendable {
	private static final String TAG = Received.class.getSimpleName();
	
	private byte[] data;
	private Command cmd;
	
	public Received(byte[] recvBuf) {
		Command cmd = Command.Convert(recvBuf[4]);
		
		byte [] args = new byte[recvBuf.length - 5];
		System.arraycopy(recvBuf, 5, args, 0, args.length);
		
		this.cmd = cmd;
		this.data = args;
	}
	
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
	
	public Bitmap DataToBitmap() {
		if (cmd == Command.SEND_MAP || cmd == Command.SEND_TOKEN) {
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			if (bmp == null) {
				throw new NullPointerException("Unable to decode bmp with length " + data.length);
			}
			
			return bmp;
		} else {
			throw new InvalidParameterException("Attempt to convert non-bmp data to bmp.");
		}
	}
}
