/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#ifndef ADC_H_
#define ADC_H_

#define ADCINPUT	PORTA0

#define ADC_INCREMENT	25
#define ADC_DIVIDER		10000L

extern void AdcInit();
extern uint16_t AdcTakeMeasure(uint8_t chanel);

#endif /* ADC_H_ */