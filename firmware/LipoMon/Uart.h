#ifndef UART_H_
#define UART_H_

// Define baud rate
#define BAUD_PRESCALE (((F_CPU / (USART_BAUDRATE * 16UL))) - 1)

extern void USART_Init(void);
extern void USART_SendByte(uint8_t u8Data);
extern uint8_t USART_ReceiveByte();

#endif /* UART_H_ */