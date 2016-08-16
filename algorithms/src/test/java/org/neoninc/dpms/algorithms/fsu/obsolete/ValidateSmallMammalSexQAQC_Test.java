package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
//import org.junit.Before;
//import org.junit.Test;
import org.testng.annotations.*;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

public class ValidateSmallMammalSexQAQC_Test {

	private String female = "f";
	private String male = "m";
	
	private Long valueIdSex = 164L;
	private Long valueIdTestes = 166L;
	private Long valueIdPreg = 168L;
	private Long valueIdNipples = 167L;
	private Long valueIdVagina = 169L;
	
	@BeforeMethod
	@BeforeClass
	public void setUp() throws Exception {
		
		BasicConfigurator.configure();
	}
	
	@Test
	public void runTest() {
		
		String sex = male;
		String nipples = null;
		String testes = "s";
		String pregnancyStatus = null;
		String vagina = null;
		
		long qaQcValId = 1L;
		DPMSMethStreamData qaQcMethStream = new DPMSMethStreamData();
		DPMSMethActivity inputActy = new DPMSMethActivity();
		ArrayList<Long> fieldList = new ArrayList<Long>();; // a list of value ids need to be tested; column names
		fieldList.add(164L); //valueIdSex
		fieldList.add(166L); //valueIdTestes
		fieldList.add(167L); //valueIdNipples
		fieldList.add(168L); //valueIdPreg
		fieldList.add(169L); //valueIdVagina

		//sex
		DPMSMethStreamData msA = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msA);
		DPMSMStreamReadout rdotA1 = new DPMSMStreamReadout();
		msA.getMSReadouts().add(rdotA1);
		rdotA1.setValueStringForValueId(fieldList.get(0), "M", true);
		DPMSMethStreamData msB = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msB);
		DPMSMStreamReadout rdotA2 = new DPMSMStreamReadout();
		msB.getMSReadouts().add(rdotA2);
		rdotA2.setValueStringForValueId(fieldList.get(1), "n", true);
		System.out.println("Test one results: ");
		ValidateSmallMammalSexQAQC qaQc = new ValidateSmallMammalSexQAQC(fieldList, inputActy, valueIdSex, valueIdTestes, valueIdPreg, valueIdNipples, valueIdVagina, qaQcMethStream, qaQcValId);
		qaQc.runAlgorithm();
		

		System.out.println("Test two results: ");
		inputActy = new DPMSMethActivity();
		DPMSMethStreamData msC = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msA);
		DPMSMStreamReadout rdotA3 = new DPMSMStreamReadout();
		msC.getMSReadouts().add(rdotA3);
		rdotA1.setValueStringForValueId(fieldList.get(0), "F", true);
		DPMSMethStreamData msD = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msB);
		DPMSMStreamReadout rdotA4 = new DPMSMStreamReadout();
		msD.getMSReadouts().add(rdotA4);
		rdotA4.setValueStringForValueId(fieldList.get(4), "p", true);
		System.out.println("Test two results: ");
		qaQc = new ValidateSmallMammalSexQAQC(fieldList, inputActy, valueIdSex, valueIdTestes, valueIdPreg, valueIdNipples, valueIdVagina, qaQcMethStream, qaQcValId);
		qaQc.runAlgorithm();
		
		testes = "s";
		sex = female;
		vagina = "p";
		System.out.println("Test three results: ");
		inputActy = new DPMSMethActivity();
		DPMSMethStreamData msF = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msA);
		DPMSMStreamReadout rdotA5 = new DPMSMStreamReadout();
		msF.getMSReadouts().add(rdotA5);
		rdotA5.setValueStringForValueId(fieldList.get(0), "F", true);
		DPMSMethStreamData msG = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msB);
		DPMSMStreamReadout rdotA6 = new DPMSMStreamReadout();
		msG.getMSReadouts().add(rdotA6);
		rdotA6.setValueStringForValueId(fieldList.get(1), "s", true);
		qaQc = new ValidateSmallMammalSexQAQC(fieldList, inputActy, valueIdSex, valueIdTestes, valueIdPreg, valueIdNipples, valueIdVagina, qaQcMethStream, qaQcValId);
		qaQc.runAlgorithm();
				
	}
}
