/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#include "Lipomon.h"

Voltage ConvertAdcToVoltage(uint16_t adcMeasurement)
{	
	uint32_t tmp = (uint32_t)adcMeasurement * ADC_INCREMENT;
	
	Voltage v;
	v.d1 = tmp / ADC_MULTIPLIER;
	v.d_10 = (tmp / (ADC_MULTIPLIER/10))%10;
	v.d_100 = (tmp / (ADC_MULTIPLIER/100))%10;
	v.d_1000 = (tmp / (ADC_MULTIPLIER/1000))%10;
	
	return v;
}

void SendAdcResultViaUsart(uint8_t channel, uint16_t measurement)
{
	Voltage result = ConvertAdcToVoltage(measurement);
	
	// Send channel number (zero based)
	UartSendByte('c');
	UartSendIntAsAscii(channel);
	
	// Send value as ASCII
	UartSendByte('v');
	UartSendIntAsAscii(result.d1);
	UartSendIntAsAscii(result.d_10);
	UartSendIntAsAscii(result.d_100);
	UartSendIntAsAscii(result.d_1000);
	
	// Send end of line
	UartSendByte('\r');
	UartSendByte('\n');
}
