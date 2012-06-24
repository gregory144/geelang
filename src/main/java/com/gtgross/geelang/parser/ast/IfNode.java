package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;

public class IfNode extends Expression {

	private final Expression conditional;
	private final StatementListNode trueBlock;
	private final StatementListNode falseBlock;

	public IfNode(Expression conditional, StatementListNode trueBlock,
			StatementListNode falseBlock) {
		super();
		this.conditional = conditional;
		this.trueBlock = trueBlock;
		this.falseBlock = falseBlock;
	}

	public Expression getConditional() {
		return conditional;
	}

	public StatementListNode getTrueBlock() {
		return trueBlock;
	}

	public StatementListNode getFalseBlock() {
		return falseBlock;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
