package org.ubc.de2vtt.comm;

import android.util.Log;

public class Message {
	private static String TAG = Message.class.getSimpleName();
	
	private Command cmd; 
	private Sendable send;
	private Direction dir;
	
	public Message(Command cmd, Sendable send, Direction dir) {
		this.cmd = cmd;
		this.send = send;
		this.dir = dir;
	}
	
	public Message(byte[] recvBuf) {
		this.cmd = Command.Convert(recvBuf[0]);
		byte [] args = new byte[recvBuf.length - 1];
		System.arraycopy(recvBuf, 1, args, 0, args.length);
		switch (cmd) {
		case CONNECT:
			// STUFF;
			break;
		default:
			Log.v(TAG, "Error: recieved invalid command code.");
			break;
		}
	}
	
	public byte[] GetByteArray() {
		byte[] args = send.ToByteArray();
		byte[] ret = new byte[args.length + 1];
		ret[0] = cmd.code;
		System.arraycopy(args, 0, ret, 1, args.length);
		return args;
	}
	
	public Direction GetDirection() {
		return dir;
	}
	
	public enum Direction {
		IN, OUT;
	}
}
