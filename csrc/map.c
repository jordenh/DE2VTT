#include "map.h"

BMP map;
int allocatedMap = 0;

void receiveMap(unsigned char *buffer) {
	if(allocatedMap) {
		free(map.color);
	} else {
		allocatedMap = 1;
	}

	receiveTokenPixArr(buffer, &map);
}

void partialMapReDraw(int x, int y, int width, int height) {
	int i, j, offset;
	short int color;

	if(allocatedMap) {
		for(i = 0; i < height; i++) {
			offset = (y + i) * map.infoheader.width + x;

			for(j = 0; j < width; j++) {
				color = map.color[offset + j];
				drawPixelFast(x+j, y+i, color);
			}
		}
	} else {
		for(i = 0; i < height; i++) {
			for(j = 0; j < width; j++) {
				drawPixelFast(x+j, y+i, 0);
			}
		}
	}
}

void drawMap() {
	drawBmp(&map, 0, 0);
	drawAllTokens();
}
