package com.gtgross.geelang.types;

import java.util.HashMap;
import java.util.Map;

public class GeeObject {

	private static IdentGenerator nextId = new IdentGenerator();

	private final int id;
	private final int typeId;
	private final Map<Integer, GeeObject> values = new HashMap<>();

	public GeeObject(int typeId) {
		this.typeId = typeId;

		id = nextId.nextId();
	}

	public GeeObject get(int fieldId) {
		return values.get(fieldId);
	}

	public int getId() {
		return id;
	}

	public int getTypeId() {
		return typeId;
	}

	public void put(int fieldId, GeeObject value) {
		values.put(fieldId, value);
	}
}
