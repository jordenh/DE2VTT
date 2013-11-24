#ifndef TOKEN_H_
#define TOKEN_H_

#include "bmp.h"
#include "message.h"
#include "utilities.h"

#define REMOVEALLVAL -1

typedef struct token {
	BMP bmp;
	unsigned int x;
	unsigned int y;
	unsigned int tokenID;
	unsigned int ownerID;
} token;

void initTokens(void);

token * allocateToken(void);

void removeTokenMsg(msg * rmvMsg);

void removeTokenFromUser(unsigned int ownerID);

void removeToken(unsigned int tokenID);

void drawAllTokens(void);

void moveTokenMsg(msg * moveMsg);

void moveToken(unsigned int tokenID, int x, int y);

msg * createResponsesMsg(msg * initialMsg, token * curTok);

void alertUsersOfTokenInfo(msg * currentMsg, int tokenID);

void removeTokensOfOneUser(msg * currentMsg, int tokenID);

#endif /* TOKEN_H_ */
