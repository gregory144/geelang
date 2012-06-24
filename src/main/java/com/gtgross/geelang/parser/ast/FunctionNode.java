package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;

public class FunctionNode extends AbstractNode {

	private final String name;
	private final ParameterListNode params;
	private final StatementListNode body;

	public FunctionNode(String name, ParameterListNode params,
			StatementListNode body) {
		super();
		this.name = name;
		this.params = params;
		this.body = body;
	}

	public String getName() {
		return name;
	}

	public ParameterListNode getParams() {
		return params;
	}

	public StatementListNode getBody() {
		return body;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
