#include "map.h"

BMP map;
int allocated_map = 0;

void receiveMap(unsigned char *buffer) {
	if(allocated_map) {
		free(map.color);
	} else {
		allocated_map = 1;
	}

	receiveTokenPixArr(buffer, &map);
}

void partialMapReDraw(int x, int y, int width, int height) {
	int i, j, offset;
	short int color;

	for(i = 0; i < height; i++) {
		offset = (y + i) * map.infoheader.width + x;

		for(j = 0; j < width; j++) {
			color = map.color[offset + j];
			drawPixelFast(x+j, y+i, color);
		}
	}
}

void drawMap() {
	drawBmp(&map, 0, 0);
	drawAllTokens();
}
