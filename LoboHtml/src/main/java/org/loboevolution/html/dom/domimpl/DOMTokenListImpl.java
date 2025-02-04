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

import com.gargoylesoftware.css.dom.DOMException;
import org.loboevolution.common.Strings;
import org.loboevolution.html.node.DOMTokenList;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * <p>DOMTokenListImpl class.</p>
 */
public class DOMTokenListImpl implements DOMTokenList {

	private final ElementImpl element;

	private final LinkedList<String> tokenset;

	/**
	 * <p>Constructor for DOMTokenListImpl.</p>
	 * @param element a {@link org.loboevolution.html.dom.domimpl.ElementImpl} object.
	 */
	public DOMTokenListImpl(ElementImpl element) {
		this.element = element;
		this.tokenset = new LinkedList<>();
	}

	/** {@inheritDoc} */
	@Override
	public int getLength() {
		return tokenset.size();
	}

	/** {@inheritDoc} */
	@Override
	public String item(int index) {
		if (index < 0 || index >= tokenset.size()) {
			return null;
		}
		return tokenset.get(index);
	}

	/** {@inheritDoc} */
	@Override
	public boolean contains(String token) {
		return tokenset.contains(token);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(String token) throws DOMException {
		if (token != null && !tokenset.contains(token)) {
			final String tok = token.trim();
			if (Strings.isBlank(tok)) {
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Token cannot contain spaces");
			}

			if (tok.contains(" ") || tok.contains("\t")) {
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Token cannot contain spaces");
			}

			tokenset.add(tok);
			final String className = element.getClassName();
			if (!className.contains(tok)) {

				if (Strings.isNotBlank(className) && (!className.contains("\t") || !className.contains("\n"))) {
					element.setClassName(className.trim() + " " + tok);
				} else {
					element.setClassName(tok);
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void remove(String token) {
		if (token != null) {
			final String tokTrim = token.trim();

			if (Strings.isBlank(tokTrim)) {
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Token cannot contain spaces");
			}

			if (tokTrim.contains(" ") || tokTrim.contains("\t")) {
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Token cannot contain spaces");
			}

			tokenset.remove(tokTrim);
			StringBuilder sb = new StringBuilder();
			tokenset.forEach(tok -> sb.append(tok).append(' '));
			element.setClassName(sb.toString().trim());
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean toggle(String token) throws DOMException {
		if (token != null) {
			final String tok = token.trim();

			if (Strings.isBlank(tok)) {
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Token cannot contain spaces");
			}

			if (tok.contains(" ")) {
				throw new DOMException(DOMException.INVALID_CHARACTER_ERR, "Token cannot contain spaces");
			}

			if (!tokenset.remove(tok)) {
				final boolean result = tokenset.add(tok);
				if (result) {
					StringBuilder sb = new StringBuilder();
					tokenset.forEach(tk -> sb.append(tk).append(' '));
					element.setClassName(sb.toString().trim());
				}
				return result;
			} else {
				StringBuilder sb = new StringBuilder();
				tokenset.forEach(tk -> sb.append(tk).append(' '));
				element.setClassName(sb.toString().trim());
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean toggle(String token, boolean force) {
		if (force) {
			add(token);
		} else {
			remove(token);
		}
		return force;
	}

	/** {@inheritDoc} */
	@Override
	public String getValue() {
		int sz = tokenset.size();
		if (sz == 0) {
			return "";
		}
		if (sz == 1) {
			return tokenset.getFirst();
		}
		StringBuilder buf = new StringBuilder(32 + 12 * tokenset.size());
		Iterator<String> it = tokenset.iterator();
		buf.append(it.next());
		while (it.hasNext()) {
			buf.append(' ').append(it.next());
		}
		return buf.toString();
	}

	/** {@inheritDoc} */
	@Override
	public void setValue(String value) throws DOMException {
		if (value == null) {
			throw new DOMException(DOMException.SYNTAX_ERR, "Token cannot be null");
		}
		tokenset.clear();
		StringTokenizer st = new StringTokenizer(value);
		while (st.hasMoreTokens()) {
			tokenset.add(st.nextToken());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean replace(String oldToken, String newToken) {
		if (Strings.isNotBlank(oldToken) && Strings.isBlank(newToken)) {
			int idx = tokenset.indexOf(oldToken);
			if (idx != -1) {
				tokenset.set(idx, newToken);
				return true;
			}
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean supports(String token) {
		// TODO Auto-generated method stub
		return false;
	}

	public void populate(String token) {
		if (token != null && !tokenset.contains(token)) {
			final String tok = token.trim();
			if (Strings.isNotBlank(tok)) {
				if(Strings.countChars(tok, '\t') == 0) tokenset.add(tok);
				final String className = element.getClassName();
				if (!className.contains(tok))
					element.setClassName(Strings.isNotBlank(className) ? className.trim() + " " + tok : tok);
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		tokenset.forEach(tok -> sb.append(tok).append(' '));
		return sb.toString().trim();
	}
}