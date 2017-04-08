package org.freeplane.securegroovy;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

import net.bytebuddy.implementation.bind.annotation.FieldValue;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

public class CachedMethodInterceptor {
	public static Method intercept(@SuperCall Callable<Method> c, @FieldValue("cachedMethod") Method cachedMethod){
		AccessPermissionChecker.checkAccessPermission(cachedMethod);
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