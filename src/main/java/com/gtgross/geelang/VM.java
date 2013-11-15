package com.gtgross.geelang;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.gtgross.geelang.types.FunctionGeeObject;
import com.gtgross.geelang.types.GeeObject;
import com.gtgross.geelang.types.IntegerGeeObject;
import com.gtgross.geelang.types.NativeFunctionGeeObject;
import com.gtgross.geelang.types.NullGeeObject;
import com.gtgross.geelang.types.TypeDef;

public class VM {

	private static final int STACK_SIZE = 1024;
	private static final int NUM_REGISTERS = 32;

	private final Map<String, TypeDef> typesByName = new HashMap<>();
	private final Map<Integer, TypeDef> types = new HashMap<>();
	private final Map<Integer, GeeObject> pool = new HashMap<>();
	private final Stack<Integer> callStack = new Stack<>();

	private final GeeObject[] registers = new GeeObject[NUM_REGISTERS];
	private final int[] stack = new int[STACK_SIZE];
	private final GeeObject nullObj = NullGeeObject.getInstance();
	private final ByteBuffer codeBuffer;

	private int ip = 0;
	private int sp = 0;
	private TypeDef currentType;

	public VM(byte[] code) {
		this.codeBuffer = ByteBuffer.wrap(code);
	}

	private int getCodeInt() throws IOException {
		int value = codeBuffer.getInt(ip);
		ip += 4;
		return value;
	}

	private byte getCodeByte(int increment) throws IOException {
		int value = codeBuffer.get(ip);
		ip += increment;
		return (byte) value;
	}

	private byte getCodeByte() throws IOException {
		return getCodeByte(1);
	}

	private void bootstrap() {
		TypeDef nullType = new TypeDef(NullGeeObject.TYPE_ID, "Null");
		typesByName.put(nullType.getName(), nullType);
		types.put(nullType.getId(), nullType);

		putInPool(nullObj);
		currentType = nullType;
		System.out.println("BOOTSTRAP: Assigning current type: "
				+ currentType.getName());

		TypeDef intType = new TypeDef(IntegerGeeObject.TYPE_ID, "Int");
		intType.addFunction(new NativeFunctionGeeObject("add", 1));
		intType.aliasFunction("add", "+");
		intType.addFunction(new NativeFunctionGeeObject("subtract", 1));
		intType.aliasFunction("subtract", "-");
		intType.addFunction(new NativeFunctionGeeObject("multiply", 1));
		intType.aliasFunction("multiply", "*");
		intType.addFunction(new NativeFunctionGeeObject("divide", 1));
		intType.aliasFunction("divide", "/");
		intType.addFunction(new NativeFunctionGeeObject("modulus", 1));
		intType.aliasFunction("modulus", "%");
		intType.addFunction(new NativeFunctionGeeObject("eq", 1));
		intType.aliasFunction("eq", "==");
		intType.addFunction(new NativeFunctionGeeObject("ne", 1));
		intType.aliasFunction("ne", "!=");
		intType.addFunction(new NativeFunctionGeeObject("lt", 1));
		intType.aliasFunction("lt", "<");
		intType.addFunction(new NativeFunctionGeeObject("lte", 1));
		intType.aliasFunction("lte", "<=");
		intType.addFunction(new NativeFunctionGeeObject("gt", 1));
		intType.aliasFunction("gt", ">");
		intType.addFunction(new NativeFunctionGeeObject("gte", 1));
		intType.aliasFunction("gte", ">=");
		typesByName.put(intType.getName(), intType);
		types.put(intType.getId(), intType);

		putInPool(IntegerGeeObject.getInstance(0));
		putInPool(IntegerGeeObject.getInstance(1));

		TypeDef functionType = new TypeDef(FunctionGeeObject.TYPE_ID,
				"Function");
		typesByName.put(functionType.getName(), functionType);
		types.put(functionType.getId(), functionType);
	}

	private void putInPool(GeeObject obj) {
		pool.put(obj.getId(), obj);
	}

	public GeeObject run() throws IOException {
		bootstrap();
		GeeObject operand1, operand2;
		int constantValue;
		int registerNum;
		int offset;
		int fieldid;
		int constantIndex;
		String currConstant;
		GeeObject obj;
		GeeObject currObj = nullObj;
		FunctionGeeObject function;
		boolean running = true;
		while (running) {
			byte instructionType = getCodeByte();
			printDebugInfo(ip, instructionType);

			switch (I.valueOf(instructionType)) {
			case HALT:
				running = false;
				break;
			case ADD:
				currObj = popObj();
				obj = getField(currObj, "+");
				if (obj instanceof FunctionGeeObject) {
					callFunction((FunctionGeeObject) obj, currObj, 1);
				} else {
					throw new RuntimeException("could not call add: " + currObj);
				}
				break;
			case SUB:
				currObj = popObj();
				obj = getField(currObj, "-");
				if (obj instanceof FunctionGeeObject) {
					callFunction((FunctionGeeObject) obj, currObj, 1);
				} else {
					throw new RuntimeException("could not call subtract: "
							+ currObj);
				}
				break;
			case MULT:
				currObj = popObj();
				obj = getField(currObj, "*");
				if (obj instanceof FunctionGeeObject) {
					callFunction((FunctionGeeObject) obj, currObj, 1);
				} else {
					throw new RuntimeException("could not call multiply: "
							+ currObj);
				}
				break;
			case DIV:
				currObj = popObj();
				obj = getField(currObj, "/");
				if (obj instanceof FunctionGeeObject) {
					callFunction((FunctionGeeObject) obj, currObj, 1);
				} else {
					throw new RuntimeException("could not call divide: "
							+ currObj);
				}
				break;
			case MOD:
				currObj = popObj();
				obj = getField(currObj, "%");
				if (obj instanceof FunctionGeeObject) {
					callFunction((FunctionGeeObject) obj, currObj, 1);
				} else {
					throw new RuntimeException("could not call modulus: "
							+ currObj);
				}
				break;
			case PUSHI:
				constantValue = getCodeInt();
				obj = IntegerGeeObject.getInstance(constantValue);
				pool.put(obj.getId(), obj);
				pushObj(obj);
				break;
			case PUSH:
				registerNum = getCodeByte();
				System.out.println("Pushing from reg: " + registerNum);
				pushObj(getRegister(registerNum));
				break;
			case PUSHZ:
				pushObj(nullObj);
				break;
			case POP:
				registerNum = getCodeByte();
				System.out.println("Popping to reg: " + registerNum);
				obj = popObj();
				setRegister(registerNum, obj);
				break;
			case PEEK:
				offset = getCodeInt();
				registerNum = getCodeByte();
				System.out.println("Popping to reg: " + registerNum);
				obj = peekObj(offset);
				setRegister(registerNum, obj);
				break;
			case POPZ:
				popObj();
				break;
			case EQ:
				operand2 = popObj();
				operand1 = popObj();
				pushObj(((IntegerGeeObject) operand1).getValue() == ((IntegerGeeObject) operand2)
						.getValue() ? IntegerGeeObject.getInstance(1) : nullObj);
				break;
			case LT:
				operand2 = popObj();
				operand1 = popObj();
				pushObj(((IntegerGeeObject) operand1).getValue() < ((IntegerGeeObject) operand2)
						.getValue() ? IntegerGeeObject.getInstance(1) : nullObj);
				break;
			case LTE:
				operand2 = popObj();
				operand1 = popObj();
				pushObj(((IntegerGeeObject) operand1).getValue() <= ((IntegerGeeObject) operand2)
						.getValue() ? IntegerGeeObject.getInstance(1) : nullObj);
				break;
			case GT:
				operand2 = popObj();
				operand1 = popObj();
				pushObj(((IntegerGeeObject) operand1).getValue() > ((IntegerGeeObject) operand2)
						.getValue() ? IntegerGeeObject.getInstance(1) : nullObj);
				break;
			case GTE:
				operand2 = popObj();
				operand1 = popObj();
				pushObj(((IntegerGeeObject) operand1).getValue() >= ((IntegerGeeObject) operand2)
						.getValue() ? IntegerGeeObject.getInstance(1) : nullObj);
				break;
			case CALLZ:
				obj = popObj();
				assert FunctionGeeObject.TYPE_ID == obj.getTypeId() : "Can't call non-function object";
				function = (FunctionGeeObject) obj;

				// get the callee
				currObj = popObj();
				assert types.containsKey(currObj.getTypeId()) : "Could not find type: "
						+ currObj.getTypeId();

				// get the number of arguments
				obj = popObj();
				assert IntegerGeeObject.TYPE_ID == obj.getTypeId() : "Unknown number of arguments for function call "
						+ types.get(obj.getTypeId()).getName();

				callFunction(function, currObj,
						((IntegerGeeObject) obj).getValue());
				break;
			case CALL:
				constantIndex = getCodeByte();
				currConstant = currentType.getConstant(constantIndex);
				assert currConstant != null : "Could not find constant "
						+ constantIndex + " for type " + currentType.getName();

				// get the callee
				currObj = popObj();
				assert types.containsKey(currObj.getTypeId()) : "Could not find type: "
						+ currObj.getTypeId();

				// get the number of arguments
				obj = popObj();
				assert IntegerGeeObject.TYPE_ID == obj.getTypeId() : "Unknown number of arguments for function call "
						+ types.get(obj.getTypeId()).getName();

				// get the function to call
				function = types.get(currObj.getTypeId()).getFunction(
						currConstant);
				assert function != null : "Could not find function: "
						+ currConstant + " for type "
						+ types.get(currObj.getTypeId()).getName();

				callFunction(function, currObj,
						((IntegerGeeObject) obj).getValue());
				break;
			case RET:
				obj = popObj();
				currentType = types.get(callStack.pop());
				System.out.println("RET: Assigning current type: "
						+ currentType.getName());
				currObj = pool.get(callStack.pop());
				sp = callStack.pop();
				ip = callStack.pop();
				pushObj(obj);
				break;
			case CON:
				addConstant(readConstant());
				break;
			case COMM:
				readConstant();
				break;
			case DEFZ:
				function = new FunctionGeeObject(ip);
				pushObj(function);
				skipToReturn();
				break;
			case DEF:
				constantIndex = getCodeByte();
				String functionName = getConstant(constantIndex);
				assert functionName != null : "Constant not found "
						+ constantIndex;
				currentType.addFunction(functionName, ip);
				putInPool(currentType.getFunction(functionName));
				skipToReturn();
				break;
			case TYPE:
				createType(readConstant());
				break;
			case GO:
				offset = getCodeByte(0);
				ip++;
				ip += offset;
				break;
			case IF:
				offset = getCodeByte(0);
				ip++;
				operand1 = popObj();
				if (operand1 != nullObj) {
					ip += offset;
				}
				break;
			case NEW:
				constantIndex = getCodeByte();
				currConstant = getConstant(constantIndex);
				System.out.println("Creating new object of type: "
						+ currConstant + " (" + constantIndex + ")");
				obj = createObject(typesByName.get(currConstant).getId());
				pushObj(obj);
				break;
			case PUT:
				constantIndex = getCodeByte();
				fieldid = currentType.getField(getConstant(constantIndex));
				obj = popObj();
				operand1 = popObj();
				obj.put(fieldid, operand1);
				break;
			case GET:
				constantIndex = getCodeByte();
				obj = popObj();
				String constant = getConstant(constantIndex);
				System.out.println("Getting constant: " + constant);
				pushObj(getField(obj, constant));
				break;
			default:
				throw new RuntimeException("Invalid instruction");
			}
		}
		return popObj();
	}

	private GeeObject getField(GeeObject obj, String constant) {
		FunctionGeeObject function = types.get(obj.getTypeId()).getFunction(
				constant);
		if (function == null) {
			int fieldid = currentType.getField(constant);
			obj = obj.get(fieldid);
		} else {
			obj = function;
		}
		return obj;
	}

	private void callFunction(FunctionGeeObject function, GeeObject currObj,
			int numArgs) {
		assert function != null : "Could not find function";
		if (function instanceof NativeFunctionGeeObject) {
			NativeFunctionGeeObject nativeFunction = (NativeFunctionGeeObject) function;
			assert nativeFunction.getNumParams() == numArgs : "Wrong number of arguments: "
					+ numArgs + ", expected: " + nativeFunction.getNumParams();
			GeeObject[] params = new GeeObject[numArgs]; // num args
			for (int i = 0; i < nativeFunction.getNumParams(); i++) {
				params[i] = popObj();
			}
			GeeObject retValue = nativeFunction.call(currObj, params);
			pushObj(retValue == null ? nullObj : retValue);
		} else {
			callStack.push(ip);
			callStack.push(sp);
			callStack.push(currObj.getId());
			callStack.push(currentType.getId());

			ip = function.getAddress();
			currentType = types.get(currObj.getTypeId());
			System.out.println("CALL: Assigning current type: "
					+ currentType.getName());
		}
	}

	public void skipToReturn() throws IOException {
		int defs = 1;
		while (defs > 0) {
			byte instructionType = getCodeByte();
			System.out.println(String.format("Skipping %s",
					I.valueOf(instructionType).toString()));
			switch (I.valueOf(instructionType)) {
			case HALT:
			case ADD:
			case SUB:
			case MULT:
			case DIV:
			case MOD:
			case POPZ:
			case EQ:
			case LT:
			case LTE:
			case GT:
			case GTE:
			case CALLZ:
			case PUSHZ:
				break;
			case DEFZ:
				defs++;
				break;
			case RET:
				defs--;
				break;

			case PUSH:
			case POP:
			case GO:
			case IF:
			case NEW:
			case PUT:
			case GET:
			case CALL:
				ip++;
				break;

			case DEF:
				defs++;
				ip++;
				break;

			case PUSHI:
				ip += 4;
				break;

			case PEEK:
				ip += 5;
				break;

			case CON:
			case TYPE:
				addConstant(readConstant());
				break;
			case COMM:
				readConstant();
				break;

			default:
				throw new RuntimeException("Invalid instruction");
			}
		}
	}

	private void printDebugInfo(int ip, byte instructionType) {
		System.out.println("------------------------------------------");
		System.out.println("Current IP: " + ip);
		System.out.println("Current SP: " + sp);
		System.out.println("Current instruction: "
				+ I.valueOf(instructionType).name());
		System.out.println("Current type: " + currentType.getName());

		if (sp == 0) {
			System.out.println("Stack is empty");
		} else {
			for (int i = 0; i < 8; i++) {
				if (sp - i > 0) {
					GeeObject obj = peekObj(i);
					assert obj != null : "Object on stack was null: " + sp
							+ ", " + i;
					System.out.println(String.format(
							"Stack[%d]: %s (type: %s)", sp - i - 1, obj, types
									.get(obj.getTypeId()).getName()));
				}
			}
		}
	}

	private String readConstant() throws IOException {
		int constantLength = getCodeByte();
		byte[] constantBuffer = new byte[constantLength];
		for (int i = 0; i < constantLength; i++) {
			constantBuffer[i] = getCodeByte();
		}
		try {
			String c = new String(constantBuffer, "UTF8");
			System.out.println("Read constant " + c);
			return c;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	private String getConstant(int constantIndex) {
		return currentType.getConstant(constantIndex);
	}

	private void addConstant(String constant) {
		currentType.addConstant(constant);
	}

	private void createType(String typeName) {
		TypeDef type = typesByName.get(typeName);
		if (type == null) {
			type = new TypeDef(typeName);
			types.put(type.getId(), type);
			typesByName.put(typeName, type);
		}
		currentType = type;
		System.out.println("CREATE TYPE: Assigning current type: "
				+ currentType.getName());
		addConstant(typeName);
	}

	private GeeObject createObject(int typeId) {
		GeeObject obj = new GeeObject(typeId);
		pool.put(obj.getId(), obj);
		return obj;
	}

	private GeeObject popObj() {
		int id = stack[sp];
		sp--;
		return pool.get(id);
	}

	private GeeObject peekObj(int offset) {
		int id = stack[sp - offset];
		return pool.get(id);
	}

	private void pushObj(GeeObject value) {
		putInPool(value);
		stack[++sp] = value.getId();
	}

	private GeeObject getRegister(int num) {
		return registers[num];
	}

	private void setRegister(int num, GeeObject value) {
		registers[num] = value;
	}
}
