package br.com.agent.desire;

import java.io.File;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;


/**
 * Para qualificar a classe aberta para verificar se o mesmo é com extensão .java
 * */
@Goal
public class FileQualifier {

	@GoalParameter
	public File fileAnalyzed;

	@GoalResult
	public boolean isJava;
	
	public FileQualifier(File fileAnalyzed) {
		System.out.println("Entrou no desejo FileQualifier !");
		this.fileAnalyzed = fileAnalyzed;
	}
	

	
}
