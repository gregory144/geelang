package com.gtgross.geelang.types;


public class NullGeeObject extends GeeObject {

	public static final int TYPE_ID = TypeDef.newTypeId();

	private static final NullGeeObject INSTANCE = new NullGeeObject();

	public static NullGeeObject getInstance() {
		return INSTANCE;
	}

	private NullGeeObject() {
		super(TYPE_ID);
	}

	@Override
	public String toString() {
		return "NULL";
	}

}
