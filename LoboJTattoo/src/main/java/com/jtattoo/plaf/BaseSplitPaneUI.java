/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*
* JTattoo is multiple licensed. If your are an open source developer you can use
* it under the terms and conditions of the GNU General Public License version 2.0
* or later as published by the Free Software Foundation.
*
* see: gpl-2.0.txt
*
* If you pay for a license you will become a registered user who could use the
* software under the terms and conditions of the GNU Lesser General Public License
* version 2.0 or later with classpath exception as published by the Free Software
* Foundation.
*
* see: lgpl-2.0.txt
* see: classpath-exception.txt
*
* Registered users could also use JTattoo under the terms and conditions of the
* Apache License, Version 2.0 as published by the Apache Software Foundation.
*
* see: APACHE-LICENSE-2.0.txt
*/

package com.jtattoo.plaf;

import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

/**
 * <p>BaseSplitPaneUI class.</p>
 *
 * Author Michael Hagen
 *
 */
public class BaseSplitPaneUI extends BasicSplitPaneUI {

	/** {@inheritDoc} */
	public static ComponentUI createUI(final JComponent c) {
		return new BaseSplitPaneUI();
	}

	protected PropertyChangeListener myPropertyChangeListener = null;

	/** {@inheritDoc} */
	@Override
	public BasicSplitPaneDivider createDefaultDivider() {
		return new BaseSplitPaneDivider(this);
	}

	/** {@inheritDoc} */
	@Override
	protected void installListeners() {
		super.installListeners();
		myPropertyChangeListener = evt -> {
			if ("flatMode".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Boolean) {
				((BaseSplitPaneDivider) getDivider()).setFlatMode((Boolean) evt.getNewValue());
			}
		};
		getSplitPane().addPropertyChangeListener(myPropertyChangeListener);
	}

	/** {@inheritDoc} */
	@Override
	protected void uninstallListeners() {
		super.uninstallListeners();
		getSplitPane().removePropertyChangeListener(myPropertyChangeListener);
	}

} // end of class BaseSplitPaneUI
