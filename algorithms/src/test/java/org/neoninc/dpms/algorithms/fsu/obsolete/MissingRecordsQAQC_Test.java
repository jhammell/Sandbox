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
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;
import org.neoninc.dpms.datastructures.DPMSLocationInfo;

public class MissingRecordsQAQC_Test {

	@BeforeMethod
	@BeforeClass
	public void setUp() throws Exception {
		
		BasicConfigurator.configure();
	}
	
	@Test
	public void runTest() {
		
		long qaQcValId = 1L;
		long qaQcValTypeId = 101L;
		int numLimit = 3;
		

		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.MAY, 21); //Year, month and day of month
		Date tranTime = cal.getTime();
		cal.set(2014, Calendar.JUNE, 1);
		Date tranTime2 = cal.getTime();
		cal.set(2014, Calendar.JULY, 1);
		Date tranTime3 = cal.getTime();

		DPMSLocationInfo locnInfo = new DPMSLocationInfo();
		locnInfo.setPlotID("HARV_001");
		
		DPMSMethStreamData qaQcMethStream1 = new DPMSMethStreamData();
		qaQcMethStream1.setMStreamValTypeID(qaQcValTypeId);
		DPMSMethActivity inputActy1 = new DPMSMethActivity();
		inputActy1.setStartDate(tranTime);
		inputActy1.setLocnInfo(locnInfo);
		inputActy1.getMethStreams().add(qaQcMethStream1);
			
		DPMSMethStreamData qaQcMethStream2 = new DPMSMethStreamData();
		qaQcMethStream2.setMStreamValTypeID(qaQcValTypeId);
		DPMSMethActivity inputActy2 = new DPMSMethActivity();
		inputActy2.setStartDate(tranTime);
		inputActy2.setLocnInfo(locnInfo);
		inputActy2.getMethStreams().add(qaQcMethStream2);
			
		DPMSMethStreamData qaQcMethStream3 = new DPMSMethStreamData();
		qaQcMethStream3.setMStreamValTypeID(qaQcValTypeId);
		DPMSMethActivity inputActy3 = new DPMSMethActivity();
		inputActy3.setStartDate(tranTime);
		inputActy3.setLocnInfo(locnInfo);
		inputActy3.getMethStreams().add(qaQcMethStream3);
		
		ArrayList<DPMSMethActivity> actyList = new ArrayList<DPMSMethActivity>();
		actyList.add(inputActy1);
		actyList.add(inputActy2);
		actyList.add(inputActy3);
		
		
			
		//Long nameLocnId, Long qaQcValId, int numLimit,ArrayList<DPMSMethActivity> inputActyList
		MissingRecordsQAQC qaQc = new MissingRecordsQAQC(qaQcValId, qaQcValTypeId, numLimit, actyList);
		qaQc.runAlgorithm();
		qaQc.runAlgorithmOnlyCountNights();

		MissingRecordsQAQC qaQc2 = new MissingRecordsQAQC(qaQcValId, qaQcValTypeId, numLimit, actyList);
		inputActy3.setStartDate(tranTime2);
		inputActy2.setStartDate(tranTime3);
		qaQc2.runAlgorithm();
		qaQc2.runAlgorithmOnlyCountNights();

	}
}
