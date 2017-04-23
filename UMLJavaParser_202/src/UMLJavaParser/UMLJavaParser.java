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
	
	private static File localFolder = null;
	private static File[] fileCount = null;
	private static ArrayList<String> implementedInterfaces = new ArrayList<String>();
	private static ArrayList<String> classVariables = new ArrayList<String>();
	private static ArrayList<String> classMethods = new ArrayList<String>();
	private static ArrayList<String> classMethodParams= new ArrayList<String>();
	private static ArrayList<String> classConsrtuctors= new ArrayList<String>();
	private static ArrayList<String> classConsrtuctorParameters= new ArrayList<String>();
	public static String classes = "";
	public boolean isClass;
	public static MethodDeclaration methodRel;
	private FileInputStream inputStream;
	public static ConstructorDeclaration cons;
	private static Map<String,String> hasRel = new HashMap<String,String>();
	private static ArrayList<CompilationUnit> parserCompilationUnits; 
	
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
			
			String grammar="";
			for(File file : fileCount){
				if(file.isFile()){
					FileInputStream inputStream = new FileInputStream(localFolder.toString()+"/"+file.getName().split("\\.")[0]+".java");
					CompilationUnit compUnit = JavaParser.parse(inputStream);
					List<Node> childNodes = compUnit.getChildrenNodes();
					for(Node child : childNodes){
						ClassOrInterfaceDeclaration classOrInterface = (ClassOrInterfaceDeclaration) child;
						if(classOrInterface.isInterface()){
							classes = "[" + "<<interface>>";
						}else{
							classes = "[";
						}
						classes = classes + classOrInterface.getName();
						for(BodyDeclaration body : ((TypeDeclaration) child).getMembers()){
							if(body instanceof ConstructorDeclaration){
								ConstructorDeclaration constructor = (ConstructorDeclaration) body;
								if(constructor.getDeclarationAsString().startsWith("public") && !classOrInterface.isInterface()){
									classes = classes + "+ " + constructor.getName() + "(";
									classConsrtuctors.add(constructor.getName().toString());
								}
								for(Object object : constructor.getChildrenNodes()){
									if(object instanceof Parameter){
										Parameter parameterType = (Parameter)object;
										classConsrtuctorParameters.add(parameterType.getChildrenNodes().get(0).toString()); 
										classes = classes + parameterType.getChildrenNodes().get(0).toString() + ":" + parameterType.getType().toString();									}
								}
							}
							classes = classes + ")";
							List<Parameter> methodP = methodRel.getParameters();
							if(!methodP.isEmpty())
							{
								for(int i=0;i<methodP.size();i++)
								{
									Type type = methodP.get(i).getType();
									if(type instanceof ReferenceType && implementedInterfaces.contains(type.toString()))
									{
										if(!hasRel.containsKey(type))
										{
											hasRel.put(type.toString(),classOrInterface.getName());
										}
									}
								}
							}
						}
					}
				}
			}
			return grammar;
		}catch(Exception e){
			System.out.println(e);
		}
		return null;
		
	}
	
	private String splitter(String input) {
        String[] splitArray = input.split(",");
        String[] components = new LinkedHashSet<String>(
                Arrays.asList(splitArray)).toArray(new String[0]);
        String result = String.join(",", components);
        return result;
    }
	
}
