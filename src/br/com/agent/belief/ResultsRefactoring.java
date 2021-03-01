package br.com.agent.belief;

import java.util.List;

public class ResultsRefactoring {

	private List<Candidate> candidates;
	private List<QualityAttributeClass> improvements;
	
	public ResultsRefactoring(List<Candidate> candidates, List<QualityAttributeClass> improvements) {
		this.candidates = candidates;
		this.improvements = improvements;
	}
	
	public List<Candidate> getCandidates() {
		return candidates;
	}
	public void setCandidates(List<Candidate> candidates) {
		this.candidates = candidates;
	}
	public List<QualityAttributeClass> getImprovements() {
		return improvements;
	}
	public void setImprovements(List<QualityAttributeClass> improvements) {
		this.improvements = improvements;
	}
	
}
