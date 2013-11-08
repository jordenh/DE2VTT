#ifndef COMMAND_H_
#define COMMAND_H_

#include "message.h"

typedef enum command{
	CONNECT,
	DISCONNECT,
	SEND_MAP,
	SEND_TOKEN,
	GET_DM,
	RELEASE_DM,
	MOVE_TOKEN,
	HANDSHAKE
} command;

int nopTest(void);

int executeCmd(msg *m);

#endif /* COMMAND_H_ */
