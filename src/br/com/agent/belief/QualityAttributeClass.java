package br.com.agent.belief;

public class QualityAttributeClass {

	private String fileClass;
	private String nameClass;
	private double impromentMaintenance;
	private double impromentReability;
	private double impromentReusability;
	
	private double impromentMaintenanceBefore;
	private double impromentReabilityBefore;
	private double impromentReusabilityBefore;	


	public QualityAttributeClass(String nameClass, String fileClass, double impromentMaintenance, double impromentReability, double impromentReusability, 
			double impromentMaintenanceBefore, double impromentReabilityBefore, double impromentReusabilityBefore) {
		this.nameClass = nameClass;
		this.fileClass = fileClass;
		this.impromentReability = impromentReability;
		this.impromentMaintenance = impromentMaintenance;
		this.impromentReusability = impromentReusability;
		
		this.impromentMaintenanceBefore = impromentMaintenanceBefore;
		this.impromentReabilityBefore = impromentReabilityBefore;
		this.impromentReusabilityBefore = impromentReusabilityBefore;			
	}


	public String getFileClass() {
		return fileClass;
	}


	public void setFileClass(String fileClass) {
		this.fileClass = fileClass;
	}


	public String getNameClass() {
		return nameClass;
	}


	public void setNameClass(String nameClass) {
		this.nameClass = nameClass;
	}


	public double getImpromentMaintenance() {
		return impromentMaintenance;
	}


	public void setImpromentMaintenance(double impromentMaintenance) {
		this.impromentMaintenance = impromentMaintenance;
	}


	public double getImpromentReability() {
		return impromentReability;
	}


	public void setImpromentReability(double impromentReability) {
		this.impromentReability = impromentReability;
	}


	public double getImpromentReusability() {
		return impromentReusability;
	}


	public void setImpromentReusability(double impromentReusability) {
		this.impromentReusability = impromentReusability;
	}


	public double getImpromentMaintenanceBefore() {
		return impromentMaintenanceBefore;
	}


	public void setImpromentMaintenanceBefore(double impromentMaintenanceBefore) {
		this.impromentMaintenanceBefore = impromentMaintenanceBefore;
	}


	public double getImpromentReabilityBefore() {
		return impromentReabilityBefore;
	}


	public void setImpromentReabilityBefore(double impromentReabilityBefore) {
		this.impromentReabilityBefore = impromentReabilityBefore;
	}


	public double getImpromentReusabilityBefore() {
		return impromentReusabilityBefore;
	}


	public void setImpromentReusabilityBefore(double impromentReusabilityBefore) {
		this.impromentReusabilityBefore = impromentReusabilityBefore;
	}


}
