package org.openhab.binding.zwave;

import org.openhab.core.binding.BindingConfig;

public class ZWaveBindingConfig implements BindingConfig {
	
	public ZWaveBindingConfig(String nodeId, ZWaveCommandClass commandClass) {
		this.nodeId = nodeId;
		this.commandClass = commandClass;
	}
	
	private String nodeId;
	
	private ZWaveCommandClass commandClass;

	public ZWaveCommandClass getCommandClass() {
		return commandClass;
	}

	public String getNodeId() {
		return nodeId;
	}
}