package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;

public class ObjectAccessNode extends Expression {

	private final Expression receiver;
	private final String fieldName;

	public ObjectAccessNode(Expression receiver, String fieldName) {
		this.receiver = receiver;
		this.fieldName = fieldName;
	}

	public Expression getReceiver() {
		return receiver;
	}

	public String getFieldName() {
		return fieldName;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
