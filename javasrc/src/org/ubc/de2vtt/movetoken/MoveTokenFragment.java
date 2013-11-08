package org.ubc.de2vtt.movetoken;

import org.ubc.de2vtt.fragments.PlaceholderFragment;
import org.ubc.de2vtt.R;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.ListView;
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
		
		// Listener to listen for short clicks on the buttons within the grid
		// this should take the user to the tabletop view of their token
		OnItemClickListener shortListener = new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
				Toast.makeText(MoveTokenFragment.this.getActivity(), "" + position, Toast.LENGTH_SHORT).show();
				
				Fragment fragment = new PlaceholderFragment();
		    	Bundle args = new Bundle();
		    	fragment.setArguments(args);
		   
		    	FragmentManager fragmentManager = getFragmentManager();
		    	fragmentManager.beginTransaction()
		    		.replace(R.id.content_frame, fragment)
		    		.commit();
		    	
		    	DrawerLayout drawerLayout;
		    	ListView drawerList;
		    	String[] drawerItems;
		    	
		    	drawerItems = getResources().getStringArray(R.array.app_drawer_array);
		        drawerLayout = (DrawerLayout)mActivity.findViewById(R.id.linear_layout);
		        drawerList = (ListView)mActivity.findViewById(R.id.left_drawer);
		    	
		    	drawerList.setItemChecked(0, true);
		    	mActivity.setTitle(drawerItems[0]);
		    	drawerLayout.closeDrawer(drawerList);
			}} ;
			
		// Listener to listen for long clicks on the buttons within the grid
		// this should take the user to the token settings activity
		OnItemLongClickListener longListener = new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long id) {
				
				Toast.makeText(MoveTokenFragment.this.getActivity(), "" + position, Toast.LENGTH_SHORT).show();
				
				Intent myIntent = new Intent(mActivity.getApplicationContext(), TokenActivity.class);
				startActivity(myIntent);
				
				return false;
			}};
			
		mGridView.setOnItemClickListener(shortListener);
		mGridView.setOnItemLongClickListener(longListener);
	}
}

