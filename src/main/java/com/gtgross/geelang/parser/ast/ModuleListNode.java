package com.gtgross.geelang.parser.ast;

import java.util.List;

import com.gtgross.geelang.parser.NodeVisitor;

public class ModuleListNode extends AbstractNode {
	private final List<ModuleNode> moduleList;

	public ModuleListNode(List<ModuleNode> moduleList) {
		super();
		this.moduleList = moduleList;
	}

	public List<ModuleNode> getModuleList() {
		return moduleList;
	}

	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
}
