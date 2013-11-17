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

void partialMapRedraw(int x, int y){

}

void drawMap() {
	drawBmp(&map, 0, 0);
	drawAllTokens();
}
