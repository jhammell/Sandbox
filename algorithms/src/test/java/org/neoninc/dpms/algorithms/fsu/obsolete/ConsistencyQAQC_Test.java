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
 * Test driver for ConsistencyQAQC.class.
 * 		
 * @author sgui, June 6, 2014
 */

public class ConsistencyQAQC_Test {
	// Creates logger instance
	static private Logger log = Logger.getLogger(ConsistencyQAQC_Test.class);

	// Instance of output method activity. It is instantiated here for
	// unit test purpose only. In reality, it should be instantiated in
	// transition level.
	DPMSMethActivity outputActy = new DPMSMethActivity();

	/**
	 * Test driver
	 */
	@Test
	public void checkConsistencyQAQC() {
		// A list of value Ids for consistency test
		ArrayList<Long> fieldList = new ArrayList<Long>(); 
		
		// A list of DPMSMethActivity to be tested for consistency.
		ArrayList<DPMSMethActivity> actyList = new ArrayList<DPMSMethActivity>();
		
		// a new meth stream to hold QA flag
		DPMSMethStreamData qaQcMethStream = new DPMSMethStreamData(); 
		
		// tagID
		Long tagIDValId = 600L;
		
		// sex
		Long sexValId = 601L;
		
		// taxonID
		Long taxonIDValId = 602L;
		
		// lifeStage
		Long lifeStageValId = 6003L;
		fieldList.add(tagIDValId);
		fieldList.add(sexValId);
		
		// quality flag id for this test
		Long qaQcValId = 802L;  
		
		// Specifies the date for the test
		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.MAY, 1); 
		Date date = cal.getTime(); 

		Date tranTime = cal.getTime();
		cal.set(2014, Calendar.MAY, 21);

		DPMSMethActivity inputActy1 = new DPMSMethActivity();
		DPMSMethActivity inputActy2 = new DPMSMethActivity();
		DPMSMethActivity inputActy3 = new DPMSMethActivity();
		DPMSMethActivity inputActy4 = new DPMSMethActivity();
		// The date instance is used to differentiate it from transition time.
		inputActy1.setStartDate(date);
		inputActy2.setStartDate(date);
		inputActy3.setStartDate(date);
		inputActy4.setStartDate(date);
		// The date instance is used to differentiate it from transition time.
		outputActy.setStartDate(date);

		// tagID MethStream 
		DPMSMethStreamData tagIDMs = new DPMSMethStreamData();
		// sex MethStream 
		DPMSMethStreamData sexMs = new DPMSMethStreamData();
		// taxonID MethStream 
		DPMSMethStreamData taxonIDMs = new DPMSMethStreamData();
		// lifeStage MethStream 
		DPMSMethStreamData lifeStageMs = new DPMSMethStreamData();

		// tagID MethStream 
		DPMSMethStreamData tagIDMs2 = new DPMSMethStreamData();
		// sex MethStream 
		DPMSMethStreamData sexMs2 = new DPMSMethStreamData();
		// taxonID MethStream 
		DPMSMethStreamData taxonIDMs2 = new DPMSMethStreamData();
		// lifeStage MethStream 
		DPMSMethStreamData lifeStageMs2 = new DPMSMethStreamData();

		inputActy1.getMethStreams().add(tagIDMs);
		inputActy1.getMethStreams().add(sexMs);
		inputActy1.getMethStreams().add(taxonIDMs);
		inputActy1.getMethStreams().add(lifeStageMs);

		// for consistent data
//		inputActy2.getMethStreams().add(tagIDMs);
//		inputActy2.getMethStreams().add(sexMs);
//		inputActy2.getMethStreams().add(taxonIDMs);
//		inputActy2.getMethStreams().add(lifeStageMs);

		// for inconsistent data
		inputActy2.getMethStreams().add(tagIDMs2);
		inputActy2.getMethStreams().add(sexMs2);
		inputActy2.getMethStreams().add(taxonIDMs2);
		inputActy2.getMethStreams().add(lifeStageMs2);

		inputActy3.getMethStreams().add(tagIDMs);
		inputActy3.getMethStreams().add(sexMs);
		inputActy3.getMethStreams().add(taxonIDMs);
		inputActy3.getMethStreams().add(lifeStageMs);
		
		inputActy4.getMethStreams().add(tagIDMs);
		inputActy4.getMethStreams().add(sexMs);
		inputActy4.getMethStreams().add(taxonIDMs);
		inputActy4.getMethStreams().add(lifeStageMs);
		
		actyList.add(inputActy1);
		actyList.add(inputActy2);
		actyList.add(inputActy3);
		actyList.add(inputActy4);
		
		DPMSMStreamReadout rdotTagID = new DPMSMStreamReadout();
		DPMSMStreamReadout rdotSex = new DPMSMStreamReadout();
		DPMSMStreamReadout rdotTaxonID = new DPMSMStreamReadout();
		DPMSMStreamReadout rdotLifeStage = new DPMSMStreamReadout();

		DPMSMStreamReadout rdotTagID2 = new DPMSMStreamReadout();
		DPMSMStreamReadout rdotSex2 = new DPMSMStreamReadout();
		DPMSMStreamReadout rdotTaxonID2 = new DPMSMStreamReadout();
		DPMSMStreamReadout rdotLifeStage2 = new DPMSMStreamReadout();

		tagIDMs.getMSReadouts().add(rdotTagID);
		rdotTagID.setReadoutTranTime(tranTime);
		rdotTagID.setValueStringForValueId(tagIDValId, "L0001", true);

		tagIDMs2.getMSReadouts().add(rdotTagID2);
		rdotTagID2.setReadoutTranTime(tranTime);
		rdotTagID2.setValueStringForValueId(tagIDValId, "R0002", true);
		
		sexMs.getMSReadouts().add(rdotSex);
		rdotSex.setReadoutTranTime(tranTime);
		rdotSex.setValueStringForValueId(sexValId, "f", true);

		sexMs2.getMSReadouts().add(rdotSex2);
		rdotSex2.setReadoutTranTime(tranTime);
		rdotSex2.setValueStringForValueId(sexValId, "m", true);

		taxonIDMs.getMSReadouts().add(rdotTaxonID);
		rdotTaxonID.setReadoutTranTime(tranTime);
		rdotTaxonID.setValueStringForValueId(taxonIDValId, "PEMA", true);

		taxonIDMs2.getMSReadouts().add(rdotTaxonID2);
		rdotTaxonID2.setReadoutTranTime(tranTime);
		rdotTaxonID2.setValueStringForValueId(taxonIDValId, "FAKE", true);

		lifeStageMs.getMSReadouts().add(rdotLifeStage);
		rdotLifeStage.setReadoutTranTime(tranTime);
		rdotLifeStage.setValueStringForValueId(lifeStageValId, "A", true);

		lifeStageMs2.getMSReadouts().add(rdotLifeStage2);
		rdotLifeStage2.setReadoutTranTime(tranTime);
		rdotLifeStage2.setValueStringForValueId(lifeStageValId, "Z", true);

		// Test for normal algorithm execution.
		ConsistencyQAQC algm = new ConsistencyQAQC(
				actyList, 
                fieldList, 
                qaQcMethStream,
                qaQcValId
				);
		
		boolean result = algm.runAlgorithm();
		if(result) {
			outputActy.getMethStreams().add(qaQcMethStream);
			ArrayList<DPMSMethStreamData> arrayMs = outputActy.getMethStreams(); 
			for (DPMSMethStreamData ms : arrayMs) {
				if(ms != null) {
					ArrayList<DPMSMStreamReadout> resRdotList = ms.getMSReadouts();
					for(DPMSMStreamReadout rDot : resRdotList) {
						log.debug("The QF value of qaQcValId in readOut: '" + rDot.getValueForValueId(qaQcValId) + "'");		
					}
				} else {
					log.error("For some reason, " + qaQcValId + " resMs is null.");
				}		
			}		
		} else {
			log.error("The algorithm of ConsistencyQAQC did not run or did not run successfully!!!");
		}

	}
}
