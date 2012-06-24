package com.gtgross.geelang.parser.ast;

import java.util.List;

import com.gtgross.geelang.parser.NodeVisitor;

public class StatementListNode extends AbstractNode implements Node {
	private final List<Statement> statements;

	public StatementListNode(List<Statement> statements) {
		super();
		this.statements = statements;
	}

	public List<Statement> getStatements() {
		return statements;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}

}
