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
	
	private static String inputFile = null;
	private static String localFilePath = null;
	private static File localFolder = null;
	private static File[] fileCount = null;
	private static ArrayList<String> implementedInterfaces = new ArrayList<String>();
	private static ArrayList<String> classVariables = new ArrayList<String>();
	private static ArrayList<String> classMethods = new ArrayList<String>();
	private static ArrayList<String> classMethodParams= new ArrayList<String>();
	private static ArrayList<String> classConsrtuctors= new ArrayList<String>();
	private static ArrayList<String> classConsrtuctorParameters= new ArrayList<String>();
	private static StringBuilder URL = new StringBuilder();
	public static String classes = "";
	public static String url = "";
	public boolean isClass;
	public static MethodDeclaration methodRel;
	private static HashMap<String,List<ClassOrInterfaceType>> mappingInterfaces = new HashMap<String,List<ClassOrInterfaceType>>();
	private static HashMap<String,List<ClassOrInterfaceType>> mappingParentClass = new HashMap<String,List<ClassOrInterfaceType>>();
	private static ConcurrentHashMap<String,String> hasRel = new ConcurrentHashMap<String,String>();
	private static ArrayList<CompilationUnit> parserCompilationUnits; 
	
	
	public static void main(String[] args) throws URISyntaxException
	{
		if(args.length == 2)
		{
			String path = args[0];
			String newFileName = args[1];
			localFolder = new File(path);
			fileCount = localFolder.listFiles();
			UMLJavaParser obj = new UMLJavaParser();
		}
		else
		{
			System.out.println("Arguments missing");
		}
	
	}
	
	public String parserGrammar(String img){
		try{
			for(File file : fileCount){
				if(file.isFile()){
					if(URL.length() > 0 && (URL.charAt(URL.length()-1) != ','))
					{
						URL.append(",");
					}
					classVariables = new ArrayList<String>();
					classMethods = new ArrayList<String>();
					classConsrtuctors = new ArrayList<String>();
					URL.append("[");
					FileInputStream inputStream = new FileInputStream(localFolder.toString()+"/"+file.getName().split("\\.")[0]+".java");
					CompilationUnit compUnit = JavaParser.parse(inputStream);
					List<Node> childNodes = compUnit.getChildrenNodes();
					for(Node child : childNodes){
						ClassOrInterfaceDeclaration inerfaceOrClass = (ClassOrInterfaceDeclaration)child;
						if(inerfaceOrClass.isInterface())
						{
							implementedInterfaces.add(inerfaceOrClass.getName());
							URL.append("<<Interface>>;");
							URL.append(inerfaceOrClass.getName());
							URL.append("");
							isClass=false;
						}
						List<ClassOrInterfaceType> implement = inerfaceOrClass.getImplements();
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
										hasRel.put(type.toString(),inerfaceOrClass.getName());
									}
								}
							}
						}
					}
				}
			}
		}catch(Exception e){
			System.out.println(e);
		}
		return img;
	}
	
	private String yUML(String javaCode) {
        String[] lines = javaCode.split(",");
        String[] uniqueLines = new LinkedHashSet<String>(
                Arrays.asList(lines)).toArray(new String[0]);
        String res = String.join(",", uniqueLines);
        return res;
    }
		
		
}
