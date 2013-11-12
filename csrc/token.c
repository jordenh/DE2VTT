
#include "token.h"

BMP map;
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

void removeToken(unsigned int tokenID) {
	int i;
	for(i = 0; i < MAX_TOKENS; i++) {
		if(tokenArr[i].tokenID == tokenID) {
			tokenArr[i].tokenID = 0;
			//STUB TBD - DRAW BACKROUND MAP SECTION IN THIS LOCATION.
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
			tokenArr[i].x = x;
			tokenArr[i].y = y;
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







