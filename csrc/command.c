#include "command.h"

extern BMP map;
extern token * tokenArr;
extern int loadedTokenCnt;
extern char dmID;

//inputs: a message to be decoded and executed
//output: -1 for an error, 0 otherwise
//purpose: Take in a raw message from middleman and execute the required functions depending on what the input command is. 
int executeCmd(msg * currentMsg) {
	if(currentMsg == NULL) {
		return -1;
	}

	unsigned int nextCmd = currentMsg->cmd;
	unsigned char byteInfo;
	msg * rspnsMsg;

	switch ((command)nextCmd) {
	case CONNECT:
		//not implemented
		break;
	case DISCONNECT:
		//not implemented
		break;
	case SEND_MAP:
		//Android sends map to DE2 - needs to be recieved, stored and drawn
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
		//Android sends token to DE2 - needs to be recieved, stored as a token, and drawn
		//	Then others need to be notified of new token
		printf("Entering send Token\n");

		//obtain free address in token array
		token *newTok = allocateToken();

		if(newTok){
			newTok->ownerID = currentMsg->androidID;
			receiveTokenPixArr(currentMsg->buffer, &(newTok->bmp));

			drawBmp(&newTok->bmp, newTok->x, newTok->y);
		} else {
			printf("Error when Android sending token!\n");
			return -1;
		}

		// respond with token ID
		rspnsMsg = createSendTokenResponsesMsg(currentMsg, newTok);
		sendMessage(rspnsMsg);
		free(rspnsMsg->buffer);
		free(rspnsMsg);

		alertUsersOfTokenInfo(currentMsg, newTok->tokenID);

		break;
	case GET_DM:
		//Android attempts to get DM and new DM information is sent to all Android Users
		printf("In get_dm\n");
		//only allow DM to be taken if it is not already taken
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
		//Android attempts to release DM and new DM information is sent to all Androi Users
		printf("In release_dm\n");
		//only the DM can release their status
		if (dmID == currentMsg->androidID)
		{
			dmID = 0;
		}

		sendAllUsersDMID(dmID);

		printf("DM id %x\n", dmID);
		break;
	case GET_DM_ID:
		//All Android users get DM information
		printf("In test_get_dm\n");

		sendAllUsersDMID(dmID);

		printf("DM id %x\n", dmID);
		break;
	case MOVE_TOKEN:
		//Android moves token on DE2 - needs structure to be updated, and redrawn
		//	Then others need to be notified of new token position
		printf("In move_token\n");
		handleMoveTokenMsg(currentMsg);
		alertUsersOfTokenInfo(currentMsg, currentMsg->buffer[0]);
		break;

	case HANDSHAKE:
		//return identical message to Android that was recieved
		printf("In hand_shake command\n");
		sendMessage(currentMsg);
		break;

	case PASS_MSG:
		//Pass message between Android users
		printf("In Pass_msg command statement\n");
		passMsg(currentMsg);
		break;

	case UPDATE_ALIAS:
		//Update the user's alias to a new string
		printf("In Update_Alias\n");
		updateConnUserAlias(currentMsg);
		alertUsersNewUser(currentMsg);
		break;

	case OUTPUT_TOKEN_INFO:
		//This is a java side command - the DE2 will update all connected androids that a token has moved.
		printf("In Output_Token_Info\n");

		break;

	case DISCONNECT_DEV:
		//A device disconnected, so cleanup everything it owns and alert all other users that this player,
		//	and their tokens no longer exist
		printf("In DISCONNECT_DEV\n");

		if (dmID == currentMsg->androidID) {
			dmID = 0;
			sendAllUsersDMID(dmID);
		}

		alertUsersOfUserDC(currentMsg); // removes their alias
		removeTokensOfOneUser(currentMsg, REMOVEALLVAL); // removes all references to DC'd players tokens from all other users.
		removeTokenFromUser(currentMsg->androidID);
		clearUserInfo(currentMsg);
		alt_up_char_buffer_clear(char_buffer); // refresh buffer.
		break;

	case REMOVE_TOKEN:
		//Android removes token from DE2 - needs to be cleaned up
		//	Then others need to be notified of token removed from game
		printf("In Remove_Token");
		byteInfo = *(currentMsg->buffer); // first byte in buffer is Token_ID;
		removeTokensOfOneUser(currentMsg, byteInfo);
		removeToken(byteInfo);
		break;

	default:
		printf("Error, invalid command received on DE2!");
		break;
	}

	return 0;
}
