/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#include "../BoardDefines.h"
#include "Adc.h"

void AdcInit() 
{
	// Direction of the port
	DDRA &= ~((1<<0)|(1<<1)|(1<<2)|(1<<3));
	
	// Enable ADC
	ADCSRA |= (1<<ADEN);
	
	// Vref = Internal 2.56V
	ADMUX |= (1<<REFS1) | (1<<REFS0);	
	
	// 10-bit mode
	ADCSRA &= ~(1<<ADLAR);	
	
	// Set prescaller to 128 (8M / 128 = 62.5 kHz, must be 50k-200kHz)	
	ADCSRA |= (1<<ADPS2)|(1<<ADPS1)|(1<<ADPS0);	
}

uint16_t AdcTakeMeasure(uint8_t channel) 
{
	ADMUX &= ~((1<<ADC0_BIT)|(1<<ADC1_BIT)|(1<<ADC2_BIT));	// Clear actual source
	ADMUX = (ADMUX&0xF4)|channel;	// Set source to channel
	ADCSRA |= (1<<ADSC);		// Start measurement
	while(ADCSRA & (1<<ADSC));	// Wait until task ends
	return ADCW;
}
