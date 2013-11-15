package com.gtgross.geelang;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;

public class CodeGen {

	public static void main(String[] args) throws Exception {
		// // public HelloWorld extends java.lang.Object
		// ClassGen classGen = new ClassGen("HelloWorld", "java.lang.Object",
		// "<generated>",
		// Constants.ACC_PUBLIC | Constants.ACC_SUPER, null);
		// ConstantPoolGen constantPoolGen = classGen.getConstantPool();
		// InstructionList instructionList = new InstructionList();
		//
		// MethodGen methodGen = new MethodGen(Constants.ACC_STATIC |
		// Constants.ACC_PUBLIC,
		// Type.VOID, new Type[] {
		// new ArrayType(Type.STRING, 1)
		// }, new String[] { "argv" },
		// "main", "HelloWorld", instructionList, constantPoolGen);
		//
		// InstructionFactory instructionFactory = new
		// InstructionFactory(classGen);
		// classGen.addEmptyConstructor(Constants.ACC_PUBLIC);
		//
		// // System.out.println
		// ObjectType printStream = new ObjectType("java.io.PrintStream");
		// instructionList.append(instructionFactory.createFieldAccess(
		// "java.lang.System", "out", printStream, Constants.GETSTATIC));
		//
		// // "Hello world!!"
		// instructionList.append(new PUSH(constantPoolGen, "Hello World!!"));
		// instructionList.append(instructionFactory.createInvoke(
		// "java.io.PrintStream", "println", Type.VOID,
		// new Type[] { Type.STRING }, Constants.INVOKEVIRTUAL));
		// methodGen.setMaxStack();
		//
		// classGen.addMethod(methodGen.getMethod());
		// instructionList.dispose();
		//
		// classGen.getJavaClass().dump("bin/classes/HelloWorld.class");
		OutputStream os = new FileOutputStream(new File(
				"bin/classes/HelloWorld.class"));
		os.write(bcelHelloWorld());
	}

	static Type printStreamT = Type.getType("Ljava/io/PrintStream;");

	static byte[] bcelHelloWorld() {
		ClassGen cg = new ClassGen("HelloWorld", "java/lang/Object",
				"<generated>", Constants.ACC_PUBLIC, null);

		cg.addEmptyConstructor(Constants.ACC_PUBLIC);

		ConstantPoolGen cp = cg.getConstantPool();
		InstructionList il = new InstructionList();
		InstructionFactory factory = new InstructionFactory(cg);

		MethodGen mg = new MethodGen(Constants.ACC_STATIC | Constants.ACC_PUBLIC,
                Type.VOID, new Type[] { 
                  new ArrayType(Type.STRING, 1) 
                }, new String[] { "argv" },
                "main", "HelloWorld", il, cp);
		il.append(factory.createGetStatic("java/lang/System", "out",
				printStreamT));
		il.append(new PUSH(cp, "Hello world!"));
		il.append(factory.createInvoke("java.io.PrintStream", "println",
				Type.VOID, new Type[] { Type.STRING }, Constants.INVOKESPECIAL));

		mg.setMaxStack();
		cg.addMethod(mg.getMethod());

		return cg.getJavaClass().getBytes();
	}
}
