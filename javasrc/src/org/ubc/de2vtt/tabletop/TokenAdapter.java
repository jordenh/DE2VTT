package org.ubc.de2vtt.tabletop;


import org.ubc.de2vtt.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class TokenAdapter extends BaseAdapter {
    private Context mContext;

    public TokenAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
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
    	
    	Integer tmp = mThumbIds[pos1];
    	
    	mThumbIds[pos1] = mThumbIds[pos2];
    	mThumbIds[pos2] = tmp;
    }	

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        
        return imageView;
    }
    
    private static Integer[] mThumbIds = {
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.earth, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
            R.drawable.black, R.drawable.black,
    };
}
