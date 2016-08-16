package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.neoninc.dpms.algorithms.fsu.obsolete.CompleteFateQF;
import org.neoninc.dpms.algorithms.fsu.CompleteTrapCoordQF;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

/**
 * Test driver for CompleteTrapCoordFateQFs.class.
 * 		
 * With method activity, we should be able to get siteId. If the 
 * method activity type is bout, we should have its siteID's NID, 
 * then obtain the NID's associated valID (or locationID). Using that 
 * valID (or locationID) we should be able to get the value of siteID.
 *  
 * The same methodology applies to the method activity types of night 
 * and capture data. The code to get siteID is expected to be implemented 
 * in transition level.
 * @author sgui, May 30, 2014
 */

public class CompleteTrapCoordFateQFs_Test {
	// Creates logger instance
	static private Logger log = Logger.getLogger(CompleteTrapCoordFateQFs_Test.class);

	// Instance of output method activity. It is instantiated here for
	// unit test purpose only. In reality, it should be instantiated in
	// transition level.
	DPMSMethActivity outputActy = new DPMSMethActivity();

	/**
	 * Test driver
	 */
//	@Test
	public void checkCompleteTrapCoordinateFateQFs() {
		// trapCoordinate
		Long trapCoordValId = 600L;
		
		// trapStatus
		Long trapStatusValId = 601L;
		
		// completeTrapCoordinateQF
		Long compTrapCoordQFValId = 700L;
		
		// L1 trapCoordinate
		Long pubTrapCoordValId = 750L;
			
		// fate
		Long fateValId = 800L;
		
		// taxonID
		Long taxonIDValId = 801L;
		
		// completeFateQF
		Long compFateQFValId = 900L;

		// L1 fate
		Long pubFateValId = 950L;
		
		// Specifies the date for the test
		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.MAY, 1); 
		Date date = cal.getTime(); 

		Date tranTime = cal.getTime();
		cal.set(2014, Calendar.MAY, 21);

		DPMSMethActivity inputActy = new DPMSMethActivity();
		// The date instance is used to differentiate it from transition time.
		inputActy.setStartDate(date);
		// The date instance is used to differentiate it from transition time.
		outputActy.setStartDate(date);

		// trapCoordinate 
		DPMSMethStreamData trapCoordMs = new DPMSMethStreamData();
		// trapStatus 
		DPMSMethStreamData trapStatusMs = new DPMSMethStreamData();
		// fate 
		DPMSMethStreamData fateMs = new DPMSMethStreamData();
		// taxonID 
		DPMSMethStreamData taxonIDMs = new DPMSMethStreamData();

		inputActy.getMethStreams().add(trapCoordMs);
		inputActy.getMethStreams().add(trapStatusMs);
		inputActy.getMethStreams().add(fateMs);
		inputActy.getMethStreams().add(taxonIDMs);
		
		DPMSMStreamReadout rdotTrapCoord = new DPMSMStreamReadout();
		trapCoordMs.getMSReadouts().add(rdotTrapCoord);
		rdotTrapCoord.setReadoutTranTime(tranTime);
//		rdotTrapCoord.setValueStringForValueId(trapCoordValId, "A9", true);
		rdotTrapCoord.setValueStringForValueId(trapCoordValId, null, true);

		DPMSMStreamReadout rdotTrapStatus = new DPMSMStreamReadout();
		trapStatusMs.getMSReadouts().add(rdotTrapStatus);
		rdotTrapStatus.setReadoutTranTime(tranTime);
//		rdotTrapStatus.setValueStringForValueId(trapStatusValId, "1", true);
		rdotTrapStatus.setValueStringForValueId(trapStatusValId, "2", true);

		DPMSMStreamReadout rdotFate = new DPMSMStreamReadout();
		fateMs.getMSReadouts().add(rdotFate);
		rdotFate.setReadoutTranTime(tranTime);
		rdotFate.setValueStringForValueId(fateValId, "N", true);
//		rdotFate.setValueStringForValueId(fateValId, null, true);

		DPMSMStreamReadout rdotTaxonID = new DPMSMStreamReadout();
		taxonIDMs.getMSReadouts().add(rdotTaxonID);
		rdotTaxonID.setReadoutTranTime(tranTime);
		rdotTaxonID.setValueStringForValueId(taxonIDValId, "PEMA", true);
		
		// L1 trapCoordinateMethStream
		DPMSMethStreamData trapCoordMethStream = new DPMSMethStreamData();  
		// L1 fateMethStream
		DPMSMethStreamData fateMethStream = new DPMSMethStreamData();  

		// Test for normal algorithm execution.
		CompleteTrapCoordFateQFs algm = new CompleteTrapCoordFateQFs(
				inputActy, 
                trapCoordMethStream, 
                fateMethStream,
                trapCoordValId,
                trapStatusValId,
                fateValId,
                taxonIDValId,
                compTrapCoordQFValId, 
                compFateQFValId,
                pubTrapCoordValId,
                pubFateValId
				);
		
		boolean result = algm.runAlgorithm();
		if(result) {
			outputActy.getMethStreams().add(trapCoordMethStream);
			outputActy.getMethStreams().add(fateMethStream);
			ArrayList<DPMSMethStreamData> arrayMs = outputActy.getMethStreams(); 
			for (DPMSMethStreamData ms : arrayMs) {
				if(ms != null) {
					ArrayList<DPMSMStreamReadout> resRdotList = ms.getMSReadouts();
					for(DPMSMStreamReadout rDot : resRdotList) {
						log.debug("The QF value of compTrapCoordQFValId in readOut: '" + rDot.getValueForValueId(compTrapCoordQFValId) + "'");		
						log.debug("The L1 value of trapCoordinate in readOut: '" + rDot.getValueStringForValueId(pubTrapCoordValId) + "'");		
						log.debug("The QF value of compFateQFValId in readOut: '" + rDot.getValueForValueId(compFateQFValId) + "'");		
						log.debug("The L1 value of fate in readOut: '" + rDot.getValueStringForValueId(pubFateValId) + "'");		
					}
				} else {
					log.error("For some reason, " + compFateQFValId + " resMs is null.");
				}		
			}		
		} else {
			log.error("The algorithm of CompleteTrapCoordFateQFs did not run successfully!!!");
		}
	}
	
//	@Test
	public void checkCompleteFateQF() {

		// fate
		Long fateValId = 800L;
		Long invalidFateQFValId = 700L;
		
		// taxonID
		Long taxonIDValId = 801L;
		Long invalidTaxonIDValId = 701L;
		
		// completeFateQF ValID
		Long compFateQFValId = 900L;

		// completeFateQF ValTypeId
		Long compFateQFValTypeId = 950L;
		
		// Specifies the date for the test
		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.MAY, 1); 
		Date date = cal.getTime(); 

		Date tranTime = cal.getTime();
		cal.set(2014, Calendar.MAY, 21);

		// create test inputs
		DPMSMethActivity outputActy = new DPMSMethActivity();
		// The date instance is used to differentiate it from transition time.
		outputActy.setStartDate(date);
		// The date instance is used to differentiate it from transition time.
		outputActy.setStartDate(date);

		// fate 
		DPMSMethStreamData fateMs = new DPMSMethStreamData();
		fateMs.setMStreamValTypeID(22L);
		outputActy.getMethStreams().add(fateMs);
		// taxonID 
		DPMSMethStreamData taxonIDMs = new DPMSMethStreamData();
		taxonIDMs.setMStreamValTypeID(23L);
		outputActy.getMethStreams().add(taxonIDMs);
		
		// create the place for test outputs to go
		// completeFateQF
		DPMSMethStreamData completeFateMs = new DPMSMethStreamData();
		completeFateMs.setMStreamValTypeID(compFateQFValTypeId);
		outputActy.getMethStreams().add(completeFateMs);

		// Test for normal algorithm execution.
		CompleteFateQF algm = new CompleteFateQF(
                fateValId, invalidFateQFValId,
                taxonIDValId, invalidTaxonIDValId,
                compFateQFValId, compFateQFValTypeId
				);
		
		// insert test input data and run the algorithm
		// test 1: fate and QF == valid values;  taxonID and QF == valid, non-empty values
		//   resulting completeFateQF should be 0.0
		fateMs.addReadoutValueString(date, date, fateValId, "N");
		fateMs.addReadoutValue(date, date, invalidFateQFValId, 0.0);
		taxonIDMs.addReadoutValueString(date, date, taxonIDValId, "PEMA");
		taxonIDMs.addReadoutValue(date, date, invalidTaxonIDValId, 0.0);
		Double expectedResult = new Double(0.0);
		
		boolean result = algm.runAlgorithm(outputActy);
		if(result) {
			Double completeFateQF = outputActy.findValueByValId(compFateQFValId);
			if ( (completeFateQF == null) || (completeFateQF.compareTo(expectedResult) != 0) ) {
				log.error("checkCompleteFateQF: test 1 failed");
				result = false;
			} else {
				result = true;
			}
		} else {
			log.error("checkCompleteFateQF: CompleteFateQF algorithm for test 1 did not run successfully!!!");
		}
		AssertJUnit.assertTrue(result);
		
		// clear the old values
		fateMs.getMSReadouts().clear();
		taxonIDMs.getMSReadouts().clear();
		completeFateMs.getMSReadouts().clear();
		
		// insert test input data and run the algorithm
		// test 2: fate==null, QF == 0.0;  taxonID and QF == valid, non-empty values
		//   resulting completeFateQF should be 1.0
		fateMs.addReadoutValueString(date, date, fateValId, null);
		fateMs.addReadoutValue(date, date, invalidFateQFValId, 0.0);
		taxonIDMs.addReadoutValueString(date, date, taxonIDValId, "PEMA");
		taxonIDMs.addReadoutValue(date, date, invalidTaxonIDValId, 0.0);
		expectedResult = new Double(1.0);
		
		result = algm.runAlgorithm(outputActy);
		if(result) {
			Double completeFateQF = outputActy.findValueByValId(compFateQFValId);
			if ( (completeFateQF == null) || (completeFateQF.compareTo(expectedResult) != 0) ) {
				log.error("checkCompleteFateQF: test 2 failed");
				result = false;
			} else {
				result = true;
			}
		} else {
			log.error("checkCompleteFateQF: CompleteFateQF algorithm for test 2 did not run successfully!!!");
		}
		AssertJUnit.assertTrue(result);
		
		// clear the old values
		fateMs.getMSReadouts().clear();
		taxonIDMs.getMSReadouts().clear();
		completeFateMs.getMSReadouts().clear();
		
		// insert test input data and run the algorithm
		// test 3: fate==invalid, QF == 1.0;  taxonID and QF == valid, non-empty values
		//   resulting completeFateQF should be -1.0
		fateMs.addReadoutValueString(date, date, fateValId, null);
		fateMs.addReadoutValue(date, date, invalidFateQFValId, 1.0);
		taxonIDMs.addReadoutValueString(date, date, taxonIDValId, "PEMA");
		taxonIDMs.addReadoutValue(date, date, invalidTaxonIDValId, 0.0);
		expectedResult = new Double(-1.0);
		
		result = algm.runAlgorithm(outputActy);
		if(result) {
			Double completeFateQF = outputActy.findValueByValId(compFateQFValId);
			if ( (completeFateQF == null) || (completeFateQF.compareTo(expectedResult) != 0) ) {
				log.error("checkCompleteFateQF: test 3 failed");
				result = false;
			} else {
				result = true;
			}
		} else {
			log.error("checkCompleteFateQF: CompleteFateQF algorithm for test 3 did not run successfully!!!");
		}
		AssertJUnit.assertTrue(result);
		
	}
	
	
//	@Test
	public void checkCompleteTrapCoordQF() {

		// trap coordinate
		Long trapCoordValId = 800L;
		Long invalidTrapCoordQFValId = 700L;
		
		// trap status
		Long trapStatusValId = 801L;
		Long invalidTrapStatusValId = 701L;
		
		// completeTrapCoordinateQF ValID
		Long compTrapCoordValId = 900L;

		// completeFateQF ValTypeId
		Long compTrapCoordQFValTypeId = 950L;
		
		// Specifies the date for the test
		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.MAY, 1); 
		Date date = cal.getTime(); 

		Date tranTime = cal.getTime();
		cal.set(2014, Calendar.MAY, 21);

		// create test inputs
		DPMSMethActivity outputActy = new DPMSMethActivity();
		// The date instance is used to differentiate it from transition time.
		outputActy.setStartDate(date);
		// The date instance is used to differentiate it from transition time.
		outputActy.setStartDate(date);
		
		// need an associated input activity for the trap status data
		DPMSMethActivity inputActy = outputActy.shallowCopy();

		// trap coordinate
		DPMSMethStreamData trapCoordMs = new DPMSMethStreamData();
		trapCoordMs.setMStreamValTypeID(22L);
		outputActy.getMethStreams().add(trapCoordMs);
		
		// trap status is in L0 (input) data only, not in L1 (output) data 
		DPMSMethStreamData trapStatusMs = new DPMSMethStreamData();
		trapStatusMs.setMStreamValTypeID(22L);
		inputActy.getMethStreams().add(trapStatusMs);
		// invalidTrapStatusQF goes in the L1 (output) data
		DPMSMethStreamData invalidTrapStatusMs = new DPMSMethStreamData();
		invalidTrapStatusMs.setMStreamValTypeID(22L);
		outputActy.getMethStreams().add(invalidTrapStatusMs);
		
		// create the place for test outputs to go
		// completeFateQF
		DPMSMethStreamData completeTrapCoordMs = new DPMSMethStreamData();
		completeTrapCoordMs.setMStreamValTypeID(compTrapCoordQFValTypeId);
		outputActy.getMethStreams().add(completeTrapCoordMs);

		// Test for normal algorithm execution.
		CompleteTrapCoordQF algm = new CompleteTrapCoordQF(
				trapCoordValId, invalidTrapCoordQFValId,
				trapStatusValId, invalidTrapStatusValId,
				compTrapCoordValId, compTrapCoordQFValTypeId
				);
		
		// insert test input data and run the algorithm
		// test 1: trapCoord and QF == valid values;  trapStatus and QF == valid, non-empty values
		//   resulting completeTrapCoordinateQF should be 0.0
		trapCoordMs.addReadoutValueString(date, date, trapCoordValId, "A01");
		trapCoordMs.addReadoutValue(date, date, invalidTrapCoordQFValId, 0.0);
		trapStatusMs.addReadoutValueString(date, date, trapStatusValId, "1");
		invalidTrapStatusMs.addReadoutValue(date, date, invalidTrapStatusValId, 0.0);
		Double expectedResult = new Double(0.0);
		
		boolean result = algm.runAlgorithm(outputActy);
		if(result) {
			Double completeTrapCoordQF = outputActy.findValueByValId(compTrapCoordValId);
			if ( (completeTrapCoordQF == null) || (completeTrapCoordQF.compareTo(expectedResult) != 0) ) {
				log.error("checkCompleteTrapCoordQF: test 1 failed");
				result = false;
			} else {
				result = true;
			}
		} else {
			log.error("checkCompleteTrapCoordQF: CompleteTrapCoordQF algorithm for test 1 did not run successfully!!!");
		}
		AssertJUnit.assertTrue(result);
		
		// clear the old values
		trapCoordMs.getMSReadouts().clear();
		trapStatusMs.getMSReadouts().clear();
		invalidTrapStatusMs.getMSReadouts().clear();
		completeTrapCoordMs.getMSReadouts().clear();
		
		// insert test input data and run the algorithm
		// test 2: trapCoord==null, QF == 0.0;  trapStatus == non-valid and QF == valid, non-empty values
		//   resulting completeTrapCoordinateQF should be 1.0
		trapCoordMs.addReadoutValueString(date, date, trapCoordValId, null);
		trapCoordMs.addReadoutValue(date, date, invalidTrapCoordQFValId, 0.0);
		trapStatusMs.addReadoutValueString(date, date, trapStatusValId, "7");
		invalidTrapStatusMs.addReadoutValue(date, date, invalidTrapStatusValId, 0.0);
		expectedResult = new Double(1.0);
		
		result = algm.runAlgorithm(outputActy);
		if(result) {
			Double completeTrapCoordQF = outputActy.findValueByValId(compTrapCoordValId);
			if ( (completeTrapCoordQF == null) || (completeTrapCoordQF.compareTo(expectedResult) != 0) ) {
				log.error("checkCompleteTrapCoordQF: test 2 failed");
				result = false;
			} else {
				result = true;
			}
		} else {
			log.error("checkCompleteTrapCoordQF: CompleteTrapCoordQF algorithm for test 2 did not run successfully!!!");
		}
		AssertJUnit.assertTrue(result);
		
		// clear the old values
		trapCoordMs.getMSReadouts().clear();
		trapStatusMs.getMSReadouts().clear();
		invalidTrapStatusMs.getMSReadouts().clear();
		completeTrapCoordMs.getMSReadouts().clear();
		
		// insert test input data and run the algorithm
		// test 3: trapCoord==invalid, QF == 1.0;  trapStatus and QF == valid, non-empty values
		//   resulting completeTrapCoordinateQF should be -1.0
		trapCoordMs.addReadoutValueString(date, date, trapCoordValId, null);
		trapCoordMs.addReadoutValue(date, date, invalidTrapCoordQFValId, 1.0);
		trapStatusMs.addReadoutValueString(date, date, trapStatusValId, "1");
		invalidTrapStatusMs.addReadoutValue(date, date, invalidTrapStatusValId, 0.0);
		expectedResult = new Double(-1.0);
		
		result = algm.runAlgorithm(outputActy);
		if(result) {
			Double completeTrapCoordQF = outputActy.findValueByValId(compTrapCoordValId);
			if ( (completeTrapCoordQF == null) || (completeTrapCoordQF.compareTo(expectedResult) != 0) ) {
				log.error("checkCompleteTrapCoordQF: test 3 failed");
				result = false;
			} else {
				result = true;
			}
		} else {
			log.error("checkCompleteTrapCoordQF: CompleteTrapCoordQF algorithm for test 3 did not run successfully!!!");
		}
		AssertJUnit.assertTrue(result);
		
	}
}
