package org.ubc.de2vtt.tabletop;

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

	private static final int width = 12;
	private int mTokPos[];
	private int startPos;
	private int tokIndex;
	private boolean mDragStarted = false;
	private TokenManager tokMan = TokenManager.getSharedInstance();
	
	public TableTopOnTouchListener()
	{
		super();
		mTokPos = new int[tokMan.size()];
		
		int id, cell;
        Token tok;
        
        for (int i = 0; i < tokMan.size(); i++) {
        	id = tokMan.getKey(i);
        	tok = tokMan.get(id);
        	
        	cell = tok.getX() + width*tok.getY();
        	
        	mTokPos[i] = cell;
		}
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
              
              int i = 0;
              while (i < mTokPos.length)
              {
            	  if (mTokPos[i] == position)
            	  {
            		  startPos = position;
            		  tokIndex = i;
            		  mDragStarted = true;
            		  break;
            	  }
            	  
            	  i++;
              }
          } else if (event.getAction() == MotionEvent.ACTION_UP) {
        	  x = (int) event.getX();
              y = (int) event.getY();
              position = gridView.pointToPosition(x, y);
              if (mDragStarted) {
            	  ImageView srcImage = (ImageView)gridView.getChildAt(startPos);
            	  ImageView destImage = (ImageView)gridView.getChildAt(position);
            	  
            	  Bitmap srcBmp = ((BitmapDrawable)srcImage.getDrawable()).getBitmap();
            	  Bitmap destBmp = ((BitmapDrawable)destImage.getDrawable()).getBitmap();
            	  
            	  srcImage.setImageBitmap(destBmp);
            	  destImage.setImageBitmap(srcBmp);
            	  
            	  ((TokenAdapter)gridView.getAdapter()).swapThumbnails(startPos,  position);
            	  
            	  mTokPos[tokIndex] = position;
            	  
            	  //Toast.makeText(this, "" + position, Toast.LENGTH_SHORT).show();
            	  Log.d("TableTopOnTouchListener", mTokPos[tokIndex] + " drag to " + position);
            	 
            	  Token tok = tokMan.get(tokMan.getKey(tokIndex));
            	  tok.move(position%width, position/width);
            	  Log.d("TableTopOnTouchListener", "Token now at (" + tok.getX() + ", " + tok.getY() + ")");
            	  
            	  int i = 0;
                  while (i < mTokPos.length)
                  {
                	  if (mTokPos[i] == startPos)
                	  {
                		  tok = tokMan.get(tokMan.getKey(i));
                		  srcImage.setImageBitmap(tok.getBitmap());
                		  break;
                	  }
                	  
                	  i++;
                  }
            	  
            	  mDragStarted = false;
              }
          }
          return true;
    }
}
