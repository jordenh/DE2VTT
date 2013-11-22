package org.ubc.de2vtt.token;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private TokenManager tokMan = TokenManager.getSharedInstance();

    public ImageAdapter(Context c) {
        mContext = c;
        mThumbIds = new Integer[tokMan.size()];
        for (int i = 0; i < tokMan.size(); i++) {
        	mThumbIds[i] = Integer.valueOf(tokMan.getKey(i));
		}
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return tokMan.get(mThumbIds[position].intValue());
    }

    public long getItemId(int position) {
        return mThumbIds[position].intValue();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        
        Token tok = tokMan.get(mThumbIds[position].intValue());
        imageView.setImageBitmap(tok.getBitmap());
        return imageView;
    }

    // references to our images
    private Integer[] mThumbIds;
}