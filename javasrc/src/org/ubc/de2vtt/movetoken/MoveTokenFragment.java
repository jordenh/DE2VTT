package org.ubc.de2vtt.movetoken;

import org.ubc.de2vtt.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class MoveTokenFragment extends Fragment {
	private View mParentView;
	private Activity mActivity;

    private GridView mGridView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mParentView = inflater.inflate(R.layout.fragment_movetoken,  container, false);
		mActivity = this.getActivity();
		
		mGridView = (GridView) mParentView.findViewById(R.id.gridview);
	    mGridView.setAdapter(new ImageAdapter(this.mActivity));
		
	    setupOnClickListeners();
		
		return mParentView;
	}

	private void setupOnClickListeners() {
		OnItemClickListener listener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				Toast.makeText(MoveTokenFragment.this.getActivity(), "" + position, Toast.LENGTH_SHORT).show();
				
			}} ;
			
		mGridView.setOnItemClickListener(listener );
	}
}

