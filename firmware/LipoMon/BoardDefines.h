/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#ifndef BOARDDEFINES_H_
#define BOARDDEFINES_H_

#include <avr/io.h>

//#include "Devices/ATmega16A.h"
#include "Devices/ATmega32A.h"
//#include "Devices/ATmega88PA.h"

#define BAUD 9600
#define USART_BAUDRATE BAUD

#include <util/delay.h>
#include <avr/interrupt.h>
#include <util/setbaud.h>

#endif /* BOARDDEFINES_H_ */