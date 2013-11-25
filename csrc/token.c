#include "token.h"

token tokenArr[MAX_TOKENS];
int loadedTokenCnt = 0;

void initTokens(void) {
	int i;
	for(i = 0; i < MAX_TOKENS; i++) {
		if(tokenArr[i].bmp.color) free(tokenArr[i].bmp.color);
		tokenArr[i].ownerID = 0;
		tokenArr[i].tokenID = 0;
		tokenArr[i].x = 0;
		tokenArr[i].y = 0;
	}

	loadedTokenCnt = 0;
}

token * allocateToken(void) {
	int i;

	for(i = 0; i < MAX_TOKENS; i++) {
		if(tokenArr[i].tokenID == 0) {
			tokenArr[i].tokenID = i + 1;
			return &(tokenArr[i]);
			loadedTokenCnt++;
		}
	}
	return NULL;
}

void removeTokenMsg(msg * rmvMsg){
	unsigned int ownerID = (unsigned int)(*(rmvMsg->buffer));

	removeTokenFromUser(ownerID);
}

void removeTokenFromUser(unsigned int ownerID) {
	int i;
	for(i = 0; i < MAX_TOKENS; i++) {
		if(tokenArr[i].ownerID == ownerID) {
			partialMapReDraw(tokenArr[i].x, tokenArr[i].y, tokenArr[i].bmp.infoheader.width, tokenArr[i].bmp.infoheader.height);

			tokenArr[i].tokenID = 0;
			tokenArr[i].ownerID = 0;
			tokenArr[i].x = 0;
			tokenArr[i].y = 0;

			if(tokenArr[i].bmp.color) free(tokenArr[i].bmp.color);
			loadedTokenCnt--;
		}
	}
}

void removeToken(unsigned int tokenID) {
	int i;
	for(i = 0; i < MAX_TOKENS; i++) {
		if(tokenArr[i].tokenID == tokenID) {
			partialMapReDraw(tokenArr[i].x, tokenArr[i].y, tokenArr[i].bmp.infoheader.width, tokenArr[i].bmp.infoheader.height);

			tokenArr[i].tokenID = 0;
			tokenArr[i].ownerID = 0;
			tokenArr[i].x = 0;
			tokenArr[i].y = 0;

			if(tokenArr[i].bmp.color) free(tokenArr[i].bmp.color);
			loadedTokenCnt--;
		}
	}
}

void drawAllTokens(void) {
	int i;
	for (i = 0; i < MAX_TOKENS; i++) {
		if(tokenArr[i].tokenID) {
			drawBmp(&tokenArr[i].bmp, tokenArr[i].x, tokenArr[i].y);
		}
	}
}

void moveTokenMsg(msg * moveMsg){
	unsigned int tokenID = (unsigned int)(*(moveMsg->buffer));
	unsigned int x1 = (unsigned int)(*(moveMsg->buffer + 1));
	unsigned int x0 = (unsigned int)(*(moveMsg->buffer + 2));
	unsigned int y1 = (unsigned int)(*(moveMsg->buffer + 3));
	unsigned int y0 = (unsigned int)(*(moveMsg->buffer + 4));

	unsigned int x = x1*255 + x0;
	unsigned int y = y1*255 + y0;

	moveToken(tokenID, x, y);
}

void moveToken(unsigned int tokenID, int x, int y) {
	int i;

	for (i = 0; i < MAX_TOKENS; i++) {
		if(tokenArr[i].tokenID == tokenID) {
			partialMapReDraw(tokenArr[i].x, tokenArr[i].y, tokenArr[i].bmp.infoheader.width, tokenArr[i].bmp.infoheader.height);

			tokenArr[i].x = x;
			tokenArr[i].y = y;

			drawBmp(&tokenArr[i].bmp, tokenArr[i].x, tokenArr[i].y);
			break;
		}
	}
}

msg * createResponsesMsg(msg * initialMsg, token * curTok) {
	int i;
	msg *responseMsg = malloc(sizeof(msg));
	responseMsg->androidID = initialMsg->androidID;
	responseMsg->cmd = initialMsg->cmd;

	responseMsg->len = 5;

	responseMsg->buffer = malloc(initialMsg->len * sizeof(char));
	responseMsg->buffer[0] = (unsigned char)curTok->tokenID;
	printf("tokenID = %d ", curTok->tokenID);

	char * x2Char = IntToCharBuf((unsigned int)curTok->x, 2);
	char * y2Char = IntToCharBuf((unsigned int)curTok->y, 2);

	for(i = 0; i < 2; i++) {
		responseMsg->buffer[1+i] = x2Char[i];
	}
	for(i = 0; i < 2; i++) {
		responseMsg->buffer[3+i] = y2Char[i];
	}
	free(x2Char);
	free(y2Char);

	return responseMsg;
}


//OUTPUT_TOKEN_INFO from DE2 to Android - used to alert Android users that a token was moved/added/deleted by another player.
void alertUsersOfTokenInfo(msg * currentMsg, int tokenID) {
	int i;

	msg alertMsg;
	alertMsg.androidID = 0;
	alertMsg.cmd = (unsigned int)OUTPUT_TOKEN_INFO;
	alertMsg.len = 6;

	//This maps the token ID, owner ID, and x,y of the token that was moved, to the correct
	//buffer location for the message to be sent out.
	alertMsg.buffer = malloc(sizeof(char) * 6);
	alertMsg.buffer[0] = tokenID; // TokenID
	alertMsg.buffer[1] = currentMsg->androidID; // Owner of Token's ID

	if((command)currentMsg->cmd == SEND_TOKEN) {
		printf("In alertUsersOfTokenInfo, setting x/y to initial vals\n");
		alertMsg.buffer[2] = 0; //Initial x,y = 0,0
		alertMsg.buffer[3] = 0;
		alertMsg.buffer[4] = 0;
		alertMsg.buffer[5] = 0;
	} else {
		printf("In alertUsersOfTokenInfo, setting x/y to moved values");
		alertMsg.buffer[2] = currentMsg->buffer[7]; // Token x1
		alertMsg.buffer[3] = currentMsg->buffer[8]; // Token x0
		alertMsg.buffer[4] = currentMsg->buffer[9]; // Token y1
		alertMsg.buffer[5] = currentMsg->buffer[10]; // Token y0
	}

	for(i = 0; i < NUM_USERS; i++) {
		if((currentMsg->androidID != connUserIDs[i]) && (connUserIDs[i] != 0)) {
			printf("in alertUsersOfTokenMove - sending to id %d about movement of %d's token\n", connUserIDs[i], currentMsg->androidID);
			alertMsg.androidID = connUserIDs[i]; // id of who is to receive the alert.
			sendMessage(&alertMsg);
		}
	}
	free(alertMsg.buffer);
}

//send all currently active Token information to a new user
void alertUserOfAllTokens(msg * currentMsg) {
	int i;

	msg alertMsg;
	alertMsg.androidID = currentMsg->androidID; //send back to the user who is joining WING
	alertMsg.cmd = (unsigned int)OUTPUT_TOKEN_INFO;
	alertMsg.len = 6;

	//This maps the token ID, owner ID, and x,y of the token that was moved, to the correct
	//buffer location for the message to be sent out.
	alertMsg.buffer = malloc(sizeof(char) * 6);

	alertMsg.buffer[1] = currentMsg->androidID; // Owner of Token's ID

	for(i = 0; i < MAX_TOKENS; i++) {
		if(tokenArr[i].tokenID != 0) {
			alertMsg.buffer[0] = tokenArr[i].tokenID; // TokenID
			alertMsg.buffer[2] = (unsigned char)(tokenArr[i].x / 255); // Token x1
			alertMsg.buffer[3] = (unsigned char)(tokenArr[i].x % 255); // Token x0
			alertMsg.buffer[4] = (unsigned char)(tokenArr[i].y / 255); // Token y1
			alertMsg.buffer[5] = (unsigned char)(tokenArr[i].y % 255); // Token y0

			printf("in alertUserOfAllTokens - sending to id %d about token %d\n", alertMsg.androidID, tokenArr[i].tokenID);
			sendMessage(&alertMsg);
		}
	}
	free(alertMsg.buffer);
}

//notify all other users that tokens of one user should be removed.
//If tokenIDRemove == -1, then removes all tokens, else removes only tokenID
void removeTokensOfOneUser(msg * currentMsg, int tokenID) {
	int i,j;

	msg alertMsg;
	alertMsg.androidID = 0;
	alertMsg.cmd = (unsigned int)REMOVE_TOKEN;
	alertMsg.len = 6;

	//This maps the token ID, owner ID, and mock x,y of the token that was moved, to the correct
	//buffer location for the message to be sent out.
	alertMsg.buffer = malloc(sizeof(char) * 6);
	alertMsg.buffer[1] = currentMsg->androidID; // Owner of Token's ID

	for(i = 0; i < NUM_USERS; i++) {
		if((currentMsg->androidID != connUserIDs[i]) && (connUserIDs[i] != 0)) {
			printf("in removeTokensOfOneUser - sending to id %d about removal of tokens from %d\n", connUserIDs[i], currentMsg->androidID);
			alertMsg.androidID = connUserIDs[i];

			//alert user of all of the tokens to be removed.
			for(j = 0; j < MAX_TOKENS; j++) {
				alertMsg.buffer[0] = tokenArr[j].tokenID;

				if(tokenArr[j].ownerID == currentMsg->androidID) {
					if(tokenID == -1 || tokenID == tokenArr[j].tokenID) {
						printf("removing  %d's token %d\n", tokenArr[j].ownerID, tokenArr[j].tokenID);
						sendMessage(&alertMsg);
					}
				}
			}
		}
	}
	free(alertMsg.buffer);
}









