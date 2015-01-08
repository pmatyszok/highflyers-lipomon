#ifndef BOARDDEFINES_H_
#define BOARDDEFINES_H_

#define F_CPU 1000000UL  // 1 MHz
#define USART_BAUDRATE 9600

#include <avr/io.h>
#include <util/delay.h>
#include <avr/interrupt.h>
#include <util/setbaud.h>

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

#endif /* BOARDDEFINES_H_ */