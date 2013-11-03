package org.ubc.de2vtt.comm;

import java.nio.ByteBuffer;

import org.ubc.de2vtt.MyApplication;
import org.ubc.de2vtt.sendables.Sendable;

public class Message {
	private static String TAG = Message.class.getSimpleName();
	
	private Command cmd; 
	private Sendable send;
	private Direction dir;
	
	public Message(Command cmd, Sendable send) {
		this.cmd = cmd;
		this.send = send;
		this.dir = Direction.OUT;
	}
	
	public static Received GetReceived(byte[] recvBuf) {
		MyApplication.id = recvBuf[0];
		
		Command cmd = Command.Convert(recvBuf[5]);
		
		byte [] args = new byte[recvBuf.length - 6];
		System.arraycopy(recvBuf, 6, args, 0, args.length);
		
		return new Received(cmd, args);
	}
	
	public byte[] GetArrayToSend() {
		byte[] args = send.ToByteArray();
		byte[] ret = new byte[args.length + 6];
		
		// byte 0 is id
		ret[0] = MyApplication.id;
		
		// bytes 1-4 are length of command data
		byte lenBuf[] = ByteBuffer.allocate(4).putInt(args.length).array();
		System.arraycopy(lenBuf, 0, ret, 1, lenBuf.length);
		
		// byte 5 is the command
		ret[5] = cmd.code;
		
		// bytes 6 and beyond are the command data
		System.arraycopy(args, 0, ret, 6, args.length);
		
		return ret;
	}
	
	public Direction GetDirection() {
		return dir;
	}
	
	public enum Direction {
		IN, OUT;
	}
}
