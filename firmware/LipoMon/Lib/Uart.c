/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#include "../BoardDefines.h"
#include "Uart.h"

void UartInit(void)
{
	// Set baud rate
	UBRRL = BAUD_PRESCALE;// Load lower 8-bits into the low byte of the UBRR register
	UBRRH = (BAUD_PRESCALE >> 8); 
	
	/* Load upper 8-bits into the high byte of the UBRR register
	Default frame format is 8 data bits, no parity, 1 stop bit
	to change use UCSRC, see AVR datasheet*/ 

	// Enable receiver and transmitter and receive complete interrupt 
	UCSRB = ((1<<TXEN)|(1<<RXEN));
	UCSRC = (1<<URSEL)|(3<<UCSZ0);
}

void UartSendByte(uint8_t u8Data)
{
	// Wait until last byte has been transmitted
	while((UCSRA &(1<<UDRE)) == 0);

	// Transmit data
	UDR = u8Data;
}

void UartSendIntAsAscii(uint8_t data)
{
	UartSendByte(data + 48);
}

// Wait until a byte has been received and return received data 
uint8_t UartReceiveByte()
{
	while((UCSRA &(1<<RXC)) == 0);
	return UDR;
}