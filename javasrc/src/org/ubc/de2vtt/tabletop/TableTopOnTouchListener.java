package org.ubc.de2vtt.tabletop;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

public class TableTopOnTouchListener implements View.OnTouchListener {

	private static int mTokPos;
	private boolean mDragStarted = false;
	
	public TableTopOnTouchListener(int tokLocation)
	{
		super();
		mTokPos = tokLocation;
	}
	
    // This is the method that the system calls when it dispatches a drag event to the
	// listener.
	  @Override
      public boolean onTouch(View v, MotionEvent event) {
          GridView gridView = (GridView)v;
          int x, y, position;
          
          if (event.getAction() == MotionEvent.ACTION_DOWN) {
              x = (int) event.getX();
              y = (int) event.getY();
              position = gridView.pointToPosition(x, y);
              
              if (position == mTokPos)
              {
            	  mDragStarted = true;
            	  //Log.d("TableTop", "Drag Started on # " + position);
              }
          } else if (event.getAction() == MotionEvent.ACTION_UP) {
        	  x = (int) event.getX();
              y = (int) event.getY();
              position = gridView.pointToPosition(x, y);
              if (mDragStarted) {
            	  ImageView srcImage = (ImageView)gridView.getChildAt(mTokPos);
            	  ImageView destImage = (ImageView)gridView.getChildAt(position);
            	  
            	  Bitmap srcBmp = ((BitmapDrawable)srcImage.getDrawable()).getBitmap();
            	  Bitmap destBmp = ((BitmapDrawable)destImage.getDrawable()).getBitmap();
            	  
            	  srcImage.setImageBitmap(destBmp);
            	  destImage.setImageBitmap(srcBmp);
            	  
            	  ((TokenAdapter)gridView.getAdapter()).swapThumbnails(mTokPos,  position);
            	  
            	  mTokPos = position;
            	  
            	  //Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
            	  Log.d("TableTop", mTokPos + " drag to " + position);
            	  mDragStarted = false;
              }
          }
          return true;
    }
}
