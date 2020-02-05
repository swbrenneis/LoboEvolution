/*
    GNU LESSER GENERAL PUBLIC LICENSE
    Copyright (C) 2006 The Lobo Project. Copyright (C) 2014 Lobo Evolution

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

    Contact info: lobochief@users.sourceforge.net; ivan.difrancesco@yahoo.it
*/
/*
 * Created on Jan 14, 2006
 */
package org.loboevolution.html.dom.domimpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.loboevolution.html.FormInput;
import org.loboevolution.html.dom.HTMLCollection;
import org.loboevolution.html.dom.HTMLFormElement;
import org.loboevolution.html.dom.filter.InputFilter;
import org.loboevolution.html.js.Executor;
import org.loboevolution.http.HtmlRendererContext;
import org.mozilla.javascript.Function;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HTMLFormElementImpl extends HTMLAbstractUIElement implements HTMLFormElement {

	public static boolean isInput(Node node) {
		final String name = node.getNodeName().toLowerCase();
		return name.equals("input") || name.equals("textarea") || name.equals("select");
	}

	private HTMLCollection elements;

	private Function onsubmit;

	public HTMLFormElementImpl() {
		super("FORM");
	}

	public HTMLFormElementImpl(String name) {
		super(name);
	}

	@Override
	public String getAcceptCharset() {
		return getAttribute("acceptCharset");
	}

	@Override
	public String getAction() {
		return getAttribute("action");
	}

	@Override
	public HTMLCollection getElements() {
		HTMLCollection elements = this.elements;
		if (elements == null) {
			elements = new HTMLCollectionImpl(this, new InputFilter());
			this.elements = elements;
		}
		return elements;
	}

	@Override
	public String getEnctype() {
		return getAttribute("enctype");
	}

	@Override
	public int getLength() {
		return getElements().getLength();
	}

	@Override
	public String getMethod() {
		String method = getAttribute("method");
		if (method == null) {
			method = "GET";
		}
		return method;
	}

	@Override
	public String getName() {
		return getAttribute("name");
	}

	public Function getOnsubmit() {
		return getEventFunction(this.onsubmit, "onsubmit");
	}

	@Override
	public String getTarget() {
		return getAttribute("target");
	}

	public Object item(final int index) {
		try {
			visit(new NodeVisitor() {
				private int current = 0;

				@Override
				public void visit(Node node) {
					if (HTMLFormElementImpl.isInput(node)) {
						if (this.current == index) {
							throw new StopVisitorException(node);
						}
						this.current++;
					}
				}
			});
		} catch (final StopVisitorException sve) {
			return sve.getTag();
		}
		return null;
	}

	public Object namedItem(final String name) {
		try {
			// TODO: This could use document.namedItem.
			visit(node -> {
				if (HTMLFormElementImpl.isInput(node)) {
					if (name.equals(((Element) node).getAttribute("name"))) {
						throw new StopVisitorException(node);
					}
				}
			});
		} catch (final StopVisitorException sve) {
			return sve.getTag();
		}
		return null;
	}

	@Override
	public void reset() { //TODO 
		/*visit(node -> {
			if (node instanceof HTMLBaseInputElement) {
				((HTMLBaseInputElement) node).resetInput();
			}
		});*/
	}

	@Override
	public void setAcceptCharset(String acceptCharset) {
		setAttribute("acceptCharset", acceptCharset);
	}

	@Override
	public void setAction(String action) {
		setAttribute("action", action);
	}

	@Override
	public void setEnctype(String enctype) {
		setAttribute("enctype", enctype);
	}

	@Override
	public void setMethod(String method) {
		setAttribute("method", method);
	}

	@Override
	public void setName(String name) {
		setAttribute("name", name);
	}

	public void setOnsubmit(Function value) {
		this.onsubmit = value;
	}

	@Override
	public void setTarget(String target) {
		setAttribute("target", target);
	}

	@Override
	public void submit() {
		this.submit(null);
	}

	/**
	 * This method should be called when form submission is done by a submit button.
	 * 
	 * @param extraFormInputs Any additional form inputs that need to be submitted,
	 *                        e.g. the submit button parameter.
	 */
	public final void submit(final FormInput[] extraFormInputs) {
		final Function onsubmit = getOnsubmit();
		if (onsubmit != null) {
			// TODO: onsubmit event object?
			if (!Executor.executeFunction(this, onsubmit, null, new Object[0])) {
				return;
			}
		}
		final HtmlRendererContext context = getHtmlRendererContext();
		if (context != null) {
			final ArrayList<FormInput> formInputs = new ArrayList<FormInput>();
			if (extraFormInputs != null) {
				for (final FormInput extraFormInput : extraFormInputs) {
					formInputs.add(extraFormInput);
				}
			}
			visit(node -> {
				if (node instanceof HTMLElementImpl) {
					final FormInput[] fis = ((HTMLElementImpl) node).getFormInputs();
					if (fis != null) {
						for (final FormInput fi : fis) {
							if (fi.getName() == null) {
								throw new IllegalStateException("Form input does not have a name: " + node);
							}
							formInputs.add(fi);
						}
					}
				}
			});
			final FormInput[] fia = (FormInput[]) formInputs.toArray(FormInput.EMPTY_ARRAY);
			String href = getAction();
			if (href == null) {
				href = getBaseURI();
			}
			try {
				final URL url = getFullURL(href);
				context.submitForm(getMethod(), url, getTarget(), getEnctype(), fia);
			} catch (final MalformedURLException mfu) {
				this.warn("submit()", mfu);
			}
		}
	}
}
