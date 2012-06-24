package com.gtgross.geelang;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.gtgross.geelang.types.GeeObject;
import com.gtgross.geelang.types.IntegerGeeObject;
import com.gtgross.geelang.types.IntegerTypeDef;
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
		printInstructions(0, 10);
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

		TypeDef intType = new IntegerTypeDef();
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
				operand1 = popObj();
				operand2 = popObj();
				if (IntegerTypeDef.TYPE_ID == operand1.getTypeId()
						&& IntegerTypeDef.TYPE_ID == operand2.getTypeId()) {
					pushObj(((IntegerGeeObject) operand1).add(operand2));
				} else {
					throw new RuntimeException("Not an integer: " + operand1
							+ " and/or " + operand2);
				}
				break;
			case SUB:
				operand1 = popObj();
				operand2 = popObj();
				if (IntegerTypeDef.TYPE_ID == operand1.getTypeId()
						&& IntegerTypeDef.TYPE_ID == operand2.getTypeId()) {
					pushObj(((IntegerGeeObject) operand1).subtract(operand2));
				} else {
					throw new RuntimeException("Not an integer: " + operand1
							+ " and/or " + operand2);
				}
				break;
			case MULT:
				operand1 = popObj();
				operand2 = popObj();
				if (IntegerTypeDef.TYPE_ID == operand1.getTypeId()
						&& IntegerTypeDef.TYPE_ID == operand2.getTypeId()) {
					pushObj(((IntegerGeeObject) operand1).multiply(operand2));
				} else {
					throw new RuntimeException("Not an integer: " + operand1
							+ " and/or " + operand2);
				}
				break;
			case DIV:
				operand1 = popObj();
				operand2 = popObj();
				if (IntegerTypeDef.TYPE_ID == operand1.getTypeId()
						&& IntegerTypeDef.TYPE_ID == operand2.getTypeId()) {
					pushObj(((IntegerGeeObject) operand1).divide(operand2));
				} else {
					throw new RuntimeException("Not an integer: " + operand1
							+ " and/or " + operand2);
				}
				break;
			case MOD:
				operand1 = popObj();
				operand2 = popObj();
				if (IntegerTypeDef.TYPE_ID == operand1.getTypeId()
						&& IntegerTypeDef.TYPE_ID == operand2.getTypeId()) {
					pushObj(((IntegerGeeObject) operand1).modulus(operand2));
				} else {
					throw new RuntimeException("Not an integer: " + operand1
							+ " and/or " + operand2);
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
				pushObj(getRegister(registerNum));
				break;
			case PUSHZ:
				pushObj(nullObj);
				break;
			case POP:
				registerNum = getCodeByte();
				obj = popObj();
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
				obj = peekObj();
				if (FunctionGeeObject.TYPE_ID != obj.getTypeId()) {
					throw new RuntimeException("Can't call non-function object");
				}
				callStack.push(ip);
				callStack.push(sp);
				callStack.push(currObj.getId());
				callStack.push(currentType.getId());
				function = (FunctionGeeObject) popObj();

				ip = function.getAddress();
				break;
			case CALL:
				constantIndex = getCodeByte();
				callStack.push(ip);
				callStack.push(sp);
				callStack.push(currObj.getId());
				callStack.push(currentType.getId());
				currConstant = currentType.getConstant(constantIndex);
				currObj = peekObj();
				function = types.get(currObj.getTypeId()).getFunction(
						currConstant);

				ip = function.getAddress();
				currentType = types.get(currObj.getTypeId());
				break;
			case RET:
				obj = popObj();
				currentType = types.get(callStack.pop());
				currObj = pool.get(callStack.pop());
				sp = callStack.pop();
				ip = callStack.pop();
				pushObj(obj);
				break;
			case CON:
				addConstant(readConstant());
				break;
			case DEFZ:
				function = new FunctionGeeObject(ip);
				pushObj(function);
				skipToReturn();
				break;
			case DEF:
				constantIndex = getCodeByte();
				String functionName = getConstant(constantIndex);
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
				function = types.get(obj.getTypeId()).getFunction(constant);
				if (function == null) {
					fieldid = currentType.getField(constant);
					obj = obj.get(fieldid);
				} else {
					obj = function;
				}
				pushObj(obj);
				break;
			default:
				throw new RuntimeException("Invalid instruction");
			}
		}
		return popObj();
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

			case CON:
			case TYPE:
				readConstant();
				break;

			default:
				throw new RuntimeException("Invalid instruction");
			}
		}
	}

	private void printDebugInfo(int ip, byte instructionType) {
		System.out.println("Current IP: " + ip);
		System.out.println("Current SP: " + sp);
		System.out.println("Current instruction: "
				+ I.valueOf(instructionType).name());
		GeeObject obj = peekObj();
		if (obj != null) {
			System.out.println(String.format("Top of the stack: %s (type: %s)",
					obj, types.get(obj.getTypeId()).getName()));
		} else {
			System.out.println("Top of the stack is null");
		}
		printInstructions(ip, 5);
	}

	private void printInstructions(int ip, int numInstructions) {

	}

	private String readConstant() throws IOException {
		int constantLength = getCodeByte();
		byte[] constantBuffer = new byte[constantLength];
		for (int i = 0; i < constantLength; i++) {
			constantBuffer[i] = getCodeByte();
		}
		try {
			String c = new String(constantBuffer, "UTF8");
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

	private GeeObject peekObj() {
		int id = stack[sp];
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
