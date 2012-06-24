package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;

public class IdentifierNode extends Expression {

	private final String id;

	public IdentifierNode(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
