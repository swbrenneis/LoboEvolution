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
package com.jtattoo.plaf.bernstein;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;

/**
 * <p>BernsteinCheckBoxUI class.</p>
 *
 * Author Michael Hagen
 *
 */
public class BernsteinCheckBoxUI extends BernsteinRadioButtonUI {

	private static BernsteinCheckBoxUI checkBoxUI = null;

	/** {@inheritDoc} */
	public static ComponentUI createUI(JComponent b) {
		if (checkBoxUI == null) {
			checkBoxUI = new BernsteinCheckBoxUI();
		}
		return checkBoxUI;
	}

	/** {@inheritDoc} */
	@Override
	public void installDefaults(AbstractButton b) {
		super.installDefaults(b);
		icon = UIManager.getIcon("CheckBox.icon");
	}

} // end of class BernsteinButtonUI
