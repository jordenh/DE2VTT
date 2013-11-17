#ifndef __MAP_H__
#define __MAP_H__

#include "vga.h"
#include "bmp.h"
#include "token.h"

extern BMP map;

void receiveMap();
void partialMapReDraw(int x, int y, int width, int height);
void drawMap();

#endif
