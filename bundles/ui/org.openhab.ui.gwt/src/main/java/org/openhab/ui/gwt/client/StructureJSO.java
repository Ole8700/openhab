package org.openhab.ui.gwt.client;

import com.google.gwt.core.client.JavaScriptObject;

import com.googlecode.mgwt.collection.client.JsLightMap;
import com.googlecode.mgwt.collection.shared.LightArray;
import com.googlecode.mgwt.collection.shared.LightMap;

public class StructureJSO {

	public static LightMap<LightArray<String>> getChildren() {
		JavaScriptObject children0 = getChildren0();

		return new JsLightMap<LightArray<String>>(children0);

	}

	private static native JavaScriptObject getChildren0()/*-{
		return $wnd.tree.children;
	}-*/;

	public static LightMap<String> getParents() {
		JavaScriptObject parents0 = getParents0();
		return new JsLightMap<String>(parents0);
	}

	private static native JavaScriptObject getParents0()/*-{
		return $wnd.tree.parent;
	}-*/;

}
