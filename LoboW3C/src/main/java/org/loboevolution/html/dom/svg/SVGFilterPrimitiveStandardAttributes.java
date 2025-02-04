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
package org.loboevolution.html.dom.svg;

/**
 * <p>SVGFilterPrimitiveStandardAttributes interface.</p>
 *
 *
 *
 */
public interface SVGFilterPrimitiveStandardAttributes extends SVGStylable {
	/**
	 * <p>getX.</p>
	 *
	 * @return a {@link org.loboevolution.html.dom.svg.SVGAnimatedLength} object.
	 */
	SVGAnimatedLength getX();

	/**
	 * <p>getY.</p>
	 *
	 * @return a {@link org.loboevolution.html.dom.svg.SVGAnimatedLength} object.
	 */
	SVGAnimatedLength getY();

	/**
	 * <p>getWidth.</p>
	 *
	 * @return a {@link org.loboevolution.html.dom.svg.SVGAnimatedLength} object.
	 */
	SVGAnimatedLength getWidth();

	/**
	 * <p>getHeight.</p>
	 *
	 * @return a {@link org.loboevolution.html.dom.svg.SVGAnimatedLength} object.
	 */
	SVGAnimatedLength getHeight();

	/**
	 * <p>getResult.</p>
	 *
	 * @return a {@link org.loboevolution.html.dom.svg.SVGAnimatedString} object.
	 */
	SVGAnimatedString getResult();
}
