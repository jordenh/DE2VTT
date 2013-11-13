package org.ubc.de2vtt.tabletop;

import org.ubc.de2vtt.R;
import org.ubc.de2vtt.token.Token;
import org.ubc.de2vtt.token.TokenManager;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class TokenAdapter extends BaseAdapter {
	private static final String TAG = TokenAdapter.class.getSimpleName();
	
	private static final int width = 12;
	private static final int height = 17;
	private static final int blackId = R.drawable.black;
	
	private Context mContext;
	private Integer[] mThumbIds = new Integer[width*height];
	private boolean[] isBlack = new boolean[width*height];
	private TokenManager tokMan = TokenManager.getSharedInstance();
	
    public TokenAdapter(Context c) {
        mContext = c;
        
        for (int i = 0; i < isBlack.length; i++) {
        	isBlack[i] = true;
        }
        
        int id, cell;
        Token tok;
        
        for (int i = 0; i < tokMan.size(); i++) {
        	id = tokMan.getKey(i);
        	tok = tokMan.get(id);
        	
        	cell = tok.getX() + width*tok.getY();
        	
        	if (isBlack[cell]) {
        		isBlack[cell] = false;
        		mThumbIds[cell] = new Integer(id);
        	}
		}
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return mThumbIds[position];
    }

    public long getItemId(int position) {
        return 0;
    }
    
    public void swapThumbnails(int pos1, int pos2)
    {
    	if ((mThumbIds.length <= pos1) || (pos1 < 0)) {
    		return;
    	} else if ((mThumbIds.length <= pos2) || (pos2 < 0)) {
    		return;
    	}
    	
    	Integer tmpInt = mThumbIds[pos1];
    	boolean tmpBool = isBlack[pos1];
    	
    	mThumbIds[pos1] = mThumbIds[pos2];
    	isBlack[pos1] = isBlack[pos2];
    	
    	mThumbIds[pos2] = tmpInt;
    	isBlack[pos2] = tmpBool;
    }	

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialise some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

       if (isBlack[position]) {
        	imageView.setImageResource(blackId);
       } else {
    	   if(mThumbIds[position] == null){
    		   Log.d(TAG,"THUMBNAILS AT " + position + " IS NULL BROTHER");
    	   }
    		
    	   Token tok = tokMan.get(mThumbIds[position].intValue());
           imageView.setImageBitmap(tok.getBitmap());
       }
       
        return imageView;
    }
}
