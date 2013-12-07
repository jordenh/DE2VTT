#include "message.h"

//Note: This file serves two purposes:
//1) It sets up functions that are used to receive, package, and send messages 
//
//2) It has a lot of functions that deal with the WING user information (ID/Alias, etc)
//	This also includes functions that can be called to send out specific messages back
//	to the android side to notify all users of DE2 updates (tokens added, players added/removed, etc)

int connUserIDs[NUM_USERS] = {0,0,0,0,0};
char * connUserAlias[NUM_USERS];

FILE* uart;

//initializatino code for serial UART and User alias array data structure
void setupMessage(void) {
	int i;

	for(i = 0; i < NUM_USERS; i++) {
		if(connUserAlias[i] == NULL) {
			connUserAlias[i] = malloc(sizeof(char) * MAX_ALIAS_SIZE);
		}

		*connUserAlias[i] = '\0';
	}

	//printf("UART Initialization\n");
	uart = fopen("/dev/uart_0", "r+");

	if(uart == NULL) {
		printf("ERROR - uart not opened properly");
	}
}

// checks if the ID is saved in the connUserIDs array, and returns true if it exists, false otherwise.
unsigned int isIDSaved(msg * inMsg) {
	int i;

	for(i = 0; i < NUM_USERS ; i++){
			if(inMsg->androidID == connUserIDs[i]){
				printf("android %d sending to DE2 already in system\n", connUserIDs[i]);
				return 1;
			}
	}

	return 0;
}

// stores an ID in the connUsersIDs array, if room available. Returns 0 if not added, 1 if added.
unsigned int storeNewID(int ID) {
	int i;

	for(i = 0; i < NUM_USERS ; i++){
		if(connUserIDs[i] == 0) {
			printf("DE2 communicating with new android - ID %d\n", ID);
			connUserIDs[i] = ID;
			return 1;
		}
	}

	return 0;
}

// if alias != null, connAlias set to alia, else set to inMsgBuffer.
unsigned int updateConnUserAlias(msg * inMsg) {
	int i;
	char buf[MAX_ALIAS_SIZE];

	for(i = 0; i < NUM_USERS; i ++) {
		if(connUserIDs[i] == inMsg->androidID) {
			if(inMsg->cmd == (unsigned int)UPDATE_ALIAS) {
				strncpy(connUserAlias[i], (char*)inMsg->buffer, (MAX_ALIAS_SIZE - 1));
			} else {
				sprintf(buf, "player%d", i);
				connUserAlias[i] = strncpy(connUserAlias[i], buf, (MAX_ALIAS_SIZE - 1)); //strlen(buf));//
			}
			connUserAlias[i][MAX_ALIAS_SIZE - 1] = '\0'; // enforce last byte to be null character, to avoid overflow
			alt_up_char_buffer_clear(char_buffer);
			return 1;
		}
	}
	return 0;
}

//purpose: Alert all current Android users of a user change (addition or alias update)
void alertUsersNewUser(msg * currentMsg) {
	int i;

	msg alertMsg;
	alertMsg.androidID = 0;
	alertMsg.buffer = malloc(sizeof(char) * MAX_ALIAS_SIZE);
	alertMsg.cmd = (unsigned int)UPDATE_ALIAS;
	alertMsg.len = MAX_ALIAS_SIZE;//currentMsg->len; // correct?

	//printf("in alertUsersNewUser, alerting of new user: %d\n", currentMsg->androidID);
	for(i = 0; i < NUM_USERS; i++) {
		if(currentMsg->androidID == connUserIDs[i]) {
			*(unsigned char*)alertMsg.buffer = (unsigned char)connUserIDs[i];
			strncpy((char*)(alertMsg.buffer + 1), connUserAlias[i], MAX_ALIAS_SIZE - 1);
			alertMsg.buffer[MAX_ALIAS_SIZE - 1] = '\0';
		}
	}
	for(i = 0; i < NUM_USERS; i++) {
		if((currentMsg->androidID != connUserIDs[i]) && (connUserIDs[i] != 0)) {
			alertMsg.androidID = connUserIDs[i];
			printf("about to send string: %s to %d in alertUsersNewUser\n", (char*)alertMsg.buffer, connUserIDs[i]);
			sendMessage(&alertMsg);
		}
	}
	free(alertMsg.buffer);
}

//purpose: Alert one user about all of the other active Android user's information
//		To be used when a phone connects, when others are already connected.
void alertUserAllUsers(msg * currentMsg) {
	int i;

	msg alertMsg;
	alertMsg.androidID = 0;
	alertMsg.buffer = malloc(sizeof(char) * MAX_ALIAS_SIZE);
	alertMsg.cmd = (unsigned int)UPDATE_ALIAS;
	alertMsg.len = MAX_ALIAS_SIZE;//currentMsg->len; // correct? TBD

	//printf("in alertUserAllUsers, alerting new user: %d\n", currentMsg->androidID);
	alertMsg.androidID = currentMsg->androidID;
	for(i = 0; i < NUM_USERS; i++) {
		if((currentMsg->androidID != connUserIDs[i]) && (connUserIDs[i] != 0)) {
			*(unsigned char *)alertMsg.buffer = (unsigned char)connUserIDs[i];
			strncpy((char*)(alertMsg.buffer + 1), connUserAlias[i], MAX_ALIAS_SIZE - 1);
			alertMsg.buffer[MAX_ALIAS_SIZE - 1] = '\0';
			printf("about to send string: %s in alertUserAlUsers\n", (char*)alertMsg.buffer);
			sendMessage(&alertMsg);
		}
	}
	free(alertMsg.buffer);
}

//purpose: Alerta all users that one user has disconnected, in order to alert Android users to remove
//		all associated user information.
void alertUsersOfUserDC(msg * currentMsg) {
	int i, j;

	msg alertMsg;
	alertMsg.androidID = 0;
	alertMsg.cmd = (unsigned int)UPDATE_ALIAS;
	alertMsg.len = 2; // size of buffer for ID and 0.

	alertMsg.buffer = malloc(2); //message of null indicates that android should remove the user from their memory.
	alertMsg.buffer[0] = currentMsg->androidID;
	alertMsg.buffer[1] = '\0';

	for(i = 0; i < NUM_USERS; i++) {
		if((currentMsg->androidID != connUserIDs[i]) && (connUserIDs[i] != 0)) {
			printf("in alertUsersOfUserDC - sending to id %d about DC of %d", connUserIDs[i], currentMsg->androidID);
			alertMsg.androidID = connUserIDs[i];
			sendMessage(&alertMsg);
		}
	}
	free(alertMsg.buffer);
}

//purpose: cleanup the alias array when a user leaves WING
void clearUserInfo(msg * currentMsg) {
	int i;

	for(i = 0; i < NUM_USERS; i++) {
		if(currentMsg->androidID == connUserIDs[i]) {
			*connUserAlias[i] = '\0';
			connUserIDs[i] = 0;
		}
	}
}

//purpose: recieve a message from the UART and package it into a msg struct
//		This function allocates room for the msg buffer
void getMessage(msg * inMsg){
	int i;
	unsigned char msgLen[4];
	inMsg->len = 0;

	//Middleman will only send a message to the DE2 if the first byte it receives is a zero
	fputc('\0', uart);

	//obtain android ID
	do {
		inMsg->androidID = (int) fgetc(uart);
	} while(inMsg->androidID == EOF || inMsg->androidID == '\n');
	printf("I got msg from ID %d\n", inMsg->androidID);

	//obtain length (4 bytes)
	for(i = ((sizeof(msgLen) / sizeof(msgLen[0])) - 1); i >= 0; i--) {
		//printf("about to fgetc\n");
		msgLen[i] = fgetc(uart);
		//printf("received: msgLen[i] %d\n", msgLen[i]);
		inMsg->len += (0xFF & msgLen[i]) << i*8;
	}

	inMsg->cmd = (unsigned int) fgetc(uart);
	printf("About to receive %d characters, from cmd %d:\n", inMsg->len, inMsg->cmd);

	int tmp;
	inMsg->buffer = malloc(inMsg->len * sizeof(char));

	if(inMsg->buffer) {
		tmp = fread(inMsg->buffer, sizeof(char), inMsg->len, uart);
		printf("num bytes read from serial stream: %d\n", tmp);
	} else {
		printf("Error, input Msg buffer not able to be allocated\n");
	}

	if(isIDSaved(inMsg) == 0) {
		if (storeNewID(inMsg->androidID)  == 0)
			printf("Error adding Android ID, ID array full\n");
		else {
			updateConnUserAlias(inMsg);
			alertUsersNewUser(inMsg); //alert current users of new user
			alertUserAllUsers(inMsg); //alert new user of all current users
			alertUserOfAllTokens(inMsg); //alert new user of all active tokens
		}
	}

//	for(i = 0; i < inMsg->len; i++) {
//		printf("%c", *(inMsg->buffer + i));
//	}

	printf("\n");

	return;
}

//purpose: sends the msg struct in the correct order to the UART given our communication protocol
//requires: pre-allocated char buffer
void sendMessage(msg * sendMsg){
	int i;
	unsigned char msgLen[4];

	if(sendMsg->buffer == NULL) {
		printf("Error in sendMessage, buffer is null!");
		return;
	} else if(uart == NULL) {
		printf("Error in sendMessage, uart is null!");
		return;
	}

	// Start with the android ID, since we are interfacing many androids
	fputc((unsigned char) sendMsg->androidID, uart);

	printf("starting to send message, with length: %d\n", sendMsg->len);

	// Start with the number of bytes in our message
	for(i = ((sizeof(msgLen) / sizeof(msgLen[0])) - 1); i >= 0; i--) {
		msgLen[i] = (sendMsg->len  >> i*8) & (0xFF);
		//printf("msgLen[i] = %d\n", msgLen[i]);
		fputc(msgLen[i], uart);
	}

	fputc(sendMsg->cmd, uart);

	// Now send the actual message to the Middleman
	fwrite(sendMsg->buffer, sizeof(char), sendMsg->len, uart);

	fflush(uart);
}

//purpose: alert all Android users about the ID of the DM
void sendAllUsersDMID(char dmID) {
	msg * rspnsMsg;
	rspnsMsg = malloc(sizeof(msg));
	rspnsMsg->androidID = 0;
	rspnsMsg->buffer = malloc(sizeof(char));
	*(rspnsMsg->buffer) = dmID;
	rspnsMsg->cmd = GET_DM_ID;
	rspnsMsg->len = 1;

	int i;

	for(i = 0; i < NUM_USERS; i++) {
		if(connUserIDs[i] != 0) {
			rspnsMsg->androidID = connUserIDs[i];
			sendMessage(rspnsMsg);
		}
	}

	alt_up_char_buffer_clear(char_buffer);
	free(rspnsMsg->buffer);
	free(rspnsMsg);
}

//purpose: Pass a message along to the correct recipient
void passMsg(msg * passMsg) {
	printf("in passMsg\n");
	if(passMsg->buffer == NULL || uart == NULL){
		printf("Error in sendMessage, buffer or uart is null!");
		return;
	}

	char * msgString = (char *)passMsg->buffer;
	unsigned int yPos = 2;
	unsigned int xPos;
	unsigned int sendID = (unsigned int)(*(msgString));

	if( sendID == 0) {
		printf("about to write to screen!\n");
		alt_up_char_buffer_clear(char_buffer);
		xPos = (SCREEN_CHAR_WIDTH / 2) - (int)(strlen(msgString) / 2);
		alt_up_char_buffer_string(char_buffer, msgString , xPos, yPos);
	} else if( (sendID == connUserIDs[0]) ||
			( sendID == connUserIDs[1]) ||
			( sendID == connUserIDs[2]) ||
			( sendID == connUserIDs[3]) ||
			( sendID == connUserIDs[4]) ) {
		*(msgString) = passMsg->androidID; // byte 6 is now the ID of the device that sent the message.
		passMsg->androidID = sendID;
		sendMessage(passMsg);
	} else {
		printf("Error, tried to pass message to non-existant device!\n");
	}

}
