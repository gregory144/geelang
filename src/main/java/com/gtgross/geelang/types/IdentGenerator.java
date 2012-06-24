package com.gtgross.geelang.types;

import java.util.concurrent.atomic.AtomicInteger;

public class IdentGenerator {

	private final AtomicInteger nextId = new AtomicInteger(1);

	public int nextId() {
		return nextId.incrementAndGet();
	}
}
