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

package org.loboevolution.html.dom.domimpl;

import org.loboevolution.html.dom.HTMLTableCaptionElement;
import org.loboevolution.html.renderstate.DisplayRenderState;
import org.loboevolution.html.renderstate.RenderState;

/**
 * The Class HTMLTableCaptionElementImpl.
 */
public class HTMLTableCaptionElementImpl extends HTMLElementImpl implements HTMLTableCaptionElement {

	/**
	 * Instantiates a new HTML table caption element impl.
	 *
	 * @param name
	 *            the name
	 */
	public HTMLTableCaptionElementImpl(final String name) {
		super(name);
	}

	/** {@inheritDoc} */
	@Override
	public String getAlign() {
		return this.getAttribute("align");
	}


	/** {@inheritDoc} */
	@Override
	public void setAlign(String align) {
		this.setAttribute("align", align);
	}

	/**
	 * Gets the caption side.
	 *
	 * @return the caption side
	 */
	public String getCaptionSide() {
		return this.getAttribute("caption-side");
	}

	/**
	 * Sets the caption side.
	 *
	 * @param captionSide
	 *            the new caption side
	 */
	public void setCaptionSide(String captionSide) {
		this.setAttribute("caption-side", captionSide);
	}

	/** {@inheritDoc} */
	@Override
	protected RenderState createRenderState(RenderState prevRenderState) {
		return new DisplayRenderState(prevRenderState, this, RenderState.DISPLAY_TABLE_CAPTION);
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "[object HTMLTableCaptionElement]";
	}
}
