/*
 * GNU GENERAL LICENSE
 * Copyright (C) 2014 - 2021 Lobo Evolution
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * verion 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General License for more details.
 *
 * You should have received a copy of the GNU General Public
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact info: ivan.difrancesco@yahoo.it
 */
package org.loboevolution.html.control;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import org.loboevolution.common.WrapperLayout;
import org.loboevolution.html.dom.domimpl.HTMLInputElementImpl;

/**
 * <p>InputControl class.</p>
 *
 *
 *
 */
public class InputControl extends BaseControl {

	private static final long serialVersionUID = 1L;
	
	private final HTMLInputElementImpl  modelNode;

	private Map<String, Image> map;
	
	/**
	 * <p>Constructor for InputControl.</p>
	 * @param modelNode a {@link org.loboevolution.html.dom.domimpl.HTMLInputElementImpl} object.
	 * @param map a {@link java.util.Map} object.
	 */
	public InputControl(HTMLInputElementImpl modelNode,  Map<String, Image> map) {
		super(modelNode);
		setLayout(WrapperLayout.getInstance());
		this.modelNode = modelNode;
		this.map = map;
	}

	/** {@inheritDoc} */
	@Override
	public void reset(final int availWidth, final int availHeight) {
		super.reset(availWidth, availHeight);
		modelNode.draw(this, map);
	}
	
	/**
	 * <p>direction.</p>
	 *
	 * @param dir a {@link java.lang.String} object.
	 * @return a {@link java.awt.ComponentOrientation} object.
	 */
	public ComponentOrientation direction(String dir) {
		if ("ltr".equalsIgnoreCase(dir)) {
			return ComponentOrientation.LEFT_TO_RIGHT;
		} else if ("rtl".equalsIgnoreCase(dir)) {
			return ComponentOrientation.RIGHT_TO_LEFT;
		} else {
			return ComponentOrientation.UNKNOWN;
		}
	}
}
