package org.ubc.de2vtt;

import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Messenger;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.comm.mailbox.Mailbox;
import org.ubc.de2vtt.fragments.ConnectionFragment;
import org.ubc.de2vtt.fragments.GameConfigFragment;
import org.ubc.de2vtt.fragments.ManageTokenFragment;
import org.ubc.de2vtt.fragments.PassMessageFragment;
import org.ubc.de2vtt.fragments.PlaceholderFragment;
import org.ubc.de2vtt.fragments.SendImageFragment;
import org.ubc.de2vtt.fragments.TableTopFragment;
import org.ubc.de2vtt.fragments.WINGFragment;
import org.ubc.de2vtt.token.Token;
import org.ubc.de2vtt.token.TokenManager;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private String[] mDrawerItems;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private static Context mContext;
	private ActionBarDrawerToggle mDrawerToggle;
	private String mTitle;
	private WINGFragment activeFragment;

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
		mDrawerLayout = (DrawerLayout) findViewById(R.id.linear_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mDrawerItems));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		setupDrawerToggle();

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		Mailbox.getSharedInstance(this);
		
		// Attempt to connect
		Messenger.GetSharedInstance();
	}

	private void setupDrawerToggle() {
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(R.string.app_name);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
				// close keyboard
				InputMethodManager inputManager = (InputMethodManager)            
					  getSystemService(Context.INPUT_METHOD_SERVICE); 
					    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),      
					    InputMethodManager.HIDE_NOT_ALWAYS);
			}
		};
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.main, menu);
		// return true;
		return false;
	}

	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Intent current = getIntent();
        Bundle b = current.getExtras();
        if (b != null) {
        	int fragment = b.getInt("fragment_sel");
            Toast.makeText(MainActivity.this, "" + fragment, Toast.LENGTH_SHORT).show();
            switchFragment(fragment);
        } else {
        	switchFragment(0);
        }
        	

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

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switchFragment(position);
		}
    }
  
	public void switchFragment(int position) {
		Log.v(TAG, "Switching fragments.");
		WINGFragment fragment = new PlaceholderFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);

		switch (position) {
			case 0:
				fragment = new TableTopFragment();
				break;
			case 1:
				fragment = new ManageTokenFragment();
				break;
			case 2:
				fragment = new GameConfigFragment();
				break;
			case 3:
				fragment = new SendImageFragment();
				break;
			case 4:
				fragment = new PassMessageFragment();
				break;
			case 5:
	    		fragment = new ConnectionFragment();
	    		break;
		}
		
		activeFragment = fragment;

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
				.replace(R.id.content_frame, fragment).commit();

		mDrawerList.setItemChecked(position, true);
		setTitle(mDrawerItems[position]);
		mTitle = mDrawerItems[position];
		mDrawerLayout.closeDrawer(mDrawerList);
	}
	
	public synchronized void onReceiveData(Received rcv) {
		Log.v(TAG, "Received data.");
		Token t;
		TokenManager tm;
		
		switch (rcv.getCommand()) {
			case MOVE_TOKEN:
				Log.v(TAG, "Moving token.");
				tm = TokenManager.getSharedInstance();
				t = new Token(rcv);
				tm.move(t);
				// signal tabletop fragment if it is active?
				break;
			case SEND_TOKEN:
				Log.v(TAG, "Receiving token.");
				tm = TokenManager.getSharedInstance();
				t = new Token(rcv);
				tm.add(t);		
				break;
			default:
				// signal active fragment
				if (!activeFragment.passReceived(rcv)) {
					Log.e(TAG, "Failed to pass message to fragment.");
				}
		}
	}
	
	public boolean acceptCommand(Command cmd) {
		// should be based on active fragment
		return false;
	}

	@Override
	public void setTitle(CharSequence title) {
		getActionBar().setTitle(title);
	}

    static public Context getAppContext() {
    	return mContext;
    }
}
