package com.gtgross.geelang.parser.ast;

import com.gtgross.geelang.parser.NodeVisitor;

public interface Node {
	void accept(NodeVisitor visitor);
}
