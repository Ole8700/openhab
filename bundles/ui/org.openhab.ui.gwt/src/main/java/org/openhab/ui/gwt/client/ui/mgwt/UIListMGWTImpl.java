package org.openhab.ui.gwt.client.ui.mgwt;

import java.util.Map;

import org.openhab.ui.gwt.client.ui.UIBase;
import org.openhab.ui.gwt.client.ui.UIList;

import com.google.gwt.user.client.ui.Widget;
import com.googlecode.mgwt.ui.client.MGWTStyle;
import com.googlecode.mgwt.ui.client.widget.WidgetList;

public class UIListMGWTImpl implements UIList {

	private WidgetList list;

	public UIListMGWTImpl() {
		list = new WidgetList();
		list.setRound(true);

	}

	@Override
	public void add(UIBase base) {
		Widget widget = (Widget) base.getWidget();

		list.add(widget);

		Map<Widget, Widget> map = getMap(list);

		map.get(widget)
				.addStyleName(
						MGWTStyle.getTheme().getMGWTClientBundle().getListCss()
								.group());

	}

	@Override
	public Object getWidget() {
		return list;
	}

	private native Map<Widget, Widget> getMap(WidgetList list)/*-{
		return list.@com.googlecode.mgwt.ui.client.widget.WidgetList::map;
	}-*/;

}
