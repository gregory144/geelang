package com.gtgross.geelang;

import java.io.IOException;

import junit.framework.TestCase;

import com.gtgross.geelang.types.GeeObject;
import com.gtgross.geelang.types.IntegerGeeObject;
import com.gtgross.geelang.types.NullGeeObject;

public class VMTest extends TestCase {

	ByteCodeEmitter code = new ByteCodeEmitter();

	public void testAdd() throws IOException {
		code.pushi(5).pushi(5).add().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(10), ret);
	}

	public void testSubtract() throws IOException {
		code.pushi(6).pushi(7).subtract().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testMultiply() throws IOException {
		code.pushi(6).pushi(7).multiply().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(42), ret);
	}

	public void testDivide() throws IOException {
		code.pushi(4).pushi(20).divide().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(5), ret);
	}

	public void testModulus() throws IOException {
		code.pushi(6).pushi(20).modulus().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(2), ret);
	}

	public void testEqualsFalse() throws IOException {
		code.pushi(20).pushi(6).eq().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(NullGeeObject.getInstance(), ret);
	}

	public void testEqualsTrue() throws IOException {
		code.pushi(20).pushi(20).eq().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testLessThanFalse() throws IOException {
		code.pushi(20).pushi(6).lt().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(NullGeeObject.getInstance(), ret);
	}

	public void testLessThanWhenEquals() throws IOException {
		code.pushi(20).pushi(20).lt().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(NullGeeObject.getInstance(), ret);
	}

	public void testLessThanTrue() throws IOException {
		code.pushi(6).pushi(20).lt().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testGreaterThanTrue() throws IOException {
		code.pushi(20).pushi(6).gt().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testGreaterThanWhenEquals() throws IOException {
		code.pushi(20).pushi(20).gt().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(NullGeeObject.getInstance(), ret);
	}

	public void testGreaterThanFalse() throws IOException {
		code.pushi(6).pushi(20).gt().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(NullGeeObject.getInstance(), ret);
	}

	public void testLessOrEqualsThanFalse() throws IOException {
		code.pushi(20).pushi(6).lte().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(NullGeeObject.getInstance(), ret);
	}

	public void testLessThanOrEqualsWhenEquals() throws IOException {
		code.pushi(20).pushi(20).lte().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testLessThanOrEqualsTrue() throws IOException {
		code.pushi(6).pushi(20).lte().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testGreaterThanOrEqualsTrue() throws IOException {
		code.pushi(20).pushi(6).gte().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testGreaterThanOrEqualsWhenEquals() throws IOException {
		code.pushi(20).pushi(20).gte().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testGreaterThanOrEqualsFalse() throws IOException {
		code.pushi(6).pushi(20).gte().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(NullGeeObject.getInstance(), ret);
	}

	public void testPopz() throws IOException {
		code.pushi(1).pushi(2).popz().halt();
		GeeObject ret = new VM(code.generate())
				.run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testPopAndPush() throws IOException {
		code.pushi(1).pushi(2);
		code.pop(0).pushi(3).push(0).halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(2), ret);
	}

	public void testIf() throws IOException {
		code.pushi(2).pushi(1).iff(5).pushi(3).halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(2), ret);
	}

	public void testGo() throws IOException {
		code.pushi(2).go(5).pushi(3).halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(2), ret);
	}

	public void testGoBackwards() throws IOException {
		code.go(7).pushi(2).go(7).go(-9).pushi(3).halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(2), ret);
	}

	public void testAnonFunc() throws IOException {
		code.defz().pushi(1).pushi(2).add().ret();
		code.pop(0).pushz().push(0);
		code.callz().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(3), ret);
	}

	public void testAnonFuncs() throws IOException {
		code.defz().pushi(1).pushi(2).add().ret();
		code.defz().pushi(3).pushi(4).add().ret();
		code.popz().callz().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(3), ret);
	}

	public void testAnonFuncsCallSecond() throws IOException {
		code.defz().pushi(3).pushi(4).add().ret();
		code.defz().pushi(1).pushi(2).add().ret();
		code.callz().halt();
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(3), ret);
	}

	public void testNestedAnonFuncs() throws IOException {
		code.defz();
		code.defz().pushi(3).pushi(4).add().ret();
		code.callz();
		code.pushi(4).add().ret().callz().halt();

		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(11), ret);
	}

	public void testType() throws IOException {
		code.type("Number");
		code.constant("square");
		code.def(1).popz().pop(0).push(0).push(0).multiply().ret();
		code.pushi(4).create(0).call(1).halt();
		
		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(16), ret);
	}

	public void testCallingConvention() throws IOException {
		code.type("Number");
		code.constant("square");
		code.def(1).popz().pop(0).push(0).push(0).multiply().ret();
		code.pushi(10).pop(0).push(0).pushi(4).create(0);
		code.call(1).popz().popz().popz().halt(); // pop object, param, ret val

		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(10), ret);
	}

	public void testTypeWithField() throws IOException {
		code.type("Number");
		code.constant("create").constant("value").constant("square");
		code.def(1).popz().pop(1).push(1).push(0).put(2).push(0).ret();
		code.def(3).pop(0).push(0).get(2).pop(1).push(1).push(1).multiply();
		code.pop(2).push(1).push(0).push(2).ret();

		code.pushi(4).create(0).pop(0).push(0).call(1);
		code.call(3).halt();

		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(16), ret);
	}

	public void testMultipleTypes() throws IOException {
		code.type("Identifier");
		code.constant("create").constant("value").constant("get-value");

		code.def(1).pop(0).pop(1).push(1).push(0).put(2).push(0).ret();
		code.def(3).get(2).ret();

		code.type("User");
		code.constant("create").constant("get-num-children");
		code.constant("id").constant("children");

		code.def(1).pop(0).pop(1).pop(2).push(1).push(0).put(3);
		code.push(2).push(0).put(4).push(0).ret();
		code.def(2).get(4).ret();

		code.type("Main");
		code.constant("Identifier").constant("User");
		code.constant("create").constant("get-num-children");
		code.constant("main");

		code.def(5);
		code.create(1).pop(0).push(0).pushi(4).push(0).call(3).popz();
		code.create(2).pop(1).pop(0);
		code.push(0).push(1).pushi(2).push(0).push(1).call(3).pop(1).pop(0);

		code.push(1).push(0).call(4);
		code.ret();

		code.create(0).call(5).halt();

		GeeObject ret = new VM(code.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(2), ret);
	}

}
