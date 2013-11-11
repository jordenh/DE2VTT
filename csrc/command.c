
#include "command.h"

extern BMP * tokenArr;
extern int loadedTokenCnt;

int nopTest(void) {

	printf("FML");
	return 0;

}

int executeCmd(msg * currentMsg) {
	if(currentMsg == NULL) {
		return -1;
	}

	unsigned int nextCmd = currentMsg->cmd;//cmdInt;

	switch ((command)nextCmd) {
	case CONNECT:

		break;
	case DISCONNECT:

		break;
	case SEND_MAP:

		break;
	case SEND_TOKEN:
		printf("Entering send Token\n");
		if(loadedTokenCnt < MAX_TOKENS){
			receiveTokenPixArr(currentMsg->buffer, &tokenArr[loadedTokenCnt]);
			loadedTokenCnt++;
		} else {
			printf("Error when Android sending token!\n");
			return -1;
		}

		printf("About to draw the token!\n");
		drawBmp(&tokenArr[loadedTokenCnt-1], 10, 10);

		break;
	case GET_DM:

		break;
	case RELEASE_DM:

		break;
	case MOVE_TOKEN:

		break;
	case HANDSHAKE:
		sendMessage(currentMsg);
		break;

	case PASS_MSG:
		printf("In Pass_msg command statement\n");
		passMsg(currentMsg);
		break;
	default:
		printf("Error, invalid command received on DE2!");
		break;
	}

	return 0;
}
