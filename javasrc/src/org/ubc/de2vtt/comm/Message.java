package org.ubc.de2vtt.comm;

import java.nio.ByteBuffer;

import org.ubc.de2vtt.comm.sendables.Sendable;

public class Message {
	private static String TAG = Message.class.getSimpleName();
	
	private Command cmd; 
	private Sendable send;
	private Direction dir;
	private int delay = 0;
	
	public Message(Command cmd, Sendable send) {
		this.cmd = cmd;
		this.send = send;
		this.dir = Direction.OUT;
	}
	
	public Message(Command cmd) {
		this.cmd = cmd;
		this.send = null;
		this.dir = Direction.OUT;
	}
	
	public byte[] GetArrayToSend() {
		byte[] args = send.ToByteArray();
		int sendLen = args.length + 5;
		//int neededSize = 1024 - (sendLen % 1024);
		
		byte[] ret = new byte[sendLen];
		
//		if (ret.length % 1024 != 0) {
//			Log.e(TAG, "Incorrect buff size.");
//		}
		
		// bytes 0-3 are length of command data
		byte lenBuf[] = ByteBuffer.allocate(4).putInt(args.length).array();
		System.arraycopy(lenBuf, 0, ret, 0, lenBuf.length);
		
		// byte 4 is the command
		ret[4] = cmd.code;
		
		// bytes 5 and beyond are the command data
		System.arraycopy(args, 0, ret, 5, args.length);
		
		return ret;
	}
	
	public Direction GetDirection() {
		return dir;
	}
	
	public void setDelay(int d) {
		delay = d;
	}
	
	public int getDelay() {
		return delay;
	}
	
	public enum Direction {
		IN, OUT;
	}
}
