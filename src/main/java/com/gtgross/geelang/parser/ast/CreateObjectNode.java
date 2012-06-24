package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;

public class CreateObjectNode extends Expression {

	private final String typeName;

	public CreateObjectNode(String typeName) {
		super();
		this.typeName = typeName;
	}

	public String getTypeName() {
		return typeName;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
