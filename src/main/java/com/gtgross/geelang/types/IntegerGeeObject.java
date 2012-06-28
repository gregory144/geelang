package com.gtgross.geelang.types;

import java.util.HashMap;
import java.util.Map;

public class IntegerGeeObject extends GeeObject {

	public static final int TYPE_ID = TypeDef.newTypeId();

	private final int value;

	private static Map<Integer, IntegerGeeObject> values = new HashMap<>();

	public static IntegerGeeObject getInstance(int value) {
		if (values.containsKey(value)) {
			return values.get(value);
		}
		IntegerGeeObject obj = new IntegerGeeObject(value);
		values.put(value, obj);
		return obj;
	}

	public static IntegerGeeObject create(int value) {
		return getInstance(value);
	}

	private IntegerGeeObject(int value) {
		super(TYPE_ID);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public GeeObject add(GeeObject[] arguments) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) arguments[0];
		int resultValue = this.getValue() + intOperand2.getValue();
		return IntegerGeeObject.getInstance(resultValue);
	}

	public GeeObject subtract(GeeObject[] arguments) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) arguments[0];
		int resultValue = this.getValue() - intOperand2.getValue();
		return IntegerGeeObject.getInstance(resultValue);
	}

	public GeeObject multiply(GeeObject[] arguments) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) arguments[0];
		int resultValue = this.getValue() * intOperand2.getValue();
		return IntegerGeeObject.getInstance(resultValue);
	}

	public GeeObject divide(GeeObject[] arguments) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) arguments[0];
		int resultValue = this.getValue() / intOperand2.getValue();
		return IntegerGeeObject.getInstance(resultValue);
	}

	public GeeObject modulus(GeeObject[] arguments) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) arguments[0];
		int resultValue = this.getValue() % intOperand2.getValue();
		return IntegerGeeObject.getInstance(resultValue);
	}

	public GeeObject eq(GeeObject[] arguments) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) arguments[0];
		return this.getValue() == intOperand2.getValue() ? IntegerGeeObject
				.getInstance(1) : null;
	}

	public GeeObject ne(GeeObject[] arguments) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) arguments[0];
		return this.getValue() != intOperand2.getValue() ? IntegerGeeObject
				.getInstance(1) : null;
	}

	public GeeObject lt(GeeObject[] arguments) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) arguments[0];
		return this.getValue() < intOperand2.getValue() ? IntegerGeeObject
				.getInstance(1) : null;
	}

	public GeeObject lte(GeeObject[] arguments) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) arguments[0];
		return this.getValue() <= intOperand2.getValue() ? IntegerGeeObject
				.getInstance(1) : null;
	}

	public GeeObject gt(GeeObject[] arguments) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) arguments[0];
		return this.getValue() > intOperand2.getValue() ? IntegerGeeObject
				.getInstance(1) : null;
	}

	public GeeObject gte(GeeObject[] arguments) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) arguments[0];
		return this.getValue() >= intOperand2.getValue() ? IntegerGeeObject
				.getInstance(1) : null;
	}

	@Override
	public String toString() {
		return "Integer: " + this.getValue();
	}

}
