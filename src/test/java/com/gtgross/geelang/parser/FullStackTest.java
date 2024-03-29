package com.gtgross.geelang.parser;

import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import com.gtgross.geelang.VM;
import com.gtgross.geelang.parser.ast.Node;
import com.gtgross.geelang.types.GeeObject;
import com.gtgross.geelang.types.IntegerGeeObject;

public class FullStackTest extends TestCase {

	private Node parseExpression(String text) throws NumberFormatException,
			ParseException, IOException {
		GeelangParser p = new GeelangParser(new StringReader(text));
		return p.Expression();
	}

	private Node parseFunction(String text) throws NumberFormatException,
			ParseException, IOException {
		GeelangParser p = new GeelangParser(new StringReader(text));
		return p.Function();
	}

	private Node parseModule(String text) throws NumberFormatException,
			ParseException, IOException {
		GeelangParser p = new GeelangParser(new StringReader(text));
		return p.Module();
	}

	private Node parseStatement(String text) throws NumberFormatException,
			ParseException, IOException {
		GeelangParser p = new GeelangParser(new StringReader(text));
		return p.Statement();
	}

	private Node parseProgram(String text) throws NumberFormatException,
			ParseException, IOException {
		GeelangParser p = new GeelangParser(new StringReader(text));
		return p.Program();
	}

	public void testAddInts() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("4+5");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(9), ret);
	}

	public void testDivideInts() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("20/4");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(5), ret);
	}

	public void testAddIntsWithUnaryOp() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("-4+10");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(6), ret);
	}

	public void testPrecedence() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("2*3+4*5");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(26), ret);
	}

	public void testPrecedence2() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("2+3*4*5");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(62), ret);
	}

	public void testPrecedenceWithParens() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("(2+3)*4*5");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(100), ret);
	}

	public void testIf() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("if 2 { 1; }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testIfWithElse() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("if 2 { 1; } else { 0; }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testConditional() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("if 5 == 5 { 1; } else { 0; }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testConditionalNotEq() throws ParseException,
			NumberFormatException,
			IOException {
		Node node = parseExpression("if 5 != 5 { 1; } else { 0; }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(0), ret);
	}

	public void testConditionalLessThan() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("if 4 < 5 { 1; } else { 0; }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testConditionalLessThanOrEquals() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("if 5 <= 5 { 1; } else { 0; }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testConditionalLessThanOrEqualsFalse() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("if 6 <= 5 { 1; } else { 0; }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(0), ret);
	}

	public void testAssignment() throws ParseException, NumberFormatException,
			IOException {
		GeelangParser p = new GeelangParser(new StringReader("{x=5;x+2;}"));
		Node node = p.Block();
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(7), ret);
	}

	public void testAssignment2() throws ParseException, NumberFormatException,
			IOException {
		GeelangParser p = new GeelangParser(new StringReader(
				"{x=5*10;y=3*7;z=x+y;}"));
		Node node = p.Block();
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(71), ret);
	}

	public void testComplexExpressionsWithIdents() throws ParseException,
			NumberFormatException, IOException {
		GeelangParser p = new GeelangParser(new StringReader(
				"{x = 3 * 4; y = (x + 4) * x; z = 192 /  y; }"));
		Node node = p.Block();
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1), ret);
	}

	public void testMainModule() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseModule("module Main { func main() { 2; } }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(2), ret);
	}

	public void testMainModuleWithCalc() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseModule("module Main { func main() { 2+2; } }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(4), ret);
	}

	public void testProgram() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseProgram("module t1 { func f1() { 3; } } module Main { func main() { 2; } }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(2), ret);
	}

	public void testCreateObject() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseProgram("module T1 { func f1() { 3; } } module Main { func main() { x = T1.new; x.f1(); } }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(3), ret);
	}

	public void testCreateObjectModifyResult() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseProgram("module T1 { func f1() { 3; } } module Main { func main() { x = T1.new; y = x.f1(); y * 10; } }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(30), ret);
	}

	public void testCreateObjectWithComplexFunc() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseProgram("module T1 { func f1() { 1+100; } } module Main { func main() { x = T1.new; y = x.f1(); y * 10; } }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(1010), ret);
	}

	public void testConvertFtoC() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseProgram("module Converter { func convert(x) { (((x *100) - 3200)*500/900) / 100; } } module Main { func main() { c = Converter.new; c.convert(90); } }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(32), ret);
	}

	public void testConvertCtoF() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseProgram("module Converter { func convert(x) { ((x*100*900/500) + 3200) / 100; } } module Main { func main() { c = Converter.new; c.convert(32); } }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(89), ret);
	}

	public void testUseThis() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseProgram("module Converter { func set(x) { this.y = x; } func get() { this.y; } } module Main { func main() { c = Converter.new; c.set(32); c.get(); } }");
		CodeGen gen = new CodeGen();
		node.accept(gen);
		GeeObject ret = new VM(gen.generate()).run();
		assertEquals(IntegerGeeObject.getInstance(32), ret);
	}
}
