package org.openhab.ui.gwt.client.ui.mgwt;

import org.openhab.ui.gwt.client.ui.UIButton;
import org.openhab.ui.gwt.client.ui.UIFactory;
import org.openhab.ui.gwt.client.ui.UIGroup;
import org.openhab.ui.gwt.client.ui.UIList;

public class UIFactoryMGwtImpl implements UIFactory{

	@Override
	public UIButton createButton() {
		return new UIButtonMGWTImpl();
	}

	@Override
	public UIList createList() {
		return new UIListMGWTImpl();
	}

	@Override
	public UIGroup createGroup() {
		return new UIGroupGWTImpl();
	}

}
