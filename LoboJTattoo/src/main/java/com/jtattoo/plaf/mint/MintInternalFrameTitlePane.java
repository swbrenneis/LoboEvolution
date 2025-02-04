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
package com.jtattoo.plaf.mint;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JInternalFrame;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseInternalFrameTitlePane;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * <p>MintInternalFrameTitlePane class.</p>
 *
 * Author Michael Hagen
 *
 */
public class MintInternalFrameTitlePane extends BaseInternalFrameTitlePane {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for MintInternalFrameTitlePane.</p>
	 *
	 * @param f a {@link javax.swing.JInternalFrame} object.
	 */
	public MintInternalFrameTitlePane(JInternalFrame f) {
		super(f);
	}

	/** {@inheritDoc} */
	@Override
	public void paintBackground(Graphics g) {
		if (JTattooUtilities.isActive(this)) {
			setBackground(AbstractLookAndFeel.getTheme().getWindowTitleBackgroundColor());
			JTattooUtilities.fillVerGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), 0, 0, getWidth(),
					getHeight());
		} else {
			setBackground(AbstractLookAndFeel.getTheme().getWindowInactiveTitleBackgroundColor());
			JTattooUtilities.fillVerGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), 0, 0,
					getWidth(), getHeight());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void paintBorder(Graphics g) {
		g.setColor(ColorHelper.darker(AbstractLookAndFeel.getTheme().getWindowTitleColorDark(), 10));
		g.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);
		g.setColor(Color.white);
		g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
	}

	/** {@inheritDoc} */
	@Override
	public void paintPalette(Graphics g) {
		if (JTattooUtilities.isFrameActive(this)) {
			JTattooUtilities.fillVerGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), 0, 0, getWidth(),
					getHeight());
		} else {
			JTattooUtilities.fillVerGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), 0, 0,
					getWidth(), getHeight());
		}
	}

} // end of class MintInternalFrameTitlePane
