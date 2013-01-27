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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents a message which is used in serial interface to communicate with usb stick
 * 
 * @author Victor Belov
 * @since 1.2.0
 */

public class SerialMessage {

	public static final byte MessageTypeRequest = 0x00;
	public static final byte MessageTypeResponse = 0x01;
	public boolean isValid = false;

	private static final Logger logger = LoggerFactory.getLogger(SerialMessage.class);
	private String messagePayload = "";
	private int messageLength = 0;
	private int messageType = 0;
	private int messageClass = 0;

	public SerialMessage() {
		logger.debug("Creating empty message");
	}
	
	public SerialMessage(byte mClass, byte mType) {
		logger.debug(String.format("Creating empty message of class = %02X, type = %02X", mClass, mType));
		messageClass = mClass;
		messageType = mType;
	}

	public SerialMessage(String buffer) {
		messageLength = buffer.getBytes()[1];
		logger.debug("Message length = " + messageLength);
		buffer = buffer.substring(0, messageLength + 2);
		logger.debug("Creating new message from buffer = " + toHex(buffer));
		byte messageCheckSumm = calculateChecksumm(buffer);
		byte messageCheckSummReceived = buffer.getBytes()[messageLength+1];
		logger.debug(String.format("Message checksum calculated = %02X", messageCheckSumm));
		logger.debug(String.format("Message checksum received = %02X", messageCheckSummReceived));
		if (messageCheckSumm == messageCheckSummReceived) {
			logger.debug("Checksumm matched");
			isValid = true;
		} else {
			// TODO: Throw some exception here
			logger.debug("Checksumm error");
			isValid = false;
			return;
		}
		setMessageType(buffer.getBytes()[2]);
		setMessageClass(buffer.getBytes()[3]);
		messagePayload = buffer.substring(4, messageLength + 1);
		logger.debug("Cut payload = " + toHex(messagePayload));
	}

	public byte calculateChecksumm(String buffer) {
		logger.info("Calculating checksum");
		byte checkSumm = (byte)0xFF;
		for (int i=1; i<buffer.length()-1; i++) {
			logger.info(String.format("Adding 0x%02X to checksumm", buffer.getBytes()[i]));
			checkSumm = (byte) (checkSumm ^ buffer.getBytes()[i]);
		}
		logger.info(String.format("Calculated checksum = 0x%02X", checkSumm));
		return checkSumm;
	}

	private String toHex(String buffer) {
		String result = "";
		for (int i=0; i< buffer.length(); i++) {
			result = result.concat(String.format("%02X ", buffer.getBytes()[i]));
		}
		return result;
	}

	public String getMessageBuffer() {
		byte result[] = new byte[1024];
		for (int j=0; j<1024; j++)
			result[j] = 0;
		result[0] = 0x01; // Add SOF in front
		int messageLength = messagePayload.length() + 3; // calculate and set length
		result[1] = (byte) messageLength;
		result[2] = (byte) messageType; // set Request/Response
		result[3] = (byte) messageClass; // set message class
		for (int i=0; i<messagePayload.length(); i++) {
			result[4 + i] = messagePayload.getBytes()[i];
		}
		result[4 + messagePayload.length()] = 0x01;
		result[4 + messagePayload.length()] = calculateChecksumm(new String(result, 0, 4 + messagePayload.length() + 1));
		String resultBuffer = new String(result, 0, 4 + messagePayload.length() + 1);
		return resultBuffer;
	}

	public int getMessageType() {
		return messageType;
	}
	
	public String getMessagePayload() {
		return messagePayload;
	}
	
	public void setMessagePayload(String messagePayload) {
		this.messagePayload = messagePayload;
	}

	public void setMessageType(int messageType) {
		this.messageType = messageType;
	}

	public int getMessageClass() {
		return messageClass;
	}

	public void setMessageClass(int messageClass) {
		this.messageClass = messageClass;
	}
}
