package com.gtgross.geelang.parser.ast;

import org.apache.commons.lang.builder.EqualsBuilder;

public abstract class AbstractNode implements Node {

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}
