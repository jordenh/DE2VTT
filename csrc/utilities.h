
#ifndef UTILITIES_H_
#define UTILITIES_H_

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
	REMOVE_TOKEN,
	GET_DM_ID,
	SEND_DM_ID
} command;

int DMId = 0;

char * IntToCharBuf(unsigned int inputInt, unsigned int numChars);

#endif /* UTILITIES_H_ */
