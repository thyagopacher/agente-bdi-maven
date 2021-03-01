package br.com.agent.desire;

import java.util.List;

import br.com.agent.belief.QualityAttributeClass;
import br.com.agent.belief.ResultsRefactoring;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;

@Goal
public class ShowResults {

	@GoalParameter
	public ResultsRefactoring results;
	
	@GoalResult
	public boolean isShowed;
	
	public ShowResults(ResultsRefactoring results){
		this.results = results;
	}
}
