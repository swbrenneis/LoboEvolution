/*
* Copyright (c) 2002 and later by MH Software-Entwicklung. All Rights Reserved.
*
* JTattoo is dual licensed. You can use it under the terms and conditions of the
* GNU General Public License version 2.0 or later as published by the Free Software
* Foundation.
*
* see: gpl-2.0.txt
*
* Registered users (this who payed for a license) could use the software under the
* terms and conditions of the GNU Lesser General Public License version 2.0 or later
* with classpath exception as published by the Free Software Foundation.
*
* see: lgpl-2.0.txt
* see: classpath-exception.txt
*/

package com.jtattoo.plaf;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

/**
 * <p>BaseButtonUI class.</p>
 *
 * Author Michael Hagen
 *
 */
public class BaseButtonUI extends BasicButtonUI {

	/** Constant viewRect */
	protected static final Rectangle viewRect = new Rectangle();
	/** Constant textRect */
	protected static final Rectangle textRect = new Rectangle();
	/** Constant iconRect */
	protected static final Rectangle iconRect = new Rectangle();
	/** Constant defaultColors */
	protected static Color[] defaultColors = null;

	/** {@inheritDoc} */
	public static ComponentUI createUI(final JComponent c) {
		return new BaseButtonUI();
	}

	/** {@inheritDoc} */
	@Override
	protected BasicButtonListener createButtonListener(AbstractButton b) {
		return new BaseButtonListener(b);
	}

	/** {@inheritDoc} */
	@Override
	public void installDefaults(AbstractButton b) {
		super.installDefaults(b);
		b.setOpaque(false);
		b.setRolloverEnabled(true);
	}

	/** {@inheritDoc} */
	@Override
	protected void installKeyboardActions(AbstractButton b) {
		super.installKeyboardActions(b);
		InputMap im = (InputMap) UIManager.get("Button.focusInputMap");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, true), "released");
		Color[] cArr = AbstractLookAndFeel.getTheme().getButtonColors();
		defaultColors = new Color[cArr.length];
		for (int i = 0; i < cArr.length; i++) {
			defaultColors[i] = ColorHelper.brighter(cArr[i], 20);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void paint(final Graphics g, final JComponent c) {
		Graphics2D g2D = (Graphics2D) g;

		AbstractButton b = (AbstractButton) c;
		Font f = c.getFont();
		g.setFont(f);
		FontMetrics fm = JTattooUtilities.getFontMetrics(b, g, b.getFont());
		Insets insets = c.getInsets();

		viewRect.x = insets.left;
		viewRect.y = insets.top;
		viewRect.width = b.getWidth() - (insets.right + viewRect.x);
		viewRect.height = b.getHeight() - (insets.bottom + viewRect.y);

		textRect.x = textRect.y = textRect.width = textRect.height = 0;
		iconRect.x = iconRect.y = iconRect.width = iconRect.height = 0;

		int iconTextGap = b.getIconTextGap();
		String text = SwingUtilities.layoutCompoundLabel(c, fm, b.getText(), b.getIcon(), b.getVerticalAlignment(),
				b.getHorizontalAlignment(), b.getVerticalTextPosition(), b.getHorizontalTextPosition(), viewRect,
				iconRect, textRect, b.getText() == null ? 0 : iconTextGap);

		paintBackground(g, b);

		if (b.getIcon() != null) {
			if (!b.isEnabled()) {
				Composite savedComposite = g2D.getComposite();
				AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
				g2D.setComposite(alpha);
				paintIcon(g, c, iconRect);
				g2D.setComposite(savedComposite);
			} else {
				if (b.getModel().isPressed() && b.getModel().isRollover()) {
					iconRect.x++;
					iconRect.y++;
				}
				paintIcon(g, c, iconRect);
			}
		}

		if (text != null && !text.equals("")) {
			View v = (View) c.getClientProperty(BasicHTML.propertyKey);
			if (v != null) {
				Object savedRenderingHint = null;
				if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
					savedRenderingHint = g2D.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
					g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				}
				v.paint(g, textRect);
				if (AbstractLookAndFeel.getTheme().isTextAntiAliasingOn()) {
					g2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, savedRenderingHint);
				}
			} else {
				paintText(g, b, textRect, text);
			}
		}

		if (b.isFocusPainted() && b.hasFocus()) {
			paintFocus(g, b, viewRect, textRect, iconRect);
		}
	}

	/**
	 * <p>paintBackground.</p>
	 *
	 * @param g a {@link java.awt.Graphics} object.
	 * @param b a {@link javax.swing.AbstractButton} object.
	 */
	protected void paintBackground(Graphics g, AbstractButton b) {
		if (!b.isContentAreaFilled() || b.getParent() instanceof JMenuBar) {
			return;
		}

		int width = b.getWidth();
		int height = b.getHeight();

		ButtonModel model = b.getModel();
		Color[] colors = AbstractLookAndFeel.getTheme().getButtonColors();
		if (b.isEnabled()) {
			Color background = b.getBackground();
			if (background instanceof ColorUIResource) {
				if (model.isPressed() && model.isArmed()) {
					colors = AbstractLookAndFeel.getTheme().getPressedColors();
				} else if (b.isRolloverEnabled() && model.isRollover()) {
					colors = AbstractLookAndFeel.getTheme().getRolloverColors();
				} else if (AbstractLookAndFeel.getTheme().doShowFocusFrame() && b.hasFocus()) {
					colors = AbstractLookAndFeel.getTheme().getFocusColors();
				} else if (JTattooUtilities.isFrameActive(b) && b.getRootPane() != null
						&& b.equals(b.getRootPane().getDefaultButton())) {
					colors = defaultColors;
				}
			} else {
				if (model.isPressed() && model.isArmed()) {
					colors = ColorHelper.createColorArr(ColorHelper.darker(background, 30),
							ColorHelper.darker(background, 10), 20);
				} else {
					if (b.isRolloverEnabled() && model.isRollover()) {
						colors = ColorHelper.createColorArr(ColorHelper.brighter(background, 50),
								ColorHelper.brighter(background, 10), 20);
					} else {
						colors = ColorHelper.createColorArr(ColorHelper.brighter(background, 30),
								ColorHelper.darker(background, 10), 20);
					}
				}
			}
		} else { // disabled
			colors = AbstractLookAndFeel.getTheme().getDisabledColors();
		}

		if (b.isBorderPainted() && b.getBorder() != null && !(b.getBorder() instanceof EmptyBorder)) {
			Insets insets = b.getBorder().getBorderInsets(b);
			int x = insets.left > 0 ? 1 : 0;
			int y = insets.top > 0 ? 1 : 0;
			int w = insets.right > 0 ? width - 1 : width;
			int h = insets.bottom > 0 ? height - 1 : height;
			JTattooUtilities.fillHorGradient(g, colors, x, y, w - x, h - y);
		} else {
			JTattooUtilities.fillHorGradient(g, colors, 0, 0, width, height);
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect, Rectangle textRect,
			Rectangle iconRect) {
		g.setColor(AbstractLookAndFeel.getFocusColor());
		BasicGraphicsUtils.drawDashedRect(g, 4, 3, b.getWidth() - 8, b.getHeight() - 6);
	}

	/** {@inheritDoc} */
	@Override
	protected void paintText(Graphics g, AbstractButton b, Rectangle textRect, String text) {
		ButtonModel model = b.getModel();
		FontMetrics fm = JTattooUtilities.getFontMetrics(b, g, b.getFont());
		int mnemIndex = b.getDisplayedMnemonicIndex();

		if (model.isEnabled()) {
			Color foreground = b.getForeground();
			Color background = b.getBackground();
			int offs = 0;
			if (model.isArmed() && model.isPressed()) {
				offs = 1;
			}
			if (!(model.isPressed() && model.isArmed())) {
				Object sc = b.getClientProperty("shadowColor");
				if (sc instanceof Color) {
					g.setColor((Color) sc);
					JTattooUtilities.drawStringUnderlineCharAt(b, g, text, mnemIndex, textRect.x + 1,
							textRect.y + 1 + fm.getAscent());
				}
			}
			if (background instanceof ColorUIResource) {
				if (model.isPressed() && model.isArmed()) {
					g.setColor(AbstractLookAndFeel.getTheme().getPressedForegroundColor());
				} else if (model.isRollover()) {
					g.setColor(AbstractLookAndFeel.getTheme().getRolloverForegroundColor());
				} else {
					g.setColor(foreground);
				}
			} else {
				g.setColor(foreground);
			}
			JTattooUtilities.drawStringUnderlineCharAt(b, g, text, mnemIndex, textRect.x + offs,
					textRect.y + offs + fm.getAscent());
		} else {
			if (ColorHelper.getGrayValue(b.getForeground()) < 128) {
				g.setColor(Color.white);
				JTattooUtilities.drawStringUnderlineCharAt(b, g, text, mnemIndex, textRect.x + 1,
						textRect.y + 1 + fm.getAscent());
			}
			g.setColor(AbstractLookAndFeel.getDisabledForegroundColor());
			JTattooUtilities.drawStringUnderlineCharAt(b, g, text, mnemIndex, textRect.x, textRect.y + fm.getAscent());
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void uninstallDefaults(AbstractButton b) {
		super.uninstallDefaults(b);
		b.setOpaque(true);
		b.setRolloverEnabled(false);
	}

} // end of class BaseButtonUI
