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
 * Created on Sep 3, 2005
 */
package org.loboevolution.html.dom.domimpl;

import com.gargoylesoftware.css.dom.CSSStyleDeclarationImpl;
import com.gargoylesoftware.css.dom.CSSStyleSheetImpl;
import com.gargoylesoftware.css.dom.DOMException;
import com.gargoylesoftware.css.parser.CSSOMParser;
import com.gargoylesoftware.css.parser.javacc.CSS3Parser;
import org.loboevolution.common.Strings;
import org.loboevolution.html.dom.HTMLElement;
import org.loboevolution.html.dom.input.FormInput;
import org.loboevolution.html.dom.nodeimpl.NodeListImpl;
import org.loboevolution.html.node.Element;
import org.loboevolution.html.node.Node;
import org.loboevolution.html.renderer.HtmlController;
import org.loboevolution.html.renderstate.RenderState;
import org.loboevolution.html.renderstate.StyleSheetRenderState;
import org.loboevolution.html.style.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * <p>HTMLElementImpl class.</p>
 */
public class HTMLElementImpl extends ElementImpl implements HTMLElement, CSSPropertiesContext {

	private Map<String, AbstractCSSProperties> computedStyles;

	private volatile AbstractCSSProperties currentStyleDeclarationState;

	private volatile AbstractCSSProperties localStyleDeclarationState;

	private boolean hasMouseOver;
	
	/**
	 * <p>Constructor for HTMLElementImpl.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 */
	public HTMLElementImpl(final String name) {
		super(name);
	}

	/**
	 * Adds style sheet declarations applicable to this element. A properties object
	 * is created if necessary when the one passed is null.
	 *
	 * @param style a {@link org.loboevolution.html.style.AbstractCSSProperties} object.
	 * @param mouseOver a {@link java.lang.Boolean } object.
	 * @return a {@link org.loboevolution.html.style.AbstractCSSProperties} object.
	 */
	protected final AbstractCSSProperties addStyleSheetDeclarations(AbstractCSSProperties style, boolean mouseOver) {
		final Node pn = this.parentNode;
		if (pn == null) {
			return style;
		}
		final String classNames = getClassName();
		final String elementName = getTagName();
		final String[] classNameArray = Strings.isNotBlank(classNames) ? Strings.split(classNames) : null;
		final List<CSSStyleSheetImpl.SelectorEntry> matchingRules = findStyleDeclarations(elementName, classNameArray, mouseOver);
		for (CSSStyleSheetImpl.SelectorEntry entry : matchingRules) {
			final CSSStyleDeclarationImpl cssStyleDeclarationImpl = entry.getRule().getStyle();
			if (style == null) {
				style = new AbstractCSSProperties(this);
			}
			style.addStyleDeclaration(cssStyleDeclarationImpl);
		}
		return style;
	}

	/** {@inheritDoc} */
	@Override
	protected void assignAttributeField(String normalName, String value) {
		if (!this.notificationsSuspended) {
			informInvalidAttibute(normalName);
		} else {
			if ("style".equals(normalName)) {
				forgetLocalStyle();
			}
		}
		super.assignAttributeField(normalName, value);
	}
	/**
	 * <p>createDefaultStyleSheet.</p>
	 * @return a {@link org.loboevolution.html.style.AbstractCSSProperties} object.
	 */
	protected AbstractCSSProperties createDefaultStyleSheet() {
		// Override to provide element defaults.
		return null;
	}

	/** {@inheritDoc} */
	@Override
	protected RenderState createRenderState(RenderState prevRenderState) {
		// Overrides NodeImpl method
		// Called in synchronized block already
		return new StyleSheetRenderState(prevRenderState, this);
	}

	/**
	 * <p>findStyleDeclarations.</p>
	 *
	 * @param elementName a {@link java.lang.String} object.
	 * @param classes an array of {@link java.lang.String} objects.
	 * @param mouseOver a {@link java.lang.Boolean } object.
	 * @return a {@link java.util.List} object.
	 */
	protected final List<CSSStyleSheetImpl.SelectorEntry> findStyleDeclarations(String elementName, String[] classes, boolean mouseOver) {
		final HTMLDocumentImpl doc = (HTMLDocumentImpl) this.document;

		if (doc == null) {
			return new ArrayList<>();
		}
		final StyleSheetAggregator ssa = doc.getStyleSheetAggregator();
		final List<CSSStyleSheetImpl.SelectorEntry> list = ssa.getActiveStyleDeclarations(this, elementName, classes, mouseOver);
		hasMouseOver = ssa.isMouseOver();
		return list;
	}

	/**
	 * <p>forgetLocalStyle.</p>
	 */
	protected final void forgetLocalStyle() {
		synchronized (this) {
			this.currentStyleDeclarationState = null;
			this.localStyleDeclarationState = null;
			this.computedStyles = null;
		}
	}

	/**
	 * <p>forgetStyle.</p>
	 *
	 * @param deep a boolean.
	 */
	protected final void forgetStyle(boolean deep) {
		synchronized (this) {
			this.currentStyleDeclarationState = null;
			this.computedStyles = null;
			if (deep) {
				nodeList.forEach(node -> {
					if (node instanceof HTMLElementImpl) {
						((HTMLElementImpl) node).forgetStyle(deep);
					}
				});
			}
		}
	}

	/**
	 * <p>getAncestor.</p>
	 *
	 * @param elementTL a {@link java.lang.String} object.
	 * @return a {@link org.loboevolution.html.dom.domimpl.HTMLElementImpl} object.
	 */
	public HTMLElementImpl getAncestor(String elementTL) {
		final Object nodeObj = getParentNode();
		if (nodeObj instanceof HTMLElementImpl) {
			final HTMLElementImpl parentElement = (HTMLElementImpl) nodeObj;
			if ("*".equals(elementTL)) {
				return parentElement;
			}
			final String pelementTL = parentElement.getTagName().toLowerCase();
			if (elementTL.equals(pelementTL)) {
				return parentElement;
			}
			return parentElement.getAncestor(elementTL);
		} else {
			return null;
		}
	}

	/**
	 * <p>getAncestorForJavaClass.</p>
	 *
	 * @param javaClass a {@link java.lang.Class} object.
	 * @return a {@link java.lang.Object} object.
	 */
	protected Object getAncestorForJavaClass(Class<?> javaClass) {
		final Object nodeObj = getParentNode();
		if (nodeObj == null || javaClass.isInstance(nodeObj)) {
			return nodeObj;
		} else if (nodeObj instanceof HTMLElementImpl) {
			return ((HTMLElementImpl) nodeObj).getAncestorForJavaClass(javaClass);
		} else {
			return null;
		}
	}

	/**
	 * <p>getAttributeAsBoolean.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a boolean.
	 */
	public boolean getAttributeAsBoolean(String name) {
		return getAttribute(name) != null;
	}

	/**
	 * <p>getAttributeAsInt.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param defaultValue a int.
	 * @return a int.
	 */
	protected int getAttributeAsInt(String name, int defaultValue) {
		final String value = getAttribute(name);
		HTMLDocumentImpl doc =  (HTMLDocumentImpl)this.document;
		return HtmlValues.getPixelSize(value, null, doc.getDefaultView(), defaultValue);
	}

	/**
	 * <p>getCharset.</p>
	 *
	 * @return a {@link java.lang.String} object.
	 */
	public String getCharset() {
		return getAttribute("charset");
	}

	/**
	 * <p>getComputedStyle.</p>
	 *
	 * @param pseudoElement a {@link java.lang.String} object.
	 * @return a {@link org.loboevolution.html.style.ComputedCSSStyleDeclaration} object.
	 */
	public ComputedCSSStyleDeclaration getComputedStyle(String pseudoElement) {

		if (pseudoElement == null) {
			pseudoElement = "";
		}

		synchronized (this) {
			final Map<String, AbstractCSSProperties> cs = this.computedStyles;
			if (cs != null) {
				final AbstractCSSProperties sds = cs.get(pseudoElement);
				if (sds != null) {
					return new ComputedCSSStyleDeclaration(this, sds);
				}
			}
		}

		AbstractCSSProperties sds = createDefaultStyleSheet();
		sds = addStyleSheetDeclarations(sds, false);

		final AbstractCSSProperties localStyle = getStyle();

		if (sds == null) {
			sds = new AbstractCSSProperties(this);
			sds.setStyleDeclarations(localStyle.getStyleDeclarations());
		}

		sds.setLocalStyleProperties(localStyle);

		synchronized (this) {
			Map<String, AbstractCSSProperties> cs = this.computedStyles;
			if (cs == null) {
				cs = new HashMap<>(2);
				this.computedStyles = cs;
			} else {
				final AbstractCSSProperties sds2 = cs.get(pseudoElement);
				if (sds2 != null) {
					return new ComputedCSSStyleDeclaration(this, sds2);
				}
			}
			cs.put(pseudoElement, sds);
		}
		return new ComputedCSSStyleDeclaration(this, sds);
	}

	/**
	 * Gets the style object associated with the element. It may return null only if
	 * the type of element does not handle stylesheets.
	 *
	 * @return a {@link org.loboevolution.html.style.AbstractCSSProperties} object.
	 */
	public AbstractCSSProperties getCurrentStyle() {
		AbstractCSSProperties sds;
		synchronized (this) {
			sds = this.currentStyleDeclarationState;
			if (sds != null) {
				return sds;
			}
		}
		sds = createDefaultStyleSheet();
		sds = addStyleSheetDeclarations(sds, false);
		final AbstractCSSProperties localStyle = getStyle();
		if (sds == null) {
			sds = new AbstractCSSProperties(this);
		}
		sds.setLocalStyleProperties(localStyle);
		synchronized (this) {
			final AbstractCSSProperties setProps = this.currentStyleDeclarationState;
			if (setProps != null) {
				return setProps;
			}
			this.currentStyleDeclarationState = sds;
			return sds;
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getDocumentBaseURI() {
		final HTMLDocumentImpl doc = (HTMLDocumentImpl) this.document;
		if (doc != null) {
			return doc.getBaseURI();
		} else {
			return null;
		}
	}

	/**
	 * Gets form input due to the current element. It should return
	 * null except when the element is a form input element.
	 *
	 * @return an array of {@link org.loboevolution.html.dom.input.FormInput} objects.
	 */
	protected FormInput[] getFormInputs() {
		// Override in input elements
		return null;
	}

	/**
	 * <p>getOffsetHeight.</p>
	 *
	 * @return a double.
	 */
	public double getOffsetHeight() {
		return calculateHeight(true, true);
	}

	/**
	 * <p>getOffsetLeft.</p>
	 *
	 * @return a int.
	 */
	public double getOffsetLeft() {
		final UINode uiNode = getUINode();
		return uiNode == null ? 0 : uiNode.getBoundsRelativeToBlock().x;
	}

	/**
	 * <p>getOffsetTop.</p>
	 *
	 * @return a int.
	 */
	public int getOffsetTop() {
		// TODO: Sometimes this can be called while parsing, and
		// browsers generally give the right answer.
		final UINode uiNode = getUINode();
		return uiNode == null ? 0 : uiNode.getBoundsRelativeToBlock().y;
	}

	/**
	 * <p>getOffsetWidth.</p>
	 *
	 * @return a int.
	 */
	public int getOffsetWidth() {
		return calculateWidth(true, true);
	}

	/**
	 * <p>getParent.</p>
	 *
	 * @param elementTL a {@link java.lang.String} object.
	 * @return a {@link org.loboevolution.html.dom.domimpl.HTMLElementImpl} object.
	 */
	public HTMLElementImpl getParent(String elementTL) {
		final Object nodeObj = getParentNode();
		if (nodeObj instanceof HTMLElementImpl) {
			final HTMLElementImpl parentElement = (HTMLElementImpl) nodeObj;
			if ("*".equals(elementTL)) {
				return parentElement;
			}
			final String pelementTL = parentElement.getTagName().toLowerCase();
			if (elementTL.equals(pelementTL)) {
				return parentElement;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public AbstractCSSProperties getParentStyle() {
		final Object parent = this.parentNode;
		if (parent instanceof HTMLElementImpl) {
			return ((HTMLElementImpl) parent).getCurrentStyle();
		}
		return null;
	}

	/**
	 * Gets the local style object associated with the element. The properties
	 * object returned only includes properties from the local style attribute. It
	 * may return null only if the type of element does not handle stylesheets.
	 *
	 * @return a {@link org.loboevolution.html.style.AbstractCSSProperties} object.
	 */
	public AbstractCSSProperties getStyle() {
		AbstractCSSProperties sds;
		synchronized (this) {
			sds = this.localStyleDeclarationState;
			if (sds != null) {
				return sds;
			}
			sds = new AbstractCSSProperties(this);
			// Add any declarations in style attribute (last takes precedence).
			final String style = getAttribute("style");
			if (Strings.isNotBlank(style)) {
                final CSSOMParser parser = new CSSOMParser(new CSS3Parser());
				try {
					final CSSStyleDeclarationImpl sd = parser.parseStyleDeclaration(style);
					sds.addStyleDeclaration(sd);
				} catch (final Exception err) {
					final String id = getId();
					final String withId = id == null ? "" : " with ID '" + id + "'";
					this.warn("Unable to parse style attribute value for element " + getTagName() + withId + " in " + getDocumentURL() + ".", err);
				}
			}
			this.localStyleDeclarationState = sds;
		}
		// Synchronization note: Make sure getStyle() does not return multiple values.
		return sds;
	}

	/** {@inheritDoc} */
	@Override
	public void informInvalid() {
		// This is called when an attribute or child changes.
		forgetStyle(false);
		super.informInvalid();
	}

	/**
	 * <p>informInvalidAttibute.</p>
	 *
	 * @param normalName a {@link java.lang.String} object.
	 */
	public void informInvalidAttibute(String normalName) {
		if ("style".equals(normalName)) {
			this.forgetLocalStyle();
		}
		forgetStyle(true);
		informInvalidRecursive();

	}

	private void informInvalidRecursive() {
		super.informInvalid();
		NodeListImpl nodeList = this.getNodeList();
		if (nodeList != null) {
			nodeList.forEach(n -> {
				if (n instanceof HTMLElementImpl) {
					HTMLElementImpl htmlElementImpl = (HTMLElementImpl) n;
					htmlElementImpl.informInvalidRecursive();
				}
			});
		}
	}

	/**
	 * <p>setCharset.</p>
	 *
	 * @param charset a {@link java.lang.String} object.
	 */
	public void setCharset(String charset) {
		setAttribute("charset", charset);
	}

	/**
	 * <p>setCurrentStyle.</p>
	 *
	 * @param value a {@link java.lang.Object} object.
	 */
	public void setCurrentStyle(Object value) {
		throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Cannot set currentStyle property");
	}
	
	/**
	 * <p>setMouseOver.</p>
	 *
	 * @param mouseOver a boolean.
	 */
	public void setMouseOver(boolean mouseOver) {
		if (hasMouseOver) {
			if (mouseOver) {
				AbstractCSSProperties sds = createDefaultStyleSheet();
				currentStyleDeclarationState = addStyleSheetDeclarations(sds, mouseOver);
				if (currentStyleDeclarationState != null) {
					informInvalidRecursive();
				}
			} else {
				forgetStyle(true);
				informInvalidRecursive();
			}
		}
	}

	/**
	 * <p>setStyle.</p>
	 *
	 * @param value a {@link java.lang.String} object.
	 *
	 */
	public void setStyle(String value) {
		this.setAttribute("style", value);
	}
	
    /** {@inheritDoc} */
    @Override
    public String getContentEditable() {
        String contenteditable = this.getAttribute("contenteditable");
        return Strings.isBlank(contenteditable) ? "true" : contenteditable;
    }

    /** {@inheritDoc} */
    @Override
    public void setContentEditable(String contenteditable) {
        this.setAttribute("contenteditable", contenteditable);
    }
 
	/** {@inheritDoc} */
	@Override
	public void warn(String message) {
		logger.log(Level.WARNING, message);
	}

	/** {@inheritDoc} */
	@Override
	public void warn(String message, Throwable err) {
		logger.log(Level.WARNING, message, err);
	}

	/** {@inheritDoc} */
	@Override
	public String getAccessKey() {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public String getAccessKeyLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public String getAutocapitalize() {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public Element getOffsetParent() {
		// TODO Auto-generated method stub
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isSpellcheck() {
		// TODO Auto-generated method stub
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDraggable() {
		// TODO Auto-generated method stub
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isHidden() {
		final String hidden = getAttribute("hidden");
		return hidden != null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isTranslate() {
		// TODO Auto-generated method stub
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public void setAccessKey(String accessKey) {
		setAttribute("accessKey", accessKey);
	}

	/** {@inheritDoc} */
	@Override
	public void setAutocapitalize(String autocapitalize) {
		// TODO Auto-generated method stub
		
	}

	/** {@inheritDoc} */
	@Override
	public void setDraggable(boolean draggable) {
		// TODO Auto-generated method stub
		
	}

	/** {@inheritDoc} */
	@Override
	public void setHidden(boolean hidden) {
		setAttribute("hidden", String.valueOf(hidden));
	}

	/** {@inheritDoc} */
	@Override
	public void setSpellcheck(boolean spellcheck) {
		// TODO Auto-generated method stub
		
	}

	/** {@inheritDoc} */
	@Override
	public void setTranslate(boolean translate) {
		// TODO Auto-generated method stub
		
	}

	/** {@inheritDoc} */
	@Override
	public void click() {
		HtmlController.getInstance().onMouseClick(this, null, 0, 0);
	}
	
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return "[object HTMLElement]";
	}
}
