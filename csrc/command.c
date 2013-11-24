#include "command.h"

extern BMP map;
extern token * tokenArr;
extern int loadedTokenCnt;
extern char dmID;

int executeCmd(msg * currentMsg) {
	if(currentMsg == NULL) {
		return -1;
	}

	unsigned int nextCmd = currentMsg->cmd;//cmdInt;
	unsigned char byteInfo;
	msg * rspnsMsg;

	switch ((command)nextCmd) {
	case CONNECT:

		break;
	case DISCONNECT:

		break;
	case SEND_MAP:
		printf("Entering send SEND_MAP\n");

		if(loadedTokenCnt < MAX_TOKENS){
			receiveMap(currentMsg->buffer);
			drawMap();
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
		printf("In get_dm\n");
		if (dmID == 0) {
			dmID = currentMsg->androidID;
			printf("New DM: %x\n", dmID);
		} else {
			printf("DM not available - player %x currently has it\n", dmID);
		}

		sendAllUsersDMID(dmID);

		printf("DM id %x\n", dmID);
		break;
	case RELEASE_DM:
		printf("In release_dm\n");
		if (dmID == currentMsg->androidID)
		{
			dmID = 0;
		}

		sendAllUsersDMID(dmID);

		printf("DM id %x\n", dmID);
		break;
	case GET_DM_ID:
		printf("In test_get_dm\n");

		sendAllUsersDMID(dmID);

		printf("DM id %x\n", dmID);
		break;
	case MOVE_TOKEN:
		printf("In move_token\n");
		moveTokenMsg(currentMsg);
		break;

	case HANDSHAKE:
		printf("In hand_shake command\n");
		sendMessage(currentMsg);
		break;

	case PASS_MSG:
		printf("In Pass_msg command statement\n");
		passMsg(currentMsg);
		break;

	case UPDATE_ALIAS:
		printf("In Update_Alias\n");
		updateConnUserAlias(currentMsg);
		//TBD - send new alias name to all phones
		alertUsersNewUser(currentMsg);
		break;

	case OUTPUT_TOKEN_INFO:
		//This is a java side command - the DE2 will update all connected androids that a token has moved.
		break;

	case DISCONNECT_DEV:
		printf("In DISCONNECT_DEV\n");
		removeTokenFromUser(currentMsg->androidID);
		alertUsersOfUserDC(currentMsg);
		clearUserInfo(currentMsg);
		alt_up_char_buffer_clear(char_buffer); // refresh buffer.
		break;

	case REMOVE_TOKEN:
		printf("In Remove_Token");
		byteInfo = *(currentMsg->buffer); // first byte in buffer is Token_ID;
		removeToken(byteInfo);
		break;

	default:
		printf("Error, invalid command received on DE2!");
		break;
	}

	return 0;
}
