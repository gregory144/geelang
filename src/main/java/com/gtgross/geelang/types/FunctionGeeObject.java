package com.gtgross.geelang.types;


public class FunctionGeeObject extends GeeObject {

	public static final int TYPE_ID = TypeDef.newTypeId();

	private final int address;

	public FunctionGeeObject(int address) {
		super(TYPE_ID);
		this.address = address;
	}

	public int getAddress() {
		return address;
	}
}
