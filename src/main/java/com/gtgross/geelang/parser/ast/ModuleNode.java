package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;

public class ModuleNode extends AbstractNode {
	private final String name;
	private final FunctionListNode functions;

	public ModuleNode(String name, FunctionListNode functions) {
		super();
		this.name = name;
		this.functions = functions;
	}
	public String getName() {
		return name;
	}

	public FunctionListNode getFunctions() {
		return functions;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
