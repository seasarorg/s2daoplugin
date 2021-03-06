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
package org.seasar.s2daoplugin.cache.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IComponentHolderElement;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.kijimuna.core.dicon.model.IPropertyElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.util.ModelUtils;
import org.seasar.s2daoplugin.cache.CacheConstants;
import org.seasar.s2daoplugin.cache.CacheNature;

public final class DiconUtil implements CacheConstants {

	public static final IContainerElement[] EMPTY_CONTAINERS =
		new IContainerElement[0];
	public static final IComponentElement[] EMPTY_COMPONENTS =
		new IComponentElement[0];
	
	public static IContainerElement[] toContainerArray(Collection collection) {
		return collection != null ? (IContainerElement[]) collection.toArray(
				new IContainerElement[collection.size()]) : EMPTY_CONTAINERS;
	}
	
	public static IComponentElement[] toComponentArray(Collection collection) {
		return collection != null ? (IComponentElement[]) collection.toArray(
				new IComponentElement[collection.size()]) : EMPTY_COMPONENTS;
	}
	
	public static IComponentElement[] getComponents(IContainerElement container) {
		return container != null ? toComponentArray(container.getComponentList()) :
			EMPTY_COMPONENTS;
	}
	
	public static IComponentElement getChildComponent(IComponentHolderElement element) {
		if (element == null) {
			return null;
		}
		IRtti rtti = (IRtti) element.getAdapter(IRtti.class);
		if (rtti == null) {
			return null;
		}
		// OGNL�w��
		IComponentElement component =
			(IComponentElement) rtti.getAdapter(IComponentElement.class);
		// <component>�w��
		if (component == null) {
			List children = element.getChildren();
			if (children.size() == 1 &&
					children.get(0) instanceof IComponentElement) {
				component = (IComponentElement) children.get(0);
			}
		}
		return component;
	}
	
	public static IPropertyElement getProperty(IComponentElement component,
			String propertyName) {
		if (component == null) {
			return null;
		}
		List props = component.getPropertyList();
		for (int i = 0; i < props.size(); i++) {
			IPropertyElement prop = (IPropertyElement) props.get(i);
			if (prop.getPropertyName().equals(propertyName)) {
				return prop;
			}
		}
		return null;
	}
	
	public static IContainerElement[] getParentContainers(
			IContainerElement container) {
		return toContainerArray(getParentContainerSet(container));
	}
	
	private static Set getParentContainerSet(IContainerElement container) {
		CacheNature nature = CacheNature.getInstance(container.getProject());
		if (nature == null) {
			return Collections.EMPTY_SET;
		}
		Set ret = new HashSet();
		List parents = ModelUtils.getParentContaienrs(container);
		for (int i = 0; i < parents.size(); i++) {
			IContainerElement org = (IContainerElement) parents.get(i);
			ret.addAll(getParentContainerSet(org));
			IContainerElement c = nature.getDeploymentDiconCache().getContainer(
					org.getStorage().getFullPath());
			if (c != null) {
				ret.add(c);
			}
		}
		return ret;
	}

}
