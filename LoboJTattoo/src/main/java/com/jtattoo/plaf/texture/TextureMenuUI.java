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
package com.jtattoo.plaf.texture;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;

import com.jtattoo.plaf.AbstractLookAndFeel;
import com.jtattoo.plaf.BaseMenuUI;
import com.jtattoo.plaf.ColorHelper;
import com.jtattoo.plaf.JTattooUtilities;

/**
 * <p>TextureMenuUI class.</p>
 *
 * Author Michael Hagen
 *
 */
public class TextureMenuUI extends BaseMenuUI {

	/** {@inheritDoc} */
	public static ComponentUI createUI(final JComponent c) {
		return new TextureMenuUI();
	}

	/**
	 * <p>Constructor for TextureMenuUI.</p>
	 */
	public TextureMenuUI() {
		paintRolloverBorder = false;
	}

	/** {@inheritDoc} */
	@Override
	protected void paintBackground(Graphics g, JComponent c, int x, int y, int w, int h) {
		JMenuItem mi = (JMenuItem) c;
		ButtonModel model = mi.getModel();
		if (c.getParent() instanceof JMenuBar) {
			if (model.isRollover() || model.isArmed() || c instanceof JMenu && model.isSelected()) {
				TextureUtils.fillComponent(g, c, TextureUtils.ROLLOVER_TEXTURE_TYPE);
			}
		} else {
			if (model.isArmed() || c instanceof JMenu && model.isSelected()) {
				TextureUtils.fillComponent(g, c, TextureUtils.ROLLOVER_TEXTURE_TYPE);
			} else {
				if (!(mi.getBackground() instanceof ColorUIResource)) {
					super.paintBackground(g, c, x, y, w, h);
				} else {
					TextureUtils.fillComponent(g, c, TextureUtils.MENUBAR_TEXTURE_TYPE);
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void paintText(Graphics g, JMenuItem menuItem, Rectangle textRect, String text) {
		if (!AbstractLookAndFeel.getTheme().isDarkTexture()) {
			super.paintText(g, menuItem, textRect, text);
			return;
		}
		ButtonModel model = menuItem.getModel();
		FontMetrics fm = JTattooUtilities.getFontMetrics(menuItem, g, menuItem.getFont());
		int mnemIndex = menuItem.getDisplayedMnemonicIndex();
		if (!model.isEnabled()) {
			// *** paint the text disabled
			g.setColor(Color.black);
			JTattooUtilities.drawStringUnderlineCharAt(menuItem, g, text, mnemIndex, textRect.x,
					textRect.y + fm.getAscent() - 1);
			g.setColor(ColorHelper.brighter(AbstractLookAndFeel.getDisabledForegroundColor(), 40));
		} else {
			// *** paint the text normally
			g.setColor(AbstractLookAndFeel.getMenuForegroundColor());
			if (menuItem.getParent() instanceof JMenuBar) {
				if (model.isRollover() || model.isArmed() || menuItem instanceof JMenu && model.isSelected()) {
					g.setColor(AbstractLookAndFeel.getMenuSelectionForegroundColor());
				}
			} else if (menuItem.isArmed() || menuItem.isSelected()) {
				g.setColor(AbstractLookAndFeel.getMenuSelectionForegroundColor());
			} else {
				g.setColor(Color.black);
				JTattooUtilities.drawStringUnderlineCharAt(menuItem, g, text, mnemIndex, textRect.x,
						textRect.y + fm.getAscent() - 1);
				Color foreColor = menuItem.getForeground();
				if (foreColor instanceof UIResource) {
					foreColor = AbstractLookAndFeel.getMenuForegroundColor();
				}
				g.setColor(foreColor);
			}
		}
		JTattooUtilities.drawStringUnderlineCharAt(menuItem, g, text, mnemIndex, textRect.x,
				textRect.y + fm.getAscent());
	}

} // end of clas TextureMenuUI
