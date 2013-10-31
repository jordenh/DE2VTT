package org.ubc.de2vtt.comm;

public enum Command {
	CONNECT((byte)0),
	DISCONNECT((byte)1),
	SEND_MAP((byte)2),
	SEND_TOKEN((byte)3),
	GET_DM((byte)4),
	RELEASE_DM((byte)5),
	MOVE_TOKEN((byte)6),
	HANDSHAKE((byte)7);
	
	public String name;
	public byte code;
	
	private Command(byte c) {
		code = c;
	}
	
	public static Command Convert(byte b) {
		return Command.values()[b];
	}
}
