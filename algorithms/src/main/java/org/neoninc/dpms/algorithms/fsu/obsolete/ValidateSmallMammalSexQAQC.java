package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;
/**
 * Description: 
 * 
 * This class evaluates input characteristics for small mammal sex.  Male questions involve ascertaining the state of the
 * testes.  Female questions concern the status of nipples, pregnancy, and vagina.  
 *
 * Parameters:
 * 
 * Input:
 * @param String sex  - "m" or "f"
 * @param String testes - "s" or "n" or null
 * @param String nipples - "e" or "n" or null
 * @param String pregnancyStatus - "p" or null
 * @param String vagina - "s" or "p" or "n" or null
 * @param qaQcMethStream - method stream to put QA/AC values
 * @param qaQcValId - QA/QC value id
 * 
 * Output:
 * 
 * Author: bpenn
 * 
 * Date: 6/4/2014
 * 
 */

public class ValidateSmallMammalSexQAQC {
	static private Logger log = Logger.getLogger(ValidateSmallMammalSexQAQC.class);

	private Integer validationTestesQF = 0;
	private Integer validationNipplesQF = 0;
	private Integer validationPregnancyStatusQF = 0;
	private Integer validationVaginaQF = 0;
	
	private Long valueIdSex = 164L;
	private Long valueIdTestes = 166L;
	private Long valueIdPreg = 168L;
	private Long valueIdNipples = 167L;
	private Long valueIdVagina = 169L;
	
	private String sex = null;
	private String testes = null;
	private String pregnancyStatus = null;
	private String nipples = null;
	private String vagina = null;
	private Long qaQcValId;  // quality flag id for this test
	private DPMSMethStreamData qaQcMethStream; // a new meth stream to hold QA flag
	private DPMSMethActivity inputActy;  //row of data
	private ArrayList<Long> fieldList; // a list of value ids need to be tested; column names

	
	public ValidateSmallMammalSexQAQC(ArrayList<Long> fieldList, DPMSMethActivity inputActy, Long valueIdSex, Long valueIdTestes, Long valueIdPreg, Long valueIdNipples, Long valueIdVagina, DPMSMethStreamData qaQcMethStream, Long qaQcValId) {
		this.fieldList = fieldList;
		this.inputActy = inputActy;
		this.valueIdSex = valueIdSex;
		this.valueIdTestes = valueIdTestes;
		this.valueIdPreg = valueIdPreg;
		this.valueIdNipples = valueIdNipples;
		this.valueIdVagina = valueIdVagina;
		this.qaQcMethStream = qaQcMethStream;
		this.qaQcValId = qaQcValId;
		// TODO Auto-generated constructor stub
	}

	public Integer getValidationTestesQF () {
		return validationTestesQF;
	}

	public void setValidationTestesQF(Integer validationTestesQF) {
		this.validationTestesQF = validationTestesQF;
	}

	public Integer getValidationNipplesQF() {
		return validationNipplesQF;
	}

	public void setValidationNipplesQF(Integer validationNipplesQF) {
		this.validationNipplesQF = validationNipplesQF;
	}

	public Integer getValidationPregnancyStatusQF() {
		return validationPregnancyStatusQF;
	}

	public void setValidationPregnancyStatusQF(Integer validationPregnancyStatusQF) {
		this.validationPregnancyStatusQF = validationPregnancyStatusQF;
	}

	public Integer getValidationVaginaQF() {
		return validationVaginaQF;
	}

	public void setValidationVaginaQF(Integer validationVaginaQF) {
		this.validationVaginaQF = validationVaginaQF;
	}
	
	public boolean runAlgorithm(){
		DPMSMStreamReadout newRdot = new DPMSMStreamReadout();
		qaQcMethStream.getMSReadouts().add(newRdot);
		
		//get values for column names
		
		for(Long fieldValId : fieldList) {
			DPMSMethStreamData ms = inputActy.findMethStreamByValId(fieldValId);
			if( ms != null) {
				if (fieldValId.equals (valueIdSex)) {
					System.out.println("Found valueIdSex");
					DPMSMStreamReadout rdot = ms.findLatestReadout();
					this.sex = rdot.getValueStringForValueId(valueIdSex);
				}
				else if(fieldValId.equals(valueIdNipples)) {
					System.out.println("Found valueIdNipples");
					DPMSMStreamReadout rdot = ms.findLatestReadout();
					this.nipples = rdot.getValueStringForValueId(valueIdNipples);
				}
				else if(fieldValId.equals(valueIdTestes)) {
					System.out.println("Found valueIdTestes");
					DPMSMStreamReadout rdot = ms.findLatestReadout();
					this.testes = rdot.getValueStringForValueId(valueIdTestes);
				}
				else if(fieldValId.equals(valueIdPreg)) {
					System.out.println("Found valueIdPreg");
					DPMSMStreamReadout rdot = ms.findLatestReadout();
					this.pregnancyStatus = rdot.getValueStringForValueId(valueIdPreg);
				}
				else if( fieldValId.equals(valueIdVagina)) {
					System.out.println("Found valueIdVagina");
					DPMSMStreamReadout rdot = ms.findLatestReadout();
					this.vagina = rdot.getValueStringForValueId(valueIdVagina);
				}
			}
		}
		
		//process values for method Activity
		
		Double qaVal = 0.;
		if(sex.equalsIgnoreCase("m")) {
			if(nipples != null && vagina != null && pregnancyStatus != null){
				log.debug("Erroneous female information for male..nipples or vagina or pregnancyStatus != null");
				qaVal = 1.;
				newRdot.setValueForValueId(qaQcValId, qaVal, true);
				return true;
			}
			if (testes != null){
				if (testes.equalsIgnoreCase("s") || testes.equalsIgnoreCase("n") || testes.equalsIgnoreCase(null)) {
					this.validationTestesQF = 1;
				}
				else {
					log.debug("Error..invalid testes value "+testes);
					qaVal = 1.;
					newRdot.setValueForValueId(qaQcValId, qaVal, true);
				}
			}
		}
		else if (sex.equalsIgnoreCase("f")) {
			if(testes != null){
				log.debug("Erroneous male information for female..testes != null");
				qaVal = 1.;
				newRdot.setValueForValueId(qaQcValId, qaVal, true);
				return true;				
			}
			if (nipples != null){
				if (nipples.equalsIgnoreCase("e") || nipples.equalsIgnoreCase("n") || nipples.equalsIgnoreCase(null)) {
					this.validationNipplesQF = 1;
				}
				else {
					log.debug("Error..invalid nipples value "+nipples);
				}				
			}
			if (pregnancyStatus != null){
				if (pregnancyStatus.equalsIgnoreCase("p") || pregnancyStatus.equalsIgnoreCase(null)) {
					this.validationPregnancyStatusQF = 1;
				}
				else {
					log.debug("Error..invalid pregnancyStatus value "+pregnancyStatus);
					qaVal = 1.;
					newRdot.setValueForValueId(qaQcValId, qaVal, true);
				}				
			}
			if (vagina != null){
				if (vagina.equalsIgnoreCase("s") || vagina.equalsIgnoreCase("p") || vagina.equalsIgnoreCase("n") || vagina.equalsIgnoreCase(null)) {
					this.validationVaginaQF = 1;
				}
				else {
					log.debug("Error..invalid vagina value "+vagina);
					qaVal = 1.;
					newRdot.setValueForValueId(qaQcValId, qaVal, true);
				}				
			}
		}
		newRdot.setValueForValueId(qaQcValId, qaVal, true);
		System.out.println(qaVal);
		log.debug("Record is complete!");
		return true;
	}
}
