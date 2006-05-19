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

import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IType;
import org.seasar.kijimuna.core.dicon.model.IContainerElement;

public class SqlMarkerUnmarkingListener extends AbstractSqlMarkerListener {

	public void diconAdded(IContainerElement container) {
	}

	public void diconUpdated(IContainerElement old, IContainerElement young) {
		unmark(old);
	}

	public void diconRemoved(IContainerElement container) {
		unmark(container);
	}
	
	private void unmark(IContainerElement container) {
		final IType[] types = getAppliedTypes(container);
		run(new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				for (int i = 0; i < types.length; i++) {
					SqlMarkerUtil.unmark(types[i]);
				}
			}
		});
	}

}