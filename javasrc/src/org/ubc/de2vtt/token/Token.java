package org.ubc.de2vtt.token;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.ubc.de2vtt.MainActivity;
import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.comm.sendables.SendableMove;
import org.ubc.de2vtt.exceptions.BitmapNotSetupException;
import org.ubc.de2vtt.exceptions.IncorrectCommandDatumExpression;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

public class Token {
	private static final String TAG = Token.class.getSimpleName();
	private static final int ID_INDEX = 0;
	private static final int X_INDEX = 1;
	private static final int Y_INDEX = 3;
	private static final String NAME_PREFIX = "Token_";
	
	static private String[] filePathColumn = { MediaStore.Images.Media.DATA };
	static private String separator = "||";
	static private int count = 0;
	
	private int x;
	private int y;
	private int id;
	private String name;
	private String picturePath;
	private Bitmap bmp;
	
	public Token(Received rcv) {
		// Check command
		if (rcv.getCommand() != Command.SEND_TOKEN) {
			throw new IncorrectCommandDatumExpression();
		}
		
		byte[] data = rcv.getData();
		id = (int) data[ID_INDEX];
		x = getX(data);
		y = getY(data);
		bmp = null;
		picturePath = null;
		name = NAME_PREFIX + count++;
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
	
	private int getShort(byte[] arr, int index) {
		return (int) (arr[index] << 8 | arr[index + 1]);
	}
	
//	public Token(String tokName, Bitmap bitmap)
//	{
//		id = count++;
//		name = NAME_PREFIX + id;
//		x = 0;
//		y = 0;
//		bmp = bitmap;
//		picturePath = null;
//	}
	
	public SendableMove getSendable() {
		return new SendableMove(id, x, y);
	}
	
	public String encode() {
		StringBuilder s = new StringBuilder();
		s.append(id + separator);
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
		id = Integer.parseInt(s[0]);
		name = s[1];
		x = Integer.parseInt(s[2]);
		y = Integer.parseInt(s[3]);
		if (s.length >= 5) {
			picturePath = s[4];
		}
	}
	
	public void setupBitmap(Uri selectedImage) {
		Context cxt = MainActivity.getAppContext();
		ContentResolver res = cxt.getContentResolver();
		Cursor cursor = res.query(selectedImage, filePathColumn, null, null, null);

		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		picturePath = cursor.getString(columnIndex);
		cursor.close();
	}
	
	public Bitmap getBitmap() {
		if (bmp != null) {
			return bmp;
		}
		
		if (picturePath == null) {
			Log.e(TAG, "Can't get a bitmap before it is setup.");
			throw new BitmapNotSetupException();
		} else {
			BitmapDecoder dec = new BitmapDecoder();
			dec.execute(picturePath);
			try {
				bmp = dec.get(3000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				Log.e(TAG, "Bitmap decode interrupted out.");
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (TimeoutException e) {
				Log.e(TAG, "Bitmap decode timed out.");
				e.printStackTrace();
			} 
			return bmp;
		}
	}
	
	private class BitmapDecoder extends AsyncTask<String, Integer, Bitmap> {

		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap bmp = BitmapFactory.decodeFile(params[0]);
			bmp = Bitmap.createScaledBitmap(bmp, 500, 500, false);
			return bmp;
		}
		
	}
	
	public void move(Received rcv) {
		byte[] data = rcv.getData();
		int rcvId = data[ID_INDEX];
		if (id == rcvId) {
			x = getX(data);
			y = getY(data);
		}
	}
	
	public void move(int x, int y) {
		this.x = x;
		this.y = y;
		// send?
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getId() {
		return id;
	}
}
