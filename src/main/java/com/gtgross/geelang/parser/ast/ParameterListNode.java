package com.gtgross.geelang.parser.ast;

import java.util.List;

import com.gtgross.geelang.parser.NodeVisitor;

public class ParameterListNode extends AbstractNode {
	private final List<String> names;

	public ParameterListNode(List<String> names) {
		this.names = names;
	}

	public List<String> getNames() {
		return names;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
