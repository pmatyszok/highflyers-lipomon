/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#ifndef BOARDDEFINES_H_
#define BOARDDEFINES_H_

#define F_CPU 8000000UL  // 1 MHz
#define BAUD 9600
#define USART_BAUDRATE BAUD

#include <avr/io.h>
#include <util/delay.h>
#include <avr/interrupt.h>
#include <util/setbaud.h>

#include "Devices/ATmega16A.h"

#endif /* BOARDDEFINES_H_ */