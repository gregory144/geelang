package com.gtgross.geelang;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class ByteCodeEmitter {

	private int byteCount = 0;
	private int labelCount = 0;
	private final Map<Integer, Integer> labels = new HashMap<>();
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();

	public ByteCodeEmitter add() throws IOException {
		write(I.ADD);
		return this;
	}

	public ByteCodeEmitter append(byte[] otherCode) throws IOException {
		write(otherCode);
		return this;
	}

	public ByteCodeEmitter call(byte value) throws IOException {
		write(I.CALL);
		write(value);
		return this;
	}

	public ByteCodeEmitter call(int value) throws IOException {
		return call((byte) value);
	}

	public ByteCodeEmitter callz() throws IOException {
		write(I.CALLZ);
		return this;
	}

	public ByteCodeEmitter constant(String constant) throws IOException {
		write(I.CON);
		write(constantToBytes(constant));
		return this;
	}

	public ByteCodeEmitter create(byte value) throws IOException {
		write(I.NEW);
		write(value);
		return this;
	}

	public ByteCodeEmitter create(int value) throws IOException {
		return create((byte) value);
	}

	public ByteCodeEmitter def(byte value) throws IOException {
		write(I.DEF);
		write(value);
		return this;
	}

	public ByteCodeEmitter def(int value) throws IOException {
		return def((byte) value);
	}

	public ByteCodeEmitter defz() throws IOException {
		write(I.DEFZ);
		return this;
	}

	public ByteCodeEmitter divide() throws IOException {
		write(I.DIV);
		return this;
	}

	public ByteCodeEmitter eq() throws IOException {
		write(I.EQ);
		return this;
	}

	public byte[] generate() {
		byte[] bytecode = out.toByteArray();
		return bytecode;
	}

	public ByteCodeEmitter get(byte value) throws IOException {
		write(I.GET);
		write(value);
		return this;
	}

	public ByteCodeEmitter get(int value) throws IOException {
		return get((byte) value);
	}

	public ByteCodeEmitter go(byte value) throws IOException {
		write(I.GO);
		write(value);
		return this;
	}

	public ByteCodeEmitter go(int value) throws IOException {
		return go((byte) value);
	}

	public ByteCodeEmitter gt() throws IOException {
		write(I.GT);
		return this;
	}

	public ByteCodeEmitter gte() throws IOException {
		write(I.GTE);
		return this;
	}

	public ByteCodeEmitter halt() throws IOException {
		write(I.HALT);
		return this;
	}

	public ByteCodeEmitter iff(byte value) throws IOException {
		write(I.IF);
		write(value);
		return this;
	}

	public ByteCodeEmitter iff(int value) throws IOException {
		return iff((byte) value);
	}

	public ByteCodeEmitter lt() throws IOException {
		write(I.LT);
		return this;
	}

	public ByteCodeEmitter lte() throws IOException {
		write(I.LTE);
		return this;
	}

	public int mark() {
		int label = labelCount;
		labels.put(label, byteCount);
		labelCount++;
		return label;
	}

	public ByteCodeEmitter modulus() throws IOException {
		write(I.MOD);
		return this;
	}

	public ByteCodeEmitter multiply() throws IOException {
		write(I.MULT);
		return this;
	}

	public int offset(int label) {
		return byteCount - labels.get(label);
	}

	public ByteCodeEmitter pop(byte value) throws IOException {
		write(I.POP);
		write(value);
		return this;
	}

	public ByteCodeEmitter pop(int value) throws IOException {
		return pop((byte) value);
	}

	public ByteCodeEmitter popz() throws IOException {
		write(I.POPZ);
		return this;
	}

	public ByteCodeEmitter push(byte value) throws IOException {
		write(I.PUSH);
		write(value);
		return this;
	}

	public ByteCodeEmitter push(int value) throws IOException {
		return push((byte) value);
	}

	public ByteCodeEmitter pushi(int value) throws IOException {
		ByteBuffer b = ByteBuffer.allocate(5);
		b.put((byte) I.PUSHI.ordinal());
		b.putInt(value);
		write(b.array());
		return this;
	}

	public ByteCodeEmitter put(byte value) throws IOException {
		write(I.PUT);
		write(value);
		return this;
	}

	public ByteCodeEmitter put(int value) throws IOException {
		return put((byte) value);
	}

	public ByteCodeEmitter ret() throws IOException {
		write(I.RET);
		return this;
	}

	public ByteCodeEmitter subtract() throws IOException {
		write(I.SUB);
		return this;
	}

	public ByteCodeEmitter type(String name) throws IOException {
		write(I.TYPE);
		write(constantToBytes(name));
		return this;
	}

	private byte[] constantToBytes(String constant) {
		byte[] constantAsBytes;
		try {
			constantAsBytes = constant.getBytes("UTF8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		byte[] geeRep = new byte[constantAsBytes.length + 1];
		geeRep[0] = (byte) constantAsBytes.length;
		for (int i = 0; i < constantAsBytes.length; i++) {
			geeRep[i + 1] = constantAsBytes[i];
		}
		return geeRep;
	}

	private ByteCodeEmitter write(byte... code) throws IOException {
		byteCount += code.length;
		out.write(code);
		return this;
	}

	private ByteCodeEmitter write(I... code) throws IOException {
		byte[] bytes = new byte[code.length];
		for (int i = 0; i < code.length; i++) {
			bytes[i] = (byte) code[i].ordinal();
		}
		write(bytes);
		return this;
	}

	public ByteCodeEmitter pushz() throws IOException {
		write(I.PUSHZ);
		return this;
	}

}
