package com.gtgross.geelang.types;

import java.util.HashMap;
import java.util.Map;

import com.gtgross.geelang.FunctionGeeObject;

public class TypeDef {

	private static final IdentGenerator nextTypeId = new IdentGenerator();

	public static int newTypeId() {
		return nextTypeId.nextId();
	}

	private final String name;

	private final int id;

	private int constantIndex = 0;

	private final int parentTypeDef = -1;

	private final Map<Integer, String> constants = new HashMap<>();

	private final Map<String, FunctionGeeObject> functions = new HashMap<>();

	private final Map<String, Integer> fields = new HashMap<>();

	private int fieldId = 0;

	public TypeDef(String name) {
		this.id = newTypeId();
		this.name = name;
	}

	public TypeDef(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void addConstant(String constant) {
		constants.put(constantIndex++, constant);
	}

	public String getConstant(int constantIndex) {
		return constants.get(constantIndex);
	}

	public void addFunction(String functionName, int address) {
		FunctionGeeObject function = new FunctionGeeObject(address);
		functions.put(functionName, function);
		fields.put(functionName, function.getId());
	}

	public FunctionGeeObject getFunction(String functionName) {
		return functions.get(functionName);
	}

	public int getField(String fieldName) {
		if (fields.containsKey(fieldName)) {
			return fields.get(fieldName);
		}
		int fieldId = this.fieldId++;
		fields.put(fieldName, fieldId);
		return fieldId;
	}
}
