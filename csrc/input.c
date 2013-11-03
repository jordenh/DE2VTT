#include "input.h"

message interMsg;
alt_up_rs232_dev* uart;


void setupIO(void) {
	//printf("UART Initialization\n");
	uart = alt_up_rs232_open_dev(RS232_0_NAME);

	unsigned char data;
	unsigned char parity;
	printf("Clearing read buffer to start\n");
	while (alt_up_rs232_get_used_space_in_read_FIFO(uart)) {
		alt_up_rs232_read_data(uart, &data, &parity);
	}

	alt_up_rs232_write_data(uart, 'a');
}

void handleKeyInput(void){
	static char keyInput;
	static short int edgeDetect0 = 0;
	static short int edgeDetect1 = 0;
	static short int edgeDetect2 = 0;
	static short int edgeDetect3 = 0;

	keyInput = IORD_8DIRECT(KEYS_BASE, 0);
	char key0 = keyInput & 0x01;
	char key1 = keyInput & 0x02;
	char key2 = keyInput & 0x04;
	char key3 = keyInput & 0x08;

	//functionality for keys being held
	if(key1) {

	} else if (key2) {

	} else {

	}

	//functionality for keys being pressed.
	if (!key0 && (edgeDetect0 == 0)) {
		edgeDetect0 = 1;

		if(interMsg.buffer == NULL) {
			free(interMsg.buffer);
		}
		interMsg = getMessage();
	} else if (key0 && (edgeDetect0 == 1)) {
		edgeDetect0 = 0;
	}

	if (!key1 && (edgeDetect1 == 0)) {
		edgeDetect1 = 1;

		if(interMsg.buffer != NULL) {
			sendMessage(interMsg);
		}
	} else if (key1 && (edgeDetect1 == 1)) {
		edgeDetect1 = 0;
	}

	if (!key2 && (edgeDetect2 == 0)) {
		edgeDetect2 = 1;

		free(interMsg.buffer);
	} else if (key2 && (edgeDetect2 == 1)) {
		edgeDetect2 = 0;
	}

	if (!key3 && (edgeDetect3 == 0)) {
		edgeDetect3 = 1;
	} else if (key3 && (edgeDetect3 == 1)) {
		edgeDetect3 = 0;
	}
}

//Stub for Switch Handling
void handleSwitchInput(void){
	static char SWInput;
	static short int prevSwitchState = 0;

	SWInput = IORD_8DIRECT(SWITCHES_BASE, 0);

	if ((SWInput & 0x80) != 0) {
		if(prevSwitchState == 0){

		}
		prevSwitchState = 1;
	} else {
		if(prevSwitchState == 1){

		}
		prevSwitchState = 0;
	}

}



message getMessage(void){
	int i;
	unsigned char data;
	unsigned char parity;
	unsigned char msgLen[4];
	message inMsg;

	while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0) {}; // TBD - have this step time out.

	//obtain android ID
	alt_up_rs232_read_data(uart, &data, &parity);
	inMsg.androidID = (int) data;

	if(isIDSaved(inMsg.androidID) == false) {
		if (storeNewID(inMsg.androidID)  == false)
			printf("Error adding Android ID, ID array full\n");
	}

	//obtain length
	for(i = 0; i < (sizeof(msgLen) / sizeof(msgLen[0])); i++) {
		alt_up_rs232_read_data(uart, (msgLen + i), &parity);
		inMsg.len += pow(16,((sizeof(msgLen) / sizeof(msgLen[0])) - 1 - i)) * (int) msgLen[i];
	}

	printf("About to receive %d characters:\n", inMsg.len);

	inMsg.buffer = malloc(inMsg.len * sizeof(char));
	for (i = 0; i < inMsg.len; i++) {
		while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0)
			;
		alt_up_rs232_read_data(uart, (inMsg.buffer + i), &parity);

		printf("%c", *(inMsg.buffer + i));
	}
	printf("\n");

	return inMsg;
}

//requires: pre-allocated char buffer
void sendMessage(message sendMsg){
	int i;
	unsigned char data;
	unsigned char parity;
	unsigned char msgLen[4];

	// Start with the android ID, since we are interfacing many androids
	alt_up_rs232_write_data(uart, (unsigned char) sendMsg.androidID);

	printf("starting to send message, with length: %d\n", strlen(sendMsg.buffer));

	// Start with the number of bytes in our message
	alt_up_rs232_write_data(uart, (unsigned char) strlen(sendMsg.buffer));
	for(i = 0; i < (sizeof(msgLen) / sizeof(msgLen[0])); i++) {
		msgLen[i] = (sendMsg.len / pow(16,((sizeof(msgLen) / sizeof(msgLen[0])) - 1 - i))) % 16;
		alt_up_rs232_write_data(uart, msgLen[i]);
	}

	// Now send the actual message to the Middleman
	for (i = 0; i < strlen(sendMsg.buffer); i++) {
		alt_up_rs232_write_data(uart, (sendMsg.buffer)[i]);
	}


}


