#ifndef __BMP_H__
#define __BMP_H__

#include<stdio.h>
#include<stdlib.h>
#include "vga.h"
#include "sd_card.h"
#include "utilities.h"

#define BYTES_PER_PIXEL 3
#define DE2_BYTES_PER_PIXEL 2

typedef struct {
	unsigned short int type;                 /* Magic identifier            */
	unsigned int size;                       /* File size in bytes          */
	unsigned short int reserved1, reserved2;
	unsigned int offset;                     /* Offset to image data, bytes */
} HEADER;

typedef struct {
   unsigned int size;               /* Header size in bytes      */
   int width,height;                /* Width and height of image */
   unsigned short int planes;       /* Number of color planes   */
   unsigned short int bits;         /* Bits per pixel            */
   unsigned int compression;        /* Compression type          */
   unsigned int imagesize;          /* Image size in bytes       */
   int xresolution,yresolution;     /* Pixels per meter          */
   unsigned int ncolors;           /* Number of colors         */
   unsigned int importantcolors;   /* Important colors         */
} INFOHEADER;

typedef struct {
	HEADER header;
	INFOHEADER infoheader;
	short int *color;
} BMP;

void parseBmp(char *fileName, BMP *bmp);
void drawBmp(BMP *bmp, int x, int y);
void eraseBmp(BMP *bmp, int x, int y);

void receiveToken (char *buffer, BMP *bmp);
void receiveTokenPixArr (unsigned char *buffer, BMP *bmp);
unsigned char readByteChar(char * buffer);
short int readWordChar(char * buffer);
int readDWordChar(char * buffer);

#endif
