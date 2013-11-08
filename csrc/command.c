
#include "command.h"

int nopTest(void) {

	printf("FML");
	return 0;

}

int executeCmd(unsigned int cmdInt) {
	unsigned int nextCmd = cmdInt;//currentMsg.cmd;

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
