package org.freeplane.securegroovy;

import groovy.lang.GroovyObject;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy.Configurable;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;

public class GroovyPatcher {
	private static final String CACHED_FIELD_CLASS = "org.codehaus.groovy.reflection.CachedField";
	private static final String CACHED_METHOD_CLASS = "org.codehaus.groovy.reflection.CachedMethod";
	
	public static void apply(final ClassLoader pachedClassLoader){
		final ByteBuddy byteBuddy = new ByteBuddy();
		TypePool typePool = TypePool.Default.of(pachedClassLoader);
		final ClassFileLocator classFileLocator = ClassFileLocator.ForClassLoader.of(pachedClassLoader);

		final MethodDelegation cachedFieldInterceptor = MethodDelegation.to(CachedFieldInterceptor.class);
		final Configurable<ClassLoader> strategy = ClassLoadingStrategy.Default.INJECTION.with(GroovyObject.class.getProtectionDomain());
		byteBuddy.rebase(typePool.describe(CACHED_FIELD_CLASS).resolve(), //
				classFileLocator)
		.method(ElementMatchers.named("getProperty")).intercept(cachedFieldInterceptor)
		.method(ElementMatchers.named("setProperty")).intercept(cachedFieldInterceptor)
		.make()
		.load(pachedClassLoader, strategy);

		final MethodDelegation cachedMethodInterceptor = MethodDelegation.to(CachedMethodInterceptor.class);
		final MethodDelegation cachedMethodInvocationInterceptor = MethodDelegation.to(CachedMethodInvocationInterceptor.class);

		byteBuddy.rebase(typePool.describe(CACHED_METHOD_CLASS).resolve(), //
				classFileLocator)
		.method(ElementMatchers.named("setAccessible")).intercept(cachedMethodInterceptor)
		.method(ElementMatchers.named("getCachedMethod")).intercept(cachedMethodInterceptor)
		.method(ElementMatchers.named("invoke")).intercept(cachedMethodInvocationInterceptor)
		.make()
		.load(pachedClassLoader, strategy);
	}
}
