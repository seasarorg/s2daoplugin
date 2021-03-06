/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.s2daoplugin.cache.deployment.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.seasar.kijimuna.core.parser.IElement;

public class ElementWrapper extends ElementAdaptor {

	private IElement element;
	private IElement root;
//	private IElement parent;
	private Set children = new LinkedHashSet();
	
	public ElementWrapper(IElement element) {
		super(element);
		this.element = element;
	}
	
	public void setRootElement(IElement root) {
		this.root = root;
	}
	
	public void setParent(IElement parent) {
//		this.parent = parent;
	}
	
	public IElement getParent() {
//		return parent;
		return element.getParent();
	}
	
	public void addChild(IElement child) {
		children.add(child);
	}
	
	public void removeChild(IElement child) {
		children.remove(child);
	}
	
	public List getChildren() {
		List ret = super.getChildren();
		ret.addAll(children);
		return ret;
	}
	
	protected List getChildren(String elementName) {
		List list = new ArrayList();
		for (Iterator it = children.iterator(); it.hasNext();) {
			IElement child = (IElement) it.next();
			if (child.getElementName().equals(elementName)) {
				list.add(child);
			}
		}
		return list;
	}
	
	protected IElement getRoot() {
		return root;
	}

}
