package com.gtgross.geelang;

import com.gtgross.geelang.types.GeeObject;
import com.gtgross.geelang.types.TypeDef;

public class FunctionGeeObject extends GeeObject {

	public static final int TYPE_ID = TypeDef.newTypeId();

	private final int address;

	private final int numLocals = -1;

	public FunctionGeeObject(int address) {
		super(TYPE_ID);
		this.address = address;
	}

	public int getAddress() {
		return address;
	}

	public int getNumLocals() {
		return numLocals;
	}
}
