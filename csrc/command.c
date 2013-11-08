
#include "command.h"

int nopTest(void) {

	printf("FML");
	return 0;

}

int executeCmd(msg * currentMsg) {
	unsigned int nextCmd = currentMsg->cmd;//cmdInt;

	switch ((command)nextCmd) {
	case CONNECT:
			break;

	case DISCONNECT:
			break;

	case SEND_MAP:

			break;
	case SEND_TOKEN:

			break;
	case GET_DM:

			break;
	case RELEASE_DM:

			break;
	case MOVE_TOKEN:

			break;
	case HANDSHAKE:

			break;
	}

	return 0;
}
