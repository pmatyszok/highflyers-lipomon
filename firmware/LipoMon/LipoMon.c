/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#include "BoardDefines.h"
//#include "Lib/Uart.h"
#include "Lib/Adc.h"
#include "Lib/SevenSegment.h"

#define CHANNELS 2

void ShowChannel(uint8_t channel)
{
	DDRD |= (1<<PORTD6)|(1<<PORTD7);	// Set port direction
	uint8_t val = (channel+1)<<6;
	PORTD |= (1<<PORTD6)|(1<<PORTD7);
	PORTD &= ~(val);
}

volatile uint8_t channel = 0;
volatile uint16_t result[CHANNELS];

int main(void)
{
	LedInit();
	AdcInit();
	
	Voltage v;
	
	for(uint8_t i=0; i<CHANNELS; ++i)
	{
		result[i] = 0x0000;
	}
	
	uint16_t ticker = 0;
	
    while(1)
    {
		if (ticker == 0)
	    {
		    if (++channel > CHANNELS)
		    channel = 0;
	    }
		
	    ShowChannel(channel);
	    result[channel] = AdcTakeMeasure(channel);
		
		uint32_t tmp = (uint32_t)result[channel] * ADC_INCREMENT;
	    v.d1 = tmp / ADC_DIVIDER;
	    v.d_10 = (tmp / (ADC_DIVIDER/10))%10;
	    v.d_100 = (tmp / (ADC_DIVIDER/100))%10;
	    v.d_1000 = (tmp / (ADC_DIVIDER/1000))%10;
				
		LedPrintVoltage(v);
		
		if (++ticker > 1024)
			ticker = 0;
    }
}

