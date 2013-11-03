
#ifndef MESSAGE_H_
#define MESSAGE_H_

#include "altera_up_avalon_rs232.h"

typedef struct message {
	unsigned int androidID;
	unsigned int len;
	unsigned char * buffer; // max 126 bytes

} message;

boolean isIDSaved(void);

void storeNewID(int ID);


#endif /* MESSAGE_H_ */
