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
package com.jtattoo.plaf.fast;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseScrollBarUI;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * <p>FastScrollBarUI class.</p>
 *
 * Author Michael Hagen
 *
 */
public class FastScrollBarUI extends BaseScrollBarUI {

	/** {@inheritDoc} */
	public static ComponentUI createUI(final JComponent c) {
		return new FastScrollBarUI();
	}

	/** {@inheritDoc} */
	@Override
	protected JButton createDecreaseButton(int orientation) {
		if (AbstractLookAndFeel.getTheme().isMacStyleScrollBarOn()) {
			return super.createDecreaseButton(orientation);
		} else {
			return new FastScrollButton(orientation, scrollBarWidth);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected JButton createIncreaseButton(final int orientation) {
		if (AbstractLookAndFeel.getTheme().isMacStyleScrollBarOn()) {
			return super.createDecreaseButton(orientation);
		} else {
			return new FastScrollButton(orientation, scrollBarWidth);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		if (!c.isEnabled()) {
			return;
		}

		g.translate(thumbBounds.x, thumbBounds.y);

		Color backColor = AbstractLookAndFeel.getTheme().getControlBackgroundColor();
		if (!JTattooUtilities.isActive(c)) {
			backColor = ColorHelper.brighter(backColor, 50);
		}
		Color frameColorHi = ColorHelper.brighter(backColor, 40);
		Color frameColorLo = ColorHelper.darker(backColor, 30);
		g.setColor(backColor);
		g.fillRect(1, 1, thumbBounds.width - 1, thumbBounds.height - 1);
		g.setColor(frameColorLo);
		g.drawRect(0, 0, thumbBounds.width - 1, thumbBounds.height - 1);
		g.setColor(frameColorHi);
		g.drawLine(1, 1, thumbBounds.width - 2, 1);
		g.drawLine(1, 1, 1, thumbBounds.height - 2);
		g.translate(-thumbBounds.x, -thumbBounds.y);
	}

	/** {@inheritDoc} */
	@Override
	protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		g.setColor(AbstractLookAndFeel.getControlColorLight());
		g.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
	}

} // end of class FastScrollBarUI
