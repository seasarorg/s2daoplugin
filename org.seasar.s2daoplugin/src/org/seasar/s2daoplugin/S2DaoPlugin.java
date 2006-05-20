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
package org.seasar.s2daoplugin;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.seasar.s2daoplugin.util.ArrayUtil;

public class S2DaoPlugin extends AbstractUIPlugin {

	private static S2DaoPlugin plugin;
	
	public S2DaoPlugin() {
		plugin = this;
	}
	
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}
	
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}
	
	public void addS2DaoNature(IProject project) throws CoreException {
		if (project == null || project.hasNature(S2DaoConstants.S2DAO_NATURE)) {
			return;
		}
		IProjectDescription desc = project.getDescription();
		String[] newIds = (String[]) ArrayUtil.add(
				desc.getNatureIds(), S2DaoConstants.S2DAO_NATURE);
		desc.setNatureIds(newIds);
		project.setDescription(desc, null);
	}
	
	public void removeS2DaoNature(IProject project) throws CoreException {
		if (project == null) {
			return;
		}
		IProjectDescription desc = project.getDescription();
		String[] ids = desc.getNatureIds();
		if (ArrayUtil.contains(ids, S2DaoConstants.S2DAO_NATURE)) {
			String[] newIds = (String[]) ArrayUtil.remove(
					ids, S2DaoConstants.S2DAO_NATURE);
			desc.setNatureIds(newIds);
			project.setDescription(desc, null);
		}
	}
	
	public static S2DaoPlugin getDefault() {
		return plugin;
	}
	
	public static boolean isEnabled(IProject project) throws CoreException {
		return project.hasNature(S2DaoConstants.S2DAO_NATURE);
	}
	
	public static void log(CoreException e) {
		getDefault().getLog().log(e.getStatus());
	}

}
