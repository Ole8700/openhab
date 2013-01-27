package org.openhab.binding.zwave.internal.protocol;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZWaveController implements SerialInterfaceEventListener {

	public static final byte MessageSerialApiGetInitData = 0x02;
	public static final byte MessageSerialApiGetCapabilities = 0x07;
	public static final byte MessageGetVersion = 0x15;
	public static final byte MessageMemoryGetId = 0x20;
	public static final byte MessageIdentifyNode = 0x41;
	public static final byte MessageGetSucNodeId = 0x56;
	public static final byte MessageRequestNodeInfo = 0x60;
	public static final byte MessageAddNodeToNetwork = 0x4a;
	public static final byte MessageRemoveNodeFromNetwork = 0x4b;
	public static final byte MessageApplicationCommandHandler = 0x04;

	public static final byte CommandClassSwitchBinary = 0x25;
	public static final byte CommandClassMeter = 0x32;
	
	public static final byte SwitchBinaryCmdSet = 0x01;
	public static final byte SwitchBinaryCmdGet = 0x02;
	public static final byte SwitchBinaryCmdReport = 0x03;

	private SerialInterface serialInterface;
	private static final Logger logger = LoggerFactory.getLogger(ZWaveController.class);
	private ArrayList<ZWaveNode> zwaveNodes;
	private String ZWaveVersion = "Unknown";
	private int ZWaveLibraryType;
	private int homeId = 0;
	private int selfNodeId = 0;
	private boolean connected = false;

	public ZWaveController(SerialInterface serialInterface) {
		logger.info("Starting Z-Wave controller");
		this.zwaveNodes = new ArrayList<ZWaveNode>();
		this.serialInterface = serialInterface;
		this.serialInterface.addEventListener(this);
	}

	public void initialize() {
		this.serialInterface.sendSimpleRequest(MessageGetVersion);
		this.serialInterface.sendSimpleRequest(MessageMemoryGetId);
		this.serialInterface.sendSimpleRequest(MessageSerialApiGetCapabilities);
		this.serialInterface.sendSimpleRequest(MessageSerialApiGetInitData);
		this.serialInterface.sendSimpleRequest(MessageGetSucNodeId);
		this.setConnected(true);
//		this.startAddNodeToNetwork();
	}
	
	public void identifyNode(int nodeId) {
    	SerialMessage newMessage = new SerialMessage(MessageIdentifyNode, SerialInterface.MessageTypeRequest);
    	byte[] newPayload = { (byte) nodeId };
    	newMessage.setMessagePayload(new String(newPayload));
    	this.serialInterface.sendMessage(newMessage);
	}

	public void requestNodeInfo(int nodeId) {
    	SerialMessage newMessage = new SerialMessage(MessageRequestNodeInfo, SerialInterface.MessageTypeRequest);
    	byte[] newPayload = { (byte) nodeId };
    	newMessage.setMessagePayload(new String(newPayload));
    	this.serialInterface.sendMessage(newMessage);
	}
	
	public void startAddNodeToNetwork() {
    	SerialMessage newMessage = new SerialMessage(MessageAddNodeToNetwork, SerialInterface.MessageTypeRequest);
    	byte[] newPayload = { (byte) 0x01 };
    	newMessage.setMessagePayload(new String(newPayload));
    	this.serialInterface.sendMessage(newMessage);		
	}
	
	public void stopAddNodeToNetwork() {
    	SerialMessage newMessage = new SerialMessage(MessageAddNodeToNetwork, SerialInterface.MessageTypeRequest);
    	byte[] newPayload = { (byte) 0x05 };
    	newMessage.setMessagePayload(new String(newPayload));
    	this.serialInterface.sendMessage(newMessage);		
	}

	public void sendLevel(int nodeId, int level) {
    	SerialMessage newMessage = new SerialMessage((byte)0x13, SerialInterface.MessageTypeRequest);
    	// NodeId, 3 is command length, 0x20 is COMMAND_CLASS_BASIC, 1 is BASIC_SET, level, 5 is TRANSMIT_OPTION_ACK+TRANSMIT_OPTION_AUTO_ROUTE
    	byte[] newPayload = { (byte) nodeId, 3, 0x20, 1, (byte)level, 5 , 0};
    	newMessage.setMessagePayload(new String(newPayload));
    	this.serialInterface.sendMessage(newMessage);		
	}
	
	@Override
	public void SerialInterfaceIncomingMessage(SerialMessage incomingMessage) {
		logger.info("Incoming message to process");
		logger.info(serialInterface.toHex(incomingMessage.getMessagePayload()));
		if (incomingMessage.getMessageType() == SerialInterface.MessageTypeResponse) {
			this.serialInterface.isWaitingResponse = false;
			logger.info("Message type = RESPONSE");
			switch (incomingMessage.getMessageClass()) {
			case MessageGetVersion:
				int libraryType = incomingMessage.getMessagePayload().getBytes()[12];
				String version = incomingMessage.getMessagePayload().substring(0, 11);
				logger.info(String.format("Got MessageGetVersion response. Version = %s, Library Type = 0x%02X", version, libraryType));
				this.setZWaveVersion(version);
				this.setZWaveLibraryType(libraryType);
				break;
			case MessageMemoryGetId:
				int homeId = ((incomingMessage.getMessagePayload().getBytes()[0] & 0xff) << 24) | ((incomingMessage.getMessagePayload().getBytes()[1] & 0xff) << 16) | ((incomingMessage.getMessagePayload().getBytes()[2] & 0xff) << 8) | (incomingMessage.getMessagePayload().getBytes()[3] & 0xff);
				int selfNodeId = incomingMessage.getMessagePayload().getBytes()[4];
				logger.info(String.format("Got MessageMemoryGetId response. Home id = 0x%08X, Node id = %d", homeId, selfNodeId));
				this.setHomeId(homeId);
				this.setSelfNodeId(selfNodeId);
				break;
			case MessageSerialApiGetInitData:
				logger.info(String.format("Got MessageSerialApiGetInitData response."));
				if (incomingMessage.getMessagePayload().getBytes()[2] == 29) {
					byte nodeId = 1;
					for (int i = 3;i < 3 + 29;i++) {
						for (int j=0;j<8;j++) {
							byte b1 = (byte) (incomingMessage.getMessagePayload().getBytes()[i] & (byte)(int)Math.pow(2.0D, j));
							byte b2 = (byte)(int)Math.pow(2.0D, j);
	//						logger.info(String.format("%02X %02X", b1, b2));
							if (b1 == b2) {
								logger.info(String.format("Found node id = %d", nodeId));
								this.identifyNode(nodeId);
							}
							nodeId = (byte)(nodeId + 1);
						}
					}
				}
				break;
			case MessageSerialApiGetCapabilities:
				logger.info(String.format("Got MessageSerialApiGetCapabilities response."));
				break;
			case MessageGetSucNodeId:
				logger.info(String.format("Got MessageGetSucNodeId response. SUC Node Id = %d", incomingMessage.getMessagePayload().getBytes()[4]));
				break;
			default:
				logger.info(String.format("TODO: Implement processing of Message = 0x%02X", incomingMessage.getMessageClass()));
				break;
			}
		} else if (incomingMessage.getMessageType() == SerialInterface.MessageTypeRequest) {
			logger.info("Message type = REQUEST");
			switch (incomingMessage.getMessageClass()) {
			case MessageApplicationCommandHandler:
				byte commandClass = incomingMessage.getMessagePayload().getBytes()[3];
				int sourceNodeId = incomingMessage.getMessagePayload().getBytes()[1];
				logger.info(String.format("SourceNodeId = %d", sourceNodeId));
				logger.info("Got MessageApplicationCommandHandler");
				switch (commandClass) {
				case CommandClassSwitchBinary:
					logger.info("Got CommandClassSwitchBinary");
					byte switchBinaryCmd = incomingMessage.getMessagePayload().getBytes()[4];
					switch (switchBinaryCmd) {
					case SwitchBinaryCmdSet:
						logger.info("SwitchBinary set");
						break;
					case SwitchBinaryCmdGet:
						logger.info("SwitchBinary get");
						break;
					case SwitchBinaryCmdReport:
						byte switchValue = incomingMessage.getMessagePayload().getBytes()[5];
						logger.info(String.format("SwitchBinary report from nodeId = %d, value = 0x%02X", sourceNodeId, switchValue));
						break;
					default:
						logger.info("Unknown SwitchBinary command");
						break;
					}
					break;
				case CommandClassMeter:
					logger.info("Got CommandClassMeter");
					break;
				default:
					logger.info(String.format("TODO: Implement processing of CommandClass = 0x%02X", commandClass));
					break;
				}
				break;
			default:
				logger.info(String.format("TODO: Implement processing of Message = 0x%02X", incomingMessage.getMessageClass()));
				break;
			}
		}
	}

	public String getZWaveVersion() {
		return ZWaveVersion;
	}

	public void setZWaveVersion(String zWaveVersion) {
		ZWaveVersion = zWaveVersion;
	}

	public int getHomeId() {
		return homeId;
	}

	public void setHomeId(int homeId) {
		this.homeId = homeId;
	}

	public int getSelfNodeId() {
		return selfNodeId;
	}

	public void setSelfNodeId(int selfNodeId) {
		this.selfNodeId = selfNodeId;
	}

	public int getZWaveLibraryType() {
		return ZWaveLibraryType;
	}

	public void setZWaveLibraryType(int zWaveLibraryType) {
		ZWaveLibraryType = zWaveLibraryType;
	}

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		this.connected = connected;
	}	
}
