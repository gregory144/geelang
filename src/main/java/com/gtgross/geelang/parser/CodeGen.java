package com.gtgross.geelang.parser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
import com.gtgross.geelang.parser.ast.ProgramNode;
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
			/*Symbol mainModule = symbols.getSymbol("Main");
			Symbol mainFunction = symbols.getSymbol("main");
			if (mainModule != null && mainFunction != null) {
				code.type("GeelangBootstrap");
				code.constant("Main");
				code.constant("main");
				code.pushi(0).create(1).call(2);
			}*/
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
			code.comment("CodeGen: FunctionNode: " + node.getName());
			int constantIndex = getConstant(node.getName());
			code.def(constantIndex);
			symbols.addSymbol("this", scopeLevel);
			node.getParams().accept(this);
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
				code.peek(0, getRegister(lhs.getId()));
			} else if (node.getLeftHandSide() instanceof ObjectAccessNode) {
				ObjectAccessNode lhs = (ObjectAccessNode) node
						.getLeftHandSide();
				lhs.getReceiver().accept(this);
				int constantIndex = getConstant(lhs.getFieldName());
				int obj = registers.pop();
				int field = registers.pop();
				code.pop(obj);
				code.pop(field);
				code.push(field);
				code.push(obj);
				code.put(constantIndex);
				code.push(field);
				registers.push(obj);
				registers.push(field);
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
			// TODO where to free this register again?
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
		try {
			code.comment("Binary Operation: " + node.getOp());
			node.getExpr2().accept(this);
			code.pushi(1);
			node.getExpr1().accept(this);
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
				code.call(getConstant("!="));
				break;
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
		for (Expression exp : node.getExpressions()) {
			exp.accept(this);
		}
	}

	@Override
	public void visit(FloatNode node) {
		throw new NotImplementedException();
	}

	/**
	 * Calling convention:
	 * 
	 * Caller: the function doing the calling Callee: the function getting
	 * called
	 * 
	 * 1. Calling a function from an ID Caller should place on the stack:
	 * 
	 * TOP OF STACK Callee Object Number of arguments Argument1 Argument2
	 * Argument3 ... BOTTOM OF STACK
	 * 
	 * 2. Calling a function with dot notation
	 * 
	 * TOP OF STACK Function Object Callee Object Number of arguments Argument1
	 * Argument2 Argument3 ... BOTTOM OF STACK
	 * 
	 * The returned value will be placed on the top of the stack.
	 * 
	 * At the end of the callee's execution, the stack pointer may have changed.
	 * 
	 * The caller is responsible for cleaning the parameters off the stack when
	 * it's finished.
	 */
	@Override
	public void visit(FunctionCallNode node) {
		try {
			int reg = registers.pop();
			code.comment("Saving function call callee to register: " + reg);
			code.peek(0, reg);
			node.getArguments().accept(this);
			code.comment("Generating call with "
					+ node.getArguments().getExpressions().size() + " params.");
			// save the number of arguments
			code.pushi(node.getArguments().getExpressions().size());
			// System.out.println("Restoring from register: " + reg);
			// code.push(reg);
			// registers.push(reg);
			if (node.getFunction() instanceof IdentifierNode) {
				IdentifierNode function = (IdentifierNode) node.getFunction();
				code.comment("CodeGen: Calling Function: " + function.getId());
				Symbol sym = symbols.getSymbol(function.getId());
				assert sym.isActive() : String.format(
						"Symbol %s is not in scope", function.getId());
				int constantIndex = constants.get(function.getId());
				call(constantIndex);
			} else if (node.getFunction() instanceof ObjectAccessNode) {
				// push the target object back on the stack
				code.push(reg);
				registers.push(reg);
				node.getFunction().accept(this);
				code.comment("CodeGen: Calling Anonymous Function");
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
		// List<Integer> used = symbols.usedRegisters();
		// for (int i = 0; i < used.size(); i++) {
		// code.push(used.get(i));
		// }

		if (index != -1) {
			code.call(index);
		} else {
			code.callz();
		}

		// restore registers
		// for (int i = used.size() - 1; i >= 0; i--) {
		// code.push(used.get(i));
		// }
	}

	@Override
	public void visit(IdentifierNode node) {
		try {
			Symbol sym = symbols.getSymbol(node.getId());
			if (sym != null && sym.isActive()) {
				code.comment("Getting value of " + node.getId()
						+ " from register " + sym.getRegister());
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
			for (String key : constants.keySet()) {
				code.comment(node.getName() + ", " + key + " = "
						+ constants.get(key));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		symbols.deactivateScope(scopeLevel);
		scopeLevel--;
	}

	@Override
	public void visit(ParameterListNode node) {
		try {
			for (int i = 0; i < node.getNames().size(); i++) {
				String name = node.getNames().get(i);
				symbols.addSymbol(name, scopeLevel);
				int register = getRegister(name);
				code.comment("Saving " + name + " to register " + register);
				code.pop(register);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(StatementListNode node) {
		for (Statement stmt : node.getStatements()) {
			stmt.accept(this);
		}
	}

	@Override
	public void visit(ProgramNode node) {
		for (ModuleNode module : node.getModuleList()) {
			module.accept(this);
		}
		for (Statement stmt : node.getMain()) {
			stmt.accept(this);
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
			code.comment("Creating object of type: " + node.getTypeName());
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
