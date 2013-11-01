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
	setupIO();

	initHardwareTimer();

	return 0;
}

int main() {
	//********* new ******************************************
	/*
	int i;
	unsigned char data;
	unsigned char parity;
	unsigned char message[] = "EECE381 is so much fun";

	printf("UART Initialization\n");
	alt_up_rs232_dev* uart = alt_up_rs232_open_dev(RS232_0_NAME);

	printf("Clearing read buffer to start\n");
	while (alt_up_rs232_get_used_space_in_read_FIFO(uart)) {
		alt_up_rs232_read_data(uart, &data, &parity);
	}

	printf("Sending the message to the Middleman\n");

	// Start with the number of bytes in our message
	alt_up_rs232_write_data(uart, (unsigned char) strlen(message));

	// Now send the actual message to the Middleman

	for (i = 0; i < strlen(message); i++) {
		alt_up_rs232_write_data(uart, message[i]);
	}

	// Now receive the message from the Middleman

	printf("Waiting for data to come back from the Middleman\n");
	while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0)
		;

	// First byte is the number of characters in our message

	alt_up_rs232_read_data(uart, &data, &parity);
	int num_to_receive = (int) data;

	printf("About to receive %d characters:\n", num_to_receive);

	for (i = 0; i < num_to_receive; i++) {
		while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0)
			;
		alt_up_rs232_read_data(uart, &data, &parity);

		printf("%c", data);
	}
	printf("\n");
	printf("Message Echo Complete\n");

	//***********************************
	 */

	if (init() == -1)
		return -1;

	startHardwareTimer();

	// main game loop;
	while (1) {
		if (hasHardwareTimerExpired() == 1) {
			startHardwareTimer();

			handleKeyInput();
			handleSwitchInput();

			swapBuffers();

			//playEpicMusic();

		}
	}

	freeBmps();
	return 0;
}


