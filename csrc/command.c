#include "command.h"

extern BMP map;
extern token * tokenArr;
extern int loadedTokenCnt;

int executeCmd(msg * currentMsg) {
	if(currentMsg == NULL) {
		return -1;
	}

	unsigned int nextCmd = currentMsg->cmd;//cmdInt;
	msg * rspnsMsg;

	switch ((command)nextCmd) {
	case CONNECT:

		break;
	case DISCONNECT:

		break;
	case SEND_MAP:
		printf("Entering send SEND_MAP\n");
		if(loadedTokenCnt < MAX_TOKENS){
			//receiveTokenPixArr(currentMsg->buffer, &map);
			loadedTokenCnt++;
		} else {
			printf("Error when Android sending map!\n");
			return -1;
		}

		break;
	case SEND_TOKEN:
		printf("Entering send Token\n");

		token *newTok = allocateToken();
		newTok->ownerID = currentMsg->androidID;

		if(newTok){
			receiveTokenPixArr(currentMsg->buffer, &(newTok->bmp));
			loadedTokenCnt++;

			drawBmp(&newTok->bmp, newTok->x, newTok->y);
		} else {
			printf("Error when Android sending token!\n");
			return -1;
		}

		// respond with token ID
		rspnsMsg = createResponsesMsg(currentMsg, newTok);
		sendMessage(rspnsMsg);
		free(rspnsMsg->buffer);
		free(rspnsMsg);

		break;
	case GET_DM:

		break;
	case RELEASE_DM:

		break;
	case MOVE_TOKEN:
		printf("In move_token\n");
		moveTokenMsg(currentMsg);
		break;

	case HANDSHAKE:
		sendMessage(currentMsg);
		break;

	case PASS_MSG:
		printf("In Pass_msg command statement\n");
		passMsg(currentMsg);
		break;

	case OUTPUT_TOKEN_INFO:
		break;

	case REMOVE_TOKEN:
		printf("In Remove_Token");
		removeTokenFromUser(currentMsg->androidID);
		break;

	default:
		printf("Error, invalid command received on DE2!");
		break;
	}

	return 0;
}
