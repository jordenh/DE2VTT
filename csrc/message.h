#ifndef MESSAGE_H_
#define MESSAGE_H_

#include "system.h"
#include "vga.h"
#include "utilities.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

#define NUM_USERS 5
#define MAX_ALIAS_SIZE 40

struct message {
	unsigned int androidID;
	unsigned int len;
	unsigned int cmd;
	unsigned char * buffer; // max 124 bytes

};

typedef struct message msg;

void setupMessage(void);

unsigned int isIDSaved(msg * inMsg);

unsigned int storeNewID(int ID);

unsigned int updateConnUserAlias(msg * inMsg);

void alertUsersNewUser(msg * currentMsg);

void alertUserAllUsers(msg * currentMsg);

void alertUsersOfUserDC(msg * currentMsg);

void clearUserInfo(msg * currentMsg);

void getMessage(msg * inMsg);

void sendMessage(msg * sendMsg);

void sendMessageToAllUsers(msg * sendMsg);

void passMsg(msg * passMsg);


#endif /* MESSAGE_H_ */
