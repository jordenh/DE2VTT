
#ifndef MESSAGE_H_
#define MESSAGE_H_

#include "altera_up_avalon_rs232.h"
#include "system.h"
#include "vga.h"
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

msg * getMessage(void);

void sendMessage(msg * sendMsg);


#endif /* MESSAGE_H_ */
