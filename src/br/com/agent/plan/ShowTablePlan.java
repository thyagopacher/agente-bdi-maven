package br.com.agent.plan;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.github.mauricioaniche.ck.CKNumber;

import br.com.agent.TableModel;
import br.com.agent.belief.Candidate;
import br.com.agent.belief.MetricsProject;
import br.com.agent.belief.QualityAttributeClass;
import br.com.agent.belief.ResultsRefactoring;
import br.com.agent.plan.designpattern.DesignPattern;
import br.com.agent.plan.designpattern.FactoryMethodPlan;
import br.com.agent.plan.designpattern.NullObjectPlan;
import br.com.agent.plan.designpattern.SingletonPlan;
import br.com.agent.plan.designpattern.StrategyPlan;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanPassed;

@Plan
public class ShowTablePlan extends UtilityPlan{
	
	private ResultsRefactoring results;
	private double totalManutencao = 0;
	private double totalUsabilidade = 0;
	private double totalLegibilidade = 0;
	
	private double totalManutencaoBefore = 0;
	private double totalUsabilidadeBefore = 0;
	private double totalLegibilidadeBefore = 0;	
	
	private int totalFactory;
	private int totalNullObject;
	private int totalSingleton;
	private int totalStrategy;
	
	public ShowTablePlan(ResultsRefactoring results) {
		this.results = results;
	}
	
	public void sumCriteries() {
		for (QualityAttributeClass qualityAttributeClass : this.results.getImprovements()) {
			totalManutencao += qualityAttributeClass.getImpromentMaintenance();
			totalUsabilidade += qualityAttributeClass.getImpromentReusability();
			totalLegibilidade += qualityAttributeClass.getImpromentReability();
			
			totalManutencaoBefore += qualityAttributeClass.getImpromentMaintenanceBefore();
			totalUsabilidadeBefore += qualityAttributeClass.getImpromentReusabilityBefore();
			totalLegibilidadeBefore += qualityAttributeClass.getImpromentReabilityBefore();			
		}
	}
	
	public double roundValue2DecimalPlaces(double value) {
		return Math.round(value * 100.0) / 100.0;
	}
	
	public void totalizaPadraoDeProjeto() {
		for (Candidate candidate : this.results.getCandidates()) {
			for (DesignPattern designPattern : candidate.getDesignPatterns()) {
				System.out.println("Achou: " + designPattern.getClass());
				if(designPattern instanceof FactoryMethodPlan) {
					totalFactory++;
				}else if(designPattern instanceof NullObjectPlan) {
					totalNullObject++;
				}else if(designPattern instanceof SingletonPlan) {
					totalSingleton++;
				}else if(designPattern instanceof StrategyPlan) {
					totalStrategy++;
				}
			}
		}
	}
	
	@PlanBody
	public boolean createJTable() {
		if(!this.results.getImprovements().isEmpty()) {
			int qtdImprovements = this.results.getImprovements().size();
			System.out.println("Founded " + qtdImprovements + " improvements ");
			
			JFrame frame = new JFrame("Source-code refactor");
			JTable tabela = new JTable(new TableModel(this.results.getImprovements()));
			
			tabela.setAutoCreateRowSorter(true);
			
			this.sumCriteries();
			JLabel label = new JLabel();
			double mediaManutencao = totalManutencao / qtdImprovements;
			double mediaUsabilidade = totalUsabilidade / qtdImprovements;
			double mediaLegibilidade = totalLegibilidade / qtdImprovements;
			
			double mediaManutencaoBefore = totalManutencaoBefore / qtdImprovements;
			double mediaUsabilidadeBefore = totalUsabilidadeBefore / qtdImprovements;
			double mediaLegibilidadeBefore = totalLegibilidadeBefore / qtdImprovements;			
			
			this.totalizaPadraoDeProjeto();
			String htmlLabel = "";
			htmlLabel += "<html>Classes Amount: " + qtdImprovements; 
			htmlLabel += "<br>Amount of Design Patterns -> Factory Method: " + totalFactory + ", NullObject: " + totalNullObject + ", Singleton:" + totalSingleton + ", Strategy: " + totalStrategy;
			
			htmlLabel += "<br><h3>Quality metrics (before) refactoring</h3> Reusability: "; 
			htmlLabel += this.roundValue2DecimalPlaces(mediaUsabilidadeBefore); 
			htmlLabel += " - Maintainability:" + this.roundValue2DecimalPlaces(mediaManutencaoBefore); 
			htmlLabel += " - Reliability: " + this.roundValue2DecimalPlaces(mediaLegibilidadeBefore); 
			
			htmlLabel += "<br><h3>Quality metrics (after) refactoring</h3> Reusability: "; 
			htmlLabel += this.roundValue2DecimalPlaces(mediaUsabilidade); 
			htmlLabel += " - Maintainability:" + this.roundValue2DecimalPlaces(mediaManutencao); 
			htmlLabel += " - Reliability: " + this.roundValue2DecimalPlaces(mediaLegibilidade); 
			htmlLabel += "</html>";
			label.setText(htmlLabel);
			
			JScrollPane scroll = new JScrollPane(tabela);
			frame.add(scroll, BorderLayout.CENTER);
			frame.add(label, BorderLayout.PAGE_START);
			frame.setLocationRelativeTo(null);
			frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH );
			frame.setVisible(true);
			
			return true;
		}
		return false;
	}

	@PlanPassed
	public void passed() {
		super.logSystem.saveContent("Plan finished successfully ShowTablePlan");
	}

	@PlanAborted
	public void aborted() {
		super.logSystem.saveContent("Severe - Plan aborted ShowTablePlan");
	}

	public double getTotalManutencaoBefore() {
		return totalManutencaoBefore;
	}

	public void setTotalManutencaoBefore(double totalManutencaoBefore) {
		this.totalManutencaoBefore = totalManutencaoBefore;
	}

}
