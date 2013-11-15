package com.gtgross.geelang.parser;

import com.gtgross.geelang.parser.ast.AssignExpression;
import com.gtgross.geelang.parser.ast.BinaryOperationNode;
import com.gtgross.geelang.parser.ast.CreateObjectNode;
import com.gtgross.geelang.parser.ast.Expression;
import com.gtgross.geelang.parser.ast.ExpressionListNode;
import com.gtgross.geelang.parser.ast.FloatNode;
import com.gtgross.geelang.parser.ast.FunctionCallNode;
import com.gtgross.geelang.parser.ast.FunctionListNode;
import com.gtgross.geelang.parser.ast.FunctionNode;
import com.gtgross.geelang.parser.ast.IdentifierNode;
import com.gtgross.geelang.parser.ast.IfNode;
import com.gtgross.geelang.parser.ast.IntegerNode;
import com.gtgross.geelang.parser.ast.ProgramNode;
import com.gtgross.geelang.parser.ast.ModuleNode;
import com.gtgross.geelang.parser.ast.NullNode;
import com.gtgross.geelang.parser.ast.ObjectAccessNode;
import com.gtgross.geelang.parser.ast.ParameterListNode;
import com.gtgross.geelang.parser.ast.Statement;
import com.gtgross.geelang.parser.ast.StatementListNode;
import com.gtgross.geelang.parser.ast.UnaryOperationNode;

public interface NodeVisitor {

	void visit(FunctionNode node);

	void visit(AssignExpression node);

	void visit(BinaryOperationNode node);

	void visit(ExpressionListNode node);

	void visit(FloatNode node);

	void visit(FunctionListNode node);

	void visit(FunctionCallNode node);

	void visit(ObjectAccessNode node);

	void visit(CreateObjectNode node);

	void visit(IdentifierNode node);

	void visit(IntegerNode node);

	void visit(ModuleNode node);

	void visit(ParameterListNode node);

	void visit(StatementListNode node);

	void visit(UnaryOperationNode node);

	void visit(Statement node);

	void visit(Expression node);

	void visit(ProgramNode program);

	void visit(IfNode ifNode);

	void visit(NullNode nullNode);
}
