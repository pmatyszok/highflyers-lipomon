/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#ifndef ATMEGA88PA_H_
#define ATMEGA88PA_H_

#define F_CPU 1000000UL

#define ADCINPUT PORTA0

// UART ports redefinition
#define UBRRL UBRR0L
#define UBRRH UBRR0H
#define UCSRB UCSR0B
#define TXEN TXEN0
#define RXEN RXEN0
#define RXCIE RXCIE0
#define UCSRA UCSR0A
#define UDRE UDRE0
#define UDR UDR0
#define UCSRA UCSR0A
#define RXC RXC0

#endif /* ATMEGA88PA_H_ */