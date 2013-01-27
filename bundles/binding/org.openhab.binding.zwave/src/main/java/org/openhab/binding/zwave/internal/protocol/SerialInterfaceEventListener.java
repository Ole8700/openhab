package org.openhab.binding.zwave.internal.protocol;

public interface SerialInterfaceEventListener {
	void SerialInterfaceIncomingMessage(SerialMessage incomingMessage);
}
