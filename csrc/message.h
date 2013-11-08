
#ifndef MESSAGE_H_
#define MESSAGE_H_

#include "altera_up_avalon_rs232.h"
#include "system.h"
#include "command.h"
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <math.h>

typedef struct message {
	unsigned int androidID;
	unsigned int len;
	unsigned int cmd;
	unsigned char * buffer; // max 126 bytes

} message;

void setupMessage(void);

unsigned int isIDSaved(message inMsg);

unsigned int storeNewID(int ID);

message getMessage(void);

void sendMessage(message sendMsg);


#endif /* MESSAGE_H_ */
