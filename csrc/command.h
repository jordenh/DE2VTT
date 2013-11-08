
#ifndef COMMAND_H_
#define COMMAND_H_

#include <stdlib.h>
#include <stdio.h>
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

int executeCmd(unsigned int cmdInt); // Question for Jeff - why cant i put message here????/

int nopTest2(void);


#endif /* COMMAND_H_ */
