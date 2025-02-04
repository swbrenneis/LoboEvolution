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
package com.jtattoo.plaf.graphite;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseRadioButtonUI;

/**
 * <p>GraphiteRadioButtonUI class.</p>
 *
 * Author Michael Hagen
 *
 */
public class GraphiteRadioButtonUI extends BaseRadioButtonUI {

	private static GraphiteRadioButtonUI radioButtonUI = null;

	/** {@inheritDoc} */
	public static ComponentUI createUI(final JComponent c) {
		if (radioButtonUI == null) {
			radioButtonUI = new GraphiteRadioButtonUI();
		}
		return radioButtonUI;
	}

	/** {@inheritDoc} */
	@Override
	protected void paintFocus(Graphics g, Rectangle t, Dimension d) {
		g.setColor(AbstractLookAndFeel.getFocusColor());
		BasicGraphicsUtils.drawDashedRect(g, t.x - 3, t.y - 1, t.width + 6, t.height + 2);
		BasicGraphicsUtils.drawDashedRect(g, t.x - 2, t.y, t.width + 4, t.height);
	}

} // end of class GraphiteRadioButtonUI
