/**
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package ti.modules.titanium.xml;

import org.appcelerator.titanium.TiContext;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;

public class AttrProxy extends NodeProxy {

	private Attr attr;
	public AttrProxy(TiContext context, Attr attr) {
		super(context, attr);
		this.attr = attr;
	}
	
	public Attr getAttr() {
		return attr;
	}
	
	public String getName() {
		return attr.getName();
	}
	
	public ElementProxy getOwnerElement() {
		return getProxy(attr.getOwnerElement());
	}
	
	public boolean getSpecified() {
		return attr.getSpecified();
	}
	
	public String getValue() {
		return attr.getValue();
	}
	
	public void setValue(String value) throws DOMException {
		attr.setValue(value);
	}
}
