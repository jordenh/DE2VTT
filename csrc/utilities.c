#include "utilities.h"

char dmID = 0;

//return a character array given an input integer and the size of the array to create
char * IntToCharBuf(unsigned int inputInt, unsigned int numChars) {
	char * charBuf = malloc(numChars * sizeof(char));
	int i;

	if(charBuf) {
		for(i = (numChars - 1); i >= 0; i--) {
			charBuf[i] = (inputInt  >> i*8) & (0xFF);
		}
	}
	return charBuf;
}

