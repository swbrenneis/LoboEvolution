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
package com.jtattoo.plaf.aluminium;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseToggleButtonUI;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * <p>AluminiumToggleButtonUI class.</p>
 *
 * @author Michael Hagen
 * @version $Id: $Id
 */
public class AluminiumToggleButtonUI extends BaseToggleButtonUI {

	/** {@inheritDoc} */
	public static ComponentUI createUI(JComponent c) {
		return new AluminiumToggleButtonUI();
	}

	/** {@inheritDoc} */
	@Override
	protected void paintBackground(Graphics g, AbstractButton b) {
		if (!b.isContentAreaFilled() || b.getParent() instanceof JMenuBar) {
			return;
		}

		if (!(b.isBorderPainted() && b.getBorder() instanceof UIResource)) {
			super.paintBackground(g, b);
			return;
		}

		int width = b.getWidth();
		int height = b.getHeight();
		ButtonModel model = b.getModel();
		Graphics2D g2D = (Graphics2D) g;
		Composite composite = g2D.getComposite();
		Object savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		Color colors[];
		if (b.isEnabled()) {
			Color background = b.getBackground();
			if (background instanceof ColorUIResource) {
				if (b.isRolloverEnabled() && model.isRollover()) {
					colors = AbstractLookAndFeel.getTheme().getRolloverColors();
				} else if (model.isPressed() && model.isArmed() || model.isSelected()) {
					colors = AbstractLookAndFeel.getTheme().getPressedColors();
				} else {
					if (AbstractLookAndFeel.getTheme().doShowFocusFrame() && b.hasFocus()) {
						colors = AbstractLookAndFeel.getTheme().getFocusColors();
					} else {
						colors = AbstractLookAndFeel.getTheme().getButtonColors();
					}
				}
			} else {
				if (model.isPressed() && model.isArmed()) {
					colors = ColorHelper.createColorArr(ColorHelper.darker(background, 30),
							ColorHelper.darker(background, 10), 20);
				} else if (b.isRolloverEnabled() && model.isRollover()) {
					if (model.isSelected()) {
						colors = ColorHelper.createColorArr(ColorHelper.darker(background, 20), background, 20);
					} else {
						colors = ColorHelper.createColorArr(ColorHelper.brighter(background, 50),
								ColorHelper.brighter(background, 10), 20);
					}
				} else if (model.isSelected()) {
					colors = ColorHelper.createColorArr(ColorHelper.darker(background, 40),
							ColorHelper.darker(background, 20), 20);
				} else {
					colors = ColorHelper.createColorArr(ColorHelper.brighter(background, 30),
							ColorHelper.darker(background, 10), 20);
				}
			}
		} else { // disabled
			colors = AbstractLookAndFeel.getTheme().getDisabledColors();
		}
		if (AbstractLookAndFeel.getTheme().doDrawSquareButtons()
				|| (width < 64 || height < 16) && (b.getText() == null || b.getText().length() == 0)) {
			JTattooUtilities.fillHorGradient(g, colors, 0, 0, width - 1, height - 1);
			if (model.isEnabled()) {
				g2D.setColor(AbstractLookAndFeel.getFrameColor());
			} else {
				g2D.setColor(ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 20));
			}
			g2D.drawRect(0, 0, width - 1, height - 1);
			AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
			g2D.setComposite(alpha);
			g2D.setColor(Color.white);
			g2D.drawRect(1, 1, width - 3, height - 3);
		} else {
			int d = height - 2;
			Shape savedClip = g.getClip();
			Area clipArea = new Area(new RoundRectangle2D.Double(0, 0, width - 1, height - 1, d, d));
			if (savedClip != null) {
				clipArea.intersect(new Area(savedClip));
			}
			g2D.setClip(clipArea);
			JTattooUtilities.fillHorGradient(g, colors, 0, 0, width - 1, height - 1);
			g2D.setClip(savedClip);

			if (model.isEnabled()) {
				g2D.setColor(AbstractLookAndFeel.getFrameColor());
			} else {
				g2D.setColor(ColorHelper.brighter(AbstractLookAndFeel.getFrameColor(), 20));
			}
			g2D.drawRoundRect(0, 0, width - 1, height - 1, d, d);

			AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f);
			g2D.setComposite(alpha);
			g2D.setColor(Color.white);
			g2D.drawRoundRect(1, 1, width - 3, height - 3, d - 2, d - 2);

		}
		g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRenderingHint);
		g2D.setComposite(composite);
	}

	/** {@inheritDoc} */
	@Override
	protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect,
			Rectangle iconRect) {
		Graphics2D g2D = (Graphics2D) g;
		int width = b.getWidth();
		int height = b.getHeight();
		if (AbstractLookAndFeel.getTheme().doDrawSquareButtons() || !b.isContentAreaFilled()
				|| !(b.getBorder() instanceof UIResource)
				|| (width < 64 || height < 16) && (b.getText() == null || b.getText().length() == 0)) {
			g.setColor(AbstractLookAndFeel.getFocusColor());
			BasicGraphicsUtils.drawDashedRect(g, 4, 3, width - 8, height - 6);
		} else {
			Object savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2D.setColor(AbstractLookAndFeel.getFocusColor());
			int d = b.getHeight() - 4;
			g2D.drawRoundRect(2, 2, b.getWidth() - 5, b.getHeight() - 5, d, d);
			g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, savedRenderingHint);
		}
	}

} // end of class AluminiumToggleButtonUI
