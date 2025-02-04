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
/*
 * Created on Apr 23, 2005
 */
package org.loboevolution.html.renderer;

import java.util.Collection;

/**
 * Author J. H. S.
 */
class OverflowException extends Exception {

	private static final long serialVersionUID = 1L;

	private final Collection<Renderable> renderables;

	/**
	 * <p>Constructor for OverflowException.</p>
	 *
	 * @param renderables a {@link java.util.Collection} object.
	 */
	public OverflowException(Collection<Renderable> renderables) {
		this.renderables = renderables;
	}

	/**
	 * <p>Getter for the field renderables.</p>
	 *
	 * @return a {@link java.util.Collection} object.
	 */
	public Collection<Renderable> getRenderables() {
		return this.renderables;
	}
}
