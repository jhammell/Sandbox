package org.neoninc.dpms.algorithms.processors;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.math.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.apache.log4j.Logger;
import org.neoninc.dpms.datastructures.DPMSMeasStreamData;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMStreamValue;

public class QAQCProcessorUtilities {
	// We randomly set this to -100.0. When specifiedK95 less than 0,
	// k95 is calculated using effective DOF. When specifiedK95 is set
	// to some positive value, k95 will be assigned to specifiedK95.
	//                   2015-08-12
	protected static double SPECIFIED_K95 = -100.0;
	
	/**Creating a SynchronizedDescriptiveStatistics object from a subset of data in DPMSMeasStreamData
	 * given a start index and window size. Note that it works on a preprocessed object.
	 * @param input
	 * @param valId
	 * @param startIndex
	 * @param windowSize
	 * @return
	 */
	static SynchronizedDescriptiveStatistics createWindowsStatistics(DPMSMeasStreamData input, Long valId, Integer startIndex, Integer windowSize) {
		// Counter
		Integer windowPos = 0;
		SynchronizedDescriptiveStatistics windowDistrib = new SynchronizedDescriptiveStatistics();
		
		// Make sure we have the preprocessed map in place.
		input.createPreprocessedMap();

		// Looping over the window while making sure we don't go over the 'end' of the time series
		while (windowPos < windowSize ) {
			DPMSMStreamReadout rdot = input.getPreprocessedReadoutByIndex(startIndex+windowPos);
			if (rdot != null) {
				Double theVal = rdot.getValueForValueId(valId);
//				if (theVal != null || !theVal.isNaN() || !theVal.isInfinite()) {
				if (theVal != null) {
					windowDistrib.addValue(theVal);
				}
			}
			
			// Moving along.
			windowPos++;
		}
        return windowDistrib;
	}
	
	   /**
	    * Calculates the median of a set of values.
	    * 
	    * @param inputData     the input data values
	    * 
	    * @return              the median
	    * 
	    */
	   public static Double computeMedian(double[] inputData) {

		  int nPoints = inputData.length; 
		  if ( nPoints < 1 ) {
			  return null;
		  }
		  Double median = null;
		   
		  // Make a local copy of the input data so we don't modify the original
		  double[] inputDataSorted = Arrays.copyOf(inputData, nPoints);
		  
		  Arrays.sort(inputDataSorted);
		  
		  int midIndex = (int) nPoints/2;
		  if ((nPoints & 1) == 1) {
		     // odd number of points - median is middle value
		     median = inputDataSorted[midIndex];
		  } else {
		     // even number of points - median is mean of two middle values
		     median = (inputDataSorted[midIndex-1] + inputDataSorted[midIndex])/2.;
		  }
	 
		  return median;
		  
	   }
	   /**
	    * Returns the coverage factor for input degrees of freedom, for 95% confidence
	    * 
	    * @param DegreesOfFreedom         the degrees of freedom   
	    * 
	    */		
	   public static double computeCoverageFactor(double DegreesOfFreedom) 
			   throws IllegalArgumentException {
		  
		   double k95;
		   if(SPECIFIED_K95 < 0.0) {
			   // calculate coverage factor with DOF
			   if ( DegreesOfFreedom < 1 ) {
				   throw new IllegalArgumentException("Degrees of freedom must be at least 1");
			   } 	
		      
		      // t-value at 95% confidence keyed by degrees of freedom 
		      // from JCGM 2008 (GUM) Table G.2
		      Double[] DOF = {  1.,   2.,   3.,   4.,   5.,   6.,   7.,   8.,   9.,  10., 
		    		           11.,  12.,  13.,  14.,  15.,  16.,  17.,  18.,  19.,  20., 
		    		           25.,  30.,  35.,  40.,  45.,  50., 100.,  Double.MAX_VALUE  };
		      double[] T95 =  {12.71, 4.30, 3.18, 2.78, 2.57, 2.45, 2.36, 2.31, 2.26, 2.23, 
		    		            2.20, 2.18, 2.16, 2.14, 2.13, 2.12, 2.11, 2.10, 2.09, 2.09, 
		    		            2.06, 2.04, 2.03, 2.02, 2.01, 2.01, 1.984,1.960};
	
		      // Interpolate as needed to find the t-value for the input DOF
		      int idx = 0;
		      while (DOF[idx] < DegreesOfFreedom) idx++;
		      
		      if (DOF[idx] == DegreesOfFreedom) {
		    	  k95 = T95[idx];
		      } else {
		    	  idx--;
		    	  int idxHi = Math.min(idx+1, DOF.length-1);
		    	  double DOFlo = DOF[idx];
		    	  double DOFhi = DOF[idxHi];
		    	  double T95lo = T95[idx];
		    	  double T95hi = T95[idxHi];
		    	  k95 = (DegreesOfFreedom - DOFlo) / (DOFhi - DOFlo) * (T95hi - T95lo) + T95lo;  
		      }
		  } else {
			  // specified coverage factor is used
			  k95 = SPECIFIED_K95; 
		  }
	      
	      return k95;	       
	   } 
	   
	   /**
	    * Returns the expanded uncertainty at 95% confidence
	    * Reference:  GUM G.6.4
	    * 
	    * @param combinedUncertainty      combined uncertainty
	    * @param EffectiveDOF         	  effective degrees of freedom   
	    * 
	    */	
	   public static double computeExpandedUncertainty(double combinedUncertainty, double EffectiveDOF) {		   
		   double tval = computeCoverageFactor(EffectiveDOF);
		   return tval * combinedUncertainty;
	   }

}

