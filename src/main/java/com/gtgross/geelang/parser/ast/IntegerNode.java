package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;

public class IntegerNode extends Expression {

	private final int num;

	public IntegerNode(int num) {
		this.num = num;
	}

	public int getNum() {
		return num;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
