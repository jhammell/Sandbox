/**
 * 
 */
package org.neoninc.dpms.algorithms.processors;

import static org.testng.AssertJUnit.assertTrue;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.net.URL;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.BasicConfigurator;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMeasStreamUtilities;
import org.neoninc.dpms.datastructures.algorithms.DespikingInputMeasStreams;

/**
 * @author fpradeau
 *
 */
public class DespikingQAQCProcessor_Test {
	static final String dataDirectory = "/testdespiking/";
	
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
	 * Test method for {@link org.neoninc.dpms.algorithms.processors.DespikingQAQCProcessor#runAlgorithm()}.
	 */
	@Test
	public void runInput1File() {
		// Input file
		String testFile1 = resourceDir + "input1.csv";
		
		// Data we will be interested in
		HashMap<Long, String> valNames = new HashMap<>();
		Long meanInputValId = 1L;
		valNames.put(1L, "data");
		valNames.put(2L, "spuriousFlag");
		valNames.put(3L, "feasibleFlag");
		DPMSMeasStreamData measData = DPMSMeasStreamUtilities.readMeasStreamFromFile(testFile1);
		measData.sortByStartTimeStamps();
		assert(measData != null);
		
		// Give it name, start/end dates, frequency and calibrated values
		measData.setMeasStrmName("test1MeasStream");
		DPMSMeasStreamUtilities.setStartEndDateFromReadoutData(measData);
        
        // Data assumed frequency
        measData.setFrequencyInMilli(10000L);
        
        // Parameters.
		Integer windowSize = 9;
		Integer windowStep = 1;
		Long qaQcSpuriousFlagValId = 2L;
		Long qaQcFeasibleFlagValId = 3L;
		Double madValueThreshold = 7.;
		Double scaleFactor = 1.4826;
		Long numSpikePointsThreshold = 4L;
		String methodName = "A";
		Double minSpikeCountPercentage = 10.;
		Double maxMissingPercentage = 10.;
		
		// Build the input structure
		DespikingInputMeasStreams input = new DespikingInputMeasStreams(measData, meanInputValId, null, null, windowSize, windowStep,  
				qaQcSpuriousFlagValId, qaQcFeasibleFlagValId, madValueThreshold, scaleFactor, numSpikePointsThreshold, methodName, minSpikeCountPercentage, 
				maxMissingPercentage);
		
		DespikingQAQCProcessor processor = new DespikingQAQCProcessor(input);
		processor.runAlgorithm();
		
		String testFileResults1 = resourceDir + "resultsAinput1.csv";
		DPMSMeasStreamUtilities.writeDPMSMeasStreamDataToCsv(measData, testFileResults1, valNames);

		// Now run method B
		methodName = "B";
		// Build the input structure
		DespikingInputMeasStreams inputB = new DespikingInputMeasStreams(measData, meanInputValId, null, null, windowSize, windowStep,  
				qaQcSpuriousFlagValId, qaQcFeasibleFlagValId, madValueThreshold, scaleFactor, numSpikePointsThreshold, methodName, minSpikeCountPercentage, 
				maxMissingPercentage);
		
		DespikingQAQCProcessor processorB = new DespikingQAQCProcessor(inputB);
		processorB.runAlgorithm();
		
		String testFileResultsB = resourceDir + "resultsBinput1.csv";
		DPMSMeasStreamUtilities.writeDPMSMeasStreamDataToCsv(measData, testFileResultsB, valNames);
		
	}

	/**
	 * Test method for {@link org.neoninc.dpms.algorithms.processors.DespikingQAQCProcessor#runAlgorithm()}.
	 */
	@Test(enabled = false)
	public void runInput2File() {
		// Input file
		String testFile = resourceDir + "input2.csv";
		
		// Data we will be interested in
		HashMap<Long, String> valNames = new HashMap<>();
		Long meanInputValId = 1L;
		valNames.put(1L, "data");
		valNames.put(2L, "spuriousFlag");
		valNames.put(3L, "feasibleFlag");
		DPMSMeasStreamData measData = DPMSMeasStreamUtilities.readMeasStreamFromFile(testFile);
		assert(measData != null);
		
		// Give it name, start/end dates, frequency and calibrated values
		measData.setMeasStrmName("testMeasStream");
		DPMSMeasStreamUtilities.setStartEndDateFromReadoutData(measData);
        
        // Data assumed frequency
        measData.setFrequencyInMilli(10000L);
        
        // Parameters.
		Integer windowSize = 9;
		Integer windowStep = 1;
		Long qaQcSpuriousFlagValId = 2L;
		Long qaQcFeasibleFlagValId = 3L;
		Double madValueThreshold = 7.;
		Double scaleFactor = 1.4826;
		Long numSpikePointsThreshold = 4L;
		String methodName = "A";
		Double minSpikeCountPercentage = 10.;
		Double maxMissingPercentage = 10.;
		
		// Build the input structure
		DespikingInputMeasStreams input = new DespikingInputMeasStreams(measData, meanInputValId, null, null, windowSize, windowStep,  
				qaQcSpuriousFlagValId, qaQcFeasibleFlagValId, madValueThreshold, scaleFactor, numSpikePointsThreshold, methodName, minSpikeCountPercentage, 
				maxMissingPercentage);
		
		DespikingQAQCProcessor processor = new DespikingQAQCProcessor(input);
		processor.runAlgorithm();
		
		String testFileResults = resourceDir + "resultsAinput2.csv";
		DPMSMeasStreamUtilities.writeDPMSMeasStreamDataToCsv(measData, testFileResults, valNames);

		// Now run method B
		methodName = "B";
		// Build the input structure
		DespikingInputMeasStreams inputB = new DespikingInputMeasStreams(measData, meanInputValId, null, null, windowSize, windowStep,  
				qaQcSpuriousFlagValId, qaQcFeasibleFlagValId, madValueThreshold, scaleFactor, numSpikePointsThreshold, methodName, minSpikeCountPercentage, 
				maxMissingPercentage);
		
		DespikingQAQCProcessor processorB = new DespikingQAQCProcessor(inputB);
		processorB.runAlgorithm();
		
		String testFileResultsB = resourceDir + "resultsBinput2.csv";
		DPMSMeasStreamUtilities.writeDPMSMeasStreamDataToCsv(measData, testFileResultsB, valNames);
		
	}

	/**
	 * Test method for {@link org.neoninc.dpms.algorithms.processors.DespikingQAQCProcessor#runAlgorithm()}.
	 */
	@Test(enabled = false)
	public void runMSFile() {
		// Input file
		String testFile = resourceDir + "MS11_PAR.csv";
		
		// Data we will be interested in
		HashMap<Long, String> valNames = new HashMap<>();
		Long meanInputValId = 6L;
		valNames.put(6L, "data");
		valNames.put(2L, "spuriousFlag");
		valNames.put(3L, "feasibleFlag");
		DPMSMeasStreamData measData = DPMSMeasStreamUtilities.readMeasStreamFromFile(testFile);
		assert(measData != null);
		
		// Give it name, start/end dates, frequency and calibrated values
		measData.setMeasStrmName("testPARMeasStream");
		DPMSMeasStreamUtilities.setStartEndDateFromReadoutData(measData);
        
        // Data assumed frequency
        measData.setFrequencyInMilli(1000L);
        
        // Parameters.
		Integer windowSize = 1800;
		Integer windowStep = 1;
		Long qaQcSpuriousFlagValId = 2L;
		Long qaQcFeasibleFlagValId = 3L;
		Double madValueThreshold = 7.;
		Double scaleFactor = 1.4826;
		Long numSpikePointsThreshold = 4L;
		String methodName = "A";
		Double minSpikeCountPercentage = 10.;
		Double maxMissingPercentage = 10.;
		
		// Build the input structure
		DespikingInputMeasStreams input = new DespikingInputMeasStreams(measData, meanInputValId, null, null, windowSize, windowStep,  
				qaQcSpuriousFlagValId, qaQcFeasibleFlagValId, madValueThreshold, scaleFactor, numSpikePointsThreshold, methodName, minSpikeCountPercentage, 
				maxMissingPercentage);
		
		DespikingQAQCProcessor processor = new DespikingQAQCProcessor(input);
		processor.runAlgorithm();
		
		String testFileResults = resourceDir + "resultsAMs.csv";
		DPMSMeasStreamUtilities.writeDPMSMeasStreamDataToCsv(measData, testFileResults, valNames);

		// Now run method B
		methodName = "B";
		// Build the input structure
		DespikingInputMeasStreams inputB = new DespikingInputMeasStreams(measData, meanInputValId, null, null, windowSize, windowStep,  
				qaQcSpuriousFlagValId, qaQcFeasibleFlagValId, madValueThreshold, scaleFactor, numSpikePointsThreshold, methodName, minSpikeCountPercentage, 
				maxMissingPercentage);
		
		DespikingQAQCProcessor processorB = new DespikingQAQCProcessor(inputB);
		processorB.runAlgorithm();
		
		String testFileResultsB = resourceDir + "resultsBMs.csv";
		DPMSMeasStreamUtilities.writeDPMSMeasStreamDataToCsv(measData, testFileResultsB, valNames);
		
	}
}
