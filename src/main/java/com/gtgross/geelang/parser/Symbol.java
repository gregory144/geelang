package com.gtgross.geelang.parser;

import java.util.concurrent.atomic.AtomicBoolean;

public class Symbol {
	private final AtomicBoolean active;
	private final String name;
	private int register;
	private boolean hasRegister = false;
	private final int scopeLevel;

	public Symbol(String name, int scopeLevel) {
		this.name = name;
		this.scopeLevel = scopeLevel;
		active = new AtomicBoolean(true);
	}

	public void deactivate() {
		active.set(false);
	}

	public String getName() {
		return name;
	}

	public int getRegister() {
		return register;
	}

	public int getScopeLevel() {
		return scopeLevel;
	}

	public boolean hasRegister() {
		return hasRegister;
	}

	public boolean isActive() {
		return active.get();
	}

	public void setRegister(int register) {
		this.hasRegister = true;
		this.register = register;
	}

}
