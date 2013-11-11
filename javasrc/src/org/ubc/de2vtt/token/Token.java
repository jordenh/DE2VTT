package org.ubc.de2vtt.token;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.ubc.de2vtt.MainActivity;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.comm.sendables.SendableMove;
import org.ubc.de2vtt.exceptions.BitmapNotSetupException;

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
	private static final int Y_INDEX = 2;
	
	static private String[] filePathColumn = { MediaStore.Images.Media.DATA };
	static private String separator = "||";
	
	private int x;
	private int y;
	private int id;
	private String name;
	private String picturePath;
	private Bitmap bmp;
	
	public Token(Received rcv) {
		name = " ";
		// Check command
		byte[] data = rcv.getData();
		id = (int) data[ID_INDEX];
		x = (int) data[X_INDEX];
		y = (int) data[Y_INDEX];
		bmp = null;
		picturePath = null;
	}
	
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
			return bmp;
		}
		
	}
	
	public void move(Received rcv) {
		byte[] data = rcv.getData();
		int rcvId = data[ID_INDEX];
		if (id == rcvId) {
			x = (int) data[X_INDEX];
			y = (int) data[Y_INDEX];
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
