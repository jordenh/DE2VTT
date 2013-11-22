package org.ubc.de2vtt.comm;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

import android.graphics.Bitmap;
import android.util.Log;

import org.ubc.de2vtt.comm.sendables.Sendable;
import org.ubc.de2vtt.exceptions.IncorrectCommandDatumExpression;
import org.ubc.de2vtt.exceptions.NotImplementedException;
import org.ubc.de2vtt.token.Token;

public class Received implements Sendable {
	private static final String TAG = Received.class.getSimpleName();
	
	private byte[] data;
	private Command cmd;
	
	public Received(byte[] recvBuf) {
		if (recvBuf.length > 4) {
			Command cmd = Command.Convert(recvBuf[4]);
			
			byte [] intBuffer = new byte[4];
			System.arraycopy(recvBuf, 0, intBuffer, 0, intBuffer.length);
			ByteBuffer bb = ByteBuffer.wrap(intBuffer);
			int length = bb.getInt();
			
			byte [] args = new byte[recvBuf.length - 5];
			System.arraycopy(recvBuf, 5, args, 0, args.length);
			
			if (length != args.length) {
				//throw new ReceivedLengthMismatchException();
				Log.e(TAG, "Received length mismatch wanted: " + length + " got: " + args.length);
			}
			
			this.cmd = cmd;
			this.data = args;
		} else {
			Log.e(TAG, "Reveived buffer too small.");
		}
	}
	
	public Received(Command c, byte[] b) {
		cmd = c;
		data = b;
	}
	
	public Received copy() {
		Command c = Command.Convert(cmd.code);
		byte[] d = data.clone();
		return new Received(c, d);
	}
	
	public void set(Received other) {
		this.cmd = other.cmd;
		this.data = new byte[other.data.length];
		System.arraycopy(other.data, 0, this.data, 0, other.data.length);
	}
	
	public byte[] getData() {
		return data;
	}
	
	public Command getCommand() {
		return cmd;
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
	
	public Token DataToToken() {
		if (cmd == Command.MOVE_TOKEN || cmd == Command.SEND_TOKEN) {
			return new Token(this);
		} else {
			RuntimeException e = new IncorrectCommandDatumExpression();
			e.printStackTrace();
			throw e;
		}
	}
	
	public int DataToInt() {
		ByteBuffer wrapped = ByteBuffer.wrap(data);
		
		return wrapped.getInt();
	}
	
	public Bitmap DataToBitmap() {
		throw new NotImplementedException();
		// if this needs to be done it should be done on a background thread
	}
}
