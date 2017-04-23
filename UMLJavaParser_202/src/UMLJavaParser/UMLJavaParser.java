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
	
}
