package UMLJavaParser;
import java.util.*;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.ReferenceType;
import com.github.javaparser.ast.type.Type;

public class Dependencies {

	private String interfce="";
	private String classes="";
	
	public String getinterfce() {
		return interfce;
	}
	
	public void setinterfce(String interfce) {
		this.interfce = interfce;
	}
	
	public String getClassName() {
		return classes;
	}
	
	public void setClassName(String className) {
		this.classes = className;
	}
}
