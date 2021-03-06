package org.ubc.de2vtt.users;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.ubc.de2vtt.comm.Received;
import android.util.Log;


//User Manager is used to store all of the currently active Users in WING on each Android device.
//	This information is used for DM purposes, as well as for message passing purposes.
//	It allows each Android user to know the Alias and IDs of all other users
public class UserManager {
		private static final String TAG = UserManager.class.getSimpleName();
        
		private List<User> user;
        
        private static UserManager sharedInstance;
        
        public static UserManager getSharedInstance() {
                if (sharedInstance == null) {
                        sharedInstance = new UserManager();
                }
                return sharedInstance;
        }
        
        protected UserManager() {
                user = new ArrayList<User>();
                user.add(new User(0, "Table"));
        }
        
        public String getAliasWithID(int id) {
        	for (User u : user) {
        		if (u.getID() == id) {
        			return u.getAlias();
        		}
        	}
        	return Integer.toString(id);
        }
        
        public boolean isIDValid(int id) {
        	for (User u : user) {
        		if (u.getID() == id) {
        			return true;
        		}
        	}
        	return false;
        }
        
        public User getAtIndex(int i) {
            return user.get(i);
        }
        
        public int count() {
            return user.size();
        }
        
        public void removeAtIndex(int index) {
            user.remove(index);
        }
        
        public void remove(User u) {
            user.remove(u);
        }
        
        public void resetUserManager() {
        	user = new ArrayList<User>();
            user.add(new User(0, "Table"));
        }
        
        public void add(User u) {
            user.add(u);
        }
        
        public void handleUpdateAlias(Received rcv) {
        	byte[] data = rcv.getData();
        	if (data.length == 0) {
        		//Do nothing - erroneous data transmission
        		return;
        	}else if (data[1] == 0) {
        		//Disconnecting this device ID - remove from user List
        		int ID = data[0];
        		Log.v(TAG, "handlingUpdateAlias - removing user");
        		for(int i = 0; i < count(); i++){
        			if(getAtIndex(i).getID() == ID) {
        				removeAtIndex(i);
        				return;
        			}
        		}
        	} else {
        		int ID = data[0];
        		int i;
        		for(i = 0; i < count(); i++){
        			if(getAtIndex(i).getID() == ID) {
        				getAtIndex(i).setAlias(new String(data, 1, data.length - 1, Charset.forName("US-ASCII")));
        				return;
        			}
        		}
        		//Add new ID and Alias from received msg. 
        		if(i == count())
        			add(new User(rcv));
        	}
        }
}
