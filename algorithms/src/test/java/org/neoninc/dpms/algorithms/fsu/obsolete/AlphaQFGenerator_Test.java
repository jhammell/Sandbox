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

/**
 * @author lzhang
 *
 */
public class AlphaQFGenerator_Test {
	private DPMSMethActivity acty = new DPMSMethActivity();
	Calendar cal = Calendar.getInstance();
	private Date startTime;
	private Date endTime;

	@BeforeMethod
	@BeforeClass
	public void setUp() throws Exception {
		
		BasicConfigurator.configure();
		cal.set(2014, Calendar.APRIL, 1);
		startTime = cal.getTime();
		cal.set(2014, Calendar.MAY, 21); //Year, month and day of month
		endTime = cal.getTime();
	}
	
	@Test
	public void testAlphaQFGenerator () {
		ArrayList<Long> qfValIdList = new ArrayList<Long>();
		Long qfCompRecValId = 206l;
		Long qfInvalidIRValId = 207l;
		Long qfDupNightValId = 209l;
		Long qfMissingNightValId = 210l;
		
		qfValIdList.add(qfCompRecValId);  //bout complete record
		qfValIdList.add(qfInvalidIRValId);  //bout invalidIdentificationReferences
		qfValIdList.add(qfDupNightValId);  //bout duplicateNight
		qfValIdList.add(qfMissingNightValId);  //bout missingRecordsPerBout
		
		DPMSMethStreamData msCompRec = new DPMSMethStreamData();
		msCompRec.addReadoutValue(startTime, endTime, qfCompRecValId, 0.);
		msCompRec.setMStreamValTypeID(2l);
		
		DPMSMethStreamData msInvalidIR = new DPMSMethStreamData();
		msInvalidIR.addReadoutValue(startTime, endTime, qfInvalidIRValId, 0.);
		msInvalidIR.setMStreamValTypeID(3l);
		
		DPMSMethStreamData msDupNight = new DPMSMethStreamData();
		msDupNight.addReadoutValue(startTime, endTime, qfDupNightValId, 0.);
		msDupNight.setMStreamValTypeID(4l);
		
		DPMSMethStreamData msMissingNight = new DPMSMethStreamData();
		msMissingNight.addReadoutValue(startTime, endTime, qfMissingNightValId, 1.);
		msMissingNight.setMStreamValTypeID(5l);
		
		acty.getMethStreams().add(msCompRec);
		acty.getMethStreams().add(msInvalidIR);
		acty.getMethStreams().add(msDupNight);
		acty.getMethStreams().add(msMissingNight);
		
		DPMSMethStreamData msAlpha = new DPMSMethStreamData();
		Long alphaValTypeId = 1l;
		msAlpha.setMStreamValTypeID(alphaValTypeId);
		acty.getMethStreams().add(msAlpha);
		
		Long alphaQFValId = 205l;
		System.out.println(acty.findValueByValId(alphaQFValId));
		
		DPMSMethStreamData tmp = acty.findMethStreamByValTypeID(alphaValTypeId);
		
		AlphaQFGenerator.generateAlphaQF(acty, alphaQFValId, tmp, qfValIdList);
		System.out.println(acty.findValueByValId(alphaQFValId));
	}
}
