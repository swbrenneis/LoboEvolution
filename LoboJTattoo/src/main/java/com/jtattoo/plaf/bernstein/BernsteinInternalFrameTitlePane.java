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

import java.awt.AlphaComposite;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JInternalFrame;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseInternalFrameTitlePane;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * <p>BernsteinInternalFrameTitlePane class.</p>
 *
 * @author Michael Hagen
 * @version $Id: $Id
 */
public class BernsteinInternalFrameTitlePane extends BaseInternalFrameTitlePane {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * <p>Constructor for BernsteinInternalFrameTitlePane.</p>
	 *
	 * @param f a {@link javax.swing.JInternalFrame} object.
	 */
	public BernsteinInternalFrameTitlePane(JInternalFrame f) {
		super(f);
	}

	/** {@inheritDoc} */
	@Override
	public void paintBackground(Graphics g) {
		BernsteinUtils.fillComponent(g, this);
		Graphics2D g2D = (Graphics2D) g;
		Composite composite = g2D.getComposite();
		AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
		g2D.setComposite(alpha);
		JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getDefaultColors(), 0, 0, getWidth(),
				getHeight());
		g2D.setComposite(composite);
		if (isActive()) {
			g.setColor(AbstractLookAndFeel.getTheme().getWindowBorderColor());
		} else {
			g.setColor(AbstractLookAndFeel.getTheme().getWindowInactiveBorderColor());
		}
		g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
	}

	/** {@inheritDoc} */
	@Override
	public void paintBorder(Graphics g) {
	}

	/** {@inheritDoc} */
	@Override
	public void paintPalette(Graphics g) {
		BernsteinUtils.fillComponent(g, this);
		Graphics2D g2D = (Graphics2D) g;
		Composite composite = g2D.getComposite();
		AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
		g2D.setComposite(alpha);
		JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getDefaultColors(), 0, 0, getWidth(),
				getHeight());
		g2D.setComposite(composite);
		if (isActive()) {
			g.setColor(AbstractLookAndFeel.getTheme().getWindowBorderColor());
		} else {
			g.setColor(AbstractLookAndFeel.getTheme().getWindowInactiveBorderColor());
		}
		g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
	}

} // end of class BernsteinInternalFrameTitlePane
