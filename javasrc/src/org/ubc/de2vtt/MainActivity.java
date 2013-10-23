package org.ubc.de2vtt;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	private String[] mDrawerItems;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private static Context mContext;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mContext = getApplicationContext();
        
        mDrawerItems = getResources().getStringArray(R.array.app_drawer_array);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        //return true;
    	return false;
    }
 
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
			
		}
    }
    
    private void selectItem(int position) {
    	Fragment fragment = new PlaceholderFragment();
    	Bundle args = new Bundle();
    	//args.putInt(PlaceholderFragment., value);
    	fragment.setArguments(args);
    	
    	FragmentManager fragmentManager = getFragmentManager();
    	fragmentManager.beginTransaction()
    		.replace(R.id.content_frame, fragment)
    		.commit();
    	
    	mDrawerList.setItemChecked(position, true);
    	setTitle(mDrawerItems[position]);
    	mDrawerLayout.closeDrawer(mDrawerList);
    }
    
    @Override
    public void setTitle(CharSequence title) {
    	getActionBar().setTitle(title);
    }
    
    /**
     * Provides a static way to get the application context
     * @return application context
     */
    static public Context getAppContext() {
    	return mContext;
    }
}
