package org.ubc.de2vtt.token;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class TokenImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Token> tokens;

    public TokenImageAdapter(Context c, List<Token> l) {
        mContext = c;
        tokens = l;
    }

    public int getCount() {
        return tokens.size();
    }

    public Object getItem(int position) {
        return tokens.get(position);
    }

    public long getItemId(int position) {
        return tokens.get(position).getId();
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
        
        Token tok = tokens.get(position);
        imageView.setImageBitmap(tok.getBitmap());
        return imageView;
    }
}