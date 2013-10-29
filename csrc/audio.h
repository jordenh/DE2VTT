#ifndef AUDIO_H
#define AUDIO_H

#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include "system.h"
#include "timer.h"
#include "sd_card.h"
#include "altera_up_avalon_audio_and_video_config.h"
#include "altera_up_avalon_audio.h"
#include "alt_types.h"
#include "sys/alt_irq.h"

#define PI 3.14159265359
#define NONE 0
#define LASER 1
#define PLAYER_DEATH 2
#define SHARK_DEATH 3
#define THEME 4
#define EPICMUSIC 5
#define FAIL -1
#define PLAYBUFFER_LEN 128

#define SONG_BUFFER_LEN 100000
#define SONG_MIN 0x100;
#define SONG_MAX 0x919060;

struct audioInfo{
	unsigned int *mainBuffer;
	unsigned int *volumeBuffer;
	unsigned int bufferLength;
	unsigned int playedWords;
	bool active;
	volatile unsigned int *playCursor;
};

void setupAudio(void);
void audioTest(void);

void readWavFile(char *wavFileName, struct audioInfo *info);
void playAudioMono(int length);
void playLaser(void);
void playPlayerDeath(void);
void playSharkDeath(void);
void playTheme(void);
void playPlayerDeath(void);
int setupAudioInterrupt(alt_up_audio_dev *audio, volatile int somethingForIrq);
void playAudio(unsigned int *leftBuffer, int leftLength, unsigned int *rightBuffer, int rightLength);
void loadLaser(void);
void loadPlayerDeath(void);
void loadSharkDeath(void);
void loadTheme(void);
void updateAudioWithVolume(char switchValues);
void changeBufferVolume(struct audioInfo *, char switchValues);
void stopTheme(void);


//Functions for project 2
void initializeEpicSong(void);
void playEpicMusic(void);

#endif
