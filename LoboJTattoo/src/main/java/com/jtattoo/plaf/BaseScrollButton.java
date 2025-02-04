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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 * <p>BaseScrollButton class.</p>
 *
 * Author Michael Hagen
 *
 */
public class BaseScrollButton extends BasicArrowButton {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	protected int buttonWidth = 24;

	/**
	 * <p>Constructor for BaseScrollButton.</p>
	 *
	 * @param direction a int.
	 * @param width a int.
	 */
	public BaseScrollButton(int direction, int width) {
		super(direction);
		buttonWidth = width;
	}

	/**
	 * <p>Getter for the field buttonWidth.</p>
	 *
	 * @return a int.
	 */
	public int getButtonWidth() {
		return buttonWidth;
	}

	/** {@inheritDoc} */
	@Override
	public Dimension getMaximumSize() {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	/** {@inheritDoc} */
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	/** {@inheritDoc} */
	@Override
	public Dimension getPreferredSize() {
		switch (getDirection()) {
		case NORTH:
            case SOUTH:
                return new Dimension(buttonWidth, buttonWidth + 1);
            case EAST:
            case WEST:
                return new Dimension(buttonWidth + 1, buttonWidth);
            default:
			return new Dimension(0, 0);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void paint(final Graphics g) {
		boolean isPressed = getModel().isPressed();
		boolean isRollover = getModel().isRollover();

		int width = getWidth();
		int height = getHeight();

		Color[] colors;
		if (isPressed) {
			colors = AbstractLookAndFeel.getTheme().getPressedColors();
		} else if (isRollover) {
			colors = AbstractLookAndFeel.getTheme().getRolloverColors();
		} else {
			colors = AbstractLookAndFeel.getTheme().getButtonColors();
		}

		boolean inverse = ColorHelper.getGrayValue(colors) < 128;

		Color frameColorHi = ColorHelper.brighter(colors[0], 20);
		Color frameColorLo = ColorHelper.darker(colors[colors.length - 1], 20);

		Graphics2D g2D = (Graphics2D) g;
		Composite savedComposite = g2D.getComposite();
		if (getDirection() == NORTH || getDirection() == SOUTH) {
			JTattooUtilities.fillVerGradient(g2D, colors, 0, 0, width, height);
		} else {
			JTattooUtilities.fillHorGradient(g2D, colors, 0, 0, width, height);
		}
		g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
		switch (getDirection()) {
		case NORTH: {
			Icon upArrow = inverse ? BaseIcons.getUpArrowInverseIcon() : BaseIcons.getUpArrowIcon();
			int x = width / 2 - upArrow.getIconWidth() / 2;
			int y = height / 2 - upArrow.getIconHeight() / 2 - 1;
			upArrow.paintIcon(this, g2D, x, y);
			break;
		}
		case SOUTH: {
			Icon downArrow = inverse ? BaseIcons.getDownArrowInverseIcon() : BaseIcons.getDownArrowIcon();
			int x = width / 2 - downArrow.getIconWidth() / 2;
			int y = height / 2 - downArrow.getIconHeight() / 2;
			downArrow.paintIcon(this, g2D, x, y);
			break;
		}
		case WEST: {
			Icon leftArrow = inverse ? BaseIcons.getLeftArrowInverseIcon() : BaseIcons.getLeftArrowIcon();
			int x = width / 2 - leftArrow.getIconWidth() / 2 - 1;
			int y = height / 2 - leftArrow.getIconHeight() / 2;
			leftArrow.paintIcon(this, g2D, x, y);
			break;
		}
		default: {
			Icon rightArrow = inverse ? BaseIcons.getRightArrowInverseIcon() : BaseIcons.getRightArrowIcon();
			int x = width / 2 - rightArrow.getIconWidth() / 2;
			int y = height / 2 - rightArrow.getIconHeight() / 2;
			rightArrow.paintIcon(this, g2D, x, y);
			break;
		}
		}
		JTattooUtilities.draw3DBorder(g2D, frameColorLo, ColorHelper.darker(frameColorLo, 10), 0, 0, width, height);
		g2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
		g2D.setColor(frameColorHi);
		g2D.drawLine(1, 1, width - 2, 1);
		g2D.drawLine(1, 1, 1, height - 2);

		g2D.setComposite(savedComposite);
	}

} // end of class BaseScrollButton
