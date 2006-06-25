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
package org.seasar.s2daoplugin.sqlmarker;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IType;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.s2daoplugin.S2DaoUtil;
import org.seasar.s2daoplugin.cache.DiconModelManager;
import org.seasar.s2daoplugin.cache.cache.IComponentCache;
import org.seasar.s2daoplugin.cache.deployment.IDeploymentChangeListener;
import org.seasar.s2daoplugin.sqlmarker.SqlMarkerUtil.ISqlMarkerCreator;

public abstract class AbstractSqlMarkerListener implements IDeploymentChangeListener {

	protected static final IType[] EMPTY_TYPES = new IType[0];
	
	private ISqlMarkerCreator marker = SqlMarkerUtil.getCreator();
	private DiconModelManager manager;
	
	public void setManager(DiconModelManager manager) {
		this.manager = manager;
	}

	public DiconModelManager getManager() {
		return manager;
	}

	public void initialize() {
	}
	
	public void finishChanged() {
	}
	
	protected IProject getProject() {
		return manager.getProject();
	}
	
	protected ISqlMarkerCreator getMarker() {
		return marker;
	}
	
	protected IType[] getAppliedTypes(IComponentElement[] components) {
		if (components.length == 0) {
			return EMPTY_TYPES;
		}
		IComponentCache cache = S2DaoUtil.getS2DaoComponentCache(getProject());
		if (cache == null) {
			return EMPTY_TYPES;
		}
		cache = cache.getComponentCache(components[0].getStorage().getFullPath());
		return cache != null ? cache.getAllAppliedTypes() : EMPTY_TYPES;
	}

}
