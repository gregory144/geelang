package com.gtgross.geelang.types;

import java.lang.reflect.InvocationTargetException;

public class NativeFunctionGeeObject extends FunctionGeeObject {

	private final int numParams;
	private final String name;

	public NativeFunctionGeeObject(String name, int numParams) {
		super(TYPE_ID);

		this.name = name;
		this.numParams = numParams;
	}

	public int getNumParams() {
		return numParams;
	}

	public String getName() {
		return name;
	}

	public GeeObject call(GeeObject currObj, GeeObject[] params) {
		java.lang.reflect.Method method;

		try {
			method = currObj.getClass().getMethod(name, GeeObject[].class);

			return (GeeObject) method.invoke(currObj, (Object) params);
		} catch (NoSuchMethodException | SecurityException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}

}
