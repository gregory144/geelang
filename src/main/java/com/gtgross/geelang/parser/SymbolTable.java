package com.gtgross.geelang.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SymbolTable {
	private final Map<String, Symbol> symbols = new HashMap<String, Symbol>();

	public Symbol addSymbol(String name, int scopeLevel) {
		return symbols.put(name, new Symbol(name, scopeLevel));
	}

	public Symbol getSymbol(String name) {
		return symbols.get(name);
	}

	public void deactivateScope(int scopeLevel) {
		for (Symbol symbol : symbols.values()) {
			if (symbol.isActive() && symbol.getScopeLevel() == scopeLevel) {
				symbol.deactivate();
			}
		}
	}

	public List<Integer> usedRegisters() {
		List<Integer> regs = new ArrayList<>();
		for (Symbol symbol : symbols.values()) {
			if (symbol.isActive()) {
				regs.add(symbol.getRegister());
			}
		}
		Collections.sort(regs);
		return regs;
	}
}
