#ifndef COMMAND_H_
#define COMMAND_H_

#include "message.h"
#include "bmp.h"
#include "token.h"
#include "utilities.h"

typedef enum command{
	CONNECT,
	DISCONNECT,
	SEND_MAP,
	SEND_TOKEN,
	GET_DM,
	RELEASE_DM,
	MOVE_TOKEN,
	HANDSHAKE,
	PASS_MSG,
	UPDATE_ALIAS,
	OUTPUT_TOKEN_INFO,
	REMOVE_ALL_TOKEN,
	REMOVE_TOKEN
} command;

int executeCmd(msg *m);

#endif /* COMMAND_H_ */
