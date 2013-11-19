
#include "message.h"

int connUserIDs[NUM_USERS] = {0,0,0,0,0};
char * connUserAlias[NUM_USERS];

FILE* uart;

void setupMessage(void) {

	int i;
	for(i = 0; i < NUM_USERS; i++) {
		if(connUserAlias[i] == NULL) {
			connUserAlias[i] = malloc(sizeof(char) * MAX_ALIAS_SIZE);
		}
		*connUserAlias[i] = '\0';
	}

	//printf("UART Initialization\n");
	uart = fopen("/dev/uart_0", "r+"); //should set to RS232_0_NAME

	if(uart == NULL) {
		printf("ERROR - uart not opened properly");
	}

	//clear buffer:
	char c;
	//while((c = fgetc(uart)) != '\n' && c != EOF);
}

// checks if the ID is saved in the connUserIDs array, and returns true if it exists, false otherwise.
unsigned int isIDSaved(msg * inMsg) {
	int i;
	for(i = 0; i < NUM_USERS ; i++){
			if(inMsg->androidID == connUserIDs[i]){
				printf("android %d sending to DE2 already in system\n", connUserIDs[i]);
				return 1;
			}
	}
	return 0;
}

// stores an ID in the connUsersIDs array, if room available. Returns 0 if not added, 1 if added.
unsigned int storeNewID(int ID) {
	int i;

	for(i = 0; i < NUM_USERS ; i++){
		if(connUserIDs[i] == 0) {
			printf("DE2 communicating with new android - ID %d\n", ID);
			connUserIDs[i] = ID;
			return 1;
		}
	}
	return 0;
}

// if alias != null, connAlias set to alia, else set to inMsgBuffer.
unsigned int updateConnUserAlias(msg * inMsg) {
	int i;
	char buf[MAX_ALIAS_SIZE];

	for(i = 0; i < NUM_USERS; i ++) {
		if(connUserIDs[i] == inMsg->androidID) {
			if(inMsg->cmd == (unsigned int)UPDATE_ALIAS) {
				strncpy(connUserAlias[i], inMsg->buffer, (sizeof(connUserAlias[i]) - 1));
			} else {
				sprintf(buf, "player%d", i);
				connUserAlias[i] = strncpy(connUserAlias[i], buf, (sizeof(connUserAlias[i]) - 1));
			}
			connUserAlias[i][sizeof(connUserAlias[i]) - 1] = '\0'; // enforce last byte to be null character, to avoid overflow
			printf("connuserAlias %d updated to  %s\n", i, connUserAlias[i]);
			return 1;
		}
	}
	return 0;
}

void getMessage(msg * inMsg){
	int i;
	unsigned char msgLen[4];
	inMsg->len = 0;

	//while (fgetc(uart) == 0) {}; // TBD - have this step time out. -- Probably need to
	//remove this line, since steven's middleman uart likely isnt passing 0's constatnly

	fputc('\0', uart);

	//obtain android ID
	do {
		inMsg->androidID = (int) fgetc(uart);
	} while(inMsg->androidID == EOF || inMsg->androidID == '\n');
	printf("I got msg from ID %d\n", inMsg->androidID);

	if(isIDSaved(inMsg) == 0) {
		if (storeNewID(inMsg->androidID)  == 0)
			printf("Error adding Android ID, ID array full\n");
		else {
			updateConnUserAlias(inMsg);
		}
	}

	//obtain length
	for(i = ((sizeof(msgLen) / sizeof(msgLen[0])) - 1); i >= 0; i--) {
		//printf("about to fgetc\n");
		msgLen[i] = fgetc(uart);
		//printf("received: msgLen[i] %d\n", msgLen[i]);
		inMsg->len += (0xFF & msgLen[i]) << i*8;
	}

	printf("About to receive %d characters:\n", inMsg->len);

	inMsg->cmd = (unsigned int) fgetc(uart);

	int tmp;
	inMsg->buffer = malloc(inMsg->len * sizeof(char));

	if(inMsg->buffer) {
		tmp = fread(inMsg->buffer, sizeof(char), inMsg->len, uart);
		printf("num bytes read from serial stream: %d\n", tmp);
	} else {
		printf("Error, input Msg buffer not able to be allocated\n");
	}

//	for(i = 0; i < inMsg->len; i++) {
//		printf("%c", *(inMsg->buffer + i));
//	}

	printf("\n");

	return;
}

//requires: pre-allocated char buffer
void sendMessage(msg * sendMsg){
	int i;
	unsigned char msgLen[4];

	if(sendMsg->buffer == NULL || uart == NULL){
		printf("Error in sendMessage, buffer or uart is null!");
		return;
	}

	// Start with the android ID, since we are interfacing many androids
	fputc((unsigned char) sendMsg->androidID, uart);

	printf("starting to send message, with length: %d\n", sendMsg->len);

	// Start with the number of bytes in our message
	for(i = ((sizeof(msgLen) / sizeof(msgLen[0])) - 1); i >= 0; i--) {
		msgLen[i] = (sendMsg->len  >> i*8) & (0xFF);
		//printf("msgLen[i] = %d\n", msgLen[i]);
		fputc(msgLen[i], uart);
	}

	fputc(sendMsg->cmd, uart);

	// Now send the actual message to the Middleman
	fwrite(sendMsg->buffer, sizeof(char), sendMsg->len, uart);

	fflush(uart);
}

void passMsg(msg * passMsg) {
	if(passMsg->buffer == NULL || uart == NULL){
		printf("Error in sendMessage, buffer or uart is null!");
		return;
	}

	char * msgString= (char *)passMsg->buffer;
	unsigned int yPos = 1;
	unsigned int xPos;

	switch( *(msgString++)) {
	case 0:
		printf("about to write to screen!\n");
		alt_up_char_buffer_clear(char_buffer);
		xPos = (SCREEN_CHAR_WIDTH / 2) - (int)(strlen(msgString) / 2);
		alt_up_char_buffer_string(char_buffer, msgString , xPos, yPos);
		break;
	/*case connUserIDs[0]:
		break;
	case connUserIDs[1]:
		break;
	case connUserIDs[2]:
		break;
	case connUserIDs[3]:
		break;
	case connUserIDs[4]:
		break; */
	}

}
