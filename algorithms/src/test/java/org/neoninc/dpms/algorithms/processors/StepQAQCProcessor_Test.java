/**
 * 
 */
package org.neoninc.dpms.algorithms.processors;

import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.net.URL;
import java.util.HashMap;
import org.apache.log4j.BasicConfigurator;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMeasStreamUtilities;
import org.neoninc.dpms.datastructures.algorithms.QAQCInputMeasStreams;

public class StepQAQCProcessor_Test {
	static final String dataDirectory = "/testStep/";
	
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
	 * Test method for {@link org.neoninc.dpms.algorithms.processors.StepQAQCProcessor#runAlgorithm()}.
	 */
	@Test
	public void runInput1File() {
		// Input file
		String testFile1 = resourceDir + "input1.csv";
		
		// Data we will be interested in
		HashMap<Long, String> valNames = new HashMap<>();
		Long meanInputValId = 1L;
		valNames.put(1L, "data");
		valNames.put(2L, "stepFlag");
		DPMSMeasStreamData measData = DPMSMeasStreamUtilities.readMeasStreamFromFile(testFile1);
		measData.sortByStartTimeStamps();
		assert(measData != null);
		
		// Give it name, start/end dates, frequency and calibrated values
		measData.setMeasStrmName("test1MeasStream");
		DPMSMeasStreamUtilities.setStartEndDateFromReadoutData(measData);
        
        // Data assumed frequency
        measData.setFrequencyInMilli(10000L);
        
        // Parameters.
		Long qaQcStepFlagValId = 2L;
		Double stepThreshold = 65.;
		
		// Build the input structure
		QAQCInputMeasStreams input = new QAQCInputMeasStreams(measData, meanInputValId,
				qaQcStepFlagValId, null, null, null, null, stepThreshold);
		
		StepQAQCProcessor processor = new StepQAQCProcessor(input);
		processor.runAlgorithm();
		
		String testFileResults1 = resourceDir + "results_input1.csv";
		DPMSMeasStreamUtilities.writeDPMSMeasStreamDataToCsv(measData, testFileResults1, valNames);

	}
}
