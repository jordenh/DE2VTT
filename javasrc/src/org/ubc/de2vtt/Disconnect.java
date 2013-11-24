package org.ubc.de2vtt;

import org.ubc.de2vtt.token.TokenManager;
import org.ubc.de2vtt.users.UserManager;

public class Disconnect {
	// To Do: Add removal of DM stuff here. TBD
	
	static UserManager mUserManager = UserManager.getSharedInstance();
	static TokenManager mTokenManager = TokenManager.getSharedInstance();
	
	public static void removeSessionData() {
		mUserManager.resetUserManager();
		mTokenManager.resetTokenManager();
	}
}