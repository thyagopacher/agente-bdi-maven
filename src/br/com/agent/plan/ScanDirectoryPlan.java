package br.com.agent.plan;

import com.github.mauricioaniche.ck.FileUtils;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPassed;

@Plan
public class ScanDirectoryPlan extends UtilityPlan{

	private String pathName;
	
	public ScanDirectoryPlan(String pathName) {
		this.pathName = pathName;
	}
	
	@PlanBody
	public String[] filesDirectory() {
		String[] javaFiles = FileUtils.getAllJavaFiles(this.pathName);
		return javaFiles;
	}

	@PlanPassed
	public void passed() {
		super.logSystem.saveContent("Plan finished successfully ScanDirectoryPlan");
	}

	@PlanAborted
	public void aborted() {
		super.logSystem.saveContent("Severe - Plan aborted ScanDirectoryPlan");
	}

}
