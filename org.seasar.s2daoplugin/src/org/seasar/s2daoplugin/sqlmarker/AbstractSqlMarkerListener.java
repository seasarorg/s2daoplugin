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
import org.seasar.kijimuna.core.dicon.model.IContainerElement;
import org.seasar.s2daoplugin.S2DaoUtil;
import org.seasar.s2daoplugin.cache.IDiconChangeListener;
import org.seasar.s2daoplugin.cache.cache.IComponentCache;
import org.seasar.s2daoplugin.sqlmarker.SqlMarkerUtil.ISqlMarkerCreator;

public abstract class AbstractSqlMarkerListener implements IDiconChangeListener {

	protected static final IType[] EMPTY_TYPES = new IType[0];
	
	private ISqlMarkerCreator marker = SqlMarkerUtil.getCreator();
	private IProject project;
	private SqlMarkerListenerContext context;
	
	public AbstractSqlMarkerListener(SqlMarkerListenerContext context) {
		if (context == null) {
			throw new IllegalArgumentException();
		}
		this.context = context;
	}
	
	public void setProject(IProject project) {
		this.project = project;
	}
	
	public void initialize() {
	}
	
	public void finishChanged() {
	}
	
	protected IProject getProject() {
		return project;
	}
	
	protected ISqlMarkerCreator getMarker() {
		return marker;
	}
	
	protected SqlMarkerListenerContext getContext() {
		return context;
	}
	
	protected IType[] getAppliedTypes(IContainerElement container) {
		if (container == null) {
			return EMPTY_TYPES;
		}
		IComponentCache cache = S2DaoUtil.getS2DaoComponentCache(getProject());
		if (cache == null) {
			return EMPTY_TYPES;
		}
		cache = cache.getComponentCache(container.getStorage().getFullPath());
		return cache != null ? cache.getAllAppliedTypes() : EMPTY_TYPES;
	}

}
