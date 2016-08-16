package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
//import org.junit.Before;
//import org.junit.Test;
import org.testng.annotations.*;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

public class CompleteRecordsQAQC_Test {

	@BeforeMethod
	@BeforeClass
	public void setUp() throws Exception {
		
		BasicConfigurator.configure();
	}
	
	@Test
	public void runTest() {
		ArrayList<Long> qfList = new ArrayList<Long>();
		qfList.add(100L);
		qfList.add(101L);
		qfList.add(102L);
		qfList.add(103L);
		qfList.add(104L);
		
		long qaQcValId = 1L;
		DPMSMethStreamData qaQcMethStream = new DPMSMethStreamData();

		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.MAY, 21); //Year, month and day of month
		Date tranTime = cal.getTime();
		cal.set(2014, Calendar.APRIL, 1);
		Date tranTime2 = cal.getTime();

		DPMSMethActivity inputActy = new DPMSMethActivity();
			
		//sex
		DPMSMethStreamData msA = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msA);
		DPMSMStreamReadout rdotA1 = new DPMSMStreamReadout();
		msA.getMSReadouts().add(rdotA1);
		rdotA1.setReadoutTranTime(tranTime);
//		rdotA1.setValueStringForValueId(qfList.get(0), "M", true);
		rdotA1.setValueForValueId(qfList.get(0), 1., true);
		DPMSMStreamReadout rdotA2 = new DPMSMStreamReadout();
		msA.getMSReadouts().add(rdotA2);
		rdotA2.setReadoutTranTime(tranTime2);
//		rdotA2.setValueStringForValueId(qfList.get(0), "NA", true);
		rdotA1.setValueForValueId(qfList.get(0), 0., true);
		
		//lifeStage
		DPMSMethStreamData msB = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msB);
		DPMSMStreamReadout rdotB1 = new DPMSMStreamReadout();
		msB.getMSReadouts().add(rdotB1);
		rdotB1.setReadoutTranTime(tranTime);
//		rdotB1.setValueStringForValueId(qfList.get(1), "A", true);
		rdotB1.setValueForValueId(qfList.get(1), 0., true);
		
		//tagID
		DPMSMethStreamData msC = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msC);
		DPMSMStreamReadout rdotC1 = new DPMSMStreamReadout();
		msC.getMSReadouts().add(rdotC1);
		rdotC1.setReadoutTranTime(tranTime);
//		rdotC1.setValueStringForValueId(qfList.get(2), "L0275", true);
		rdotC1.setValueForValueId(qfList.get(2), 0., true);
		
		//earLength
		DPMSMethStreamData msD = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msD);
		DPMSMStreamReadout rdotD1 = new DPMSMStreamReadout();
		msD.getMSReadouts().add(rdotD1);
		rdotD1.setReadoutTranTime(tranTime);
//		rdotD1.setValueStringForValueId(qfList.get(3), "13", true);
		rdotD1.setValueForValueId(qfList.get(3), 1., true);

		//taxonID
		DPMSMethStreamData msE = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msE);
		DPMSMStreamReadout rdotE1 = new DPMSMStreamReadout();
		msE.getMSReadouts().add(rdotE1);
		rdotE1.setReadoutTranTime(tranTime);
//		rdotE1.setValueStringForValueId(qfList.get(4), "PEMA", true);
		rdotE1.setValueForValueId(qfList.get(4), 0., true);
		

		CompleteRecordsQAQC qaQc = new CompleteRecordsQAQC(qfList, inputActy, qaQcMethStream, qaQcValId);
		qaQc.runAlgorithm();

		cal.set(2014, Calendar.JUNE, 1);
		Date tranTime3 = cal.getTime();
		msA.getMSReadouts().add(rdotA2);
		rdotA2.setReadoutTranTime(tranTime3);
//		rdotA2.setValueStringForValueId(qfList.get(0), "NA", true);
		rdotA2.setValueForValueId(qfList.get(0), 1., true);
		CompleteRecordsQAQC qaQc2 = new CompleteRecordsQAQC(qfList, inputActy, qaQcMethStream, qaQcValId);
		qaQc2.runAlgorithm();

	}
}
