#ifndef __MAP_H__
#define __MAP_H__

#include "bmp.h"
#include "token.h"

extern BMP map;

void receiveMap();
void partialMapReDraw(int x, int y);
void drawMap();

#endif
