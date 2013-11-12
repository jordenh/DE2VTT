#ifndef TOKEN_H_
#define TOKEN_H_

#include "bmp.h"
#include "message.h"
#include "utilities.h"

typedef struct token {
	BMP bmp;
	unsigned int x;
	unsigned int y;
	unsigned int tokenID;
	unsigned int ownerID;
} token;

void initTokens(void);

token * allocateToken(void);

void removeToken(unsigned int tokenID);

void drawAllTokens(void);

void moveToken(unsigned int tokenID, int x, int y);

msg * createResponsesMsg(msg * initialMsg, token * curTok);

#endif /* TOKEN_H_ */
