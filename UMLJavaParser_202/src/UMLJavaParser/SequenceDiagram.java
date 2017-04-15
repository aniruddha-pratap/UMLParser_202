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
	private FileInputStream inputStream;
	private ArrayList<CompilationUnit> compilationUnitArray;
	
	public SequenceDiagram(String in, String out){
		path = new String[2];
		path[0] = in;
		path[1] = out;
	}
	
	public void sequenceDiagram() throws Exception{
		try{
			compilationUnitArray = new ArrayList<CompilationUnit>();
			File files = new File(path[0]);
			for(File file: files.listFiles()){
				if(file.isFile()){
					inputStream = new FileInputStream(file);
					compilationUnitArray.add(JavaParser.parse(inputStream));
				}
			}
			for(CompilationUnit compilationUnit : compilationUnitArray){
				StringBuilder cName = new StringBuilder();
				List<TypeDeclaration> typeDeclarations = compilationUnit.getTypes();
				Iterator<TypeDeclaration> i =typeDeclarations.listIterator();
				while(i.hasNext()){
					ClassOrInterfaceDeclaration classOrDeclaration = (ClassOrInterfaceDeclaration) i.next();
					cName.append(classOrDeclaration.getName());
					List<BodyDeclaration> bodyList = classOrDeclaration.getMembers();
					Iterator<BodyDeclaration> bodyIterator = bodyList.listIterator();
					while(bodyIterator.hasNext()){
						BodyDeclaration bodyDeclaration = (BodyDeclaration) bodyIterator.next();
						if(bodyDeclaration instanceof MethodDeclaration){
							List<MethodCallExpr> methodCallExpr = new ArrayList<MethodCallExpr>();
							List<Node> methodDeclaration = bodyDeclaration.getChildrenNodes();
							Iterator<Node> methodDecIterator = methodDeclaration.listIterator(); 
						}
					}
				}
			}
		}catch(Exception e){
			
		}finally{
			inputStream.close();
		}
	}
	
}
