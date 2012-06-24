package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;

public class FunctionCallNode extends Expression {

	private final Expression function;
	private final ExpressionListNode arguments;

	public FunctionCallNode(Expression function, ExpressionListNode arguments) {
		super();
		this.function = function;
		this.arguments = arguments;
	}

	public Expression getFunction() {
		return function;
	}

	public ExpressionListNode getArguments() {
		return arguments;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
