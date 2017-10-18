package org.neoninc.dpms.algorithms.processors;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.BasicConfigurator;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMeasStreamUtilities;
import org.neoninc.dpms.datastructures.algorithms.PersistenceInputMeasStreams;

public class PersistenceQAQCProcessor_Test {

	static final String dataDirectory = "/testPersistence/";
	
	private String resourceDir = new String();

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeMethod
	public void setUp() throws Exception {
		
		BasicConfigurator.configure();
		
		/*
		 * Creating objects for the testing.
		 * Transition info
		 */
		URL resourceFile = this.getClass().getResource(dataDirectory);
		resourceDir = resourceFile.getPath();
	}

	/**
	 * Test method for {@link org.neoninc.dpms.algorithms.processors.PersistenceQAQCProcessor#runAlgorithm()}.
	 */
	@Test
	public void runInput1File() {
		// Input file
		String testFile1 = resourceDir + "soilTemp.csv";  //"input1.csv";
		
		// Data we will be interested in
		HashMap<Long, String> valNames = new HashMap<>();
		Long meanInputValId = 507L;  //1L;
		valNames.put(507L, "data");  //(1L, "data");
		valNames.put(2L, "PersistenceFlag");
		DPMSMeasStreamData measData = DPMSMeasStreamUtilities.readMeasStreamFromFile(testFile1);
		measData.sortByStartTimeStamps();
		assert(measData != null);
		
		// Give it name, start/end dates, frequency and calibrated values
		measData.setMeasStrmName("test1MeasStream");
		DPMSMeasStreamUtilities.setStartEndDateFromReadoutData(measData);
        
        // Data assumed frequency
        measData.setFrequencyInMilli(10000L);
        
        // Parameters.
		Long qaQcPersistFlagValId = 2L;
		double persistThreshold = 0.1;  //5.;
		double intervalThreshold = 3600;  //9.;
		
		// Build the input structure
		PersistenceInputMeasStreams input = new PersistenceInputMeasStreams(
				measData, meanInputValId, qaQcPersistFlagValId, null, null,
				persistThreshold, intervalThreshold);
		
		PersistenceQAQCProcessor processor = new PersistenceQAQCProcessor(input);
		processor.runAlgorithm();
		
		String testFileResults1 = resourceDir + "results_soilTemp.csv";   //"results_input1.csv";
		DPMSMeasStreamUtilities.writeDPMSMeasStreamDataToCsv(measData, testFileResults1, valNames);
	}
}
