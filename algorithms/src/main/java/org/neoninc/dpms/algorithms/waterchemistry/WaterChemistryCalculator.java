package org.neoninc.dpms.algorithms.waterchemistry;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;
import org.neoninc.dpms.systemutil.resource.ResourceExaminer;
import org.neoninc.dpms.systemutil.runprocess.ProcessBuilderRunner;

public class WaterChemistryCalculator {

    public static final String MEQ_PER_L_KEY = "MeqPerLValue";
    public static final String MG_PER_L_KEY = "MgPerLValue";
    public static final String WC_SAMPLE_TYPE_ANC = "ANC";
    public static final String WC_SAMPLE_TYPE_ALK = "ALK";
    public static final String WC_METHOD_TYPE_IPT = "IPT";
    public static final String WC_METHOD_TYPE_GRAN = "GRAN";

    private static final double IPT_CORRECTION_FACTOR = 1.01;
    private static final String RESOURCE_DIR_NAME = "/WaterChemistry/";
    private static final String SCRIPT_FILE_NAME1 = "gran_calculator.pl";
    private static final String SCRIPT_FILE_NAME2 = "gran_calculations.pl";
    
    private static Logger log = Logger.getLogger(WaterChemistryCalculator.class);
    private static boolean scriptsCopied = false;
    private static String scriptDir = null;

    /**
     * Perform the Water Chemistry calculations based on the titration data passed in
     * and return the calculated values.
     * 
     * @param titrationData - The string of titration data points.
     *                        The titration data is a string of format x,y;x1,y1;x2,y2;...
     *                        Using ';' to separate pairs and ',' to separate values within the pair.  
     * @param methodType - The titration method type, IPT or GRAN
     * @param sampleType - The sample type, ALK or ANC
     * @param sampleVolume - The volume of the sample
     * @param normality - The normality value
     * @param initialSampleTemp - The initial sample temperature
     * @param specificConductance - The specific conductance of the sample
     * 
     * @return a map of the calculated values where the key indicates whether it is the MEQ/L or MG/L.
     *          The constants defined in this file (MEQ_PER_L_KEY or MG_PER_L_KEY) should be used 
     *          as the keys for getting the values.
     */
    public Map<String, Double> calculate (String titrationData, String methodType, String sampleType, Double sampleVolume, 
            Double normality, Double initialSampleTemp, Double specificConductance) {

        Map<String,Double> values = null;

        try {
            // Verify a valid sample type.
            if ((sampleType.equalsIgnoreCase(WC_SAMPLE_TYPE_ALK)) ||
                (sampleType.equalsIgnoreCase(WC_SAMPLE_TYPE_ANC))) {

                // If there is titration data, then calculate values based on the method type.
                if (titrationData != null && !titrationData.isEmpty()) {
                    if (methodType != null && methodType.equalsIgnoreCase(WC_METHOD_TYPE_IPT)) {
                        values = calculateIPTValues (titrationData, sampleVolume, normality);
                    }
                    else if (methodType != null && methodType.equalsIgnoreCase(WC_METHOD_TYPE_GRAN)) {
                        values = calculateGranValues (titrationData, methodType, sampleVolume, normality, initialSampleTemp, specificConductance);
                    }
                    else {
                        log.warn("Unknown titration METHOD (" + methodType + "), skipping.");
                    }
                }
                else {
                    // Titration data is missing.
                    log.warn("No titration data for activity, skipping.");
                }
            }
            else {
                log.warn("Invalid sample type (" + sampleType + ") for activity.");
            }
        } catch (Exception ex) {
            log.warn("Exception occurred while calculating " + sampleType + "/" + 
                    methodType + "values, skipping. Error: " + ex.getMessage());
        }
        
        return values;
    }

    /**
     * Calculate and save the values for titrations using the GRAN method.
     * These calculations are actually done via a Perl script from the USGS.
     * Create a process to run the Perl script and then get the values it
     * outputs to save.
     * 
     * @param activity - the domainLabData activity
     * @param titrationData - the titration curve
     * @return true = successfully calculated values, false otherwise.
     */
    private Map<String, Double> calculateGranValues(String titrationData, String methodType, Double sampleVolume, 
            Double normality, Double initialSampleTemp, Double specificConductance)
    {
        Map<String,Double> values = null;
        
        // Run the process to calculate the values.
        values = runGranCalculation (titrationData, methodType, sampleVolume, normality, initialSampleTemp, specificConductance);

        return values;
    }

    /**
     * Call the script to do the actual calculation. If successful then read
     * the resulting values, which the script wrote to stdout, and return
     * the pair in the calculatedValues parameter.
     * 
     * @param activity - the activity
     * @param titrationData - the titration curve
     * @return a pair representing the MeqPerL and MgPerL calculated values
     */
    private Map<String, Double> runGranCalculation(String titrationData, String methodType, Double sampleVolume, 
            Double normality, Double initialSampleTemp, Double specificConductance) {

        Map<String,Double> values = new HashMap<String,Double>();
        List<String> stdout = new ArrayList<String>();
        List<String> stderr = new ArrayList<String>();
        List<String> scriptArgs = new ArrayList<String>();

        // Copy the scripts if necessary.
        copyScripts();

        String workingDir = getScriptDir();
        String cmd = "perl";
        Double meqLValue = null;
        Double mgLValue = null;

        // Convert the curve to pairs. This is done here in order to remove odd points 
        // that might cause problems with the calculation.
        List<Pair<Double,Double>> curve = getTitrationCurve (titrationData);
        
        scriptArgs.add(SCRIPT_FILE_NAME1);
        scriptArgs.add((initialSampleTemp != null) ? initialSampleTemp.toString() : "0");
        scriptArgs.add((sampleVolume != null) ? sampleVolume.toString() : "0");
        scriptArgs.add((specificConductance != null) ? specificConductance.toString() : "50"); // Default to 50 according to ATBD
        scriptArgs.add((normality != null) ? normality.toString() : "0");
        scriptArgs.add(curve.toString());

        int rtn = ProcessBuilderRunner.runShellScript(
                workingDir,
                cmd,
                scriptArgs,
                stdout,
                stderr);

        if (rtn == 0) {
            // Get the calculated values. These should be in the stdout stream.
            // They should be the last 2 lines, but just in case the lines are not
            // in the actual order written let's go through the whole list.
            log.debug("STDOUT: GRAN calculation script created " + stdout.size() + " output lines.");
            for (String line : stdout) {
                log.debug("STDOUT: " + line);
                if (line.startsWith("meqL:")) {
                    String[] tokens = line.split(":");
                    if (tokens.length == 2) {
                        try {
                            meqLValue = Double.parseDouble(tokens[1]);
                            log.debug("Found GRAN meq/L value: " + meqLValue);
                        } catch (NumberFormatException e) {
                            log.debug("Exception converting meq/L value to a number.");
                        }
                    }
                }
                else if (line.startsWith("mgL:")) {
                    String[] tokens = line.split(":");
                    if (tokens.length == 2) {
                        try {
                            mgLValue = Double.parseDouble(tokens[1]);
                            log.debug("Found GRAN mg/L value: " + mgLValue);
                        } catch (NumberFormatException e) {
                            log.debug("Exception converting mg/L value to a number.");
                        }
                    }
                }
            }
        }
        
        // For debugging only, log anything from stderr.
        log.debug("STDERR: GRAN calculation script created " + stderr.size() + " output lines.");
        for (String line : stderr) {
            log.debug("STDERR: " + line);
        }

        values.put(MEQ_PER_L_KEY, meqLValue);
        values.put(MG_PER_L_KEY, mgLValue);
        
        return values;
    }

    /**
     * Calculate and save the values for titrations using the IPT method.
     * Once calculated the values are saved within the activity.
     *  
     * @param activity - the domainLabData activity
     * @param titrationData - the titration data
     * @return true = successfully calculated values, false otherwise.
     */
    protected Map<String, Double> calculateIPTValues(String titrationData, Double sampleVolume, Double normality) {

        Map<String,Double> values = null;
        values = performIPTCalculation(titrationData, sampleVolume, normality);
        return values;
    }

    /**
     * Perform the actual calculation to determine the MeqPerL and MgPerL values
     * for the IPT method.
     * 
     * @param activity - the activity 
     * @param titrationData - the titration data
     * @return a pair representing the MeqPerL and MgPerL calculated values
     */
    protected Map<String, Double> performIPTCalculation(String titrationData, Double sampleVolume, Double normality) {

        Map<String,Double> values = new HashMap<String,Double>();
        
        Double valueMeqPerL = new Double(0.0);
        Double valueMgPerL = new Double(0.0);
        
        try {
            if (sampleVolume != null && normality != null) {
                List<Pair<Double,Double>> curve = getTitrationCurve (titrationData);

                // Determine the change in pH per change in counter.
                // Get the counter average at peak change and preceding counter reading.
                Double counterAverage = getCounterAverageAtPeak (curve);
                
                // VolumeAcid is average of counter reading at peak and preceding counter reading, 
                // returned by previous call, divided by 800.
                Double volumeAcid = counterAverage / 800.0;
                
                valueMeqPerL = (1000.0 * volumeAcid * normality * IPT_CORRECTION_FACTOR) / sampleVolume;
                valueMgPerL = (50044.0 * volumeAcid * normality * IPT_CORRECTION_FACTOR) / sampleVolume;
            }
            else {
                log.warn("Missing sampleVolume (" + sampleVolume + ") or normality value (" + normality + ")");
            }
        } catch (Exception ex) {
            log.warn("Exception calculating IPT values: " + ex.getMessage());
        }

        values.put(MEQ_PER_L_KEY, valueMeqPerL);
        values.put(MG_PER_L_KEY, valueMgPerL);
        return values;
    }

    /**
     * Determine the average counter reading at the max change in pH per change in counter.
     * If there are multiple peaks with the same rate of change than the average returned
     * is the average of the lowest and highest peaks. This is to match the way the USGS
     * online calculator handles this condition.
     * 
     * @param curve - the titration curve. It is assumed to be sorted by counter reading.
     * @return the average of the counter and previous counter at the steepest part of the 
     *          curve, i.e. max change in pH per change in counter
     */
    protected Double getCounterAverageAtPeak(
            List<Pair<Double, Double>> curve) {

        double lastCounter = 0.0;
        double lastPH = 0.0;
        double maxChange = 0.0;
        boolean initialized = false;
        double avgMaxChangeCounterDiffs[] = {0.0, 0.0};
        Double counterAverage = 0.0;
        
        // Loop through the curve and find the largest change in pH per change in counter.
        for (Pair<Double,Double> point : curve) {
            if (!initialized) {
                // Set initial starting values.
                lastCounter = point.getLeft();
                lastPH = point.getRight();
                initialized = true;
            }
            else {
                Double currentCounter = point.getLeft();
                Double currentPH = point.getRight();
                if (currentCounter - lastCounter != 0.0) {
                    // The bicarbonate peak is the one of interest, so skip any
                    // values where the pH is too high.
                    if (currentPH < 7.0) {
                        double pHChangePerCounter = roundForCalculations(Math.abs(currentPH - lastPH) / (currentCounter - lastCounter));
                        
                        // If this change is the largest then save that value, as well as the 
                        // max and preceding counter values.
                        if (pHChangePerCounter > maxChange) {
                            maxChange = pHChangePerCounter;
                            
                            // Save the average of these counter because they are the highest pH change/counter so far.
                            // Also, clear the second value since this is a new high.
                            avgMaxChangeCounterDiffs[0] = (currentCounter + lastCounter) / 2.0;
                            avgMaxChangeCounterDiffs[1] = 0.0;
                            log.trace ("Found new max slope (" + maxChange + ") with counter points (" + lastCounter + "," + currentCounter + ")");
                        }
                        else if (pHChangePerCounter == maxChange) {
                            // A duplicate point with the same slope (i.e. pH change/counter change). Save this counter diff
                            // average too. This will be used to average the volumes.
                            avgMaxChangeCounterDiffs[1] = (currentCounter + lastCounter) / 2.0;
                            log.trace ("Found duplicate max slope (" + maxChange + ") with counter points (" + lastCounter + "," + currentCounter + ")");
                        }
                    }
                }
                
                // Update values for next time through loop.
                lastCounter = currentCounter;
                lastPH = currentPH;
            }
        }

        // If two or more points have the same maximum slope,
        // determine the average by averaging the points.
        if (avgMaxChangeCounterDiffs[1] != 0.0) {
            counterAverage = (avgMaxChangeCounterDiffs[0] + avgMaxChangeCounterDiffs[1]) / 2.0;
            log.debug("Multiple peaks found, averaging lowest and highest ("+ avgMaxChangeCounterDiffs[0] + "," + avgMaxChangeCounterDiffs[1] + ")");
        }
        else {
            counterAverage = avgMaxChangeCounterDiffs[0];
        }
        
        log.debug("Returning average counter reading of: " + counterAverage );
        return counterAverage;
    }

    /**
     * Parse a titration data string into the list of points. The points returned will
     * be sorted. The string of points is expected to be in the format:
     * 
     * counter-1,pH-1;counter-2,pH-2;...;counter-N,pH-N
     * 
     * Using ';' to separate point pairs and ',' to separate the values within the pair.
     * 
     * @param titrationData - The titration data string
     * @return the curve as a list of points.
     */
    protected List<Pair<Double, Double>> getTitrationCurve(String titrationData) {

        List<Pair<Double, Double>> curve = new ArrayList<Pair<Double,Double>>();

        // Parse data and create a list of points.
        log.debug("titration data: " + titrationData);

        if (titrationData != null && !titrationData.isEmpty()) {
            String[] pointPairs = titrationData.split(";");
            for (String pointPair : pointPairs) {
                String[] points = pointPair.split(",");
                Double x = new Double(points[0]);
                Double y = new Double(points[1]);
                curve.add(Pair.of(x, y));
            }
            
            // Now make sure collection is sorted. 
            Collections.sort(curve);
            
            // Check to see if any data is incorrect, i.e. as the counter
            // goes up the pH should go down. If for some reason the pH goes
            // up, remove that point so it is ignored in the calculations.
            Double lastPH = 0.0;
            Double lastCounter = 0.0;
            boolean initialized = false;
            Pair<Double,Double> lastPoint = null;
            List<Pair<Double,Double>> removePoints = new ArrayList<Pair<Double,Double>>();
            for (Pair<Double,Double> point : curve) {
                Double counter = point.getLeft();
                Double pH = point.getRight();
                if (!initialized) {
                    if (counter != 0.0) {
                        log.warn("Titration curve does not start with a zero titrant counter.");
                    }
                    initialized = true;
                }
                else {
                    if (lastCounter >= counter) {
                        // This should never happen because we just sorted this list on the counter.
                        log.warn("Processing error: Titrant counter not sorted correctly, calculations may not be accurate.");
                    }
                    if (lastPH <= pH) {
                        // The pH went up from the last point, it should always decrease. 
                        // Therefore save the last point to remove later, unless it is the initial point. 
                        // If we remove it here it causes a co-modification exception because we are still  
                        // iterating through the list.
                        if (lastCounter != 0.0) {
                            removePoints.add(lastPoint);
                        }
                        else {
                            // Don't remove the initial point, remove this point instead
                            // and continue to next point.
                            removePoints.add(point);
                            continue;
                        }
                    }
                }
                lastCounter = counter;
                lastPH = pH;
                lastPoint = point;
            }

            // Now remove any points where the data is incorrect.
            for (Pair<Double,Double> removePoint : removePoints) {
                log.warn("Titration curve has unexpected increase in pH. The pH is expected to decrease sequentially as titrant is added. This data point (" + removePoint.getLeft() + "," + removePoint.getRight() + ") will be ignored in calculations.");
                curve.remove(removePoint);
            }
        }

        log.debug("titration curve has " + curve.size() + " points.");
        log.debug("titration curve: " + curve.toString());
        return curve;
    }

    private boolean copyScripts () {

        if (!scriptsCopied) {

            try {
                String resourceDir = RESOURCE_DIR_NAME;
                String resourceFileName = resourceDir + SCRIPT_FILE_NAME1;
                String destFolderName = getScriptDir();
                String destScriptFileName = destFolderName + SCRIPT_FILE_NAME1;
                File destFile = new File(destScriptFileName);

                // Copy 1st script if necessary.
                if (!destFile.exists()) {
                    // Copy file because it doesn't exist.
                    File destDir = new File(destFolderName);
                    ResourceExaminer.copyResourceFileToDir(resourceFileName, destDir);
                }
                
                // Copy 2nd script if necessary.
                resourceFileName = resourceDir + SCRIPT_FILE_NAME2;
                destScriptFileName = destFolderName + SCRIPT_FILE_NAME2;
                File destFile2 = new File(destScriptFileName);
                if (!destFile2.exists()) {
                    // Copy file because it doesn't exist.
                    File destDir = new File(destFolderName);
                    ResourceExaminer.copyResourceFileToDir(resourceFileName, destDir);
                }

                scriptsCopied = true;
            } catch (Exception e) {
                log.debug("Exception copying script files (" + e.getLocalizedMessage() + "). Stack trace: " + e.getStackTrace());
            }
        }
        
        return scriptsCopied;
    }
    
    private String getScriptDir () {

        // If we haven't determined the script directory yet, then do it now.
        if (scriptDir == null) {
            try {
                String jarFolder = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replace('\\', '/').replace("/C:/", "C:/");
                if (jarFolder.endsWith(".jar")) {
                    // Actual jar file, so strip off the last part which is the jar file name
                    // which will leave us with the directory that the jar file is in.
                    Path path = Paths.get(jarFolder);
                    scriptDir = path.getParent().toString();
                }
                else {
                    scriptDir = jarFolder;
                }
                
                // Make sure it ends with a '/'
                if (!scriptDir.endsWith("/")) {
                    scriptDir = scriptDir + "/";
                }
            } catch (URISyntaxException e) {
                log.debug("Exception getting script directory (" + e.getMessage() + "). Stack trace: " + e.getStackTrace());
                scriptDir = "./";
            }
        }
        
        return scriptDir;
    }
    
    /**
     * Round the MgPerL value. The rounding is based on the value, < 1000 is rounded
     * to whole numbers, more than that is rounded to 3 significant figures.
     * 
     * From ATBD section 5.2.6.
     * 
     * @param value
     * @return the rounded value
     */
    protected Double roundMgPerLValue (Double value) {
        double decimalPlaces = 0.0;
        Double roundedValue = null;

        if (value != null) {
            if (Math.abs(value) >= 1000.0) {
                decimalPlaces = 3;
            }
        
            roundedValue = roundToDecimalPlaces(value, decimalPlaces);
        }
        return roundedValue;
    }
    
    /**
     * Round the MeqPerL value to 3 significant figures.
     * 
     * From ATBD section 5.2.6.
     * 
     * @param value
     * @return the value rounded to 3 significant figures.
     */
    protected Double roundMeqPerLValue (Double value) {
        Double roundedValue = null;

        if (value != null) {
            roundedValue = roundToDecimalPlaces(value, 3);
        }
        return roundedValue;
    }
    
    /**
     * Round a value to 6 decimal places for calculation. This avoids the problem of
     * slight errors in the representation of floating point numbers.
     * 
     * @param value
     * @return the value rounded to 6 significant figures.
     */
    protected double roundForCalculations (double value) {
        double roundedValue = roundToDecimalPlaces(value, 6);
        return roundedValue;
    }
    
    /**
     * Round a value to the indicated number of decimal places.
     * e.g. round 2.12345 to 3 decimal places:
     *      roundToDecimalPlaces (2.12345, 3)
     *      returns 2.123
     *      
     * @param value - value to be rounded
     * @param decimalPlaces - the number of decimal places to round to
     * @return the rounded value
     */
    public static double roundToDecimalPlaces (double value, double decimalPlaces) {
        double roundMuliplier = Math.pow(10.0, decimalPlaces);
        double roundedValue = (double)Math.round(value * roundMuliplier) / roundMuliplier;
        return roundedValue;
    }
}
