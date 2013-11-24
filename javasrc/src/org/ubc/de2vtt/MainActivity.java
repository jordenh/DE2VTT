package org.ubc.de2vtt;

import org.ubc.de2vtt.bulletin.Bulletin;
import org.ubc.de2vtt.bulletin.BulletinManager;
import org.ubc.de2vtt.comm.Command;
import org.ubc.de2vtt.comm.Mailbox;
import org.ubc.de2vtt.comm.Message;
import org.ubc.de2vtt.comm.Messenger;
import org.ubc.de2vtt.comm.Received;
import org.ubc.de2vtt.comm.sendables.SendableNull;
import org.ubc.de2vtt.comm.sendables.SendableString;
import org.ubc.de2vtt.fragments.*;
import org.ubc.de2vtt.fragments.WINGFragment.FragDrawerId;
import org.ubc.de2vtt.notifications.notifications;
import org.ubc.de2vtt.token.Token;
import org.ubc.de2vtt.token.TokenManager;
import org.ubc.de2vtt.users.UserManager;

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
	private static Context mContext;
	
	private String[] mDrawerItems;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
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

		// For later static access
		mContext = getApplicationContext();

		setupDrawerList();
		setupDrawerToggle();

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		
		Mailbox.getSharedInstance(this);
		
		// Attempt to connect
		Messenger messenger = Messenger.GetSharedInstance();
		
		Message msg = new Message(Command.GET_DM_ID, SendableNull.GetSharedInstance());
        
		messenger.send(msg);
	}

	private void setupDrawerList() {
		mDrawerItems = getResources().getStringArray(R.array.app_drawer_array);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.linear_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mDrawerItems));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
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
        
        Intent intent = getIntent();
        try{
            String action = intent.getAction().toUpperCase();
            Log.v(TAG, "OnCreate: intent action" + action);

            if(action != null){
                if(action.equalsIgnoreCase(mContext.getResources().getString(R.string.in_msg_notification))){
                	notifications.removeNotify(mContext, 
                			mContext.getResources().getString(R.string.in_msg_notification));
                	switchFragment(WINGFragment.FragDrawerId.BulletinFragDrawerId.ordinal()); // hard coded to switch to bulletin board!
                }
            }else{
                Log.v(TAG, "Oncreate: Intent was null");
            }
        }catch(Exception e){
            Log.e(TAG, "Problem consuming action from intent", e);              
        }
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

		switch (WINGFragment.FragDrawerId.values()[position]) {
			case TableTopFragDrawerId:
				fragment = new TableTopFragment();
				break;
			case ManageTokenFragDrawerId:
				fragment = new ManageTokenFragment();
				break;
			case GameConfigFragDrawerId:
				fragment = new GameConfigFragment();
				break;
			case SendImageFragDrawerId:
				fragment = new SendImageFragment();
				break;
			case PassMessageFragDrawerId:
				fragment = new PassMessageFragment();
				break;
			case BulletinFragDrawerId:
				fragment = new BulletinFragment();
				break;
			case DieRollFragDrawerId:
				fragment = new DieRollFragment();
				break;
			case ConnectionFragDrawerId:
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
		BulletinManager bm;
		Bulletin b;
		
		switch (rcv.getCommand()) {
			case MOVE_TOKEN:
				Log.v(TAG, "Moving token.");
				tm = TokenManager.getSharedInstance();
				t = new Token(rcv);
				tm.move(t);
				
				if (activeFragment instanceof TableTopFragment) {
					// signal fragment that a token moved
					activeFragment.passReceived(rcv);
				}
				
				break;
			case SEND_TOKEN:
				Log.v(TAG, "Receiving token.");
				tm = TokenManager.getSharedInstance();
				t = new Token(rcv);
				tm.add(t);		
				
				if (activeFragment instanceof ManageTokenFragment) {
					// signal fragment that there is a new token
					activeFragment.passReceived(rcv);
				}
				
				break;
			case PASS_MSG:
				Log.v(TAG, "Receiving a bulletin.");
				bm = BulletinManager.getSharedInstance();
				b = new Bulletin(rcv);
				bm.add(b);
				
				if (activeFragment instanceof BulletinFragment) {
					// Notify of new bulletin
					activeFragment.passReceived(rcv);
				} else {
					notifications.notify(mContext, 
							mContext.getResources().getString(R.string.in_msg_notification));
				}
				
				break;
			case UPDATE_ALIAS:
				Log.v(TAG, "Updating Alias List.");
				UserManager um = UserManager.getSharedInstance();
				um.handleUpdateAlias(rcv);
				break;
			case GET_DM_ID:
				Log.v(TAG, "Updating DM id");
				
				byte[] data = rcv.getData();
				
				if (data.length == 1) {
					int dmID = data[0];
					Toast.makeText(this, "dm id :" + dmID, Toast.LENGTH_SHORT).show();
					SharedPreferencesManager man = SharedPreferencesManager.getSharedInstance();
					man.putInt(GameConfigFragment.SHARED_PREFS_DM_ID, dmID);
					
					if (activeFragment instanceof GameConfigFragment) {
						// Notify of new bulletin
						activeFragment.passReceived(rcv);
					}
				} else {
					Log.v(TAG, "Unable to update DMID");
				}
				
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
