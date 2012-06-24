package com.gtgross.geelang.parser;

import java.io.PrintStream;
import java.util.ListIterator;

import com.gtgross.geelang.parser.ast.AssignExpression;
import com.gtgross.geelang.parser.ast.BinaryOperationNode;
import com.gtgross.geelang.parser.ast.CreateObjectNode;
import com.gtgross.geelang.parser.ast.Expression;
import com.gtgross.geelang.parser.ast.ExpressionListNode;
import com.gtgross.geelang.parser.ast.FloatNode;
import com.gtgross.geelang.parser.ast.FunctionCallNode;
import com.gtgross.geelang.parser.ast.FunctionListNode;
import com.gtgross.geelang.parser.ast.FunctionNode;
import com.gtgross.geelang.parser.ast.IdentifierNode;
import com.gtgross.geelang.parser.ast.IfNode;
import com.gtgross.geelang.parser.ast.IntegerNode;
import com.gtgross.geelang.parser.ast.ModuleListNode;
import com.gtgross.geelang.parser.ast.ModuleNode;
import com.gtgross.geelang.parser.ast.NullNode;
import com.gtgross.geelang.parser.ast.ObjectAccessNode;
import com.gtgross.geelang.parser.ast.ParameterListNode;
import com.gtgross.geelang.parser.ast.Statement;
import com.gtgross.geelang.parser.ast.StatementListNode;
import com.gtgross.geelang.parser.ast.UnaryOperationNode;

public class NodePrinter implements NodeVisitor {

	private final PrintStream out;

	public NodePrinter(PrintStream out) {
		this.out = out;
	}

	@Override
	public void visit(FunctionNode node) {
		out.print("Function: " + node.getName() + "(");
		node.getParams().accept(this);
		out.println(") {");
		node.getBody().accept(this);
		out.println(String.format("}"));
	}

	@Override
	public void visit(AssignExpression node) {
		out.print("Assigning ");
		if (node.getLeftHandSide() instanceof IdentifierNode) {
			out.print(((IdentifierNode) node.getLeftHandSide()).getId());
		} else {
			node.getLeftHandSide().accept(this);
		}
		out.print(" to ");
		node.getRightHandSide().accept(this);
		out.println();
	}

	@Override
	public void visit(Expression node) {
		node.accept(this);
	}

	@Override
	public void visit(BinaryOperationNode node) {
		out.print("Binary Op(");
		node.getExpr1().accept(this);
		out.print(" " + node.getOp() + " ");
		node.getExpr2().accept(this);
		out.print(")");
	}

	@Override
	public void visit(ExpressionListNode node) {
		ListIterator<Expression> i = node.getExpressions().listIterator();
		Expression expr;
		while ((expr = i.hasNext() ? i.next() : null) != null) {
			expr.accept(this);
			out.print(i.hasNext() ? ", " : "");
		}
	}

	@Override
	public void visit(FloatNode node) {
		out.print(node.getNum());
	}

	@Override
	public void visit(FunctionCallNode node) {
		out.print("Calling function ");
		node.getFunction().accept(this);
		if (node.getArguments().getExpressions().isEmpty()) {
			out.print(" with no args");
		} else {
			out.print(" with args: ");
			node.getArguments().accept(this);
		}
		out.println();
	}

	@Override
	public void visit(IdentifierNode node) {
		out.print(node.getId());
	}

	@Override
	public void visit(IntegerNode node) {
		out.print(node.getNum());
	}

	@Override
	public void visit(ModuleNode node) {
		out.println("Module: " + node.getName());
		node.getFunctions().accept(this);
		out.println();
	}

	@Override
	public void visit(ParameterListNode node) {
		ListIterator<String> i = node.getNames().listIterator();
		String param;
		while ((param = i.hasNext() ? i.next() : null) != null) {
			out.print(param + (i.hasNext() ? ", " : ""));
		}
	}

	@Override
	public void visit(StatementListNode node) {
		for (Statement stmt : node.getStatements()) {
			stmt.accept(this);
			out.println(";");
		}
	}

	@Override
	public void visit(CreateObjectNode node) {
		out.println(String.format("Creating object of type %s",
				node.getTypeName()));
	}

	@Override
	public void visit(Statement node) {
		node.accept(this);
	}

	@Override
	public void visit(UnaryOperationNode node) {
		out.print("Unary Op: ");
		node.getExpr().accept(this);
		out.println(" " + node.getOp() + " ");
		out.println();
	}

	@Override
	public void visit(FunctionListNode node) {
		for (FunctionNode func : node.getFunctionList()) {
			func.accept(this);
		}
	}

	@Override
	public void visit(ModuleListNode node) {
		for (ModuleNode module : node.getModuleList()) {
			module.accept(this);
		}
	}

	@Override
	public void visit(ObjectAccessNode node) {
		out.print("Object access:");
		node.getReceiver().accept(this);
		out.print(String.format(" accesses %s ", node.getFieldName()));

	}

	@Override
	public void visit(IfNode ifNode) {
		out.print("IF: ");
		ifNode.getConditional().accept(this);
		out.println("THEN: ");
		ifNode.getTrueBlock().accept(this);
		out.println("ELSE: ");
		if (ifNode.getFalseBlock() != null)
			ifNode.getFalseBlock().accept(this);
	}

	@Override
	public void visit(NullNode nullNode) {
		out.println("NULL");
	}

}
