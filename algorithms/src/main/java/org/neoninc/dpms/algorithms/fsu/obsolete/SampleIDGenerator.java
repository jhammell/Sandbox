package org.neoninc.dpms.algorithms.fsu.obsolete;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * This class implements algorithm of generating Sample ID. It is currently used  
 * by 5.1.2 - 11 of NEON.DOC.001244. The intention is to make this class generic.
 *
 * @author sgui, June 6, 2014
 */
public class SampleIDGenerator {
	// Instantiates logger instance
	static private Logger log = Logger.getLogger(SampleIDGenerator.class);
	
	// final string
	final String DATE_FORMAT = "yyyyMMdd";
	
	// input meth activity
	private DPMSMethActivity inputActy;

	// input plotID
	private String plotID;
	
	// value Id of tagID
	private Long tagIDValId;  
	
	// value Id of input (for blood, or fecal, or ear, or hair, or whisker)  
	private Long inputValId;  
		
	// value Id of output SampleID
	private Long outputSampleIDValId;  

	// a new meth stream to hold L1 values of sampleID (for blood, or fecal, or ear, or hair, or whisker)
	private DPMSMethStreamData outputMethStream; 

	// holds sample value for internal use.
	private String sample   = null;

	/**
	 * Constructor to instantiate field values.
	 * 
	 * @param inputActy - activity needs to be tested
	 * @param plotID    - String value of plotID
	 * @param outputMethStream - method stream to hold L1 values of sampleID (for blood, or fecal, or ear, or hair, or whisker)
	 * @param tagIDValId          - Long L0 value Id for tagID
	 * @param inputValId          - Long L0 value Id for sampleID (for blood, or fecal, or ear, or hair, or whisker)
	 * @param outputSampleIDValId - Long L1 value Id for sampleID (for blood, or fecal, or ear, or hair, or whisker)
	 */
	public SampleIDGenerator(DPMSMethActivity inputActy, 
									String plotID,
			                        DPMSMethStreamData outputMethStream, 
			                        Long tagIDValId,
			                        Long inputValId,
			                        Long outputSampleIDValId
			) {
		this.inputActy = inputActy;
		this.plotID    = plotID;
		this.outputMethStream    = outputMethStream;
		this.tagIDValId          = tagIDValId;
		this.inputValId          = inputValId;
		this.outputSampleIDValId = outputSampleIDValId;
	}
	
	/**
	 * This method runs algorithm to generate sample IDs for blood, or fecal, or ear, or hair, or whisker.
	 * 
	 * @return boolean: true if this algorithm runs fine. Otherwise, it returns false.
	 */
	public boolean runAlgorithm() {
		boolean result = false;
		if (inputActy == null) {
			log.error("Invalid input for SampleIDGenerator - no algorithm will be run!");
			return false;
		}
		
		String headStr = createHeadStr();
//		if(headStr == null) {
//			log.error("Head string for sample ID is null! No algorithm will be run!");
//			return false;
//		}
		
		// for sampleID
		result = generateSampleID(headStr); 
//		if(! result) {
//			log.error("The algorithm to generate sampleID for '" + sample + "' did NOT run successfully!");		
//		}
		
		return result;
	}

	/**
	 * This method creates head string in the format of siteID.dateString(yyyyMMdd).tagID.
	 * @return head string (for example, CPER.20120720.R1234)
	 */
//	private String createHeadStr() {
	public String createHeadStr() {
		String siteID = null;
		
		if(plotID != null && plotID.length() > 4) {
			siteID = this.plotID.substring(0, 4);
		} else {
			log.error("The value of plotID '" + plotID + "' is null or too short. Check plotID '" + plotID + "'!");
		}
			
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String dateStr = sdf.format(this.inputActy.getStartDate()); 

		String tagID = inputActy.findValueStringByValId(this.tagIDValId); 
		log.debug("siteID: '" + siteID + "'    dateStr: '" + dateStr + "'    tagID: '" + tagID + "'");
		String headStr = siteID + "." + dateStr + "." + tagID; 
		
		if(siteID == null || tagID == null || headStr.length() < 14) {
			log.error("The head string is '" + headStr + "'. Check plotID '" + plotID + "' or tagID '" + tagID + "'.");
			headStr = null;
		}
		
		return headStr;
	}
	
	/**
	 * This method generates sampleID (for blood, or fecal, or ear, or hair, or whisker),
	 * depending on inputValId. Since the values for blood (r, b, m), fecal (f, o), 
	 * ear (e), hair (h), whisker (w) do not overlap, we can use this single method
	 * to generate sampleID for each of them without additional type (blood, or fecal,
	 * or ear, or hair, or whisker) information.   
	 * 
	 * @param headStr: The head string for generating sample ID
	 * @return boolean: true if this method runs fine. Otherwise, it returns false.
	 */
	private boolean generateSampleID(String headStr) {
		boolean result  = false;
		String sampleID = null;
		
		try {	
			if(headStr == null) sampleID = "-9999.";
			// L0 sample value 
			sample = inputActy.findValueStringByValId(inputValId);
			// implementation of 5.1.2 - 11.a (see NEON.DOC.001244)
			if(sample == null || sample.isEmpty()) {
//				log.error("Sample is null. The associated sampleID cannot be generated!");
				log.debug("Sample is null. Sample ID will be filled in as -9999");
				sampleID = "-9999";
			} else if(sample.trim().toLowerCase().equals("r") || sample.trim().toLowerCase().equals("b") || sample.trim().toLowerCase().equals("m")) {
				// The blood sampleID is successfully generated.
				sampleID = headStr + ".B";
				result = true;
			} else if(sample.trim().toLowerCase().equals("f") || sample.trim().toLowerCase().equals("o")) {
				// The fecal sampleID is successfully generated.
				sampleID = headStr + ".F";
				result = true;
			} else if(sample.trim().toLowerCase().equals("e")) {
				// The ear sampleID is successfully generated.
				sampleID = headStr + ".E";
				result = true;
			} else if(sample.trim().toLowerCase().equals("h")) {
				// The hair sampleID is successfully generated.
				sampleID = headStr + ".H";
				result = true;
			} else if(sample.trim().toLowerCase().equals("w")) {
				// The whisker sampleID is successfully generated.
				sampleID = headStr + ".W";
				result = true;
			} else {
				log.debug("Sample value is '" + sample + "', which is out of ATBD's scope!");
				sampleID = "-9999";
			}
		} catch(Exception e) {
			// deals with situation that wrong value Ids are provided.
			log.error("Check sample '" + sample + "' or head string '" + headStr + "'! " + String.valueOf(e));
			result = false;
		}
		
//		if(result) {
			log.debug("For sample '" + sample + "', its sampleID is '" + sampleID + "'.");
//			DPMSMStreamReadout newRdot = new DPMSMStreamReadout();
//			outputMethStream.getMSReadouts().add(newRdot);
//			newRdot.setValueStringForValueId(outputSampleIDValId, sampleID, true);
//			newRdot.setReadoutTranTime(new Date());
			
			outputMethStream.addReadoutValueString(inputActy.getStartDate(), inputActy.getEndDate(), outputSampleIDValId, sampleID);
//		}

		return result;	
	}
}
