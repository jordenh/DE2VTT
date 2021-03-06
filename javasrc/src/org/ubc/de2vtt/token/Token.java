package org.ubc.de2vtt.token;

import java.nio.ByteBuffer;

import org.ubc.de2vtt.MainActivity;
import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Message;
import org.ubc.de2vtt.comm.Messenger;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.comm.sendables.SendableMove;
import org.ubc.de2vtt.exceptions.IncorrectCommandDatumException;
import org.ubc.de2vtt.exceptions.InvalidTokenPositionException;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

public class Token {
	private static final String TAG = Token.class.getSimpleName();
	private static final int TOKEN_ID_INDEX = 0;
	private static final int X_INDEX = 1;
	private static final int Y_INDEX = 3;
	private static final String NAME_PREFIX = "Token_";
	public static final int SCREEN_WIDTH = 340;
	public static final int SCREEN_HEIGHT = 240;

	static private String[] filePathColumn = { MediaStore.Images.Media.DATA };
	static private String separator = "||";
	static private int count = 0;

	private float x;
	private float y;
	private int tokenID;
	private int playerID;
	private String picturePath; // not used
	private String name;
	private Bitmap bmp;
	private ImageView mImgView;
	private Boolean local;

	public Token(Received rcv) {
		byte[] data = rcv.getData();
		tokenID = (int) data[TOKEN_ID_INDEX];

		switch (rcv.getCommand()) {
		case SEND_TOKEN:
			playerID = 0;
			x = ((float) getX(data)) / ((float) SCREEN_WIDTH);
			y = ((float) getY(data)) / ((float) SCREEN_HEIGHT);
			local = true;
			break;
		case MOVE_TOKEN:
		case OUTPUT_TOKEN_INFO:
			playerID = (int) data[1];
			x = ((float) getShort(data, 2)) / ((float) SCREEN_WIDTH);
			y = ((float) getShort(data, 4)) / ((float) SCREEN_HEIGHT);
			local = false;
			break;
		case REMOVE_TOKEN:
			playerID = (int) data[1];
			x = 0;
			y = 0;
			local = false;
			break;
		default:
			throw new IncorrectCommandDatumException();
		}
		
		if (x < 0 || y < 0) {
			throw new InvalidTokenPositionException();
		}

		name = NAME_PREFIX + count++;
		bmp = null;
		picturePath = "";
	}

	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}

	private int getX(byte[] data) {
		int x = getShort(data, X_INDEX);
		return x;
	}

	private int getY(byte[] data) {
		int y = getShort(data, Y_INDEX);
		return y;
	}

	/**
	 * 
	 * @param Array
	 *            of bytes
	 * @param index
	 *            where short begins
	 * @return
	 */
	private int getShort(byte[] arr, int index) {
		ByteBuffer wrapped = ByteBuffer.wrap(arr, index, 2); // big-endian by default
		short num = wrapped.getShort(); 
		return num;
	}

	public int getPlayerID() {
		return playerID;
	}

	public SendableMove getSendable() {
		return new SendableMove(tokenID, (int) (x * SCREEN_WIDTH), (int) (y * SCREEN_HEIGHT));
	}

	public String encode() {
		StringBuilder s = new StringBuilder();
		s.append(tokenID + separator);
		s.append(name + separator);
		s.append(x + separator);
		s.append(y + separator);
		if (picturePath != null) {
			s.append(picturePath + separator);
		}
		return s.toString();
	}

	public Token(String code) {
		String[] s = code.split("\\|\\|");
		tokenID = Integer.parseInt(s[0]);
		name = s[1];
		x = Integer.parseInt(s[2]);
		y = Integer.parseInt(s[3]);
		if (s.length >= 5) {
			picturePath = s[4];
		}
	}

	public boolean isLocal() {
		return local;
	}

	public void setupBitmap(Uri selectedImage) {
		Context cxt = MainActivity.getAppContext();
		ContentResolver res = cxt.getContentResolver();
		Cursor cursor = res.query(selectedImage, filePathColumn, null, null,
				null);

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		picturePath = cursor.getString(columnIndex);
		cursor.close();
	}

	public Bitmap getBitmap() {
		return bmp;
	}

	public void move(Received rcv) {
		byte[] data = rcv.getData();
		int rcvId = data[TOKEN_ID_INDEX];
		if (tokenID == rcvId) {
			x = getX(data);
			y = getY(data);
		}
	}

	public void move(float x, float y) {
		this.x = Math.abs(x);
		this.y = Math.abs(y);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public int getId() {
		return tokenID;
	}

	public void setImageView(ImageView v) {
		mImgView = v;
	}

	public ImageView getImageView() {
		return mImgView;
	}
}
