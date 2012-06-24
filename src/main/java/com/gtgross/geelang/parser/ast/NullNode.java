package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;

public class NullNode extends Expression {

	public NullNode() {
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
