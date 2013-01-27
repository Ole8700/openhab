/**
 * openHAB, the open Home Automation Bus.
 * Copyright (C) 2010-2013, openHAB.org <admin@openhab.org>
 *
 * See the contributors.txt file in the distribution for a
 * full listing of individual contributors.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses>.
 *
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or
 * combining it with Eclipse (or a modified version of that library),
 * containing parts covered by the terms of the Eclipse Public License
 * (EPL), the licensors of this Program grant you additional permission
 * to convey the resulting work.
 */

package org.openhab.binding.zwave.internal.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.TooManyListenersException;
import java.util.concurrent.ArrayBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

/**
 * This class implements communications with a standard USB Z-Wave stick over serial protocol
 * 
 * @author Victor Belov
 * @since 1.2.0
 */

public class SerialInterface implements SerialPortEventListener {


	public static final byte[] zwave_nak = new byte[] { 0x15 };
	public static final byte[] zwave_ack = new byte[] { 0x06 };
	public static final byte[] zwave_can = new byte[] { 0x18 };

	public static final byte MessageTypeRequest = 0x00;
	public static final byte MessageTypeResponse = 0x01;

	private static final Logger logger = LoggerFactory.getLogger(SerialInterface.class);
	private SerialPort serialPort;
    private InputStream inputStream;
    private OutputStream outputStream;
    protected int maxBufferSize = 1024;
    protected ArrayBlockingQueue<SerialMessage> inputQueue = new ArrayBlockingQueue<SerialMessage>(maxBufferSize, true);
    protected ArrayBlockingQueue<SerialMessage> outputQueue = new ArrayBlockingQueue<SerialMessage>(maxBufferSize, true);;
    protected ArrayBlockingQueue<SerialMessage> sentQueue = new ArrayBlockingQueue<SerialMessage>(maxBufferSize, true);;
    private boolean isSending = false;
    public boolean isWaitingResponse = false;
    private ArrayList<SerialInterfaceEventListener> eventListeners;

    public SerialInterface(String serialPortName) {
		logger.info("Initializing serial port " + serialPortName);
		this.eventListeners = new ArrayList<SerialInterfaceEventListener>();
		try {
			CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(serialPortName);
			CommPort commPort = portIdentifier.open("org.openhab.binding.zwave",2000);
			serialPort = (SerialPort) commPort;
			serialPort.setSerialPortParams(115200,SerialPort.DATABITS_8,SerialPort.STOPBITS_1,SerialPort.PARITY_NONE);
			inputStream = serialPort.getInputStream();
			outputStream = serialPort.getOutputStream();
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			logger.info("Serial port is initialized");
			outputStream.write(0x15);
			outputStream.flush();
			logger.info("NAK sent");
			SerialInterfaceSender serialInterfaceSender = new SerialInterfaceSender(this.outputStream);
			serialInterfaceSender.start();
		} catch (NoSuchPortException e) {
			logger.error(e.getMessage());
		} catch (PortInUseException e) {
			logger.error(e.getMessage());
		} catch (UnsupportedCommOperationException e) {
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} catch (TooManyListenersException e) {
			logger.error(e.getMessage());
		}
	}

    public void sendSimpleRequest(byte requestFunction) {
    	SerialMessage newMessage = new SerialMessage(requestFunction, MessageTypeRequest);
    	sendMessage(newMessage);
    }
    
    public void sendMessage(SerialMessage message) {
    	try {
			outputQueue.put(message);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void processIncomingMessage(String buffer) {
		int sofOffset = 0;
		if (buffer.getBytes()[0] == 0x18) { // CAN received
			return;
		}
		for (int i=0; i<buffer.length(); i++) {
			if (buffer.getBytes()[i] == 0x01) {
				sofOffset = i;
				break;
			}
		}
		logger.info("SOF found at " + sofOffset);
		buffer = buffer.substring(sofOffset);
		SerialMessage serialMessage = new SerialMessage(buffer);
		if (serialMessage.isValid) {
			logger.info("Message is valid, sending ACK");
			try {
				outputStream.write(zwave_ack);
				outputStream.flush();
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		} else {
			logger.info("Message is not valid");
			return;
		}
		for (SerialInterfaceEventListener eventListener : this.eventListeners) {
			eventListener.SerialInterfaceIncomingMessage(serialMessage);
		}
    }

	@Override
	public void serialEvent(SerialPortEvent serialPortEvent) {
        switch (serialPortEvent.getEventType()) {
        case SerialPortEvent.BI:
        case SerialPortEvent.OE:
        case SerialPortEvent.FE:
        case SerialPortEvent.PE:
        case SerialPortEvent.CD:
        case SerialPortEvent.CTS:
        case SerialPortEvent.DSR:
        case SerialPortEvent.RI:
        case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
        case SerialPortEvent.DATA_AVAILABLE:
            // we get here if data has been received
            StringBuilder sb = new StringBuilder();
            byte[] readBuffer = new byte[20];
            try {
                    do {
                            // read data from serial device
                            while (inputStream.available() > 0) {
                                    int bytes = inputStream.read(readBuffer);
                                    sb.append(new String(readBuffer, 0, bytes));
                            }
                            try {
                                    // add wait states around reading the stream, so that interrupted transmissions are merged
                                    Thread.sleep(100);
                            } catch (InterruptedException e) {
                                    // ignore interruption
                            }
                    } while (inputStream.available() > 0);
                    String result = sb.toString();
            		logger.info("Incoming message: " + toHex(result));
                    // TODO: process incoming message
                    processIncomingMessage(result);
            } catch (IOException e) {
            	e.printStackTrace();
            }
            break;
        }
    }

	public String toHex(String buffer) {
		String result = "";
		for (int i=0; i< buffer.length(); i++) {
			result = result.concat(String.format("%02X ", buffer.getBytes()[i]));
		}
		result = result.substring(0, result.length()-1); // remove trailing space ;-)
		return result;
	}
	
	public void addEventListener(SerialInterfaceEventListener serialInterfaceEventListener) {
		this.eventListeners.add(serialInterfaceEventListener);
	}
	
	public void removeEventListener(SerialInterfaceEventListener serialInterfaceEventListener) {
		this.eventListeners.remove(serialInterfaceEventListener);
	}
	
	private class SerialInterfaceSender extends Thread {

		private OutputStream outputStream;
		private final Logger logger = LoggerFactory.getLogger(SerialInterfaceSender.class);
		
		public SerialInterfaceSender(OutputStream outputStream) {
			this.outputStream = outputStream;
		}
		
		public void run() {
			while (true) {
//				if (!SerialInterface.this.isWaitingResponse && !SerialInterface.this.outputQueue.isEmpty()) {
				if (!SerialInterface.this.outputQueue.isEmpty()) {
					try {
						logger.info("Sending next message from queue");
						SerialMessage nextMessage = SerialInterface.this.outputQueue.poll();
						logger.info(toHex(nextMessage.getMessageBuffer()));
						outputStream.write(nextMessage.getMessageBuffer().getBytes());
						if (nextMessage.getMessageType() == MessageTypeRequest) {
							SerialInterface.this.isWaitingResponse = true;
						}
					} catch (IOException e) {
						logger.error(e.getMessage());
					}
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					logger.error(e.getMessage());
				}
			}
		}
	
	}

}
