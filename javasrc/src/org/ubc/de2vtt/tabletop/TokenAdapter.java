package org.ubc.de2vtt.tabletop;

import java.util.ArrayList;
import java.util.List;

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
	private ArrayList<Integer> mThumbIds = new ArrayList<Integer>();
	private ArrayList<Boolean> isBlack = new ArrayList<Boolean>();
	private TokenManager tokMan = TokenManager.getSharedInstance();
	
    public TokenAdapter(Context c) {
        mContext = c;
        
        Integer id;
        Token tok;
        
        for (int i = 0; i < tokMan.sizeLocal(); i++) {
        	id = tokMan.getLocalKey(i);
        	tok = tokMan.getLocal(id);
        	
    		isBlack.add(Boolean.valueOf(true));
    		mThumbIds.add(Integer.valueOf(id));
		}
    }

    public int getCount() {
        return mThumbIds.size();
    }

    public Object getItem(int position) {
        return mThumbIds.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }
    
    public void swapThumbnails(int pos1, int pos2)
    {
    	if ((mThumbIds.size() <= pos1) || (pos1 < 0)) {
    		return;
    	} else if ((mThumbIds.size() <= pos2) || (pos2 < 0)) {
    		return;
    	}
    	
    	Integer tmpInt = mThumbIds.get(pos1);
    	Boolean tmpBool = isBlack.get(pos1);
    	
    	mThumbIds.set(pos1, mThumbIds.get(pos2));
    	isBlack.set(pos1, isBlack.get(pos2));
    	
    	mThumbIds.set(pos2, tmpInt);
    	isBlack.set(pos2, tmpBool);
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

       if (isBlack.get(position)) {
        	imageView.setImageResource(blackId);
       } else {
    	   if(mThumbIds.get(position) == null){
    		   Log.d(TAG,"THUMBNAILS AT " + position + " IS NULL BROTHER");
    	   }
    		
    	   Token tok = tokMan.getLocal(mThumbIds.get(position).intValue());
           imageView.setImageBitmap(tok.getBitmap());
       }
       
        return imageView;
    }
}
