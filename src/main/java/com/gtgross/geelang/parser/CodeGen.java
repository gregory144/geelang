package com.gtgross.geelang.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.gtgross.geelang.ByteCodeEmitter;
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
import com.gtgross.geelang.parser.ast.ModuleListNode;
import com.gtgross.geelang.parser.ast.ModuleNode;
import com.gtgross.geelang.parser.ast.NullNode;
import com.gtgross.geelang.parser.ast.ObjectAccessNode;
import com.gtgross.geelang.parser.ast.ParameterListNode;
import com.gtgross.geelang.parser.ast.Statement;
import com.gtgross.geelang.parser.ast.StatementListNode;
import com.gtgross.geelang.parser.ast.UnaryOperationNode;

public class CodeGen implements NodeVisitor {

	private final ByteCodeEmitter code = new ByteCodeEmitter();
	private final SymbolTable symbols = new SymbolTable();
	private int scopeLevel = 0;
	Map<String, Integer> constants = new HashMap<>();
	Stack<Integer> registers = new Stack<>();

	public CodeGen() {
		for (int i = 255; i >= 0; i--) {
			registers.push(i);
		}
	}

	public byte[] generate() {
		try {
			Symbol mainModule = symbols.getSymbol("Main");
			Symbol mainFunction = symbols.getSymbol("Main");
			if (mainModule != null && mainFunction != null) {
				code.type("GeelangBootstrap");
				code.constant("Main");
				code.constant("main");
				code.create(1).call(2);
			}
			code.halt();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return code.generate();
	}

	@Override
	public void visit(FunctionNode node) {
		Symbol func = symbols.addSymbol(node.getName(), scopeLevel);
		scopeLevel++;
		try {
			int constantIndex = getConstant(node.getName());
			code.def(constantIndex);
			// TODO params?
			node.getBody().accept(this);
			code.ret();
		} catch (IOException e) {
			e.printStackTrace();
		}
		symbols.deactivateScope(scopeLevel);
		scopeLevel--;
	}

	@Override
	public void visit(AssignExpression node) {
		try {
			node.getRightHandSide().accept(this);
			if (node.getLeftHandSide() instanceof IdentifierNode) {
				IdentifierNode lhs = (IdentifierNode) node.getLeftHandSide();
				symbols.addSymbol(lhs.getId(), scopeLevel);
				int register = getRegister(lhs.getId());
				code.pop(register);
				code.push(register);
			} else if (node.getLeftHandSide() instanceof ObjectAccessNode) {
				ObjectAccessNode lhs = (ObjectAccessNode) node
						.getLeftHandSide();
				lhs.getReceiver().accept(this);
				int constantIndex = getConstant(lhs.getFieldName());
				code.put(constantIndex);
			} else {
				throw new RuntimeException(
						"Unknown left hand side in assignment statement");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int getRegister(String varName) {
		Symbol sym = symbols.getSymbol(varName);
		if (!sym.hasRegister()) {
			sym.setRegister(registers.pop());
		}
		return sym.getRegister();
	}

	@Override
	public void visit(Expression node) {
		node.accept(this);
	}

	@Override
	public void visit(BinaryOperationNode node) {
		node.getExpr1().accept(this);
		node.getExpr2().accept(this);
		try {
			switch (node.getOp()) {
			case ADD:
				code.call(getConstant("+"));
				break;
			case MINUS:
				code.call(getConstant("-"));
				break;
			case MULTIPLY:
				code.call(getConstant("*"));
				break;
			case DIVIDE:
				code.call(getConstant("/"));
				break;
			case MODULUS:
				code.call(getConstant("%"));
				break;

			case EQ:
				code.call(getConstant("=="));
				break;
			case NE:
				throw new RuntimeException("Not implemented yet!");
				// break;
			case LT:
				code.call(getConstant("<"));
				break;
			case LTE:
				code.call(getConstant("<="));
				break;
			case GT:
				code.call(getConstant(">"));
				break;
			case GTE:
				code.call(getConstant(">="));
				break;
			default:
				throw new RuntimeException("Unknown Operator");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(ExpressionListNode node) {

	}

	@Override
	public void visit(FloatNode node) {

	}

	@Override
	public void visit(FunctionCallNode node) {
		try {
			if (node.getFunction() instanceof IdentifierNode) {
				IdentifierNode function = (IdentifierNode) node.getFunction();
				Symbol sym = symbols.getSymbol(function.getId());
				if (sym.isActive()) {
					System.out
							.println("generating call to " + function.getId());
					int constantIndex = constants.get(function.getId());
					call(constantIndex);
				} else {
					throw new RuntimeException(String.format(
							"Symbol %s is not in scope", function.getId()));
				}
			} else if (node.getFunction() instanceof ObjectAccessNode) {
				System.out.println("Calling!");
				node.getFunction().accept(this);
				call(-1);
			} else {
				throw new RuntimeException(
						"Unable to call function of non-object");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void call(int index) throws IOException {
			// save registers
			List<Integer> used = symbols.usedRegisters();
			for (int i = 0; i < used.size(); i++) {
				code.push(used.get(i));
			}
			if (index != -1) {
				code.call(index);
			} else {
				code.callz();
			}
			// save registers
			for (int i = used.size() - 1; i >= 0; i--) {
				code.push(used.get(i));
			}
	}

	@Override
	public void visit(IdentifierNode node) {
		try {
			Symbol sym = symbols.getSymbol(node.getId());
			if (sym.isActive()) {
				code.push(sym.getRegister());
			} else {
				throw new RuntimeException(String.format(
						"Symbol %s is not in scope", node.getId()));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(IntegerNode node) {
		try {
			code.pushi(node.getNum());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(ModuleNode node) {
		Symbol mod = symbols.addSymbol(node.getName(), scopeLevel);
		scopeLevel++;
		constants.clear();
		try {
			constants.put(node.getName(), constants.size());
			code.type(node.getName());
			node.getFunctions().accept(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (String key : constants.keySet()) {
			System.out.println(node.getName() + ", " + key + " = "
					+ constants.get(key));
		}
		symbols.deactivateScope(scopeLevel);
		scopeLevel--;
	}

	@Override
	public void visit(ParameterListNode node) {
	}

	@Override
	public void visit(StatementListNode node) {
		for (Statement stmt : node.getStatements()) {
			stmt.accept(this);
		}
	}

	@Override
	public void visit(ModuleListNode node) {
		for (ModuleNode module : node.getModuleList()) {
			module.accept(this);
		}
	}

	@Override
	public void visit(Statement node) {
		node.accept(this);
	}

	@Override
	public void visit(UnaryOperationNode node) {
		node.getExpr().accept(this);
		try {
			switch (node.getOp()) {
			case MINUS:
				code.pushi(-1);
				code.multiply();
				break;
			case NEGATE:
				throw new RuntimeException("Not implemented yet");
				// break;
			default:
				throw new RuntimeException("Unknown unary operator");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(FunctionListNode node) {
		for (FunctionNode func : node.getFunctionList()) {
			func.accept(this);
		}
	}

	@Override
	public void visit(ObjectAccessNode node) {
		node.getReceiver().accept(this);
		try {
			code.get(getConstant(node.getFieldName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(CreateObjectNode node) {
		try {
			System.out
					.println("Creating object of type: " + node.getTypeName());
			code.create(getConstant(node.getTypeName()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int getConstant(String name) throws IOException {
		int constantIndex;
		if (!constants.containsKey(name)) {
			constantIndex = constants.size();
			constants.put(name, constantIndex);
			code.constant(name);
		} else {
			constantIndex = constants.get(name);
		}
		return constantIndex;
	}

	@Override
	public void visit(IfNode ifNode) {
		ifNode.getConditional().accept(this);
		try {
			byte[] falseBlockCode = new byte[0];
			if (ifNode.getFalseBlock() != null) {
				CodeGen falseBlockGen = new CodeGen();
				ifNode.getFalseBlock().accept(falseBlockGen);
				falseBlockCode = falseBlockGen.generate();
			}
			CodeGen trueBlockGen = new CodeGen();
			ifNode.getTrueBlock().accept(trueBlockGen);
			byte[] trueBlockCode = trueBlockGen.generate();
			code.iff(falseBlockCode.length + 2);
			code.append(falseBlockCode);
			code.go(trueBlockCode.length);
			code.append(trueBlockCode);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(NullNode nullNode) {
		try {
			code.pushz();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
