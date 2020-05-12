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
package com.jtattoo.plaf.smart;

import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;

import javax.swing.Icon;
import javax.swing.JButton;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseIcons;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * <p>SmartIcons class.</p>
 *
 * @author Michael Hagen
 * @version $Id: $Id
 */
public class SmartIcons extends BaseIcons {

	// ------------------------------------------------------------------------------
	private static class TitleButtonIcon implements Icon {

		private static final Color CLOSER_COLOR_LIGHT = new Color(241, 172, 154);
		private static final Color CLOSER_COLOR_DARK = new Color(224, 56, 2);
		public static final int ICON_ICON_TYP = 0;
		public static final int MIN_ICON_TYP = 1;
		public static final int MAX_ICON_TYP = 2;
		public static final int CLOSE_ICON_TYP = 3;
		private int iconTyp = ICON_ICON_TYP;

		public TitleButtonIcon(int typ) {
			iconTyp = typ;
		}

		@Override
		public int getIconHeight() {
			return 20;
		}

		@Override
		public int getIconWidth() {
			return 20;
		}

		@Override
		public void paintIcon(Component c, Graphics g, int x, int y) {
			int w = c.getWidth();
			int h = c.getHeight();

			JButton b = (JButton) c;
			Graphics2D g2D = (Graphics2D) g;

			boolean isActive = JTattooUtilities.isActive(b);
			boolean isPressed = b.getModel().isPressed();
			boolean isArmed = b.getModel().isArmed();
			boolean isRollover = b.getModel().isRollover();

			Color cHi = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getWindowTitleColorLight(), 40);
			Color cLo = ColorHelper.darker(AbstractLookAndFeel.getTheme().getWindowTitleColorDark(), 10);
			if (iconTyp == CLOSE_ICON_TYP) {
				cHi = CLOSER_COLOR_LIGHT;
				cLo = CLOSER_COLOR_DARK;
			}

			Color fcHi = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getWindowTitleColorDark(), 80);
			Color fcLo = ColorHelper.darker(AbstractLookAndFeel.getTheme().getWindowTitleColorDark(), 40);

			if (!isActive) {
				cHi = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getWindowInactiveTitleColorLight(), 40);
				cLo = ColorHelper.darker(AbstractLookAndFeel.getTheme().getWindowInactiveTitleColorDark(), 10);
				fcHi = ColorHelper.brighter(AbstractLookAndFeel.getTheme().getWindowInactiveTitleColorLight(), 60);
				fcLo = ColorHelper.darker(AbstractLookAndFeel.getTheme().getWindowInactiveTitleColorDark(), 10);
			}
			if (isPressed && isArmed) {
				Color cTemp = ColorHelper.darker(cLo, 10);
				cLo = ColorHelper.darker(cHi, 10);
				cHi = cTemp;
			} else if (isRollover) {
				cHi = ColorHelper.brighter(cHi, 30);
				cLo = ColorHelper.brighter(cLo, 30);
			}

			Shape savedClip = g.getClip();
			Area area = new Area(new RoundRectangle2D.Double(1, 1, w - 1, h - 1, 3, 3));
			g2D.setClip(area);

			g2D.setPaint(new GradientPaint(0, 0, fcLo, w, h, fcHi));
			g.fillRect(1, 1, w - 1, h - 1);

			g2D.setPaint(new GradientPaint(0, 0, ColorHelper.brighter(cHi, 80), w, h, ColorHelper.darker(cLo, 30)));
			g.fillRect(2, 2, w - 3, h - 3);

			g2D.setPaint(new GradientPaint(0, 0, cHi, w, h, cLo));
			g.fillRect(3, 3, w - 5, h - 5);

			g2D.setClip(savedClip);

			cHi = Color.white;
			cLo = ColorHelper.darker(cLo, 30);
			Icon icon = null;
			switch (iconTyp) {
			case ICON_ICON_TYP:
				icon = new BaseIcons.IconSymbol(cHi, cLo, null);
				break;
			case MIN_ICON_TYP:
				icon = new BaseIcons.MinSymbol(cHi, cLo, null);
				break;
			case MAX_ICON_TYP:
				icon = new BaseIcons.MaxSymbol(cHi, cLo, null);
				break;
			case CLOSE_ICON_TYP:
				icon = new BaseIcons.CloseSymbol(cHi, cLo, null);
				break;
			default:
				break;
			}
			if (icon != null) {
				icon.paintIcon(c, g, 0, 0);
			}
		}
	}

	/**
	 * <p>getCloseIcon.</p>
	 *
	 * @return a {@link javax.swing.Icon} object.
	 */
	public static Icon getCloseIcon() {
		if (closeIcon == null) {
			if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
				closeIcon = new MacCloseIcon();
			} else {
				closeIcon = new TitleButtonIcon(TitleButtonIcon.CLOSE_ICON_TYP);
			}
		}
		return closeIcon;
	}

	/**
	 * <p>getIconIcon.</p>
	 *
	 * @return a {@link javax.swing.Icon} object.
	 */
	public static Icon getIconIcon() {
		if (iconIcon == null) {
			if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
				iconIcon = new MacIconIcon();
			} else {
				iconIcon = new TitleButtonIcon(TitleButtonIcon.ICON_ICON_TYP);
			}
		}
		return iconIcon;
	}

	/**
	 * <p>getMaxIcon.</p>
	 *
	 * @return a {@link javax.swing.Icon} object.
	 */
	public static Icon getMaxIcon() {
		if (maxIcon == null) {
			if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
				maxIcon = new MacMaxIcon();
			} else {
				maxIcon = new TitleButtonIcon(TitleButtonIcon.MAX_ICON_TYP);
			}
		}
		return maxIcon;
	}

	/**
	 * <p>getMinIcon.</p>
	 *
	 * @return a {@link javax.swing.Icon} object.
	 */
	public static Icon getMinIcon() {
		if (minIcon == null) {
			if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
				minIcon = new MacMinIcon();
			} else {
				minIcon = new TitleButtonIcon(TitleButtonIcon.MIN_ICON_TYP);
			}
		}
		return minIcon;
	}

} // end of class SmartIcons
