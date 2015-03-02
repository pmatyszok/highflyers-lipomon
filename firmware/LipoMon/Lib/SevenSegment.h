/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#ifndef SEVENSEGMENT_H_
#define SEVENSEGMENT_H_

#include "../BoardDefines.h"

#define LED_DIG_PORT	PORTB
#define LED_DIG_DDR		DDRB
#define LED_D1		0
#define LED_D2		1
#define LED_D3		2
#define LED_D4		3

#define LED_SEG_PORT	PORTC
#define LED_SEG_DDR		DDRC

#define LED_DOT	7
#define LED_A	0
#define LED_B	1
#define LED_C	2
#define LED_D	3
#define LED_E	4
#define LED_F	5
#define LED_G	6

typedef struct
{
	uint8_t d1;		/** Integer value of voltage (1 in 1.234). */
	uint8_t d_10;	/** 1/10 value of voltage (2 in 1.234). */
	uint8_t d_100;	/** 1/100 value of voltage (3 in 1.234). */
	uint8_t d_1000;	/** 1/1000 value of voltage (4 in 1.234). */
} Voltage;

extern void LedInit();
extern void LedClear();
extern void LedPrintVoltage(Voltage value);
//extern void LedPrintVoltage(uint8_t *v);

#endif /* SEVENSEGMENT_H_ */