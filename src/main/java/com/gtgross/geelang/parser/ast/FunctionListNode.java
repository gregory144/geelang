package com.gtgross.geelang.parser.ast;

import java.util.List;

import com.gtgross.geelang.parser.NodeVisitor;

public class FunctionListNode extends AbstractNode {
	private final List<FunctionNode> functionList;

	public FunctionListNode(List<FunctionNode> functionList) {
		this.functionList = functionList;
	}

	public List<FunctionNode> getFunctionList() {
		return functionList;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
}
