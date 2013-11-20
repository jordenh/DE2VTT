#include <stdio.h>
#include <stdlib.h>
#include "audio.h"
#include "timer.h"
#include "sd_card.h"
#include "vga.h"
#include "input.h"
#include "message.h"
#include "command.h"
#include "token.h"
#include "io.h"
#include "system.h"
#include "altera_nios2_qsys_irq.h"
#include "sys/alt_irq.h"

int init() {
	if (openSdCard() == -1) {
		printf("Error: Failed to open sd card\n");
		return -1;
	} else {
		printf("Opened SD card\n");
	}

	initVga();
	setupAudio();
	setupMessage();
	initTokens();

	initHardwareTimer();

	return 0;
}

int main() {
	msg msg_m;
	msg_m.buffer = NULL;
	int statusInt;

	if (init() == -1)
		return -1;

	//startHardwareTimer();

	while (1) {
		if(msg_m.buffer != NULL) {
			free(msg_m.buffer);
			msg_m.buffer = NULL;
		}

		drawUserIDs();
		printf("Obtaining message\n");
		getMessage(&msg_m);

		printf("Executing message command\n");
		statusInt = executeCmd(&msg_m);

		if(statusInt == -1) {
			printf("error occurred in executing Command.\n");
		} else {
			printf("Completed message command\n");
		}

		/*if (hasHardwareTimerExpired() == 1) {
			startHardwareTimer();

			handleKeyInput();
			handleSwitchInput();
			//playEpicMusic();
			//Check if message to receive?
		}*/
	}

	return 0;
}
