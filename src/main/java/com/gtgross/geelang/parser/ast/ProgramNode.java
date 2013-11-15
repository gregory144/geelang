package com.gtgross.geelang.parser.ast;

import java.util.List;

import com.gtgross.geelang.parser.NodeVisitor;

public class ProgramNode extends AbstractNode {
	private final List<ModuleNode> moduleList;
	private final List<Statement> main;

	public ProgramNode(List<ModuleNode> moduleList, List<Statement> main) {
		super();
		this.moduleList = moduleList;
		this.main = main;
	}

	public List<ModuleNode> getModuleList() {
		return moduleList;
	}

	public List<Statement> getMain() {
		return main;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
}
