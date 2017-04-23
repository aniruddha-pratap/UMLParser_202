package UMLJavaParser;
import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.ModifierSet;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.VoidType;

public class UMLJavaParser {

	private List<CompilationUnit> compilationUnitArray = new ArrayList<CompilationUnit>();
	private String grammarForUML = "";
	//private Map<String, Boolean> classOrInterfaceMap = new HashMap<String, Boolean>();
	//private Map<String, String> relationshipMap = new HashMap<String, String>();
	
	public String parserGrammar(String inputFile){
		try{
			
			String classOrInterfaceName = "";
			List<TypeDeclaration> typeDeclarationArray;
			Map<String, Boolean> classOrInterfaceMap = new HashMap<String, Boolean>();
			Map<String, String> relationshipMap = new HashMap<String, String>();
			//classOrInterfaceMap = new HashMap<String, Boolean>();
			//compilationUnitArray = new ArrayList<CompilationUnit>();
			
			File folder = new File(inputFile);
			for(File file: folder.listFiles()){
				if(file.isFile()){
					FileInputStream inputStream = new FileInputStream(file);
					try{
						compilationUnitArray.add(JavaParser.parse(inputStream));
					}finally{
						inputStream.close();
					}
				}
			}
			
			Iterator<CompilationUnit> compilationUnitIterator = compilationUnitArray.iterator();
			while(compilationUnitIterator.hasNext()){
				CompilationUnit compilationUnit = compilationUnitIterator.next();
				typeDeclarationArray = compilationUnit.getTypes();
				Iterator<TypeDeclaration> typeDeclarationIterator = typeDeclarationArray.iterator();
				while(typeDeclarationIterator.hasNext()){
					TypeDeclaration typeDeclaration = typeDeclarationIterator.next();
					ClassOrInterfaceDeclaration classOrInterface = (ClassOrInterfaceDeclaration) typeDeclaration;
					//System.out.println(classOrInterface);
					classOrInterfaceMap.put(classOrInterface.getName(), classOrInterface.isInterface());
				}
			}
			
			Iterator<CompilationUnit> compUntItrtr = compilationUnitArray.iterator();
			while(compUntItrtr.hasNext()){
				CompilationUnit compUnit = compUntItrtr.next();
				typeDeclarationArray = compUnit.getTypes();
				Node firstNode = typeDeclarationArray.get(0);
				//System.out.println(firstNode);
				ClassOrInterfaceDeclaration tempCI = (ClassOrInterfaceDeclaration) firstNode;
				if(tempCI.isInterface()){
					classOrInterfaceName = "[" + "<<interface>>;"; 
				}else{
					classOrInterfaceName = "[";
				}
				classOrInterfaceName +=  tempCI.getName();
				//System.out.println(classOrInterfaceName);
				//String className = tempCI.getName();
				grammarForUML += constructorOrMethodGrammar(firstNode, tempCI, classOrInterfaceName, classOrInterfaceMap, relationshipMap);
				//System.out.println(grammarForUML);
			}
			
			String tempGramar = "";
			
			Set<String> keysFromRelMap = relationshipMap.keySet();
			for(String key: keysFromRelMap){
				String splitKeys[] =  key.split("-");
				if(classOrInterfaceMap.get(splitKeys[0])){
					tempGramar += "[<<interface>>;" + splitKeys[0] + "]";
				}else{
					tempGramar += "[" + splitKeys[0] + "]";
				}
				tempGramar += relationshipMap.get(key);
				if(classOrInterfaceMap.get(splitKeys[1])){
					tempGramar += "[<<interface>>;" + splitKeys[1] + "]";
				}else{
					tempGramar += "[" + splitKeys[1] + "]";
				}
				tempGramar += ",";
			}
			
			grammarForUML += tempGramar;
			
			//System.out.println(grammarForUML);
			String [] yUMLGrammar = grammarForUML.split(",");
			String [] uniqueyUMLGrammarComponents = new LinkedHashSet<String>(Arrays.asList(yUMLGrammar)).toArray(new String[0]);
			grammarForUML = String.join(",", uniqueyUMLGrammarComponents);
			
			//ConnectyUML.connectToYUml(grammarForUML, "E://Aniruddha//TestUMLParser//Test5" + "\\ ClassDiagram1");
			System.out.println(grammarForUML);
		}catch(Exception e){
			System.out.println(e);
		}
		return null;
		
	}
	
	public String constructorOrMethodGrammar(Node typeDeclaration, ClassOrInterfaceDeclaration classOrInterface, String classOrInterfaceName,Map<String, Boolean> classOrInterfaceMap, Map<String, String> relationshipMap){
		String format = ",";
		String methodName = "";
		String attributes = "";
		String finalGrammarString = "";
		boolean nextMember = false; 
		boolean nextAttribute = false;
		List<String> attributesList = new ArrayList<String>();
		List<BodyDeclaration> bodyDeclaration = ((TypeDeclaration) typeDeclaration).getMembers();
		
		Iterator<BodyDeclaration> constBodyDecIterator = bodyDeclaration.iterator();
		while(constBodyDecIterator.hasNext()){
			BodyDeclaration bodyDeclrtn = constBodyDecIterator.next();
			if(bodyDeclrtn instanceof ConstructorDeclaration){
				ConstructorDeclaration constructorDeclaration = (ConstructorDeclaration) bodyDeclrtn;
				if(constructorDeclaration.getDeclarationAsString().startsWith("public") && !classOrInterface.isInterface()){
					if(nextMember){
						methodName += ";";
					}
					methodName += "+ " + constructorDeclaration.getName() + "(";
					List<Node> constrctrChildNodes = constructorDeclaration.getChildrenNodes();
					Iterator<Node> childNodesIterator = constrctrChildNodes.iterator();
					while(childNodesIterator.hasNext()){
						Node childNode = childNodesIterator.next();
						if(childNode instanceof Parameter){
							Parameter parameter = (Parameter) childNode;
							methodName += parameter.getChildrenNodes().get(0).toString() + ":" + parameter.getType().toString(); 
							if(classOrInterfaceMap.containsKey(parameter.getType().toString()) && !classOrInterfaceMap.get(classOrInterface.getName())){
								format += "[" + classOrInterface.getName() + "] uses -.->";
								if(classOrInterfaceMap.get(parameter.getType().toString())){
									format += "[<<interface>>;" + parameter.getType().toString() + "]";
								}else{
									format += "[" + parameter.getType().toString() + "]";
								}
							}
							format += ",";
						}
					}
					methodName += ")";
	                nextMember = true;
				}
			}
			
			if(bodyDeclrtn instanceof MethodDeclaration){
				MethodDeclaration methodDeclaration = (MethodDeclaration) bodyDeclrtn;
				if(methodDeclaration.getDeclarationAsString().startsWith("public") && !classOrInterface.isInterface()){
					if(methodDeclaration.getName().startsWith("set") || methodDeclaration.getName().startsWith("get")){
						attributesList.add(methodDeclaration.getName().substring(3).toLowerCase());
					}else{
						if(nextMember){
							methodName += ";";
						}
						methodName += "+ " + methodDeclaration.getName() + "(";
						List<Node> methodChildNodes = methodDeclaration.getChildrenNodes();
						Iterator<Node> methodChildNodesIterator = methodChildNodes.iterator();
						while(methodChildNodesIterator.hasNext()){
							Node methodChildNode = methodChildNodesIterator.next();
							if(methodChildNode instanceof Parameter){
								Parameter mParameter = (Parameter) methodChildNode;
								methodName += mParameter.getChildrenNodes().get(0).toString() + ":" + mParameter.getType().toString();
								if(classOrInterfaceMap.containsKey(mParameter.getType().toString()) && !classOrInterfaceMap.get(classOrInterface.getName())){
									format += "[" + classOrInterface.getName() + "] uses -.->";
									if(classOrInterfaceMap.get(mParameter.getType().toString())){
										format += "[<<interface>>;" + mParameter.getType().toString() + "]";
									}else{
										format += "[" + mParameter.getType().toString() + "]";
									}
								}
								format += ",";
							}else{
								String methodImplArray[] = methodChildNode.toString().split(" ");
								for (int i=0;i<methodImplArray.length;i++) {
									if(classOrInterfaceMap.containsKey(methodImplArray[i]) && !classOrInterfaceMap.get(classOrInterface.getName())){
										format += "[" + classOrInterface.getName() + "] uses -.->";
										if (classOrInterfaceMap.get(methodImplArray[i])){
                                        	format += "[<<interface>>;" + methodImplArray[i] + "]";
                                        }
                                        else{
                                            format += "[" + methodImplArray[i] + "]";	
                                        }
										format += ",";
									}
								}
							}
						}
						methodName += ") : " + methodDeclaration.getType();
                        nextMember = true;
					}
				}
			}	
			
			if(bodyDeclrtn instanceof FieldDeclaration){
				FieldDeclaration fieldDeclrtn = (FieldDeclaration) bodyDeclrtn;
				String atrbutAccessModifier = "";
				String bodyAccess = bodyDeclrtn.toStringWithoutComments().substring(0, bodyDeclrtn.toStringWithoutComments().indexOf(" "));
				if(bodyAccess.equals("public")){
					atrbutAccessModifier = "+";
				}else if(bodyAccess.equals("private")){
					atrbutAccessModifier = "-";
				}else{
					atrbutAccessModifier ="";
				}
				String atrbutDataType = fieldDeclrtn.getType().toString();
				atrbutDataType = atrbutDataType.replace("[", "(");
				atrbutDataType = atrbutDataType.replace("]", ")");
				atrbutDataType = atrbutDataType.replace("<", "(");
				atrbutDataType = atrbutDataType.replace(">", ")");
				String atrbutName = fieldDeclrtn.getChildrenNodes().get(1).toString();
				if(atrbutName.contains("=")){
					atrbutName = atrbutName.substring(0, atrbutName.indexOf("=") - 1);
				}
				if(atrbutAccessModifier.equals("-") && attributesList.contains(atrbutName.toLowerCase())){
					atrbutAccessModifier = "+";
				}
				String relationShip = "";
				boolean hasMultiplicity = false;
				if(atrbutDataType.contains("(")){
					relationShip = atrbutDataType.substring(atrbutDataType.indexOf("(") + 1, atrbutDataType.indexOf(")"));
					hasMultiplicity = true;
				}else{
					if(classOrInterfaceMap.containsKey(atrbutDataType)){
						relationShip = atrbutDataType;
					}
				}
				
				//relationshipMap = new HashMap<String, String>();
				if(!relationShip.isEmpty() && classOrInterfaceMap.containsKey(relationShip)){
					String rel = "-";
					if(relationshipMap.containsKey(relationShip + "-" + classOrInterface.getName())){
						rel = relationshipMap.get(relationShip + "-" + classOrInterface.getName());
						if(hasMultiplicity){
							rel = "*" + rel;
						}
						relationshipMap.put(relationShip + "-" + classOrInterface.getName(), rel);
					}else{
						if(hasMultiplicity){
							rel += "*";
						}
						relationshipMap.put(classOrInterface.getName() + "-" + relationShip, rel);
					}
				}
				if(atrbutAccessModifier.equals("+") || atrbutAccessModifier.equals("-")){
					if(nextAttribute){
						attributes += "; ";
					}
					attributes += atrbutAccessModifier + " " + atrbutName + " : " + atrbutDataType;
					nextAttribute = true;
				}
			}
		}
		
		/*Iterator<BodyDeclaration> methodBodyDecIterator = bodyDeclaration.iterator();
		while(methodBodyDecIterator.hasNext()){
			BodyDeclaration methodBodyDeclrtn = methodBodyDecIterator.next();
			
		}
		
		Iterator<BodyDeclaration> fieldBodyDecIterator = bodyDeclaration.iterator();
		while(fieldBodyDecIterator.hasNext()){
			BodyDeclaration fieldBodyDeclrtn = fieldBodyDecIterator.next();
			
		}*/
						
		if(!classOrInterface.getExtends().isEmpty()){
			format += "[" + classOrInterface.getName() + "] " + "-^ " + classOrInterface.getExtends() + ",";
		}

		if(!classOrInterface.getImplements().isEmpty()){
			List<ClassOrInterfaceType> implementedInterfaces = classOrInterface.getImplements();
			Iterator<ClassOrInterfaceType> interfaceItrtor = implementedInterfaces.iterator();
			while(interfaceItrtor.hasNext()){
				ClassOrInterfaceType getIntrfc = interfaceItrtor.next();
				format += "[" + classOrInterface.getName() + "] " + "-.-^ " + "[" + "<<interface>>;" + getIntrfc + "]" + ",";
			}
		}
		
		finalGrammarString += classOrInterfaceName;
		
		if(!attributes.isEmpty()){
			String atr = attributes.toString();
			atr = atr.replace("[", "(");
			atr = atr.replace("]", ")");
			atr = atr.replace("<", "(");
			atr = atr.replace(">", ")");
			finalGrammarString += "|" + atr;
		}
		
		if(!methodName.isEmpty()){
			methodName = methodName.replace("[", "(");
			methodName = methodName.replace("]", ")");
			methodName = methodName.replace("<", "(");
			methodName = methodName.replace(">", ")");
			finalGrammarString += "|" + methodName;
		}
		
		finalGrammarString += "]" + format;
				
		return finalGrammarString;
	}
	
	public static void main(String args[]) throws Exception{
		try{
			UMLJavaParser newParser = new UMLJavaParser();
			newParser.parserGrammar("E://Aniruddha//TestUMLParser//Test4");
		}catch(Exception e){
			
		}
	}
	
}
