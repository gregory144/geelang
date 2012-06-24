package com.gtgross.geelang.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.gtgross.geelang.parser.ast.AssignExpression;
import com.gtgross.geelang.parser.ast.BinaryOperationNode;
import com.gtgross.geelang.parser.ast.Expression;
import com.gtgross.geelang.parser.ast.ExpressionListNode;
import com.gtgross.geelang.parser.ast.FloatNode;
import com.gtgross.geelang.parser.ast.FunctionCallNode;
import com.gtgross.geelang.parser.ast.FunctionListNode;
import com.gtgross.geelang.parser.ast.FunctionNode;
import com.gtgross.geelang.parser.ast.IdentifierNode;
import com.gtgross.geelang.parser.ast.IntegerNode;
import com.gtgross.geelang.parser.ast.ModuleNode;
import com.gtgross.geelang.parser.ast.Node;
import com.gtgross.geelang.parser.ast.ParameterListNode;
import com.gtgross.geelang.parser.ast.Statement;
import com.gtgross.geelang.parser.ast.StatementListNode;
import com.gtgross.geelang.parser.ast.UnaryOperationNode;

public class GeelangParserTest extends TestCase {

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

	private void assertDeepEquals(Object expected, Object actual) {
		assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
	}

	private Node binOp(Operator op, double f1, double f2) {
		return new BinaryOperationNode(op, new FloatNode(f1), new FloatNode(f2));
	}

	private Expression binOp(Operator op, int i1, int i2) {
		return new BinaryOperationNode(op, new IntegerNode(i1),
				new IntegerNode(i2));
	}

	private void printNode(Node node) {
		NodeVisitor printer = new NodePrinter(System.out);
		node.accept(printer);
	}

	public void testAddInts() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("4+5");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(binOp(Operator.ADD, 4, 5), node);
	}

	public void testMultInts() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("4*5");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(binOp(Operator.MULTIPLY, 4, 5), node);
	}

	public void testMultThreeInts() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("4*5*6");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		printNode(node);
		assertDeepEquals(
				new BinaryOperationNode(Operator.MULTIPLY, binOp(
						Operator.MULTIPLY, 4, 5), new IntegerNode(6)), node);
	}

	public void testAddFloats() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("4.5+4.2");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(binOp(Operator.ADD, 4.5, 4.2), node);
	}

	public void testSubtractInts() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("8-5");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(binOp(Operator.MINUS, 8, 5), node);
	}

	public void testInt() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("9");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(new IntegerNode(9), node);
	}

	public void testFloat() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("8.8");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(new FloatNode(8.8), node);
	}

	public void testIdent() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("variableName");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(new IdentifierNode("variableName"), node);
	}

	public void testIdentWithUnderscore() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("var_name");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(new IdentifierNode("var_name"), node);
	}

	public void testIdentWithDigits() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("x1");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(new IdentifierNode("x1"), node);
	}

	public void testIdentStartsWithUnderscore() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("_x");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(new IdentifierNode("_x"), node);
	}

	public void testNegativeInt() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("-5");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(new UnaryOperationNode(Operator.MINUS,
				new IntegerNode(5)), node);
	}

	public void testNegativeFloat() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("-5.5");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(new UnaryOperationNode(Operator.MINUS, new FloatNode(
				5.5)), node);
	}

	public void testParens() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("(1)");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(new IntegerNode(1), node);
	}

	public void testPrecedence() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("2*3+4*5");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(
				new BinaryOperationNode(Operator.ADD, binOp(Operator.MULTIPLY,
						2, 3), binOp(Operator.MULTIPLY, 4, 5)), node);
	}

	public void testPrecedence2() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("2+(3*4*5)");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		printNode(node);
		assertDeepEquals(
				new BinaryOperationNode(Operator.ADD, new IntegerNode(2),
						new BinaryOperationNode(Operator.MULTIPLY, binOp(
								Operator.MULTIPLY, 3, 4), new IntegerNode(5))),
				node);
	}

	public void testDivision() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("2/4");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(binOp(Operator.DIVIDE, 2, 4), node);
	}

	public void testMultiplication() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("2*4");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(binOp(Operator.MULTIPLY, 2, 4), node);
	}

	public void testModulus() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("5%4");
		assertNotNull(node);
		assertTrue(node instanceof Expression);
		assertDeepEquals(binOp(Operator.MODULUS, 5, 4), node);
	}

	public void testFunction() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseFunction("func x() {}");
		assertNotNull(node);
		assertTrue(node instanceof FunctionNode);
		printNode(node);
		assertEquals(new FunctionNode("x", new ParameterListNode(
				new ArrayList<String>()), new StatementListNode(
				new ArrayList<Statement>())), node);
	}

	public void testFunctionWithSingleParam() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseFunction("func x(y) {}");
		assertNotNull(node);
		assertTrue(node instanceof FunctionNode);
		printNode(node);
		List<String> params = new ArrayList<String>();
		params.add("y");
		assertEquals(new FunctionNode("x", new ParameterListNode(params),
				new StatementListNode(new ArrayList<Statement>())), node);
	}

	public void testFunctionWithMultipleParams() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseFunction("func x(y, z) {}");
		assertNotNull(node);
		assertTrue(node instanceof FunctionNode);
		printNode(node);
		List<String> params = new ArrayList<String>();
		params.add("y");
		params.add("z");
		assertEquals(new FunctionNode("x", new ParameterListNode(params),
				new StatementListNode(new ArrayList<Statement>())), node);
	}

	public void testFunctionWithStatement() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseFunction("func x() {y;}");
		assertNotNull(node);
		assertTrue(node instanceof FunctionNode);
		printNode(node);
		List<Statement> stmts = new ArrayList<Statement>();
		stmts.add(new IdentifierNode("y"));
		assertEquals(new FunctionNode("x", new ParameterListNode(
				new ArrayList<String>()), new StatementListNode(stmts)), node);
	}

	public void testFunctionWithStatements() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseFunction("func x() {y;z;}");
		assertNotNull(node);
		assertTrue(node instanceof FunctionNode);
		printNode(node);
		List<Statement> stmts = new ArrayList<Statement>();
		stmts.add(new IdentifierNode("y"));
		stmts.add(new IdentifierNode("z"));
		assertEquals(new FunctionNode("x", new ParameterListNode(
				new ArrayList<String>()), new StatementListNode(stmts)), node);
	}

	public void testAssignment() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseExpression("x=5");
		assertNotNull(node);
		printNode(node);
		assertTrue(node instanceof AssignExpression);
		assertEquals(new AssignExpression(new IdentifierNode("x"),
				new IntegerNode(5)), node);
	}

	public void testExpressionAsStatement() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseStatement("5;");
		assertNotNull(node);
		assertTrue(node instanceof IntegerNode);
		printNode(node);
		assertEquals(new IntegerNode(5), node);
	}

	public void testFunctionCall() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("x()");
		assertNotNull(node);
		printNode(node);
		assertTrue(node instanceof FunctionCallNode);
		assertEquals(new FunctionCallNode(new IdentifierNode("x"),
				new ExpressionListNode(
				new ArrayList<Expression>())), node);
	}

	public void testFunctionCallWithSingleParam() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("x(y)");
		assertNotNull(node);
		printNode(node);
		assertTrue(node instanceof FunctionCallNode);
		List<Expression> params = new ArrayList<Expression>();
		params.add(new IdentifierNode("y"));
		assertEquals(new FunctionCallNode(new IdentifierNode("x"),
				new ExpressionListNode(params)),
				node);
	}

	public void testFunctionCallWithMultipleParam() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseExpression("x(y, z)");
		assertNotNull(node);
		printNode(node);
		assertTrue(node instanceof FunctionCallNode);
		List<Expression> params = new ArrayList<Expression>();
		params.add(new IdentifierNode("y"));
		params.add(new IdentifierNode("z"));
		assertEquals(new FunctionCallNode(new IdentifierNode("x"),
				new ExpressionListNode(params)),
				node);
	}

	public void testModule() throws ParseException, NumberFormatException,
			IOException {
		Node node = parseModule("module x{}");
		assertNotNull(node);
		printNode(node);
		assertTrue(node instanceof ModuleNode);
		assertEquals(new ModuleNode("x", new FunctionListNode(
				new ArrayList<FunctionNode>())), node);
	}

	public void testModuleWithFunction() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseModule("module x{func y(){}}");
		assertNotNull(node);
		printNode(node);
		assertTrue(node instanceof ModuleNode);
		List<FunctionNode> functions = new ArrayList<FunctionNode>();
		functions.add(new FunctionNode("y", new ParameterListNode(
				new ArrayList<String>()), new StatementListNode(
				new ArrayList<Statement>())));
		assertEquals(new ModuleNode("x", new FunctionListNode(functions)), node);
	}

	public void testModuleWithFunctions() throws ParseException,
			NumberFormatException, IOException {
		Node node = parseModule("module x{func y(){}func z(){}}");
		assertNotNull(node);
		printNode(node);
		assertTrue(node instanceof ModuleNode);
		List<FunctionNode> functions = new ArrayList<FunctionNode>();
		functions.add(new FunctionNode("y", new ParameterListNode(
				new ArrayList<String>()), new StatementListNode(
				new ArrayList<Statement>())));
		functions.add(new FunctionNode("z", new ParameterListNode(
				new ArrayList<String>()), new StatementListNode(
				new ArrayList<Statement>())));
		assertEquals(new ModuleNode("x", new FunctionListNode(functions)), node);
	}
}
