package UMLJavaParser;
import java.io.*;
import java.util.*;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.Expression;
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
	
	public void sequenceDiagram(String className, String functionName) throws Exception{
		try{
			Map<String, String> classMap = new HashMap<String, String>();
		    Map<String, List<MethodCallExpr>> methodClMap  = new HashMap<String, List<MethodCallExpr>>();
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
							MethodDeclaration methodDeclaration = (MethodDeclaration) bodyIterator.next();
							List<MethodCallExpr> methodCallExpr = new ArrayList<MethodCallExpr>();
							List<Node> methodDecList = methodDeclaration.getChildrenNodes();
							Iterator<Node> methodDecIterator = methodDecList.listIterator(); 
							while(methodDecIterator.hasNext()){
								Object blockStmt = methodDecIterator.next();
								if(blockStmt instanceof BlockStmt){
									List<Node> expsStmt = ((Node) blockStmt).getChildrenNodes();
									Iterator<Node> expsStmtIterator = expsStmt.listIterator();
									while(expsStmtIterator.hasNext()){
										Node expressionStmt  =  expsStmtIterator.next();
										if(expressionStmt instanceof ExpressionStmt){
											Expression expression = ((ExpressionStmt) expressionStmt).getExpression();
											if(expression instanceof MethodCallExpr){
												methodCallExpr.add((MethodCallExpr) expression);											
											}
										}
									}
								}
								methodClMap.put(methodDeclaration.getName(), methodCallExpr);
								classMap.put(methodDeclaration.getName(), cName.toString());
							}
						}
					}
				}
			}
			umlCode.append("@startuml\n actor user #black\n ");
			String temp = "user" + " -> " + className + " : " + functionName + "\n";
			umlCode.append(temp);
			temp= "activate " + classMap.get(functionName) + "\n";
			umlCode.append(temp);
		}catch(Exception e){
			
		}finally{
			inputStream.close();
		}
	}
	
}
