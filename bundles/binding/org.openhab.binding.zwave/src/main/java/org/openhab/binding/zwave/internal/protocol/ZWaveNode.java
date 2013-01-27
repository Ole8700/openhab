package org.openhab.binding.zwave.internal.protocol;

public class ZWaveNode {

	public enum BasicClass {		
		CONTROLLER(1),
		STATIC_CONTROLLER(2),
		SLAVE(3),
		ROUTING_SLAVE(4);
		
		BasicClass(int id) {
			this.id = id;
		}
		
		private int id;
		
		public int getId() {
			return this.id;
		}
	}

	private int nodeId;
	private int basicDeviceClass;
	private int genericDeviceClass;
	private int specificDeviceClass;
	private boolean listening;
	
	public ZWaveNode(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getNodeId() {
		return nodeId;
	}
	public void setNodeId(int nodeId) {
		this.nodeId = nodeId;
	}

	public int getBasicDeviceClass() {
		return basicDeviceClass;
	}

	public void setBasicDeviceClass(int basicDeviceClass) {
		this.basicDeviceClass = basicDeviceClass;
	}

	public int getGenericDeviceClass() {
		return genericDeviceClass;
	}

	public void setGenericDeviceClass(int genericDeviceClass) {
		this.genericDeviceClass = genericDeviceClass;
	}

	public int getSpecificDeviceClass() {
		return specificDeviceClass;
	}

	public void setSpecificDeviceClass(int specificDeviceClass) {
		this.specificDeviceClass = specificDeviceClass;
	}

	public boolean isListening() {
		return listening;
	}

	public void setListening(boolean listening) {
		this.listening = listening;
	}

}
