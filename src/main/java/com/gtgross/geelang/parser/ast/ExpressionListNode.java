package com.gtgross.geelang.parser.ast;

import java.util.List;

import com.gtgross.geelang.parser.NodeVisitor;

public class ExpressionListNode extends AbstractNode {
	private final List<Expression> expressions;

	public ExpressionListNode(List<Expression> expressions) {
		super();
		this.expressions = expressions;
	}

	public List<Expression> getExpressions() {
		return expressions;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
