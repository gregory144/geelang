package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;
import com.gtgross.geelang.parser.Operator;

public class ConditionalNode extends Expression {
	private final Operator op;
	private final Expression expr1;
	private final Expression expr2;

	public ConditionalNode(Operator op, Expression expr1, Expression expr2) {
		this.op = op;
		this.expr1 = expr1;
		this.expr2 = expr2;
	}

	public Operator getOp() {
		return op;
	}

	public Expression getExpr1() {
		return expr1;
	}

	public Expression getExpr2() {
		return expr2;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
