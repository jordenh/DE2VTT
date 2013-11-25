package org.ubc.de2vtt.users;

import org.ubc.de2vtt.comm.Received;

import android.util.Log;

public class DMManager {
		private static final String TAG = DMManager.class.getSimpleName();
        
		private int dmId;
		private boolean isUserDM;
		private boolean isDMAvailable;
		private String dmAlias;
        
		private static UserManager usrMan = UserManager.getSharedInstance();
        private static DMManager sharedInstance;
        
        public static DMManager getSharedInstance() {
                if (sharedInstance == null) {
                        sharedInstance = new DMManager();
                }
                return sharedInstance;
        }
        
        protected DMManager() {
                dmId = 0;
                isUserDM = false;
                isDMAvailable = false;
                dmAlias = "";
        }
        
        public int getDMID() {
        	return dmId;
        }
        
        public boolean isUserDM() {
        	return isUserDM;
        }
        
        public boolean isDMAvailable() {
        	return isDMAvailable;
        }
        
        public String getDMAlias() {
        	return dmAlias;
        }
        
        public void handleGetDMId(Received rcv) {
        	Log.v(TAG, "Updating DM id");
			
			byte[] data = rcv.getData();
			
			if (dmId == data[0]) {
				// dmId is correct but should double check the alias
				if (dmId == 0) {
					isDMAvailable = true;
				}
			} else {
				dmId = data[0];
				
				if (dmId == 0) {
					isDMAvailable = true;
					isUserDM = false;
					dmAlias = "";
				} else {
					if (usrMan.isIDValid(dmId)) {
						// User is within the manager therefore it is a different player
						isDMAvailable = false;
						isUserDM = false;
						dmAlias = usrMan.getAliasWithID(dmId);
					} else {
						// you are the dm
						isDMAvailable = false;
						isUserDM = true;
						dmAlias = "You are the DM";
					}
				}
			}	
        }

        public void updateDMAlias() {
        	dmAlias = usrMan.getAliasWithID(dmId);
        }
}