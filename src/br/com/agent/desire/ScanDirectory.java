package br.com.agent.desire;

import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;


/**
 * Para ver quais arquivos tem naquele diretorio
 * */
@Goal
public class ScanDirectory {

	@GoalParameter
	public String pathName;

	@GoalResult
	public String[] filesInProject;
	
	public ScanDirectory(String pathName) {
		this.pathName = pathName;
	}
	

	
}
