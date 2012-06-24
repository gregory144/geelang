package com.gtgross.geelang.types;

import java.util.HashMap;
import java.util.Map;

public class IntegerGeeObject extends GeeObject {

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
		super(IntegerTypeDef.TYPE_ID);
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public GeeObject add(GeeObject operand2) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) operand2;
		int resultValue = this.getValue() + intOperand2.getValue();
		return IntegerGeeObject.getInstance(resultValue);
	}

	public GeeObject subtract(GeeObject operand2) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) operand2;
		int resultValue = this.getValue() - intOperand2.getValue();
		return IntegerGeeObject.getInstance(resultValue);
	}

	public GeeObject multiply(GeeObject operand2) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) operand2;
		int resultValue = this.getValue() * intOperand2.getValue();
		return IntegerGeeObject.getInstance(resultValue);
	}

	public GeeObject divide(GeeObject operand2) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) operand2;
		int resultValue = this.getValue() / intOperand2.getValue();
		return IntegerGeeObject.getInstance(resultValue);
	}

	public GeeObject modulus(GeeObject operand2) {
		IntegerGeeObject intOperand2 = (IntegerGeeObject) operand2;
		int resultValue = this.getValue() % intOperand2.getValue();
		return IntegerGeeObject.getInstance(resultValue);
	}

	@Override
	public String toString() {
		return "Integer: " + this.getValue();
	}

}
