package org.neoninc.dpms.algorithms.fsu.obsolete;

import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
//import org.junit.Before;
//import org.junit.Test;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * test validationLookupQAQC, validationRangeQAQC, validationRegExQAQC
 * @author lzhang June 2014
 */
public class ValidationQAQC_Test {

	private DPMSMethActivity inputActy = new DPMSMethActivity();
	Calendar cal = Calendar.getInstance();
	private Date tranTime;
	private Date tranTime2;

	@BeforeMethod
	@BeforeTest
	public void setUp() throws Exception {
		
		BasicConfigurator.configure();
		cal.set(2014, Calendar.MAY, 21); //Year, month and day of month
		tranTime = cal.getTime();
		cal.set(2014, Calendar.APRIL, 1);
		tranTime2 = cal.getTime();
	}
	
	@Test
	public void testValidationLookupList() {
		Long inputValID = 101L;
		boolean inputCanBeNull = false;
		
		Long outputValTypeID = 201L;
		Long outputValID = 202L;
		Long outputQFValID = 203L;

		DPMSMethActivity inputActy = new DPMSMethActivity();
		inputActy.setStartDate(tranTime);
		inputActy.setEndDate(tranTime2);
		
		DPMSMethActivity outputActy = inputActy.shallowCopy();
		
		DPMSMethStreamData inputMSD = new DPMSMethStreamData();
		inputMSD.addReadoutValueString(tranTime, tranTime2, inputValID, "D10KMT05");
		inputActy.getMethStreams().add(inputMSD);
		
		DPMSMethStreamData outputMSD = new DPMSMethStreamData();
		outputMSD.setMStreamValTypeID(outputValTypeID);
		outputActy.getMethStreams().add(outputMSD);
		

		String[] techIdList = new String[4];
		techIdList[0] = "D10KMT05";
		techIdList[1] = "D10BJP03";
		techIdList[2] = "C03ZLY04";
		techIdList[3] = "C03MDR02";
		
		ValidationLookupQAQC qaQcForMap = new ValidationLookupQAQC(inputActy, inputValID, inputCanBeNull,
				outputValTypeID, outputValID, outputQFValID, techIdList, null);
		qaQcForMap.runAlgorithmForList();
		
		String outputString = outputActy.findValueStringByValId(outputValID);
		Double outputQF = outputActy.findValueByValId(outputQFValID);
		
		assertTrue( outputString.compareTo("D10KMT05") == 0);
		assertTrue( outputQF < 1.0 );
		System.out.println("ValidationLookupQAQC for List successful");
	}
	
	@Test
	public void testValidationLookupMap() {
		Long inputValID = 101L;
		boolean inputCanBeNull = false;
		
		Long outputValTypeID = 201L;
		Long outputValID = 202L;
		Long outputQFValID = 203L;

		DPMSMethActivity inputActy = new DPMSMethActivity();
		inputActy.setStartDate(tranTime);
		inputActy.setEndDate(tranTime2);
		
		DPMSMethActivity outputActy = inputActy.shallowCopy();
		
		DPMSMethStreamData inputMSD = new DPMSMethStreamData();
		inputMSD.addReadoutValueString(tranTime, tranTime2, inputValID, "D10KMT05");
		inputActy.getMethStreams().add(inputMSD);
		
		DPMSMethStreamData outputMSD = new DPMSMethStreamData();
		outputMSD.setMStreamValTypeID(outputValTypeID);
		outputActy.getMethStreams().add(outputMSD);
		

		HashMap<String,String> techIdMap = new HashMap<String,String>();
		techIdMap.put("D10KMT05", "XXXX");
		techIdMap.put("D10BJP03", "D10BJP03");
		techIdMap.put("C03ZLY04", "C03ZLY04");
		techIdMap.put("C03MDR02", "C03MDR02");
		
		ValidationLookupQAQC qaQcForMap = new ValidationLookupQAQC(inputActy, inputValID, inputCanBeNull,
				outputValTypeID, outputValID, outputQFValID, null, techIdMap);
		qaQcForMap.runAlgorithmForMap();
		
		String outputString = outputActy.findValueStringByValId(outputValID);
		Double outputQF = outputActy.findValueByValId(outputQFValID);
		
		assertTrue( outputString.compareTo("XXXX") == 0);
		assertTrue( outputQF < 1.0 );
		System.out.println("ValidationLookupQAQC for Map successful");
	}
	
	@Test
	public void testValidationRange() {
		long valId = 237l; //L1-capture weight
		long qaQcValId = 282l; //L1-capture invalid weight QF
		double minVal = 0.;
		double maxVal = 601.;
		
		DPMSMethStreamData msC = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msC);
		DPMSMStreamReadout rdotC1 = new DPMSMStreamReadout();
		msC.getMSReadouts().add(rdotC1);
		rdotC1.setReadoutTranTime(tranTime);
//		rdotC1.setValueForValueId(valId, 710., true);
		rdotC1.setValueStringForValueId(valId, "110", true);
		ValidationRangeQAQC qaQc = new ValidationRangeQAQC(valId, inputActy, qaQcValId, minVal, maxVal);
		qaQc.runAlgorithm();
	}
	
	@Test
	public void testValidationRegEx() {
		long valId = 214l;
		long qaQcValId = 246l;
		//String pattern = "[A-J,X][1-9,X][0-9+]";
		String pattern1 = "[A-J][1-9]";
		String pattern2 = "X+";
		String pattern3 = "[A-J]10";
		ArrayList<String> regExList = new ArrayList<String>();
		regExList.add(pattern1);
		regExList.add(pattern2);
		regExList.add(pattern3);
		
		DPMSMethStreamData msD = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msD);
		DPMSMStreamReadout rdotD1 = new DPMSMStreamReadout();
		msD.getMSReadouts().add(rdotD1);
		rdotD1.setReadoutTranTime(tranTime);
		rdotD1.setValueStringForValueId(valId, "XXXX", true);
		
		ValidationRegExQAQC qaQc = new ValidationRegExQAQC(valId, inputActy, qaQcValId, regExList);
		qaQc.runAlgorithm();
	}
}
