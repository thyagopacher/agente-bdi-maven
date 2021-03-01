package br.com.agent.plan;

import java.util.ArrayList;
import java.util.List;

import com.github.mauricioaniche.ck.CKNumber;

import br.com.agent.belief.Candidate;
import br.com.agent.belief.MetricsProject;
import br.com.agent.belief.QualityAttributeClass;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPassed;

/**
 * calculo é feito tendo em base PM = ((original x 100) / refatorado) - 100
 * */
@Plan
public class CheckImprovementPlan extends UtilityPlan{

	private MetricsProject metricsProject;

	public CheckImprovementPlan(MetricsProject metricsProject) {
		this.metricsProject = metricsProject;
	}

	@PlanBody
	public List<QualityAttributeClass> improvementsClass() {
		List<QualityAttributeClass> improvements = new ArrayList<>();
		
		for (CKNumber metricBefore : metricsProject.getMetricsByClassBeforeRefactoring()) {
			if(metricBefore.getClassName() != null && !metricBefore.getClassName().isEmpty()) {
				CKNumber metricAfter = metricsProject.getMetricsByClassAfterRefactoring().stream().filter(l -> l.getClassName().equals(metricBefore.getClassName())).findAny().get();
	
				/*atributos para medir usabilidade*/
				int resultDit = metricBefore.getDit();
				int resultDitBefore = resultDit;
				/**verificando se realmente houve alguma alteração com a refatoração*/
				if(metricAfter.getDit() != metricBefore.getDit() && metricBefore.getDit() > 0) {
					resultDit = ((metricAfter.getDit() * 100) / metricBefore.getDit()) - 100;
				}
				
				int resultLoc = metricBefore.getLoc();
				int resultLocBefore = resultLoc;
				if(metricAfter.getLoc() != metricBefore.getLoc() && metricBefore.getLoc() > 0) {
					resultLoc = ((metricAfter.getLoc() * 100) / metricBefore.getLoc()) - 100;
				}
				
				int resultCC = metricBefore.getWmc();
				int resultCCBefore = resultCC;
				if(metricAfter.getWmc() != metricBefore.getWmc() && metricBefore.getWmc() > 0) {
					resultCC = ((metricAfter.getWmc() * 100) / metricBefore.getWmc()) - 100;
				}
				
				double resultMaintenance = (resultDit + resultLoc + resultCC) / 3;
				double resultReability = (resultCC + resultLoc) / 2;
				double resultReusability = (resultDit + resultLoc) / 2;
	
				double resultMaintenanceBefore = (resultDitBefore + resultLocBefore + resultCCBefore) / 3;
				double resultReabilityBefore = (resultCCBefore + resultLocBefore) / 2;
				double resultReusabilityBefore = (resultDitBefore + resultLocBefore) / 2;			
				
				improvements.add(new QualityAttributeClass(metricBefore.getClassName(), metricBefore.getFile(), resultMaintenance, resultReability, 
						resultReusability, resultMaintenanceBefore, resultReabilityBefore, resultReusabilityBefore));
			}
		}
		return improvements;
	}

	@PlanPassed
	public void passed() {
		super.logSystem.saveContent("Plan finished successfully CheckImprovementPlan");
	}

	@PlanAborted
	public void aborted() {
		super.logSystem.saveContent("Severe - Plan aborted CheckImprovementPlan");
	}

}
