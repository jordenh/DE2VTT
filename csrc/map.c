#include "map.h"

BMP map;

void receiveMap(unsigned char *buffer) {
	receiveTokenPixArr(buffer, &map);
}

void drawMap() {
	drawBmp(&map, 0, 0);
}
