package org.freeplane.securegroovy;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import org.codehaus.groovy.runtime.InvokerInvocationException;

import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class CachedMethodInvocationInterceptor {
	public static <T> T intercept(@SuperCall Callable<T> c, @FieldValue("cachedMethod") Method cachedMethod){
        try {
    		AccessPermissionChecker.checkAccessPermission(cachedMethod);
        } catch (IllegalArgumentException e) {
            throw new InvokerInvocationException(e);
        }
        try {
			return c.call();
		} catch (RuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}