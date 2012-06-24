package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;

public class FloatNode extends Expression {

	private final double num;

	public FloatNode(double num) {
		this.num = num;
	}

	public double getNum() {
		return num;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
