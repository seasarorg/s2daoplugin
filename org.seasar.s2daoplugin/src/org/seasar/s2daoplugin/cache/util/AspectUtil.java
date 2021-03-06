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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.seasar.kijimuna.core.dicon.info.IAspectInfo;
import org.seasar.kijimuna.core.dicon.info.IPointcut;
import org.seasar.kijimuna.core.dicon.model.IArgElement;
import org.seasar.kijimuna.core.dicon.model.IAspectElement;
import org.seasar.kijimuna.core.dicon.model.IComponentElement;
import org.seasar.kijimuna.core.dicon.model.IComponentHolderElement;
import org.seasar.kijimuna.core.dicon.model.IInitMethodElement;
import org.seasar.kijimuna.core.rtti.IRtti;
import org.seasar.kijimuna.core.rtti.RttiLoader;
import org.seasar.s2daoplugin.cache.CacheConstants;

public final class AspectUtil implements CacheConstants {

	public static boolean hasInterceptor(IAspectElement aspect, IType interceptorType) {
		return hasInterceptor(aspect, new IType[] {interceptorType});
	}
	
	public static boolean hasInterceptor(IAspectElement aspect, IType[] interceptorTypes) {
		return hasInterceptor(DiconUtil.getChildComponent(aspect), interceptorTypes);
	}
	
	public static boolean hasInterceptor(IComponentElement interceptor,
			IType interceptorType) {
		return hasInterceptor(interceptor, new IType[] {interceptorType});
	}
	
	public static boolean hasInterceptor(IComponentElement interceptor,
			IType[] interceptorTypes) {
		if (interceptor == null || interceptorTypes == null ||
				interceptorTypes.length == 0) {
			return false;
		}
		if (isInterceptorChain(interceptor)) {
			IComponentElement[] interceptors = getInterceptors(interceptor);
			for (int i = 0; i < interceptors.length; i++) {
				if (hasInterceptor(interceptors[i], interceptorTypes)) {
					return true;
				}
			}
		} else {
			IRtti rtti = interceptor.getRttiLoader().loadRtti(
					interceptor.getComponentClassName());
			if (rtti != null) {
				for (int i = 0; i < interceptorTypes.length; i++) {
					if (interceptorTypes[i] != null &&
							interceptorTypes[i].equals(rtti.getType())) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static IComponentElement[] getAllInterceptors(IComponentHolderElement aspect) {
		return getAllInterceptors(DiconUtil.getChildComponent(aspect));
	}
	
	public static IComponentElement[] getAllInterceptors(IComponentElement interceptor) {
		if (interceptor == null) {
			return DiconUtil.EMPTY_COMPONENTS;
		}
		Set result = new HashSet();
		if (isInterceptorChain(interceptor)) {
			IComponentElement[] interceptors = getInterceptors(interceptor);
			for (int i = 0; i < interceptors.length; i++) {
				result.addAll(Arrays.asList(getAllInterceptors(interceptors[i])));
			}
		} else {
			result.add(interceptor);
		}
		return DiconUtil.toComponentArray(result);
	}
	
	public static boolean isApplied(IAspectElement aspect, IMethod method) {
		if (aspect == null || method == null) {
			return false;
		}
		if (!isApplieableMethodModifier(method)) {
			return false;
		}
		IAspectInfo info = (IAspectInfo) aspect.getAdapter(IAspectInfo.class);
		if (info != null && matchPointcut(info.getPointcuts(), method)) {
			return true;
		}
		return false;
	}
	
	private static boolean isApplieableMethodModifier(IMethod method) {
		IType type = method.getDeclaringType();
		if (FlagsUtil.isInterface(type)) {
			return true;
		}
		if (FlagsUtil.isFinal(type) ||
				FlagsUtil.isFinal(method) || FlagsUtil.isStatic(method) ||
				FlagsUtil.isPrivate(method) || FlagsUtil.isProtected(method) ||
				FlagsUtil.isPackagePrivate(method)) {
			return false;
		}
		return true;
	}
	
	private static boolean matchPointcut(IPointcut[] pointcuts, IMethod method) {
		for (int i = 0; i < pointcuts.length; i++) {
			// pointcutが無指定の場合
			if (pointcuts[i].isAutoApply()) {
				// interfaceのメソッドだけが有効
				return isApplieableMethodOnInterface(method);
			}
			// 継承元も許すためIRttiMethodDescriptorで比較しない
			try {
				Pattern p = Pattern.compile(pointcuts[i].getRegexp());
				if (p.matcher(method.getElementName()).matches()) {
					return true;
				}
			} catch (PatternSyntaxException ignore) {
			}
		}
		return false;
	}
	
	private static boolean isApplieableMethodOnInterface(IMethod method) {
		return isDeclaredOnInterface(method.getDeclaringType(), method);
	}
	
	private static boolean isDeclaredOnInterface(IType type, IMethod method) {
		if (type == null) {
			return false;
		}
		if (FlagsUtil.isInterface(type)) {
			IMethod[] methods = TypeUtil.getMethods(type);
			for (int i = 0; i < methods.length; i++) {
				if (method.isSimilar(methods[i])) {
					return true;
				}
			}
		}
		IType[] interfaces = TypeUtil.findSuperInterfaces(type);
		for (int i = 0; i < interfaces.length; i++) {
			if (isDeclaredOnInterface(interfaces[i], method)) {
				return true;
			}
		}
		return false;
	}
	
	private static boolean isInterceptorChain(IComponentElement component) {
		RttiLoader loader = component.getRttiLoader();
		IRtti chainRtti = loader.loadRtti(INTERCEPTOR_CHAIN);
		if (!RttiUtil.existsType(chainRtti)) {
			return false;
		}
		IRtti rtti = loader.loadRtti(component.getComponentClassName());
		return rtti != null && chainRtti.getType().equals(rtti.getType());
	}
	
	private static IComponentElement[] getInterceptors(IComponentElement interceptorChain) {
		Set result = new HashSet();
		List initMethods = interceptorChain.getInitMethodList();
		for (int i = 0; i < initMethods.size(); i++) {
			IInitMethodElement initMethod = (IInitMethodElement) initMethods.get(i);
			if (!"add".equals(initMethod.getMethodName())) {
				continue;
			}
			List args = initMethod.getArgList();
			if (args.size() != 1) {
				continue;
			}
			IComponentElement component =
				DiconUtil.getChildComponent((IArgElement) args.get(0));
			if (component != null) {
				result.add(component);
			}
		}
		return DiconUtil.toComponentArray(result);
	}

}
