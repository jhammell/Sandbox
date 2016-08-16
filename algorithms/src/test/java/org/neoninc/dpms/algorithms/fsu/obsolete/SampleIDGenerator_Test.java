package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.testng.annotations.Test;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;
import org.testng.annotations.Test;

/**
 * Test driver for SampleIDGenerator.class.
 * 		
 * @author sgui, June 6, 2014
 */

public class SampleIDGenerator_Test {
	// Creates logger instance
	static private Logger log = Logger.getLogger(SampleIDGenerator_Test.class);

	// Instance of output method activity. It is instantiated here for
	// unit test purpose only. In reality, it should be instantiated in
	// transition level.
	DPMSMethActivity outputActy = new DPMSMethActivity();

	/**
	 * Test driver for small mammal
	 */
	@Test
	public void generateSampleIDs() {
		String plotID = "HARV_016";
		
		// tagID
		Long tagIDValId = 600L;
		
		// input value Id for blood, or fecal, or ear, or hair, or whisker
		Long inputValId = 601L;
		
		// L1 output value Id for sampleID of blood, or fecal, or ear, or hair, or whisker
		Long sampleIDValId = 801L;
				
		// Specifies the date for the test
		Calendar cal = Calendar.getInstance();
		cal.set(2013, Calendar.JUNE, 19); 
		Date date = cal.getTime(); 

		Date tranTime = cal.getTime();
		cal.set(2014, Calendar.MAY, 21);

		DPMSMethActivity inputActy = new DPMSMethActivity();
		// The date instance is used to differentiate it from transition time.
		inputActy.setStartDate(date);
		// The date instance is used to differentiate it from transition time.
		outputActy.setStartDate(date);

		// meth stream for tagID
		DPMSMethStreamData tagIDMs = new DPMSMethStreamData();
		// input meth stream for blood, or fecal, or ear, or hair, or whisker 
		DPMSMethStreamData inputMs = new DPMSMethStreamData();

		inputActy.getMethStreams().add(tagIDMs);
		inputActy.getMethStreams().add(inputMs);
		
		DPMSMStreamReadout rdotTagID = new DPMSMStreamReadout();
		tagIDMs.getMSReadouts().add(rdotTagID);
		rdotTagID.setReadoutTranTime(tranTime);
		rdotTagID.setValueStringForValueId(tagIDValId, "L0001", true);
//		rdotTagID.setValueStringForValueId(tagIDValId, null, true);

		DPMSMStreamReadout inputRdot = new DPMSMStreamReadout();
		inputMs.getMSReadouts().add(inputRdot);
		inputRdot.setReadoutTranTime(tranTime);
		// for blood
		inputRdot.setValueStringForValueId(inputValId, "B", true);
//		inputRdot.setValueStringForValueId(inputValId, "R", true);
//		inputRdot.setValueStringForValueId(inputValId, "M", true);
		// for fecal
//		inputRdot.setValueStringForValueId(inputValId, "F", true);
//		inputRdot.setValueStringForValueId(inputValId, "O", true);
		// for ear
//		inputRdot.setValueStringForValueId(inputValId, "E", true);
		// for hair
//		inputRdot.setValueStringForValueId(inputValId, "H", true);
		// for whisker
//		inputRdot.setValueStringForValueId(inputValId, "W", true);
		// for something weird
//		inputRdot.setValueStringForValueId(inputValId, "weird", true);
		
		// L1 output sampleID MethStream
		DPMSMethStreamData sampleIDMethStream = new DPMSMethStreamData();  

		// Test for normal algorithm execution.
		SampleIDGenerator algm = new SampleIDGenerator(
				inputActy, 
                plotID, 
                sampleIDMethStream,
                tagIDValId,
                inputValId,
                sampleIDValId
				);
		
		boolean result = algm.runAlgorithm();
		if(result) {
			outputActy.getMethStreams().add(sampleIDMethStream);
			ArrayList<DPMSMethStreamData> arrayMs = outputActy.getMethStreams(); 
			for (DPMSMethStreamData ms : arrayMs) {
				if(ms != null) {
					ArrayList<DPMSMStreamReadout> resRdotList = ms.getMSReadouts();
					for(DPMSMStreamReadout rDot : resRdotList) {
						log.debug("The QF value of sampleIDValId in readOut: '" + rDot.getValueStringForValueId(sampleIDValId) + "'");		
					}
				} else {
					log.error("For some reason, " + sampleIDValId + " resMs is null.");
				}		
			}		
		} else {
			log.error("The algorithm of BloodFecalEarHairWhiskerSampleIDs did not run successfully!!!");
		}
	}	
}
