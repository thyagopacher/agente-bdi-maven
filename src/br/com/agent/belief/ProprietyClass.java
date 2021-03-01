package br.com.agent.belief;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;

/**
 * Código escrito em uma linguagem
 * */
public class ProprietyClass extends File{

	private static final long serialVersionUID = 1L;
	private CompilationUnit cu;
	private ClassOrInterfaceDeclaration type;
	private Map<MethodDeclaration, List<Statement>> mapMethodsAnalyzed = new HashMap<MethodDeclaration, List<Statement>>();
	private String contentFile;
	
	public ProprietyClass(String pathname) {
		super(pathname);
		try {
			this.contentFile = this.readContent(pathname);
			if(!contentFile.contains("abstract") || !contentFile.contains("interface")) {
				FileInputStream file = new FileInputStream(pathname);
				CompilationUnit cu = JavaParser.parse(file);
				setCu(cu);
				if(cu.getTypes().size() > 0) {
					if(cu.getType(0) instanceof ClassOrInterfaceDeclaration) {
						setType((ClassOrInterfaceDeclaration) cu.getType(0));
			
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error caused by - PropriertyClass: " + e.getMessage());
		}
	}

	public String readContent(String pathName) throws IOException {
		FileInputStream inputStream = null;
		String texto = "";
	    try {
	    	inputStream = new FileInputStream(pathName);
	        texto = IOUtils.toString(inputStream);
	    } catch(Exception ex){
	    	System.out.println("Error caused by - PropriertyClass - readContent: " + ex.getMessage());
	    }finally {
	        inputStream.close();
	    }
	    return texto;
	}
	
	public CompilationUnit getCu() {
		return cu;
	}

	public void setCu(CompilationUnit cu) {
		this.cu = cu;
	}

	public ClassOrInterfaceDeclaration getType() {
		return type;
	}

	public void setType(ClassOrInterfaceDeclaration type) {
		this.type = type;
	}

	public Type getTypeClass() {
		return JavaParser.parseClassOrInterfaceType(type.getNameAsString());
	}

	public Map<MethodDeclaration, List<Statement>> getMapMethodsAnalyzed() {
		return mapMethodsAnalyzed;
	}

	public void setMapMethodsAnalyzed(Map<MethodDeclaration, List<Statement>> mapMethodsAnalyzed) {
		this.mapMethodsAnalyzed = mapMethodsAnalyzed;
	}

	public String getContentFile() {
		return contentFile;
	}

	public void setContentFile(String contentFile) {
		this.contentFile = contentFile;
	}		
}
