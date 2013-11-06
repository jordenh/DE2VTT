#include "input.h"

message interMsg;

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


	} else if (key0 && (edgeDetect0 == 1)) {
		edgeDetect0 = 0;
		if(interMsg.buffer == NULL) {
			free(interMsg.buffer);
		}
		interMsg = getMessage();
	}

	if (!key1 && (edgeDetect1 == 0)) {
		edgeDetect1 = 1;

	} else if (key1 && (edgeDetect1 == 1)) {
		edgeDetect1 = 0;

		//Temporary Hard coded stuff:
	//	interMsg.androidID = 2;
	//	interMsg.len = 5;
	//	interMsg.buffer = "hello";


		if(interMsg.buffer != NULL) {
			sendMessage(interMsg);
		}
	}

	if (!key2 && (edgeDetect2 == 0)) {
		edgeDetect2 = 1;
	} else if (key2 && (edgeDetect2 == 1)) {
		edgeDetect2 = 0;
		free(interMsg.buffer);
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






