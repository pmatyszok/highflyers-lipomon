/**
 * Author: Michal Witas
 * Version: 1.0
 **/

#ifndef UART_H_
#define UART_H_

// Define baud rate
#define BAUD_PRESCALE (((F_CPU / (USART_BAUDRATE * 16UL))) - 1)

/**
 * Initializes UART module
 */
extern void UartInit(void);

/**
 * Sends byte via UART.
 */
extern void UartSendByte(uint8_t u8Data);

/**
 * Receives byte via UART.
 * @return Byte taken from UART.
 */
extern uint8_t UartReceiveByte();

/**
 * Converts integer to byte and sends via UART.
 * @param data Integer to convert and send.
 */
extern void UartSendIntAsAscii(uint8_t data);

#endif /* UART_H_ */