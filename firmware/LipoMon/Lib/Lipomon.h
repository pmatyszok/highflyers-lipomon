/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#ifndef LIPOMON_H_
#define LIPOMON_H_

#include "Adc.h"
#include "Uart.h"

/**
 * Structure holding floating point number from range 0.000 -> 9.999
 * in form of integers.
 */
typedef struct
{
	uint8_t d1;		/** Integer value of voltage (1 in 1.234). */
	uint8_t d_10;	/** 1/10 value of voltage (2 in 1.234). */
	uint8_t d_100;	/** 1/100 value of voltage (3 in 1.234). */
	uint8_t d_1000;	/** 1/1000 value of voltage (4 in 1.234). */
} Voltage;

/**
 * Converts value taken from ADC to Voltage structure.
 * @param adcMeasurement Value taken from ADC.
 * @returns Voltage ADC measurement converted to Voltage structure.
 */
extern Voltage ConvertAdcToVoltage(uint16_t adcMeasurement);

/**
 * Sends ADC measurement via USART.
 * @param channel Number of channel for which measurement was taken.
 * @param measurement Measurement taken from ADC.
 */
extern void SendAdcResultViaUsart(uint8_t chanel, uint16_t measurement);

#endif /* LIPOMON_H_ */