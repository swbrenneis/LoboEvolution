package org.loboevolution.html.dom.svgimpl;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;

import org.loboevolution.html.dom.svg.SVGAnimatedLength;
import org.loboevolution.html.dom.svg.SVGLineElement;
import org.loboevolution.html.dom.svg.SVGMatrix;

public class SVGLineElementImpl extends SVGGraphic implements SVGLineElement {

	public SVGLineElementImpl(String name) {
		super(name);
	}

	@Override
	public SVGAnimatedLength getX1() {
		return new SVGAnimatedLengthImpl(new SVGLengthImpl(this.getAttribute("x1")));
	}

	@Override
	public SVGAnimatedLength getY1() {
		return new SVGAnimatedLengthImpl(new SVGLengthImpl(this.getAttribute("y1")));
	}

	@Override
	public SVGAnimatedLength getX2() {
		return new SVGAnimatedLengthImpl(new SVGLengthImpl(this.getAttribute("x2")));
	}

	@Override
	public SVGAnimatedLength getY2() {
		return new SVGAnimatedLengthImpl(new SVGLengthImpl(this.getAttribute("y2")));
	}

	@Override
	public void draw(Graphics2D graphics) {
		final SVGMatrix ctm = getCTM();
		final Shape shape = createShape(ctm.getAffineTransform());
		animate(this);
		drawable(graphics, shape);
	}

	@Override
	public Shape createShape(AffineTransform transform) {
		AffineTransform inverseTransform;
		try {
			inverseTransform = transform.createInverse();
		} catch (NoninvertibleTransformException e) {
			inverseTransform = null;
		}
		GeneralPath path = new GeneralPath();
		float x1 = ((SVGLengthImpl) getX1().getAnimVal()).getTransformedLength(inverseTransform);
		float y1 = ((SVGLengthImpl) getY1().getAnimVal()).getTransformedLength(inverseTransform);
		float x2 = ((SVGLengthImpl) getX2().getAnimVal()).getTransformedLength(inverseTransform);
		float y2 = ((SVGLengthImpl) getY2().getAnimVal()).getTransformedLength(inverseTransform);
		path.moveTo(x1, y1);
		path.lineTo(x2, y2);
		return path;
	}
}