/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#include "BoardDefines.h"
#include "Lib/Uart.h"
#include "Lib/Adc.h"
#include "Lib/Lipomon.h"

#define CHANNELS 3

int main(void)
{
	UartInit();
	AdcInit();
	
    while(1)
    {
		for (int i=0; i < CHANNELS; ++i)
		{
			SendAdcResultViaUsart(i, AdcTakeMeasure(i));
		}
    }
}

