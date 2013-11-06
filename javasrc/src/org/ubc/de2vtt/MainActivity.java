package org.ubc.de2vtt;

import org.ubc.de2vtt.comm.ConnectionFragment;
import org.ubc.de2vtt.tabletop.TableTopFragment;

import android.os.Bundle;
import android.os.StrictMode;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends Activity {

	private String[] mDrawerItems;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private static Context mContext;
	private ActionBarDrawerToggle mDrawerToggle;
	private String mTitle;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle = "WING";
        
	    // This call will result in better error messages if you
		// try to do things in the wrong thread.
		// From tutorial 2
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
			.detectDiskReads().detectDiskWrites().detectNetwork()
			.penaltyLog().build());
        
        setContentView(R.layout.activity_main);
        
        mContext = getApplicationContext();
        
        mDrawerItems = getResources().getStringArray(R.array.app_drawer_array);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        
        setupDrawerToggle();

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

    }

	private void setupDrawerToggle() {
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(R.string.app_name);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main, menu);
        //return true;
    	return false;
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
          return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
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
    	fragment.setArguments(args);
    	
    	switch (position) {
    	case 0:
    		fragment = new TableTopFragment();
    	case 3:
    		fragment = new ConnectionFragment();
    		break;
    	}
   
    	
    	FragmentManager fragmentManager = getFragmentManager();
    	fragmentManager.beginTransaction()
    		.replace(R.id.content_frame, fragment)
    		.commit();
    	
    	mDrawerList.setItemChecked(position, true);
    	setTitle(mDrawerItems[position]);
    	mTitle = mDrawerItems[position];
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
