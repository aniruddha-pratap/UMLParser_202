package UMLJavaParser;
import java.io.*;
import java.util.*;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.*;

public class SequenceDiagram {
	
	private String path[];
	private StringBuilder umlCode;
	private ArrayList<CompilationUnit> compilationUnit;
	
	public SequenceDiagram(String in, String out){
		path = new String[2];
		path[0] = in;
		path[1] = out;
	}
	
	public void sequenceDiagram() throws Exception{
		try{
			
		}catch(Exception e){
			
		}
	}
	
}
