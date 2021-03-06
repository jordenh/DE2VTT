#include "bmp.h"

//purpose: parse a BMP file and package in a BMP structure
void parseBmp (char *fileName, BMP *bmp) {
	int i, j, k;
	char b, g, r;
	int pixels, rowOffset, offset;
	short int fh;

	fh = openFile(fileName);

	bmp->header.type = readWord(fh);
	bmp->header.size = readDWord(fh);
	bmp->header.reserved1 = readWord(fh);
	bmp->header.reserved2 = readWord(fh);
	bmp->header.offset = readDWord(fh);

	bmp->infoheader.size = readDWord(fh);
	bmp->infoheader.width = readDWord(fh);
	bmp->infoheader.height = readDWord(fh);
	bmp->infoheader.planes = readWord(fh);
	bmp->infoheader.bits = readWord(fh);
	bmp->infoheader.compression = readDWord(fh);
	bmp->infoheader.imagesize = readDWord(fh);
	bmp->infoheader.xresolution = readDWord(fh);
	bmp->infoheader.yresolution = readDWord(fh);
	bmp->infoheader.ncolors = readDWord(fh);
	bmp->infoheader.importantcolors = readDWord(fh);

	pixels = bmp->infoheader.width * bmp->infoheader.height;
	bmp->color = malloc(BYTES_PER_PIXEL * pixels);

	for(i = 0; i < bmp->infoheader.height; i++) {
		rowOffset = i*bmp->infoheader.width;
		for(j = 0; j < bmp->infoheader.width; j++ ){
			offset = pixels - rowOffset - j - 1;

			b = (readByte(fh) & 0xF1) >> 3;
			g = (readByte(fh) & 0xFC) >> 2;
			r = (readByte(fh) & 0xF1) >> 3;

			//Filter out the pink pixels
			if(b == 0x1E && g == 0 && r == 0x1E) {
				bmp->color[offset] = 0x0;
			} else {
				bmp->color[offset] = (r << 11) | (g << 5) | b;
			}
		}

		if((BYTES_PER_PIXEL*bmp->infoheader.width) % 4 != 0) {
			for (k = 0; k <  (4 - ((BYTES_PER_PIXEL*bmp->infoheader.width) % 4)); k++) {
				readByte(fh);
			}
		}
	}

	closeFile(fh);
}

//purpose: Recieve a token in WING from a given bmp buffer and package in a BMP struct
void receiveToken (char *buffer, BMP *bmp) {
	int i, j, k;
	char b, g, r;
	int pixels, rowOffset, offset;
	short int fh;

	bmp->header.type = readWordChar(buffer);
	buffer += 2;
	bmp->header.size = readDWordChar(buffer);
	buffer += 4;
	bmp->header.reserved1 = readWordChar(buffer);
	buffer += 2;
	bmp->header.reserved2 = readWordChar(buffer);
	buffer += 2;
	bmp->header.offset = readDWordChar(buffer);
	buffer += 4;

	bmp->infoheader.size = readDWordChar(buffer);
	buffer += 4;
	bmp->infoheader.width = readDWordChar(buffer);
	buffer += 4;
	bmp->infoheader.height = readDWordChar(buffer);
	buffer += 4;
	bmp->infoheader.planes = readWordChar(buffer);
	buffer += 2;
	bmp->infoheader.bits = readWordChar(buffer);
	buffer += 2;
	bmp->infoheader.compression = readDWordChar(buffer);
	buffer += 4;
	bmp->infoheader.imagesize = readDWordChar(buffer);
	buffer += 4;
	bmp->infoheader.xresolution = readDWordChar(buffer);
	buffer += 4;
	bmp->infoheader.yresolution = readDWordChar(buffer);
	buffer += 4;
	bmp->infoheader.ncolors = readDWordChar(buffer);
	buffer += 4;
	bmp->infoheader.importantcolors = readDWordChar(buffer);
	buffer += 4;

	pixels = bmp->infoheader.width * bmp->infoheader.height;
	bmp->color = malloc(BYTES_PER_PIXEL * pixels);

	for(i = 0; i < bmp->infoheader.height; i++) {
		rowOffset = i*bmp->infoheader.width;
		for(j = 0; j < bmp->infoheader.width; j++ ){
			offset = pixels - rowOffset - j - 1;

			b = (readByteChar(buffer++) & 0xF1) >> 3;
			g = (readByteChar(buffer++) & 0xFC) >> 2;
			r = (readByteChar(buffer++) & 0xF1) >> 3;

			//Filter out the pink pixels
			if(b == 0x1E && g == 0 && r == 0x1E) {
				bmp->color[offset] = 0x0;
			} else {
				bmp->color[offset] = (r << 11) | (g << 5) | b;
			}
		}

		if((BYTES_PER_PIXEL*bmp->infoheader.width) % 4 != 0) {
			for (k = 0; k <  (4 - ((BYTES_PER_PIXEL*bmp->infoheader.width) % 4)); k++) {
				readByteChar(buffer++);
			}
		}
	}

	closeFile(fh);
}

//purpose: Recieve a token in WING from a given pixel array buffer and package in a BMP struct
//		This function makes use of knowing how the pixel arrays are created on the Android devices 
//		(sent with xxxx/yyyyy/actualPixelArrayIn565Format)
void receiveTokenPixArr (unsigned char *buffer, BMP *bmp) {
	unsigned char sizeArr[4];
	int i, j;
	char byte1, byte2;
	int pixels, rowOffset, offset;
	unsigned int cursor = 0;

	bmp->infoheader.width = 0;
	bmp->infoheader.height = 0;

	//obtain width
	for(i = ((sizeof(sizeArr) / sizeof(sizeArr[0])) - 1); i >= 0; i--) {
		sizeArr[i] = buffer[cursor++];
		printf("received: sizeArr[i] %d\n", sizeArr[i]);
		bmp->infoheader.width += (0xFF & sizeArr[i]) << i*8;
	}

	//obtain height
	for(i = ((sizeof(sizeArr) / sizeof(sizeArr[0])) - 1); i >= 0; i--) {
		sizeArr[i] = buffer[cursor++];
		printf("received: sizeArr[i] %d\n", sizeArr[i]);
		bmp->infoheader.height += (0xFF & sizeArr[i]) << i*8;
	}

	pixels = bmp->infoheader.width * bmp->infoheader.height;
	printf("pixels set to: %d\n", pixels);
	bmp->color = malloc(BYTES_PER_PIXEL * pixels);

	if(bmp->color) {
		for(i = 0; i < bmp->infoheader.height; i++) {
			rowOffset = i * bmp->infoheader.width;
			for(j = 0; j < bmp->infoheader.width; j++ ){
				offset = rowOffset + j;

				byte1 = buffer[cursor++];
				byte2 = buffer[cursor++];

				bmp->color[offset] = ((byte1 << 8) & 0xFF00) | (byte2 & 0xFF);
			}
		}
	} else {
		printf("Error, didn't allocate memory for token color\n");
	}
}

//return a byte from a character buffer
unsigned char readByteChar(char * buffer) {
	return *buffer;
}

//return 2 bytes in a short int from a character buffer
short int readWordChar(char * buffer) {
	short int byte1, byte2;

	byte1 = (short int)(*buffer);
	byte2 = (short int)(*(buffer+1));

	return ((unsigned short int)byte1 << 8) | ((unsigned short int)byte2 & 0x00FF);
}

//return a 4 bytes in an int from a character buffer
int readDWordChar(char * buffer) {
	short int byte1, byte2, byte3, byte4;

	byte1 = (short int)(*buffer);
	byte2 = (short int)(*(buffer+1));
	byte3 = (short int)(*(buffer+2));
	byte4 = (short int)(*(buffer+3));

	return ((unsigned short int)byte1 << 24) | ((unsigned short int)byte2 << 16) | ((unsigned short int)byte3 << 8) | (unsigned short int)byte4;
}

//draw a BMP at a given x/y location on the monitor, making use of drawPixelFast
void drawBmp (BMP *bmp, int x, int y) {
	int i,j;
	int offset;

	for(i = 0; i < bmp->infoheader.height; i++) {
		if(y + i < SCREEN_HEIGHT && y + i > 0) {
			offset = i * bmp->infoheader.width;

			for(j = 0; j < bmp->infoheader.width; j++){
				if(x + j >= SCREEN_WIDTH || x + j <= 0)
					continue;

				drawPixelFast(x + j, y + i, bmp->color[offset +j]);
			}
		}
	}
}

//draw black over a given BMP at a given x/y on the monitor, making use of drawPixelFast.
void eraseBmp (BMP *bmp, int x, int y) {
	int i,j;
	int offset;

	for(i = 0; i < bmp->infoheader.height; i++) {
		if(y + i < SCREEN_HEIGHT && y + i > 0) {
			offset = i * bmp->infoheader.width;

			for(j = 0; j < bmp->infoheader.width; j++){
				if(x + j >= SCREEN_WIDTH || x + j <= 0)
					continue;

				drawPixelFast(x + j, y + i, 0);
			}
		}
	}
}
