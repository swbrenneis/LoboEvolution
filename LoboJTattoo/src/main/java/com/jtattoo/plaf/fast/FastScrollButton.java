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

import javax.swing.Icon;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseIcons;
import com.jtattoo.plaf.BaseScrollButton;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * <p>FastScrollButton class.</p>
 *
 *
 *
 */
public class FastScrollButton extends BaseScrollButton {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for FastScrollButton.</p>
	 *
	 * @param direction a int.
	 * @param width a int.
	 */
	public FastScrollButton(int direction, int width) {
		super(direction, width);
	}

	/** {@inheritDoc} */
	@Override
	public void paint(final Graphics g) {
		boolean isPressed = getModel().isPressed();

		int width = getWidth();
		int height = getHeight();

		Color backColor = AbstractLookAndFeel.getTheme().getControlBackgroundColor();
		if (!JTattooUtilities.isActive(this)) {
			backColor = ColorHelper.brighter(backColor, 50);
		}
		if (isPressed) {
			backColor = ColorHelper.darker(backColor, 10);
		}
		Color hiColor = ColorHelper.brighter(backColor, 40);
		Color loColor = ColorHelper.darker(backColor, 30);
		g.setColor(backColor);
		g.fillRect(0, 0, width, height);

		switch (getDirection()) {
		case NORTH: {
			Icon upArrow = BaseIcons.getUpArrowIcon();
			int x = width / 2 - upArrow.getIconWidth() / 2;
			int y = height / 2 - upArrow.getIconHeight() / 2 - 1;
			upArrow.paintIcon(this, g, x, y);
			break;
		}
		case SOUTH: {
			Icon downArrow = BaseIcons.getDownArrowIcon();
			int x = width / 2 - downArrow.getIconWidth() / 2;
			int y = height / 2 - downArrow.getIconHeight() / 2;
			downArrow.paintIcon(this, g, x, y);
			break;
		}
		case WEST: {
			Icon leftArrow = BaseIcons.getLeftArrowIcon();
			int x = width / 2 - leftArrow.getIconWidth() / 2 - 1;
			int y = height / 2 - leftArrow.getIconHeight() / 2;
			leftArrow.paintIcon(this, g, x, y);
			break;
		}
		default: {
			Icon rightArrow = BaseIcons.getRightArrowIcon();
			int x = width / 2 - rightArrow.getIconWidth() / 2;
			int y = height / 2 - rightArrow.getIconHeight() / 2;
			rightArrow.paintIcon(this, g, x, y);
			break;
		}
		}

		JTattooUtilities.draw3DBorder(g, ColorHelper.brighter(loColor, 20), loColor, 0, 0, width, height);
		if (!isPressed) {
			g.setColor(hiColor);
			g.drawLine(1, 1, width - 2, 1);
			g.drawLine(1, 1, 1, height - 2);
		}
	}

} // end of class FastScrollButton
