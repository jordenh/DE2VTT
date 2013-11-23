
#ifndef UTILITIES_H_
#define UTILITIES_H_

#define MAX_TOKENS 100

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
	DISCONNECT_DEV,
	REMOVE_TOKEN,
	GET_DM_ID
} command;

char * IntToCharBuf(unsigned int inputInt, unsigned int numChars);

#endif /* UTILITIES_H_ */
