package br.com.agent;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.github.mauricioaniche.ck.CKNumber;

import br.com.agent.belief.Candidate;
import br.com.agent.belief.MetricsProject;
import br.com.agent.belief.ProprietyClass;
import br.com.agent.belief.QualityAttributeClass;
import br.com.agent.belief.ResultsRefactoring;
import br.com.agent.desire.CheckImprovement;
import br.com.agent.desire.DirectoryQualifier;
import br.com.agent.desire.Extractor;
import br.com.agent.desire.FileQualifier;
import br.com.agent.desire.IdentifyCandidates;
import br.com.agent.desire.Measure;
import br.com.agent.desire.ScanDirectory;
import br.com.agent.desire.ShowResults;
import br.com.agent.plan.CheckImprovementPlan;
import br.com.agent.plan.DirectoryPlan;
import br.com.agent.plan.ExtractorPlan;
import br.com.agent.plan.FileExtensionPlan;
import br.com.agent.plan.MeasurePlan;
import br.com.agent.plan.RefactoringPlan;
import br.com.agent.plan.ScanDirectoryPlan;
import br.com.agent.plan.ShowTablePlan;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;

@Plans({ @Plan(trigger = @Trigger(goals = FileQualifier.class), body = @Body(FileExtensionPlan.class)),
		@Plan(trigger = @Trigger(goals = DirectoryQualifier.class), body = @Body(DirectoryPlan.class)),
		@Plan(trigger = @Trigger(goals = ScanDirectory.class), body = @Body(ScanDirectoryPlan.class)),
		@Plan(trigger = @Trigger(goals = Extractor.class), body = @Body(ExtractorPlan.class)),
		@Plan(trigger = @Trigger(goals = Measure.class), body = @Body(MeasurePlan.class)),
		@Plan(trigger = @Trigger(goals = CheckImprovement.class), body = @Body(CheckImprovementPlan.class)),
		@Plan(trigger = @Trigger(goals = ShowResults.class), body = @Body(ShowTablePlan.class)),
		@Plan(trigger = @Trigger(goals = IdentifyCandidates.class), body = @Body(RefactoringPlan.class)) })
@Description("Refactoring Agent of projects with Design Patterns")
@Arguments(@Argument(name = "pathName", 
	description = "path to be analyzed", 
	clazz = String.class, defaultvalue = ""))
@Agent
public class RefactoringBDI {
	
	@Belief(dynamic = true)
	public ProprietyClass object;

	@Belief
	public Candidate candidate;

	@Belief
	public boolean directoryExists;

	@Belief
	public MetricsProject metricsProject = new MetricsProject();

	@Belief
	public String[] filesProject;

	@Belief
	public File fileAnalyzed;

	@Belief
	public List<QualityAttributeClass> improvements;

	@Belief
	public List<Candidate> candidates = new ArrayList<Candidate>();	
	
	@Belief
	public ResultsRefactoring results;		
	
	@AgentArgument
	public String pathName = "";

	/**
	 * used for iterate for desires.
	 */
	@AgentFeature
	protected IBDIAgentFeature bdiFeature;

	private Log logSystem = new Log();
	
	@AgentCreated
	public void init() {
		logSystem.saveContent("=====================================\nStarting the Agent ...");
	}

	@AgentBody
	public void body() {

			this.directoryExists = (Boolean) bdiFeature.dispatchTopLevelGoal(new DirectoryQualifier(pathName)).get();
			if (!this.directoryExists) {
				throw new IllegalStateException("Directory not exists: " + pathName);
			}
			System.out.println("\n->Scanning directory in project");
			this.filesProject = (String[]) bdiFeature.dispatchTopLevelGoal(new ScanDirectory(pathName)).get();

			this.metricsProject.setMetricsByClassBeforeRefactoring(
					(Collection<CKNumber>) bdiFeature.dispatchTopLevelGoal(new Measure(pathName)).get());

			if (filesProject == null || filesProject.length == 0) {
				throw new IllegalStateException("Error caused by: files not found in directory");
			} else {
				for (String filePath : filesProject) {
					if (!filePath.isEmpty()) {
						File fileAnalyzed = new File(filePath);
						logSystem.saveContent("Analisando path: " + filePath);
						
						boolean isJava = (Boolean) bdiFeature.dispatchTopLevelGoal(new FileQualifier(fileAnalyzed))
								.get();
						if (isJava) {
							this.object = (ProprietyClass) bdiFeature
									.dispatchTopLevelGoal(new Extractor(fileAnalyzed.getAbsolutePath())).get();

							if (object != null) {
								boolean ehClasse = false;
								if (object.getCu() != null && object.getCu().getTypes() != null
										&& !object.getCu().getTypes().isEmpty()) {
									/** condicional para ver se realmente existe uma classe no arquivo java */
									ehClasse = object.getCu().getType(0).isClassOrInterfaceDeclaration()
											&& !object.getType().isAbstract()
											&& !object.getType().isInterface();
									if (!ehClasse) {
										continue;
									}

									this.candidate = (Candidate) bdiFeature
											.dispatchTopLevelGoal(new IdentifyCandidates(this.object)).get();
									if(this.candidate != null) {
										int numberCandidates = this.candidate.getDesignPatterns().size();
										candidates.add(this.candidate);
										
										if (numberCandidates > 0) {
											System.out.println("Founded " + numberCandidates
													+ " design patterns applicable for this class ");
										}
									}else {
										System.out.println("Nenhuma classe candidata retornou para objeto: " + this.object.getAbsolutePath());
									}
								}
							}
						}
					} else {
						System.out.println("Não é um arquivo .java:" + fileAnalyzed.getName());
					}
				}				
				
				/* apresentar tabela de atributos e metricas */
				this.metricsProject.setMetricsByClassAfterRefactoring(
						(Collection<CKNumber>) bdiFeature.dispatchTopLevelGoal(new Measure(pathName)).get());

				logSystem.saveContent("Measure class metrics after refactoring");

				improvements = (List<QualityAttributeClass>) bdiFeature
						.dispatchTopLevelGoal(new CheckImprovement(this.metricsProject)).get();	
				
				results = new ResultsRefactoring(candidates, improvements);
				boolean showedTable = (boolean) bdiFeature.dispatchTopLevelGoal(new ShowResults(results)).get();
				System.out.println("Table is showed: " + showedTable);
			}
		

	}

}
