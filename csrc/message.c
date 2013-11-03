
#include "message.h"

int connUserIDs[5] = {0,0,0,0,0};

// checks if the ID is saved in the connUserIDs array, and returns true if it exists, false otherwise.
boolean isIDSaved(void) {
	int i;
	for(i = 0; i < sizeof(connUserIDs) / sizeof(connUserIDs[0]) ; i++){
			if(inMsg.androidID == connUserIDs[i]){
				printf("android %d sending to DE2 already in system\n", connUserIDs[i]);
				return true;
			}
	}
	return false;
}

// stores an ID in the connUsersIDs array, if room available. Returns fals if not added, true if added.
boolean storeNewID(int ID) {
	for(i = 0; i < sizeof(connUserIDs) / sizeof(connUserIDs[0]) ; i++){
		if(connUserIDs[i] == 0) {
			printf("DE2 communicating with new android - ID %d\n", inMsg.androidID);
			connUserIDs[i] = ID;
			return true;
		}
	}
	return false;
}


