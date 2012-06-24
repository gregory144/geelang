package com.gtgross.geelang;

public enum I {
	INVALID, ADD, SUB, MULT, DIV, MOD, PUSHI, PUSH, POP, POPZ, EQ, LT, GT, LTE, GTE, CALLZ, CALL, RET, GO, IF, NEW, GET, PUT, CON, DEFZ, DEF, TYPE, HALT, PUSHZ;

	private static I[] cached = I.values();

	public static I valueOf(byte b) {
		return cached[b];
	}
}