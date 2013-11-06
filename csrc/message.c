
#include "message.h"

int connUserIDs[5] = {0,0,0,0,0};

FILE* uart;

void setupMessage(void) {
	//printf("UART Initialization\n");
	uart = fopen("/dev/uart_0", "r+"); //should set to RS232_0_NAME

	if(uart == NULL) {
		printf("ERROR - uart not opened properly");
	}
}

// checks if the ID is saved in the connUserIDs array, and returns true if it exists, false otherwise.
unsigned int isIDSaved(message inMsg) {
	int i;
	for(i = 0; i < sizeof(connUserIDs) / sizeof(connUserIDs[0]) ; i++){
			if(inMsg.androidID == connUserIDs[i]){
				printf("android %d sending to DE2 already in system\n", connUserIDs[i]);
				return 1;
			}
	}
	return 0;
}

// stores an ID in the connUsersIDs array, if room available. Returns fals if not added, true if added.
unsigned int storeNewID(int ID) {
	int i;

	for(i = 0; i < sizeof(connUserIDs) / sizeof(connUserIDs[0]) ; i++){
		if(connUserIDs[i] == 0) {
			printf("DE2 communicating with new android - ID %d\n", ID);
			connUserIDs[i] = ID;
			return 1;
		}
	}
	return 0;
}

message getMessage(void){
	int i;
	unsigned char msgLen[4];
	message inMsg;

	//while (fgetc(uart) == 0) {}; // TBD - have this step time out. -- Probably need to
	//remove this line, since steven's middleman uart likely isnt passing 0's constatnly

	//obtain android ID
	inMsg.androidID = (int) fgetc(uart);
	printf("I got msg from ID %d", inMsg.androidID);

	if(isIDSaved(inMsg) == 0) {
		if (storeNewID(inMsg.androidID)  == 0)
			printf("Error adding Android ID, ID array full\n");
	}

	//obtain length
	for(i = 0; i < (sizeof(msgLen) / sizeof(msgLen[0])); i++) {
		msgLen[i] = fgetc(uart);
		inMsg.len += pow(256,((sizeof(msgLen) / sizeof(msgLen[0])) - 1 - i)) * (int) msgLen[i];
	}

	printf("About to receive %d characters:\n", inMsg.len);

	inMsg.buffer = malloc(inMsg.len * sizeof(char));

	fread(inMsg.buffer, sizeof(char), inMsg.len, uart);

	for(i = 0; i < inMsg.len; i++) {
		printf("%c", *(inMsg.buffer));
	}

	printf("\n");

	return inMsg;
}

//requires: pre-allocated char buffer
void sendMessage(message sendMsg){
	int i;
	unsigned char msgLen[4];

	if(sendMsg.buffer == NULL){
		return;
	}

	// Start with the android ID, since we are interfacing many androids
	fputc((unsigned char) sendMsg.androidID, uart);

	printf("starting to send message, with length: %d\n", sendMsg.len);

	// Start with the number of bytes in our message
	for(i = 0; i < (sizeof(msgLen) / sizeof(msgLen[0])); i++) {
		msgLen[i] = (int)(sendMsg.len / pow(256,((sizeof(msgLen) / sizeof(msgLen[0])) - 1 - i))) % 256;
		printf("msgLen[i] = %d", msgLen[i]);
		fputc(msgLen[i], uart);
	}

	// Now send the actual message to the Middleman
	fwrite(sendMsg.buffer, sizeof(char), sendMsg.len, uart);
}

