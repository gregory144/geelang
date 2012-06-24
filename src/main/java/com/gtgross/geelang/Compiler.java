package com.gtgross.geelang;

import java.io.IOException;

import com.gtgross.geelang.parser.CodeGen;
import com.gtgross.geelang.parser.GeelangParser;
import com.gtgross.geelang.parser.ParseException;
import com.gtgross.geelang.parser.ast.Node;

public class Compiler {

	public static void main(String[] args) {
		GeelangParser c = new GeelangParser(System.in);
		try {
			Node node = c.Program();
			CodeGen gen = new CodeGen();
			node.accept(gen);
			VM vm = new VM(gen.generate());
			System.out.println(vm.run());
		} catch (NumberFormatException | ParseException | IOException e) {
			e.printStackTrace();
		}
	}

}
