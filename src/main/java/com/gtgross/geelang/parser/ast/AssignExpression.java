package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;

public class AssignExpression extends Expression {
	private final Expression leftHandSide;
	private final Expression rightHandSide;

	public AssignExpression(Expression leftHandSide, Expression rightHandSide) {
		super();
		this.leftHandSide = leftHandSide;
		this.rightHandSide = rightHandSide;
	}

	public Expression getLeftHandSide() {
		return leftHandSide;
	}

	public Expression getRightHandSide() {
		return rightHandSide;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
