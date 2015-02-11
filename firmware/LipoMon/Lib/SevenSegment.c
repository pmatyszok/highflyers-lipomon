/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#include "../BoardDefines.h"
#include "SevenSegment.h"

uint8_t digits[] = {
	~((1<<LED_A)|(1<<LED_B)|(1<<LED_C)|(1<<LED_D)|(1<<LED_E)|(1<<LED_F)),
	~((1<<LED_B)|(1<<LED_C)),
	~((1<<LED_A)|(1<<LED_B)|(1<<LED_G)|(1<<LED_D)|(1<<LED_E)),
	~((1<<LED_A)|(1<<LED_B)|(1<<LED_G)|(1<<LED_C)|(1<<LED_D)),
	~((1<<LED_B)|(1<<LED_C)|(1<<LED_F)|(1<<LED_G)),
	~((1<<LED_A)|(1<<LED_C)|(1<<LED_D)|(1<<LED_F)|(1<<LED_G)),
	~((1<<LED_A)|(1<<LED_C)|(1<<LED_D)|(1<<LED_E)|(1<<LED_F)|(1<<LED_G)),
	~((1<<LED_A)|(1<<LED_B)|(1<<LED_C)),
	~((1<<LED_A)|(1<<LED_B)|(1<<LED_C)|(1<<LED_D)|(1<<LED_E)|(1<<LED_F)|(1<<LED_G)),
	~((1<<LED_A)|(1<<LED_B)|(1<<LED_C)|(1<<LED_D)|(1<<LED_F)|(1<<LED_G)),
	0xFF
};

void LedInit()
{
	// Output direction of selector pins
	LED_DIG_DDR |= (1<<LED_D1)|(1<<LED_D2)|(1<<LED_D3)|(1<<LED_D4);
	
	// Output direction of segments pin
	LED_SEG_DDR |= 0xFF;
	
	// Turn off all digits
	LED_DIG_PORT |= (1<<LED_D1)|(1<<LED_D2)|(1<<LED_D3)|(1<<LED_D4);
	
	// Turn off all segments
	LED_SEG_PORT = 0xFF;
	
}

void LedClearDigit() 
{
	LED_SEG_PORT = 0xFF;	
}

/*void LedPrintVoltage(uint8_t *v)
{
	static uint8_t activeDigit = 0;
	
	if(activeDigit > 3)
	{
		activeDigit = 0;
	}
	
	LED_DIG_PORT |= ((1<<LED_D1)|(1<<LED_D2)|(1<<LED_D3)|(1<<LED_D4));
	LED_DIG_PORT &= ~(1<<(LED_D1+activeDigit));
	
	LED_SEG_PORT = digits[v[activeDigit]];

	if (activeDigit == 0) 
	{
		LED_SEG_PORT &= ~(1<<LED_DOT);
	}
	
	activeDigit++;
	
	_delay_ms(1);
}*/

void LedPrintVoltage(Voltage value)
{
	static uint8_t activeDigit = 0;
	
	if(activeDigit > 3)
	{
		activeDigit = 0;
	}
	
	LED_DIG_PORT |= ((1<<LED_D1)|(1<<LED_D2)|(1<<LED_D3)|(1<<LED_D4));
	LED_DIG_PORT &= ~(1<<(LED_D1+activeDigit));
	
	switch(activeDigit) {
		case 0:
		LED_SEG_PORT = digits[value.d1];
		LED_SEG_PORT &= ~(1<<LED_DOT);
		break;
		
		case 1:
		LED_SEG_PORT = digits[value.d_10];
		break;
		
		case 2:
		LED_SEG_PORT = digits[value.d_100];
		break;
		
		case 3:
		LED_SEG_PORT = digits[value.d_1000];
		break;
	}
	
	activeDigit++;
	
	_delay_ms(1);
}