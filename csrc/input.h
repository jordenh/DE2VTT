#ifndef INPUT_H_
#define INPUT_H_

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>

#include "message.h"
#include "io.h"
#include "system.h"
#include "altera_nios2_qsys_irq.h"
#include "sys/alt_irq.h"

void handleKeyInput(void);

void handleSwitchInput(void);

#endif /* INPUT_H_ */
