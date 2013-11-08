#include <stdio.h>
#include <stdlib.h>
#include "audio.h"
#include "timer.h"
#include "sd_card.h"
#include "vga.h"
#include "bmp.h"
#include "input.h"
#include "message.h"
#include "io.h"
#include "system.h"
#include "altera_nios2_qsys_irq.h"
#include "sys/alt_irq.h"



int init(void) {
	if (openSdCard() == -1) {
		printf("Error: Failed to open sd card\n");
		return -1;
	} else {
		printf("Opened SD card\n");
	}

	initVga();
	//parseBmps();
	setupAudio();
	setupMessage();

	initHardwareTimer();

	return 0;
}

int main() {
	message msg;
	int statusInt;

	if (init() == -1)
		return -1;

	startHardwareTimer();

	// main game loop;
	while (1) {
		//receive msg
		//execute command
		if(msg.buffer != NULL) {
			free(msg.buffer);
		}
		msg = getMessage();
		statusInt = executeCmd(msg.cmd);

		if(statusInt == -1) {
			printf("error occured in executing Command.\n");
		}


		/*if (hasHardwareTimerExpired() == 1) {
			startHardwareTimer();

			handleKeyInput();
			handleSwitchInput();
			//playEpicMusic();
			//Check if message to receive?
		}*/
	}

	freeBmps();
	return 0;
}


