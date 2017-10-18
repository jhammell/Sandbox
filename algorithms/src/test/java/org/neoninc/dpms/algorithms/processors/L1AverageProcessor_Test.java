package org.neoninc.dpms.algorithms.processors;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.BasicConfigurator;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMeasStreamUtilities;
import org.neoninc.dpms.datastructures.algorithms.DespikingInputMeasStreams;
import org.neoninc.dpms.datastructures.algorithms.L1AverageInputMeasStreams;
import org.neoninc.dpms.datastructures.algorithms.L1AverageOutputMeasStreams;

public class L1AverageProcessor_Test {
	static final String dataDirectory = "/testL1Average/";
	
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

	@Test
	public void testRunAlgorithm() {
		// Input file
		String testFile1 = resourceDir + "input1.csv";
		
		// Data we will be interested in
		HashMap<Long, String> valNames = new HashMap<>();
		Long meanInputValId = 1L;
		Long averageValId = 2L;
		Long maxValId = 3L;
		Long minValId = 4L;
		Long numPtsValId = 5L;
		Long varianceValId = 6L;
		
		valNames.put(1L, "data");
		valNames.put(2L, "average");
		valNames.put(3L, "maximum");
		valNames.put(4L, "minimum");
		valNames.put(5L, "numPoints");
		valNames.put(6L, "variance");
		long combinedUValId = 7L;
		valNames.put(combinedUValId, "combinedUncert");
		long expandedUValId = 8L;
		valNames.put(expandedUValId, "expandedUncert");
		DPMSMeasStreamData measData = DPMSMeasStreamUtilities.readMeasStreamFromFile(testFile1);
		measData.sortByStartTimeStamps();
		assert(measData != null);
		
		// Give it name, start/end dates, frequency and calibrated values
		measData.setMeasStrmName("test1MeasStream");
		DPMSMeasStreamUtilities.setStartEndDateFromReadoutData(measData);
        
        // Data assumed frequency
        measData.setFrequencyInMilli(10000L);
        
		
		// Build the input structure
        ArrayList<Long> flagsToIgnore = new ArrayList<>();
		L1AverageInputMeasStreams input = new L1AverageInputMeasStreams(measData, meanInputValId, meanInputValId, flagsToIgnore, null, null);
		L1AverageOutputMeasStreams output = new L1AverageOutputMeasStreams();
		
		// Regular run - all data
		DPMSMeasStreamData measDataOut = new DPMSMeasStreamData();
		measDataOut.setMeasStrmName("results1mininput");
		output.setOutputDataStream(measDataOut, averageValId, combinedUValId, expandedUValId, numPtsValId);
		output.setMaxDataStream(measDataOut, maxValId);
		output.setMinDataStream(measDataOut, minValId);
		output.setVarianceDataStream(measDataOut, varianceValId);

		// One minute average
		L1AverageProcessor processor = new L1AverageProcessor(input, output, 60L);
		processor.runAlgorithm();
		
		String testFileResults1 = resourceDir + "results1mininput1.csv";
		DPMSMeasStreamUtilities.writeDPMSMeasStreamDataToCsv(measDataOut, testFileResults1, valNames);

		// Re-run with subset only - take off 2 minutes
		measDataOut.getMSReadouts().clear();
		Long deltaInMillis = 120L*1000L;
		Long t1 = measData.getStartDate().toGregorianCalendar().getTimeInMillis();
		Date stDate = new Date(t1+deltaInMillis);
		Date eDate = new Date(measData.getEndDate().toGregorianCalendar().getTimeInMillis() - deltaInMillis);
		L1AverageInputMeasStreams input2 = new L1AverageInputMeasStreams(measData, meanInputValId, meanInputValId, flagsToIgnore, stDate, eDate);
		
		// One minute average
		L1AverageProcessor processor2 = new L1AverageProcessor(input2, output, 60L);
		processor2.runAlgorithm();
		String testFileResults2 = resourceDir + "results1mininput1Delay.csv";
		DPMSMeasStreamUtilities.writeDPMSMeasStreamDataToCsv(measDataOut, testFileResults2, valNames);
		
		// 3 minute average
		L1AverageOutputMeasStreams output3 = new L1AverageOutputMeasStreams();
		DPMSMeasStreamData measDataOut3 = new DPMSMeasStreamData();
		measDataOut3.setMeasStrmName("results3mininput");
		output3.setOutputDataStream(measDataOut3, averageValId, combinedUValId, expandedUValId, numPtsValId);
		output3.setMaxDataStream(measDataOut3, maxValId);
		output3.setMinDataStream(measDataOut3, minValId);
		output3.setVarianceDataStream(measDataOut3, varianceValId);
		L1AverageProcessor processor3 = new L1AverageProcessor(input, output3, 180L);
		processor3.runAlgorithm();
		
		String testFileResults3 = resourceDir + "results3mininput1.csv";
		DPMSMeasStreamUtilities.writeDPMSMeasStreamDataToCsv(measDataOut3, testFileResults3, valNames);

	}

}
