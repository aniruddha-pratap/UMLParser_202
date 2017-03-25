package UMLJavaParser;
import java.io.File;
import java.net.URISyntaxException;
import java.util.*;

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
	public static String classes = "";
	public static String url = "";
	private HashMap<String,List<ClassOrInterfaceType>> mappingInterfaces = new HashMap<String,List<ClassOrInterfaceType>>();
	private HashMap<String,List<ClassOrInterfaceType>> mappingParentClass = new HashMap<String,List<ClassOrInterfaceType>>();
	private String[] dataTypes = {"byte","short","int","long","float","double","boolean","char",
			"Byte","Short","Integer","Long","Float","Double","Boolean","Char"};
	
	
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
}
