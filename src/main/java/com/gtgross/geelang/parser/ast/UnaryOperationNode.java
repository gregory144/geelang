package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;
import com.gtgross.geelang.parser.Operator;

public class UnaryOperationNode extends Expression {
	private final Operator op;
	private final Expression expr;

	public UnaryOperationNode(Operator op, Expression expr) {
		this.op = op;
		this.expr = expr;
	}

	public Operator getOp() {
		return op;
	}

	public Expression getExpr() {
		return expr;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
