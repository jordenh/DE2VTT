package org.ubc.de2vtt.comm;

import android.util.Log;

public enum Command {	
	CONNECT((byte)0),
	DISCONNECT((byte)1),
	SEND_MAP((byte)2),
	SEND_TOKEN((byte)3),
	GET_DM((byte)4),
	RELEASE_DM((byte)5),
	MOVE_TOKEN((byte)6),
	HANDSHAKE((byte)7),
	PASS_MSG((byte)8),
	UPDATE_ALIAS((byte)9);
	
	public byte code;
	
	private Command(byte c) {
		code = c;
	}

	public static Command Convert(byte b) {
		
		//return Command.values()[b];
		switch (b) {
		case (byte)0:
			return CONNECT;
		case (byte)1:
			return DISCONNECT;
		case (byte)2:
			return SEND_MAP;
		case (byte)3:
			return SEND_TOKEN;
		case (byte)4:
			return GET_DM;
		case (byte)5:
			return RELEASE_DM;
		case (byte)6:
			return MOVE_TOKEN;
		case (byte)7:
			return HANDSHAKE;
		case (byte)8:
			return PASS_MSG;
		case (byte) 9:
			return UPDATE_ALIAS;
		default:
			Log.v("Command", "Attempt to convert invalid command.");
			return HANDSHAKE;
		}
	}
}
