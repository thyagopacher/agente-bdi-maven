package br.com.agent.plan;

import java.io.File;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPassed;

/**
 * Simple Plan to check the file extension and see if it is eligible for refactoring.
 * */
@Plan
public class FileExtensionPlan extends UtilityPlan{

	private File fileAnalyzed;

	public FileExtensionPlan(File fileAnalyzed) {
		this.fileAnalyzed = fileAnalyzed;
	}

	@PlanBody
	public boolean isFileJava() {
		boolean temAbstrata = this.fileAnalyzed.getName().contains("Abstract");		
		boolean temTest = this.fileAnalyzed.getName().contains("Test");		
		boolean terminaComJava = this.fileAnalyzed.getName().endsWith(".java");
		boolean ehStrategy = this.fileAnalyzed.getName().contains("Strategy");
		boolean res = !temAbstrata && !ehStrategy && terminaComJava && !temTest;
		return res;
	}

	@PlanPassed
	public void passed() {
		super.logSystem.saveContent("Plan finished successfully FileExtensionPlan: " + this.fileAnalyzed.getAbsolutePath());
	}

	@PlanAborted
	public void aborted() {
		super.logSystem.saveContent("Severe - Plan aborted FileExtensionPlan");
	}

}
