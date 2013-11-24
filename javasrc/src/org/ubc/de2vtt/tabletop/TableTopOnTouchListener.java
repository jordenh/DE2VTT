package org.ubc.de2vtt.tabletop;

import java.util.List;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Message;
import org.ubc.de2vtt.comm.Messenger;
import org.ubc.de2vtt.comm.sendables.SendableMove;
import org.ubc.de2vtt.token.Token;
import org.ubc.de2vtt.token.TokenManager;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

public class TableTopOnTouchListener implements View.OnTouchListener {
	private static final String TAG = TableTopOnTouchListener.class.getSimpleName();
	private static final int MAP_SCALE = 20;

	private static final int width = 12;
	private static final int height = 17;
	private static final int blackId = R.drawable.black;
	
	private int mTokPos[];
	private int mStartPos;
	private int mTokIndex;
	private boolean mDragStarted = false;
	private TokenManager mTokMan = TokenManager.getSharedInstance();

	public TableTopOnTouchListener()
	{
		super();
		mTokPos = new int[mTokMan.sizeLocal()];

		int cell;
		int i = 0;
		
		List<Token> l = mTokMan.getList();
		
		for (Token t : l) {
			cell = t.getX() + width * t.getY();
			mTokPos[i++] = cell;
		}
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		GridView gridView = (GridView)v;

		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			onActionDown(gridView, event);
		} else if (event.getAction() == MotionEvent.ACTION_UP) {
			if (mDragStarted){
				onActionUp(gridView, event);
			}
		}
		return true;
	}
	
	private void onActionDown(GridView gridView, MotionEvent event) {
		int x, y, pos, res;
		
		// find out what grid was selected as the starting point
		x = (int)event.getX();
		y = (int)event.getY();
		pos = gridView.pointToPosition(x, y);

		// see if the grid selected matches a grid that has a token in it
		res = findFirstMatch(pos);
		
		// if the grid has a token in it than a drag has been started 
		if ((res != -1) && (res < width*height)) {
			mStartPos = pos;
			mTokIndex = res;
			mDragStarted = true;
		}
	}
	
	private void onActionUp(GridView gridView, MotionEvent event) {
		int x, y, pos, res;
		
		// find out what grid was selected as the ending point
		x = (int)event.getX();
		y = (int)event.getY();
		pos = gridView.pointToPosition(x, y);
		
		// if the token isn't moving anywhere
		if (mStartPos == pos) {
			mDragStarted = false;
			return;
		} else if (pos == -1) {
			mDragStarted = false;
			return;
		}
		
		// get the two ImageViews at the start and end of the drag
		ImageView srcImage = (ImageView)gridView.getChildAt(mStartPos);
		ImageView destImage = (ImageView)gridView.getChildAt(pos);
	
		// grab the two bmps from those ImageView
		Bitmap srcBmp = ((BitmapDrawable)srcImage.getDrawable()).getBitmap();
		Bitmap destBmp = ((BitmapDrawable)destImage.getDrawable()).getBitmap();
	
		// determine if there is a token at the destination
		setSrcDestination(pos, srcImage, destImage, srcBmp, destBmp);
	
		//((TokenAdapter)gridView.getAdapter()).swapThumbnails(mStartPos,  pos);
	
		// update the token location in the mTokPos array
		mTokPos[mTokIndex] = pos;

		Log.d(TAG, mTokPos[mTokIndex] + " drag to " + pos);
	
		// update the token itself
		Token tok1 = mTokMan.getLocal(mTokMan.getLocalKey(mTokIndex)); // TODO: broken now, but apparently going away
		tok1.move(pos % width, pos / width);
		
		Log.d(TAG, "Token now at (" + tok1.getX() + ", " + tok1.getY() + ")");
	
		// if there is another token at the same start position than show it
		res = findFirstMatch(mStartPos);
		
		if (res != -1)
		{
				Token tok2 = mTokMan.getLocal(mTokMan.getLocalKey(res));
				srcImage.setImageBitmap(tok2.getBitmap());
		}
		
		SendableMove mv = new SendableMove(tok1.getId(), tok1.getX() * MAP_SCALE, tok1.getY() * MAP_SCALE);
		Message msg = new Message(Command.MOVE_TOKEN, mv);
		Messenger messenger = Messenger.GetSharedInstance();
		messenger.send(msg);
		
		mDragStarted = false;
	}

	private void setSrcDestination(int pos, ImageView srcImage,
			ImageView destImage, Bitmap srcBmp, Bitmap destBmp) {
		int res;
		res = findFirstMatch(pos);
		
		if (res == -1) {
			// there is no token at the end point: swap the bmps
			srcImage.setImageBitmap(destBmp);
			destImage.setImageBitmap(srcBmp);
		} else {
			// there is a token at the end point;
			// set the ImageView at the start point to black
			srcImage.setImageResource(blackId);
			// set the ImageView at the end to the token
			destImage.setImageBitmap(srcBmp);
		}
	}
	
	private int findFirstMatch(int pos) {
		int i = 0;
		
		// iterate through all elements of mTokPos
		while (i < mTokPos.length)
		{
			// if you find a match then return the index
			if (mTokPos[i] == pos)
			{
				return i;
			}

			i++;
		}
		
		// if there are no matches than return -1
		return -1;
	}
}
