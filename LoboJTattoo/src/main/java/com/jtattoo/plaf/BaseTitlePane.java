/*
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
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
import java.awt.Component;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;

/**
 * This class is a modified copy of the javax.swing.plaf.metal.MetalTitlePaneUI
 *
 * Class that manages a JLF awt.Window-descendant class's title bar.
 * <p>
 * This class assumes it will be created with a particular window decoration
 * style, and that if the style changes, a new one will be created.
 *
 * @version 1.12 01/23/03
 * Author Terry Kellerman
 * Author Michael Hagen
 * @since 1.4
 */
public class BaseTitlePane extends JComponent implements TitlePane {

	protected class CloseAction extends AbstractAction {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public CloseAction() {
			super(UIManager.getString("MetalTitlePane.closeTitle"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			close();
		}
	}

	protected class IconifyAction extends AbstractAction {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public IconifyAction() {
			super(UIManager.getString("MetalTitlePane.iconifyTitle"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			iconify();
		}
	}

	protected class MaximizeAction extends AbstractAction {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public MaximizeAction() {
			super(UIManager.getString("MetalTitlePane.maximizeTitle"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			maximize();
		}
	}

	// -----------------------------------------------------------------------------------------------
	protected class PropertyChangeHandler implements PropertyChangeListener {

		@Override
		public void propertyChange(PropertyChangeEvent pce) {
			String name = pce.getPropertyName();
			// Frame.state isn't currently bound.
			if ("resizable".equals(name) || "state".equals(name)) {
				Frame frame = getFrame();
				if (frame != null) {
					setState(getFrame().getExtendedState(), true);
				}
				if ("resizable".equals(name)) {
					getRootPane().repaint();
				}
			} else if ("title".equals(name)) {
				repaint();
			} else if ("componentOrientation".equals(name)) {
				revalidate();
				repaint();
//            // a call to setMaximizedBounds may cause an invalid frame size on multi screen environments
//            // see: http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6699851
//            // try and error to avoid the setMaximizedBounds bug
//            } else if (!JTattooUtilities.isMac() && useMaximizedBounds && "windowMaximize".equals(name)) {
//                Frame frame = getFrame();
//                if (frame != null) {
//                    GraphicsConfiguration gc = frame.getGraphicsConfiguration();
//                    Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
//                    Rectangle screenBounds = gc.getBounds();
//                    int x = Math.max(0, screenInsets.left);
//                    int y = Math.max(0, screenInsets.top);
//                    int w = screenBounds.width - (screenInsets.left + screenInsets.right);
//                    int h = screenBounds.height - (screenInsets.top + screenInsets.bottom);
//                    // Keep taskbar visible
//                    frame.setMaximizedBounds(new Rectangle(x, y, w, h));
//                }
//            } else if (!JTattooUtilities.isMac() && useMaximizedBounds && "windowMaximized".equals(name)) {
//                Frame frame = getFrame();
//                if (frame != null) {
//                    GraphicsConfiguration gc = frame.getGraphicsConfiguration();
//                    Rectangle screenBounds = gc.getBounds();
//                    if (frame.getSize().width > screenBounds.width || frame.getSize().height > screenBounds.height) {
//                        useMaximizedBounds = false;
//                        frame.setMaximizedBounds(null);
//                        restore();
//                        maximize();
//                    }
//                }
//            } else if (!JTattooUtilities.isMac() && "windowMoved".equals(name)) {
//                useMaximizedBounds = true;
			}

			if ("windowRestored".equals(name)) {
				wasMaximized = false;
			} else if ("windowMaximized".equals(name)) {
				wasMaximized = true;
			}
		}
	}

	protected class RestoreAction extends AbstractAction {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public RestoreAction() {
			super(UIManager.getString("MetalTitlePane.restoreTitle"));
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			restore();
		}
	}

	// -----------------------------------------------------------------------------------------------
	protected class SystemMenuBar extends JMenuBar {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		public SystemMenuBar() {
			setOpaque(false);
		}

		protected int computeHeight() {
			FontMetrics fm = JTattooUtilities.getFontMetrics(this, null, getFont());
			return fm.getHeight() + 6;
		}

		@Override
		public Dimension getMinimumSize() {
			return getPreferredSize();
		}

		@Override
		public Dimension getPreferredSize() {
			Dimension size = super.getPreferredSize();
			Image image = getFrameIconImage();
			if (image != null) {
				int iw = image.getWidth(null);
				int ih = image.getHeight(null);
				int th = computeHeight();
				if (ih > th) {
					double scale = (double) th / (double) ih;
					iw = (int) (scale * iw);
					ih = (int) (scale * ih);
				}
				return new Dimension(Math.max(iw, size.width), Math.max(ih, size.height));
			} else {
				return size;
			}
		}

		@Override
		public void paint(final Graphics g) {
			Image image = getFrameIconImage();
			if (image != null) {
				Graphics2D g2D = (Graphics2D) g;
				Object savedHint = g2D.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
				g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				int x = 0;
				int y = 0;
				int iw = image.getWidth(null);
				int ih = image.getHeight(null);
				if (ih > getHeight()) {
					double scale = (double) (getHeight() - 2) / (double) ih;
					iw = (int) (scale * iw);
					ih = (int) (scale * ih);
				} else {
					y = (getHeight() - ih) / 2;
				}
				g2D.drawImage(image, x, y, iw, ih, null);
				if (savedHint != null) {
					g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, savedHint);
				}
			} else {
				Icon icon = UIManager.getIcon("InternalFrame.icon");
				if (icon != null) {
					icon.paintIcon(this, g, 2, 2);
				}
			}
		}
	}

	// -----------------------------------------------------------------------------------------------
	protected class TitlePaneLayout implements LayoutManager {

		@Override
		public void addLayoutComponent(String name, Component c) {
		}

		protected int computeHeight() {
			FontMetrics fm = JTattooUtilities.getFontMetrics(BaseTitlePane.this, null, getFont());
			return fm.getHeight() + 6;
		}

		@Override
		public void layoutContainer(Container c) {
			if (AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn()) {
				layoutMacStyle(c);
			} else {
				layoutDefault(c);
			}
		}

		public void layoutDefault(Container c) {
			boolean leftToRight = isLeftToRight();

			int spacing = getHorSpacing();
			int w = getWidth();
			int h = getHeight();

			// assumes all buttons have the same dimensions these dimensions include the
			// borders
			int btnHeight = h - getVerSpacing();
			int btnWidth = btnHeight;

			if (menuBar != null) {
				int mw = menuBar.getPreferredSize().width;
				int mh = menuBar.getPreferredSize().height;
				if (leftToRight) {
					menuBar.setBounds(2, (h - mh) / 2, mw, mh);
				} else {
					menuBar.setBounds(getWidth() - mw, (h - mh) / 2, mw, mh);
				}
			}

			int x = leftToRight ? w - spacing : 0;
			int y = Math.max(0, (h - btnHeight) / 2 - 1);

			if (closeButton != null) {
				x += leftToRight ? -btnWidth : spacing;
				closeButton.setBounds(x, y, btnWidth, btnHeight);
				if (!leftToRight) {
					x += btnWidth;
				}
			}

			if (maxButton != null && maxButton.getParent() != null) {
				if (Toolkit.getDefaultToolkit().isFrameStateSupported(BaseRootPaneUI.MAXIMIZED_BOTH)) {
					x += leftToRight ? -spacing - btnWidth : spacing;
					maxButton.setBounds(x, y, btnWidth, btnHeight);
					if (!leftToRight) {
						x += btnWidth;
					}
				}
			}

			if (iconifyButton != null && iconifyButton.getParent() != null) {
				x += leftToRight ? -spacing - btnWidth : spacing;
				iconifyButton.setBounds(x, y, btnWidth, btnHeight);
				if (!leftToRight) {
					x += btnWidth;
				}
			}

			buttonsWidth = leftToRight ? w - x : x;

			if (customTitlePanel != null) {
				int maxWidth = w - buttonsWidth - spacing - 20;
				if (menuBar != null) {
					maxWidth -= menuBar.getPreferredSize().width;
					maxWidth -= spacing;
				}
				int cpw = Math.min(maxWidth, customTitlePanel.getPreferredSize().width);
				int cph = h;
				int cpx = leftToRight ? w - buttonsWidth - cpw : buttonsWidth;
				int cpy = 0;
				customTitlePanel.setBounds(cpx, cpy, cpw, cph);
				buttonsWidth += customTitlePanel.getPreferredSize().width;
			}
		}

		public void layoutMacStyle(Container c) {
			int spacing = getHorSpacing();
			getWidth();
			int h = getHeight();

			// assumes all buttons have the same dimensions these dimensions include the
			// borders
			int btnHeight = h - getVerSpacing() - 1;
			int btnWidth = btnHeight;

			int x = 2;
			int y = centerButtons() ? Math.max(0, (h - btnHeight) / 2 - 1) : 0;

			if (closeButton != null) {
				closeButton.setBounds(x, y, btnWidth, btnHeight);
				x += btnWidth + spacing;
			}
			if (iconifyButton != null && iconifyButton.getParent() != null) {
				iconifyButton.setBounds(x, y, btnWidth, btnHeight);
				x += btnWidth + spacing;
			}
			if (maxButton != null && maxButton.getParent() != null) {
				if (Toolkit.getDefaultToolkit().isFrameStateSupported(BaseRootPaneUI.MAXIMIZED_BOTH)) {
					maxButton.setBounds(x, y, btnWidth, btnHeight);
					x += btnWidth + spacing;
				}
			}

			buttonsWidth = x;

			if (customTitlePanel != null) {
				int cpx = buttonsWidth + 5;
				int cpy = 0;
				int cpw = customTitlePanel.getPreferredSize().width;
				int cph = h;
				customTitlePanel.setBounds(cpx, cpy, cpw, cph);
				buttonsWidth += cpw + 5;
			}
		}

		@Override
		public Dimension minimumLayoutSize(Container c) {
			return preferredLayoutSize(c);
		}

		@Override
		public Dimension preferredLayoutSize(Container c) {
			int height = computeHeight();
			return new Dimension(height, height);
		}

		@Override
		public void removeLayoutComponent(Component c) {
		}
	}

	// -----------------------------------------------------------------------------------------------
	protected class WindowHandler extends WindowAdapter {

		@Override
		public void windowActivated(WindowEvent ev) {
			setActive(true);
		}

		@Override
		public void windowDeactivated(WindowEvent ev) {
			setActive(false);
		}

		@Override
		public void windowDeiconified(WindowEvent e) {
			// Workarround to avoid a bug within OSX and Java 1.7
			if (JTattooUtilities.isMac() && JTattooUtilities.getJavaVersion() >= 1.7 && wasMaximized) {
				SwingUtilities.invokeLater(BaseTitlePane.this::maximize);
			}
		}
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	/** Constant PAINT_ACTIVE="paintActive" */
	public static final String PAINT_ACTIVE = "paintActive";
	/** Constant ICONIFY="Iconify" */
	public static final String ICONIFY = "Iconify";
	/** Constant MAXIMIZE="Maximize" */
	public static final String MAXIMIZE = "Maximize";
	/** Constant CLOSE="Close" */
	public static final String CLOSE = "Close";

	static int getInt(Object key, int defaultValue) {
		Object value = UIManager.get(key);
		if (value instanceof Integer) {
			return (Integer) value;
		}
		if (value instanceof String) {
			try {
				return Integer.parseInt((String) value);
			} catch (NumberFormatException nfe) {
			}
		}
		return defaultValue;
	}

	protected PropertyChangeListener propertyChangeListener;
	protected Action closeAction;
	protected Action iconifyAction;
	protected Action restoreAction;
	protected Action maximizeAction;
	protected JMenuBar menuBar;
	protected JPanel customTitlePanel;
	protected JButton iconifyButton;
	protected JButton maxButton;
	protected JButton closeButton;
	protected final Icon iconifyIcon;
	protected final Icon maximizeIcon;
	protected final Icon minimizeIcon;

	protected final Icon closeIcon;
	protected WindowListener windowListener;

	protected Window window;

	protected final JRootPane rootPane;

	protected final BaseRootPaneUI rootPaneUI;

	protected int buttonsWidth;

	protected int state;

	// This flag is used to avoid a bug with OSX and java 1.7. The call to
	// setExtendedState
	// with both flags ICONIFY and MAXIMIZED_BOTH throws an illegal state exception,
	// so we
	// have to switch off the MAXIMIZED_BOTH flag in the iconify() method. If frame
	// is deiconified
	// we use the wasMaximized flag to restore the maximized state.
	protected boolean wasMaximized;

	// This flag indicates a maximize error. This occurs on multiscreen environments
	// where the first
	// screen does not have the same resolution as the second screen. In this case
	// we only simulate the
	// maximize/restore behaviour. It's not a perfect simulation (frame border will
	// stay visible,
	// and we have to restore the bounds if look and feel changes in maximized
	// state)
	protected boolean wasMaximizeError = false;

	protected BufferedImage backgroundImage = null;

	protected float alphaValue = 0.85f;

	/**
	 * <p>Constructor for BaseTitlePane.</p>
	 *
	 * @param root a {@link javax.swing.JRootPane} object.
	 * @param ui a {@link com.jtattoo.plaf.BaseRootPaneUI} object.
	 */
	public BaseTitlePane(JRootPane root, BaseRootPaneUI ui) {
		rootPane = root;
		rootPaneUI = ui;
		state = -1;
		wasMaximized = false;
		iconifyIcon = UIManager.getIcon("InternalFrame.iconifyIcon");
		maximizeIcon = UIManager.getIcon("InternalFrame.maximizeIcon");
		minimizeIcon = UIManager.getIcon("InternalFrame.minimizeIcon");
		closeIcon = UIManager.getIcon("InternalFrame.closeIcon");

		installSubcomponents();
		installDefaults();
		setLayout(createLayout());
	}

	/** {@inheritDoc} */
	@Override
	public void addNotify() {
		super.addNotify();
		uninstallListeners();
		window = SwingUtilities.getWindowAncestor(this);
		if (window != null) {
			if (window instanceof Frame) {
				setState(((Frame) window).getExtendedState());
			} else {
				setState(0);
			}
			setActive(window.isActive());
			installListeners();
		}
	}

	/**
	 * <p>calculateMaxBounds.</p>
	 *
	 * @param frame a {@link java.awt.Frame} object.
	 * @return a {@link java.awt.Rectangle} object.
	 */
	protected Rectangle calculateMaxBounds(Frame frame) {
		GraphicsConfiguration gc = frame.getGraphicsConfiguration();
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
		Rectangle maxBounds = gc.getBounds();
		maxBounds.x = Math.max(0, screenInsets.left);
		maxBounds.y = Math.max(0, screenInsets.top);
		maxBounds.width -= screenInsets.left + screenInsets.right;
		maxBounds.height -= screenInsets.top + screenInsets.bottom;
		// If Taskbar is in auto hide mode the maximum bounds are not correct, currently
		// I don't now
		// how to fix this issue, so I just let one pixel space arround the window.
		if (screenInsets.top == 0 && screenInsets.left == 0 && screenInsets.bottom == 0 && screenInsets.right == 0) {
			maxBounds.x += 1;
			maxBounds.y += 1;
			maxBounds.width -= 2;
			maxBounds.height -= 2;
		}
		return maxBounds;
	}

	/**
	 * <p>centerButtons.</p>
	 *
	 * @return a boolean.
	 */
	protected boolean centerButtons() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public void close() {
		if (window != null) {
			window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
		}
	}

	/**
	 * <p>createActions.</p>
	 */
	protected void createActions() {
		closeAction = new CloseAction();
		iconifyAction = new IconifyAction();
		restoreAction = new RestoreAction();
		maximizeAction = new MaximizeAction();
	}

	/**
	 * <p>createButtons.</p>
	 */
	public void createButtons() {
		iconifyButton = new BaseTitleButton(iconifyAction, ICONIFY, iconifyIcon, 1.0f);
		maxButton = new BaseTitleButton(restoreAction, MAXIMIZE, maximizeIcon, 1.0f);
		closeButton = new BaseTitleButton(closeAction, CLOSE, closeIcon, 1.0f);
	}

	/**
	 * <p>createLayout.</p>
	 *
	 * @return a {@link java.awt.LayoutManager} object.
	 */
	public LayoutManager createLayout() {
		return new TitlePaneLayout();
	}

	/**
	 * <p>createMenuBar.</p>
	 */
	protected void createMenuBar() {
		menuBar = new SystemMenuBar();
		if (rootPane.getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
			JMenu menu = new JMenu("   ");

			JMenuItem mi = menu.add(restoreAction);
			int mnemonic = getInt("MetalTitlePane.restoreMnemonic", -1);
			if (mnemonic != -1) {
				mi.setMnemonic(mnemonic);
			}
			mi = menu.add(iconifyAction);
			mnemonic = getInt("MetalTitlePane.iconifyMnemonic", -1);
			if (mnemonic != -1) {
				mi.setMnemonic(mnemonic);
			}

			if (Toolkit.getDefaultToolkit().isFrameStateSupported(BaseRootPaneUI.MAXIMIZED_BOTH)) {
				mi = menu.add(maximizeAction);
				mnemonic = getInt("MetalTitlePane.maximizeMnemonic", -1);
				if (mnemonic != -1) {
					mi.setMnemonic(mnemonic);
				}
			}
			menu.addSeparator();
			mi = menu.add(closeAction);
			mnemonic = getInt("MetalTitlePane.closeMnemonic", -1);
			if (mnemonic != -1) {
				mi.setMnemonic(mnemonic);
			}

			menuBar.add(menu);
		}
	}

	/**
	 * <p>createWindowListener.</p>
	 *
	 * @return a {@link java.awt.event.WindowListener} object.
	 */
	protected WindowListener createWindowListener() {
		return new WindowHandler();
	}

	/**
	 * <p>createWindowPropertyChangeListener.</p>
	 *
	 * @return a {@link java.beans.PropertyChangeListener} object.
	 */
	protected PropertyChangeListener createWindowPropertyChangeListener() {
		return new PropertyChangeHandler();
	}

	/**
	 * <p>getFrame.</p>
	 *
	 * @return a {@link java.awt.Frame} object.
	 */
	protected Frame getFrame() {
		if (window instanceof Frame) {
			return (Frame) window;
		}
		return null;
	}

	/**
	 * <p>getFrameIconImage.</p>
	 *
	 * @return a {@link java.awt.Image} object.
	 */
	protected Image getFrameIconImage() {
		// try to find icon for dialog windows
		if (getFrame() == null) {
			List<Image> icons = getWindow().getIconImages();
			// No icon found ? search in window chain for an icon
			if (icons == null || icons.isEmpty()) {
				Window owner = getWindow().getOwner();
				while (owner != null) {
					icons = owner.getIconImages();
					// found ? return the icon
					if (icons != null && !icons.isEmpty()) {
						return icons.get(0);
					}
					owner = owner.getOwner();
				}
			} else {
				return icons.get(0);
			}
			// No icon found ? return icon of the first frame
			if (icons == null || icons.isEmpty()) {
				if (Frame.getFrames() != null && Frame.getFrames().length > 0) {
					return Frame.getFrames()[0].getIconImage();
				}
			}
			return null;
		} else {
			if (getFrame() != null) {
				return getFrame().getIconImage();
			}
		}
		return null;
	}

	/**
	 * <p>getHorSpacing.</p>
	 *
	 * @return a int.
	 */
	protected int getHorSpacing() {
		return 3;
	}

	/**
	 * <p>getIconWidth.</p>
	 *
	 * @return a int.
	 */
	protected int getIconWidth() {
		Image image = getFrameIconImage();
		if (image != null) {
			int h = getHeight();
			int ih = image.getHeight(null);
			int iw = image.getWidth(null);
			if (ih > h) {
				double fac = (double) iw / (double) ih;
				ih = h;
				iw = (int) (fac * ih);
			}
			return iw;
		}
		return 0;
	}

	/** {@inheritDoc} */
	@Override
	public JRootPane getRootPane() {
		return rootPane;
	}

	/**
	 * <p>getTitle.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	protected String getTitle() {
		if (window instanceof Frame) {
			return ((Frame) window).getTitle();
		} else if (window instanceof Dialog) {
			return ((Dialog) window).getTitle();
		}
		return null;
	}

	/**
	 * <p>getVerSpacing.</p>
	 *
	 * @return a int.
	 */
	protected int getVerSpacing() {
		return 3;
	}

	/**
	 * <p>Getter for the field window.</p>
	 *
	 * @return a {@link java.awt.Window} object.
	 */
	protected Window getWindow() {
		return window;
	}

	/** {@inheritDoc} */
	@Override
	public void iconify() {
		Frame frame = getFrame();
		if (frame != null) {
			if (JTattooUtilities.isMac() && JTattooUtilities.getJavaVersion() >= 1.7) {
				// Workarround to avoid a bug within OSX and Java 1.7
				frame.setExtendedState(state & ~BaseRootPaneUI.MAXIMIZED_BOTH | Frame.ICONIFIED);
			} else {
				frame.setExtendedState(state | Frame.ICONIFIED);
			}
		}
	}

	private void installDefaults() {
		setFont(UIManager.getFont("InternalFrame.titleFont"));
		if (rootPane.getClientProperty("customTitlePanel") instanceof JPanel) {
			setCustomizedTitlePanel((JPanel) rootPane.getClientProperty("customTitlePanel"));
		}
	}

	/**
	 * <p>installListeners.</p>
	 */
	protected void installListeners() {
		if (window != null) {
			windowListener = createWindowListener();
			window.addWindowListener(windowListener);
			propertyChangeListener = createWindowPropertyChangeListener();
			window.addPropertyChangeListener(propertyChangeListener);
		}
	}

	private void installSubcomponents() {
		createActions();
		createButtons();
		if (rootPane.getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
			if (!isMacStyleWindowDecoration()) {
				createMenuBar();
				add(menuBar);
			}
			add(iconifyButton);
			add(maxButton);
		}
		add(closeButton);
	}

	/**
	 * <p>isActive.</p>
	 *
	 * @return a boolean.
	 */
	protected boolean isActive() {
		return window == null || window.isActive();
	}

	/**
	 * <p>isLeftToRight.</p>
	 *
	 * @return a boolean.
	 */
	protected boolean isLeftToRight() {
		return window == null ? getRootPane().getComponentOrientation().isLeftToRight()
				: window.getComponentOrientation().isLeftToRight();
	}

	/**
	 * <p>isMacStyleWindowDecoration.</p>
	 *
	 * @return a boolean.
	 */
	protected boolean isMacStyleWindowDecoration() {
		return AbstractLookAndFeel.getTheme().isMacStyleWindowDecorationOn();
	}

	/** {@inheritDoc} */
	@Override
	public void maximize() {
		Frame frame = getFrame();
		if (frame != null) {
			validateMaximizedBounds();
			PropertyChangeListener[] pcl = frame.getPropertyChangeListeners();
			for (PropertyChangeListener pcl1 : pcl) {
				pcl1.propertyChange(new PropertyChangeEvent(this, "windowMaximize", Boolean.FALSE, Boolean.FALSE));
			}
			frame.setExtendedState(state | BaseRootPaneUI.MAXIMIZED_BOTH);
			for (PropertyChangeListener pcl1 : pcl) {
				pcl1.propertyChange(new PropertyChangeEvent(this, "windowMaximized", Boolean.FALSE, Boolean.FALSE));
			}

		}
	}

	/**
	 * <p>paintBackground.</p>
	 *
	 * @param g a {@link java.awt.Graphics} object.
	 */
	public void paintBackground(Graphics g) {
		if (isActive()) {
			Graphics2D g2D = (Graphics2D) g;
			Composite savedComposite = g2D.getComposite();
			if (backgroundImage != null) {
				g.drawImage(backgroundImage, 0, 0, null);
				AlphaComposite alpha = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphaValue);
				g2D.setComposite(alpha);
			}
			JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowTitleColors(), 0, 0, getWidth(),
					getHeight());
			g2D.setComposite(savedComposite);
		} else {
			JTattooUtilities.fillHorGradient(g, AbstractLookAndFeel.getTheme().getWindowInactiveTitleColors(), 0, 0,
					getWidth(), getHeight());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void paintComponent(Graphics g) {
		if (getFrame() != null) {
			setState(getFrame().getExtendedState());
		}

		paintBackground(g);

		g.setFont(getFont());
		FontMetrics fm = JTattooUtilities.getFontMetrics(this, g, getFont());
		int width = getWidth();
		int height = getHeight();
		int x = 0;
		int y = (height - fm.getHeight()) / 2 + fm.getAscent();
		int titleWidth = width - buttonsWidth - 4;
		String frameTitle = getTitle();
		if (isLeftToRight()) {
			if (isMacStyleWindowDecoration()) {
				int iconWidth = getIconWidth();
				titleWidth -= iconWidth + 4;
				frameTitle = JTattooUtilities.getClippedText(frameTitle, fm, titleWidth);
				int titleLength = fm.stringWidth(frameTitle);
				x += buttonsWidth + (titleWidth - titleLength) / 2;
				paintIcon(g, x);
				x += iconWidth + 4;
			} else {
				if (getWindow() instanceof JDialog) {
					int iconWidth = paintIcon(g, x);
					titleWidth -= iconWidth + 4;
					frameTitle = JTattooUtilities.getClippedText(frameTitle, fm, titleWidth);
					if (AbstractLookAndFeel.getTheme().isCenterWindowTitleOn()) {
						int titleLength = fm.stringWidth(frameTitle);
						x += iconWidth + 4;
						x += (titleWidth - titleLength) / 2;
					} else {
						x += iconWidth + 4;
					}
				} else {
					int menuBarWidth = menuBar == null ? 0 : menuBar.getWidth();
					titleWidth -= menuBarWidth + 4;
					frameTitle = JTattooUtilities.getClippedText(frameTitle, fm, titleWidth);
					if (AbstractLookAndFeel.getTheme().isCenterWindowTitleOn()) {
						int titleLength = fm.stringWidth(frameTitle);
						x += menuBarWidth + 4;
						x += (titleWidth - titleLength) / 2;
					} else {
						x += menuBarWidth + 4;
					}
				}
			}
		} else {
			int iconWidth = getIconWidth();
			if (isMacStyleWindowDecoration()) {
				titleWidth -= iconWidth + 4;
				frameTitle = JTattooUtilities.getClippedText(frameTitle, fm, titleWidth);
				int titleLength = fm.stringWidth(frameTitle);
				x = buttonsWidth + 4 + (titleWidth - titleLength) / 2;
				paintIcon(g, x + titleLength + 4);
			} else {
				if (getWindow() instanceof JDialog) {
					x = width - iconWidth;
					paintIcon(g, x);
					titleWidth -= iconWidth + 4;
					frameTitle = JTattooUtilities.getClippedText(frameTitle, fm, titleWidth);
					int titleLength = fm.stringWidth(frameTitle);
					if (AbstractLookAndFeel.getTheme().isCenterWindowTitleOn()) {
						x = buttonsWidth + 4 + (titleWidth - titleLength) / 2;
					} else {
						x = width - iconWidth - 4 - titleLength;
					}
				} else {
					int menuBarWidth = menuBar == null ? 0 : menuBar.getWidth();
					titleWidth -= menuBarWidth + 4;
					frameTitle = JTattooUtilities.getClippedText(frameTitle, fm, titleWidth);
					int titleLength = fm.stringWidth(frameTitle);
					if (AbstractLookAndFeel.getTheme().isCenterWindowTitleOn()) {
						x = buttonsWidth + 4 + (titleWidth - titleLength) / 2;
					} else {
						x = width - menuBarWidth - 4 - titleLength;
					}
				}
			}
		}
		paintText(g, x, y, frameTitle);
	}

	/**
	 * <p>paintIcon.</p>
	 *
	 * @param g a {@link java.awt.Graphics} object.
	 * @param x a int.
	 * @return a int.
	 */
	protected int paintIcon(Graphics g, int x) {
		Image image = getFrameIconImage();
		if (image != null) {
			Graphics2D g2D = (Graphics2D) g;
			Object savedHint = g2D.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
			g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			int h = getHeight() - 2;
			int ih = image.getHeight(null);
			int iw = image.getWidth(null);
			if (ih <= h) {
				g2D.drawImage(image, x, (h - ih) / 2, iw, ih, null);
			} else {
				double fac = (double) iw / (double) ih;
				ih = h;
				iw = (int) (fac * ih);
				g2D.drawImage(image, x, 0, iw, ih, null);
			}
			if (savedHint != null) {
				g2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, savedHint);
			}
			return iw;
		}
		return 0;
	}

	/**
	 * <p>paintText.</p>
	 *
	 * @param g a {@link java.awt.Graphics} object.
	 * @param x a int.
	 * @param y a int.
	 * @param title a {@link java.lang.String} object.
	 */
	public void paintText(Graphics g, int x, int y, String title) {
		if (isActive()) {
			g.setColor(AbstractLookAndFeel.getWindowTitleForegroundColor());
		} else {
			g.setColor(AbstractLookAndFeel.getWindowInactiveTitleForegroundColor());
		}
		JTattooUtilities.drawString(rootPane, g, title, x, y);
	}

	/** {@inheritDoc} */
	@Override
	public void removeNotify() {
		super.removeNotify();
		uninstallListeners();
		window = null;
	}

	/** {@inheritDoc} */
	@Override
	public void restore() {
		Frame frame = getFrame();
		if (frame != null) {
			wasMaximizeError = false;
			PropertyChangeListener[] pcl = frame.getPropertyChangeListeners();
			for (PropertyChangeListener pcl1 : pcl) {
				pcl1.propertyChange(new PropertyChangeEvent(this, "windowRestore", Boolean.FALSE, Boolean.FALSE));
			}
			if ((state & Frame.ICONIFIED) != 0) {
				frame.setExtendedState(state & ~Frame.ICONIFIED);
			} else {
				frame.setExtendedState(state & ~BaseRootPaneUI.MAXIMIZED_BOTH);
			}
			for (PropertyChangeListener pcl1 : pcl) {
				pcl1.propertyChange(new PropertyChangeEvent(this, "windowRestored", Boolean.FALSE, Boolean.FALSE));
			}
		}
	}

	/**
	 * <p>setActive.</p>
	 *
	 * @param flag a boolean.
	 */
	protected void setActive(boolean flag) {
		if (rootPane.getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
			Boolean active = flag ? Boolean.TRUE : Boolean.FALSE;
			iconifyButton.putClientProperty(PAINT_ACTIVE, active);
			closeButton.putClientProperty(PAINT_ACTIVE, active);
			maxButton.putClientProperty(PAINT_ACTIVE, active);
		}
		getRootPane().repaint();
	}

	/**
	 * <p>setAlphaTransparency.</p>
	 *
	 * @param alpha a float.
	 */
	public void setAlphaTransparency(float alpha) {
		alphaValue = alpha;
	}

	/**
	 * <p>Setter for the field backgroundImage.</p>
	 *
	 * @param bgImage a {@link java.awt.image.BufferedImage} object.
	 */
	public void setBackgroundImage(BufferedImage bgImage) {
		backgroundImage = bgImage;
	}

	/**
	 * <p>setCustomizedTitlePanel.</p>
	 *
	 * @param panel a {@link javax.swing.JPanel} object.
	 */
	public void setCustomizedTitlePanel(JPanel panel) {
		if (customTitlePanel != null) {
			remove(customTitlePanel);
			customTitlePanel = null;
		}
		if (panel != null) {
			customTitlePanel = panel;
			add(customTitlePanel);
		}
		rootPane.putClientProperty("customTitlePanel", customTitlePanel);
		revalidate();
		repaint();
	}

	/**
	 * <p>Setter for the field state.</p>
	 *
	 * @param state a int.
	 */
	protected void setState(int state) {
		setState(state, false);
	}

	/**
	 * <p>Setter for the field state.</p>
	 *
	 * @param state a int.
	 * @param updateRegardless a boolean.
	 */
	protected void setState(int state, boolean updateRegardless) {
		if (window != null && rootPane.getWindowDecorationStyle() == BaseRootPaneUI.FRAME) {
			if (this.state == state && !updateRegardless) {
				return;
			}

			final Frame frame = getFrame();
			if (frame != null) {

				if ((state & BaseRootPaneUI.MAXIMIZED_BOTH) != 0
						&& (rootPane.getBorder() == null || rootPane.getBorder() instanceof UIResource)
						&& frame.isShowing()) {
					rootPane.setBorder(null);
				} else if ((state & BaseRootPaneUI.MAXIMIZED_BOTH) == 0) {
					rootPaneUI.installBorder(rootPane);
				}

				if (frame.isResizable()) {
					if ((state & BaseRootPaneUI.MAXIMIZED_BOTH) != 0) {
						updateMaxButton(restoreAction, minimizeIcon);
						maximizeAction.setEnabled(false);
						restoreAction.setEnabled(true);
					} else {
						updateMaxButton(maximizeAction, maximizeIcon);
						maximizeAction.setEnabled(true);
						restoreAction.setEnabled(false);
					}
					if (maxButton.getParent() == null || iconifyButton.getParent() == null) {
						add(maxButton);
						add(iconifyButton);
						revalidate();
						repaint();
					}
					maxButton.setText(null);
				} else {
					maximizeAction.setEnabled(false);
					restoreAction.setEnabled(false);
					if (maxButton.getParent() != null) {
						remove(maxButton);
						revalidate();
						repaint();
					}
				}
				// BUGFIX
				// When programatically maximize a frame via setExtendedState in a multiscreen
				// environment the width
				// and height may not be set correctly. We fix this issue here.
				if ((state & BaseRootPaneUI.MAXIMIZED_BOTH) != 0) {
					validateMaximizedBounds();
					rootPane.setBorder(null);
					SwingUtilities.invokeLater(() -> {
						Rectangle maxBounds = calculateMaxBounds(frame);
						if (frame.getWidth() != maxBounds.width || frame.getHeight() != maxBounds.height) {
							restore();
							wasMaximizeError = true;
							frame.setMaximizedBounds(null);
							maximize();
						}
					});
				}
			} else {
				// Not contained in a Frame
				maximizeAction.setEnabled(false);
				restoreAction.setEnabled(false);
				iconifyAction.setEnabled(false);
				remove(maxButton);
				remove(iconifyButton);
				revalidate();
				repaint();
			}
			closeAction.setEnabled(true);
			this.state = state;
		}
	}

	/**
	 * <p>uninstallDefaults.</p>
	 */
	protected void uninstallDefaults() {
	}

	/**
	 * <p>uninstallListeners.</p>
	 */
	protected void uninstallListeners() {
		if (window != null) {
			window.removeWindowListener(windowListener);
			window.removePropertyChangeListener(propertyChangeListener);
		}
	}

	/**
	 * <p>updateMaxButton.</p>
	 *
	 * @param action a {@link javax.swing.Action} object.
	 * @param icon a {@link javax.swing.Icon} object.
	 */
	protected void updateMaxButton(Action action, Icon icon) {
		maxButton.setAction(action);
		maxButton.setIcon(icon);
	}

	/**
	 * <p>validateMaximizedBounds.</p>
	 */
	protected void validateMaximizedBounds() {
		Frame frame = getFrame();
		if (frame != null && !wasMaximizeError) {
			Rectangle maxBounds = calculateMaxBounds(frame);
			frame.setMaximizedBounds(maxBounds);
		}
	}

}
