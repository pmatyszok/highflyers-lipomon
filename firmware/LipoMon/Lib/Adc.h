/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#ifndef ADC_H_
#define ADC_H_

#include "../BoardDefines.h"

// V_ref = 2.56V
#define ADC_INCREMENT 25			// round(V_ref/1024 * ADC_MULTIPLIER)
#define ADC_MULTIPLIER 10000L

/**
 * Initializes ADC unit.
 */
extern void AdcInit();

/**
 * Returns measurement taken from ADC.
 * @param channel Channel from which measurement should be taken.
 * @return Measurement taken from ADC.
 */
extern uint16_t AdcTakeMeasure(uint8_t channel);

#endif /* ADC_H_ */