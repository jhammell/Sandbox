package org.neoninc.dpms.algorithms.depthprofile;

//some change

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.log4j.Logger;

public class DepthProfileCalculator {

    private static Logger log = Logger.getLogger(DepthProfileCalculator.class);
    
    public static String THERMAL_STRATIFICATION_STRATIFIED = "stratified";
    public static String THERMAL_STRATIFICATION_NON_STRATIFIED = "non-stratified";

    public static String METALIMNION_UPPER = "upperMetalimnion";
    public static String METALIMNION_UPPER2 = "upperMetalimnion2";
    public static String METALIMNION_LOWER = "lowerMetalimnion";
    public static String METALIMNION_LOWER2 = "lowerMetalimnion2";
    public static String METALIMNION_ESTIMATED_UPPER = "estimatedUpperMetalimnion";
    public static String METALIMNION_ESTIMATED_UPPER2 = "estimatedUpperMetalimnion2";
    public static String METALIMNION_ESTIMATED_LOWER = "estimatedLowerMetalimnion";
    public static String METALIMNION_ESTIMATED_LOWER2 = "estimatedLowerMetalimnion2";
    
    
    /**
     * Calculate the secchi mean depth
     *  
     * @param depth1 - secchi depth 1
     * @param depth2 - secchi depth 2
     * @return the calculated mean depth or null if no depths (i.e. clear to bottom)
     */
    public Double calculateSecchiMeanDepth(Double depth1, Double depth2) {
        Double meanDepth = null;
        
        if (depth1 != null && depth2 != null) {
            meanDepth = new Double ((depth1 + depth2) / 2.0);
        }
        else if (depth1 != null) {
            meanDepth = depth1;
        }
        else if (depth2 != null) {
            meanDepth = depth2;
        }
        log.debug("Secchi Mean Depth calculated: " + meanDepth + "(depth1: " + depth1 + ", depth2: " + depth2 + ")");
        return meanDepth;
    }

    /**
     * Calculate the euphotic depth.
     * 
     * @param secchiMeanDepth - the secchi mean depth for this observation
     * @param maxDepth - the max depth of lake
     * @return the euphotic depth or null if unable to calculate
     */
    public Double calculateEuphoticDepth(Double secchiMeanDepth, Double maxDepth) {
        Double euphoticDepth = null;
        
        if (secchiMeanDepth != null) {
            euphoticDepth = new Double (secchiMeanDepth * 2.5);
            if (maxDepth != null && euphoticDepth > maxDepth) {
                euphoticDepth = maxDepth;
            }
        }
        
        log.debug("Euphotic Depth calculated: " + euphoticDepth + "(secchiMeanDepth: " + 
                secchiMeanDepth + ", maxDepth: " + maxDepth + ")");
        return euphoticDepth;
    }

    /**
     * This method will determine the metalminions and thermal stratification of
     * the given depth profile data. The list should contain the depth
     * profile data for a single profile ID. 
     * 
     * The appropriate values will be set in the records within the list.
     * 
     * The algorithm is implemented in multiple methods, but the overall
     * flow of the algorithm is:
     * 
     * - Sort the list by depth and then get the depth and temp pairs.
     * - Use these pairs to create a new table of depthProfileEntries, which has
     *   additional fields for tracking change in depth and temp.
     * - Determine previous depth, and if necessary the previous one before that
     *   in order to get a depth that is at least 1m less than current depth.
     * - Calculate change in temp/depth for each row.
     * - Flag each row where change in temp/depth > 1C/m as a thermocline
     * - Save the rows where the thermocline flag changes, this defines
     *   the different layers of water in the lake
     * - For each layer, determine the start and end of each layer and its thickness
     * - Determine the metalimnions based on whether there are multiple thermoclines 
     *   or not and how thick they are.
     * 
     * @param depthProfileEntriesFullList - the depth profile data for a single profile ID
     * @param maxDepth - the max depth of the lake
     * @return true if successfully determined values, false if error or no data
     */
    public boolean determineMetalimnionAndStratification (List<DepthProfileEntry> depthProfileEntriesFullList, Double maxDepth) {
        boolean status = false;

        if (depthProfileEntriesFullList == null || depthProfileEntriesFullList.isEmpty()) {
            log.debug("determineMetalimnionAndStratification: Empty list of entries");
            return status;
        }

        // Make sure the list is sorted by depth to start.
        DepthProfileEntryComparator comp = new DepthProfileEntryComparator();
        Collections.sort(depthProfileEntriesFullList, comp);
        
        if (depthProfileEntriesFullList.size() > 1) {
            List<DepthProfileEntry> profileEntriesToUse = determineProfileEntries (depthProfileEntriesFullList);
            calculateMetalimnionAndStratification(profileEntriesToUse, depthProfileEntriesFullList, maxDepth);
        }
        else {
            // Just a single row, so shallow and not stratified.
            log.debug("determineMetalimnionAndStratification: only a single profile data activity, non-stratified.");
            DepthProfileEntry entry = depthProfileEntriesFullList.get(0);
            entry.setStratification(THERMAL_STRATIFICATION_NON_STRATIFIED);
        }
        
        status = true;
        return status;
    }


    /**
     * Use the data entries to determine the metalimnion and stratification
     * values. This method will update those in the corresponding entries.
     *
     * This implements the following section of R code from the ATBD:
     *
     *  runs <- rle(data$thermocline)
     *  firstrows <- cumsum(c(1, runs$lengths[-length(runs$lengths)]))
     *  data2 <- data[firstrows,]
     * 
     * @param profileEntriesToUse - the profile entry data to use for calculation
     * @param depthProfileEntriesFullList - the full list of depth profile data 
     * @param maxDepth - the max depth of the lake
     */
    private void calculateMetalimnionAndStratification(
            List<DepthProfileEntry> profileEntriesToUse,
            List<DepthProfileEntry> depthProfileEntriesFullList, Double maxDepth) {
        
        List<DepthProfileEntry> thermoclineChangeRows = new ArrayList<>();
        boolean currentThermocline = false;
        for (DepthProfileEntry entry : profileEntriesToUse) {
            if (thermoclineChangeRows.isEmpty()) {
                // First entry, so save this as the start
                thermoclineChangeRows.add(entry);
                currentThermocline = entry.isThermocline();
            }
            else if (currentThermocline != entry.isThermocline()) {
                thermoclineChangeRows.add(entry);
                currentThermocline = entry.isThermocline();
            }
        }
        
        log.debug("Number of entries in thermoclineChangeRows: " + thermoclineChangeRows.size());
        
        // Now we have the records indicating the transition zones. 
        // Use these to calculate the thickness of each section.
        // Get the max sample depth, because it is likely different
        // than the max depth of the lake. 
        // Also, make sure we have a max depth. If not, then use the
        // max sampling depth.
        Double maxSampleDepth = null;
        DepthProfileEntry lastEntry = depthProfileEntriesFullList.get(depthProfileEntriesFullList.size()-1);
        maxSampleDepth = lastEntry.getDepth();
        if (maxDepth == null) {
            maxDepth = maxSampleDepth;
            log.warn("No max depth provided, using max depth from profile data records");
        }
        determineSectionThickness (thermoclineChangeRows, maxSampleDepth, maxDepth);
        
        // Now we have all the values necessary. Determine the 
        // actual metalimnion and stratification values.
        determineMetalimnions (thermoclineChangeRows, profileEntriesToUse, depthProfileEntriesFullList);
        
        // Now determine and set the stratification. If the previous 
        // step determined there is a metalimnion, then it is stratified.
        String stratificationString = THERMAL_STRATIFICATION_NON_STRATIFIED;
        for (DepthProfileEntry entry : depthProfileEntriesFullList) {
            if (entry.getMetalimnion() != null && !entry.getMetalimnion().isEmpty()) {
                stratificationString = THERMAL_STRATIFICATION_STRATIFIED;
                break;
            }
        }
        
        for (DepthProfileEntry entry : depthProfileEntriesFullList) {
            // Skip setting this for duplicate records with different water temp values according to ATBD.
            if (entry.getDuplicateFlag() == null || entry.getDuplicateFlag() != 2.0) {
                entry.setStratification(stratificationString);
            }
            else {
                // See if this duplicate should be set or not.
                if (useDuplicate(entry, depthProfileEntriesFullList)) {
                    entry.setStratification(stratificationString);
                }
            }
        }
    }


    /**
     * Look through the temp/depth data at transitions and determine
     * the actual metalimnions and stratification values.
     * 
     * This implements the following section of R code from the ATBD
     * (though it does it in a different way and not as direct of an 
     * implementation as some of the other methods):
     *
     * if(max(data$thermocline)>0){
     *    x = T
     *    if(data2$thermocline[1]==T){data$metalimnionDepth[1] <- "upperMetalimnion"}
     *    for(i in 2: nrow(data2)){
     *      if (data2$thermocline[i]==T & data2$thermocline[i-1]==F & x == F & data2$deltaDepth1m[i] <=1){data$metalimnionDepth[data$depth==data2$sectionmin[i]] <- "upperMetalimnion2"}
     *      if (data2$thermocline[i]==T & data2$thermocline[i-1]==F & x == F & data2$deltaDepth1m[i] >1){data$metalimnionDepth[data$depth==data2$sectionmin[i]] <- "estimatedUpperMetalimnion2"}
     *      if (data2$thermocline[i]==F & data2$thermocline[i-1]==T & x == F & data2$deltaDepth1m[i] <=1){data$metalimnionDepth[data$depth==data2$sectionmax[i-1]] <- "lowerMetalimnion2"}
     *      if (data2$thermocline[i]==F & data2$thermocline[i-1]==T & x == F & data2$deltaDepth1m[i] >1){data$metalimnionDepth[data$depth==data2$sectionmax[i-1]] <- "estimatedLowerMetalimnion2"}
     *      if (data2$thermocline[i]==T & data2$thermocline[i-1]==F & x == T & data2$deltaDepth1m[i] <=1){data$metalimnionDepth[data$depth==data2$sectionmin[i]] <- "upperMetalimnion"}
     *      if (data2$thermocline[i]==T & data2$thermocline[i-1]==F & x == T & data2$deltaDepth1m[i] >1){data$metalimnionDepth[data$depth==data2$sectionmin[i]] <- "estimatedUpperMetalimnion"}
     *      if (data2$thermocline[i]==F & data2$thermocline[i-1]==T & x == T & data2$deltaDepth1m[i] <=1){data$metalimnionDepth[data$depth==data2$sectionmax[i-1]] <- "lowerMetalimnion"
     *        x <- F }
     *      if (data2$thermocline[i]==F & data2$thermocline[i-1]==T & x == T & data2$deltaDepth1m[i] >1){data$metalimnionDepth[data$depth==data2$sectionmax[i-1]] <- "estimatedLowerMetalimnion"
     *        x <- F }
     *    }
     *    if(data2$thermocline[nrow(data2)]==T & x==T & data2$deltaDepth1m[i] <=1){data$metalimnionDepth[nrow(data)] <- "lowerMetalimnion"}
     *    if(data2$thermocline[nrow(data2)]==T & x==T & data2$deltaDepth1m[i] >1){data$metalimnionDepth[nrow(data)] <- "estimatedLowerMetalimnion"}
     *    if(data2$thermocline[nrow(data2)]==T & x==F & data2$deltaDepth1m[i] <=1){data$metalimnionDepth[nrow(data)] <- "lowerMetalimnion2"}
     *    if(data2$thermocline[nrow(data2)]==T & x==F & data2$deltaDepth1m[i] >1){data$metalimnionDepth[nrow(data)] <- "estimatedLowerMetalimnion2"}
     *  }
     * } 
     * data$thermalStratification <- NA
     * ifelse(all(is.na(data$metalimnionDepth)), data$thermalStratification <- "non-stratified", data$thermalStratification <- "stratified")
     * 
     * pubProfileData$metalimnionDepth[pubProfileData$profileID==setGroup[k]] <- data$metalimnionDepth
     * pubProfileData$thermalStratification[pubProfileData$profileID==setGroup[k]] <- data$thermalStratification
     * 
     * @param thermoclineChangeEntries - the entries for the transition zones with the 
     *                         temps, depths, thickness of each section
     * @param profileEntriesToUse - the list of all profile data entries to use
     * @param depthProfileEntriesFullList - the full list of depth profile data 
     */
    private void determineMetalimnions(
            List<DepthProfileEntry> thermoclineChangeRows,
            List<DepthProfileEntry> profileEntriesToUse,
            List<DepthProfileEntry> depthProfileEntriesFullList) {

        boolean currentThermoclines = false;
        boolean hasThermoclines = false;
        boolean multipleThermoclines = false;
        Double upperDepth1 = null;
        Double upperDepth2 = null;
        Double lowerDepth1 = null;
        Double lowerDepth2 = null;
        DepthProfileEntry previousEntry = null;
        
        // Go through all the thermoclines and determine if there
        // are any thermoclines and if so if there are multiple 
        // thermoclines or not. Save the upper and lower depth
        // of each thermocline.
        for (DepthProfileEntry entry : thermoclineChangeRows) {
            if (entry.isThermocline()) {
                hasThermoclines = true;
                if (!currentThermoclines) {
                    if (upperDepth1 == null) {
                        upperDepth1 = entry.getSectionDepthMin();
                    }
                    else {
                        multipleThermoclines = true;
                        upperDepth2 = entry.getSectionDepthMin();
                    }
                }
                currentThermoclines = true;
            }
            else {
                if (currentThermoclines) {
                    if (lowerDepth1 == null) {
                        lowerDepth1 = previousEntry.getSectionDepthMax();
                    }
                    else {
                        lowerDepth2 = previousEntry.getSectionDepthMax();
                    }
                }
                currentThermoclines = false;
            }
            previousEntry = entry;
        }
        // Now check if the last entry was the lowest thermocline
        // in which case we need to save the depth.
        if (currentThermoclines) {
            if (lowerDepth1 == null) {
                lowerDepth1 = previousEntry.getSectionDepthMax();
            }
            else if (lowerDepth2 == null) {
                lowerDepth2 = previousEntry.getSectionDepthMax();
            }
        }
        
        log.debug("determineMetalimnions: Thermoclines exist: " + hasThermoclines);
        log.debug("determineMetalimnions: Has multiple Thermoclines: " + multipleThermoclines);

        if (hasThermoclines) {
            // Now set the metalimnion value for each depth saved. Note that
            // for the upper thermoclines use the change rows to determine the
            // depth change, but for the lower limits get the depth change from 
            // the corresponding depth in the full list because that will have
            // the proper value. The depth change is used to determine if the 
            // metalimnion is "estimated" or not.
            DepthProfileEntry currentEntry = getEntryForMinSectionDepth (upperDepth1, thermoclineChangeRows);
            if (currentEntry.getDeltaDepth1m() <= 1.0) {
                setMetalimnionValueForDepth(depthProfileEntriesFullList, upperDepth1, METALIMNION_UPPER);
            }
            else {
                setMetalimnionValueForDepth(depthProfileEntriesFullList, upperDepth1, METALIMNION_ESTIMATED_UPPER);
            }

            currentEntry = getEntryForDepth (lowerDepth1, profileEntriesToUse);
            if (currentEntry.getDeltaDepth1m() <= 1.0) {
                setMetalimnionValueForDepth(depthProfileEntriesFullList, lowerDepth1, METALIMNION_LOWER);
            }
            else {
                setMetalimnionValueForDepth(depthProfileEntriesFullList, lowerDepth1, METALIMNION_ESTIMATED_LOWER);
            }
            
            // See if there is a second thermocline
            if (upperDepth2 != null) {
                currentEntry = getEntryForMinSectionDepth (upperDepth2, thermoclineChangeRows);
                if (currentEntry.getDeltaDepth1m() <= 1.0) {
                    setMetalimnionValueForDepth(depthProfileEntriesFullList, upperDepth2, METALIMNION_UPPER2);
                }
                else {
                    setMetalimnionValueForDepth(depthProfileEntriesFullList, upperDepth2, METALIMNION_ESTIMATED_UPPER2);
                }

                currentEntry = getEntryForDepth (lowerDepth2, profileEntriesToUse);
                if (currentEntry.getDeltaDepth1m() <= 1.0) {
                    setMetalimnionValueForDepth(depthProfileEntriesFullList, lowerDepth2, METALIMNION_LOWER2);
                }
                else {
                    setMetalimnionValueForDepth(depthProfileEntriesFullList, lowerDepth2, METALIMNION_ESTIMATED_LOWER2);
                }
            }
        }
    }


    private void setMetalimnionValueForDepth(
            List<DepthProfileEntry> depthProfileEntriesFullList,
            Double depth, String metalimnionValue) {
        
        log.debug("Setting metalimnion for depth: " + depth + " to " + metalimnionValue);
        for (DepthProfileEntry entry : depthProfileEntriesFullList) {
            if (entry.getDepth().equals(depth)) {
                // This is the depth entry we want.
                if (entry.getDuplicateFlag() != null && entry.getDuplicateFlag() == 2.0) {
                    // This depth has duplicates. Check and see if we should
                    // use it or not. If so, then set the metalimnion value
                    // and continue looking for other possible duplicates
                    // for this depth.
                    if (useDuplicate(entry, depthProfileEntriesFullList)) {
                        entry.setMetalimnion(metalimnionValue);
                    }
                    else {
                        // The proper depth, but is a duplicate that should 
                        // not be used, so stop.
                        break;
                    }
                }
                else {
                    entry.setMetalimnion(metalimnionValue);
                    break;
                }
            }
        }
    }


    private DepthProfileEntry getEntryForDepth(Double depth,
            List<DepthProfileEntry> profileEntriesToUse) {
        DepthProfileEntry depthEntry = null;
        for (DepthProfileEntry entry : profileEntriesToUse) {
            if (entry.getDepth().equals(depth)) {
                depthEntry = entry;
                break;
            }
        }

        return depthEntry;
    }


    private DepthProfileEntry getEntryForMinSectionDepth(Double depth,
            List<DepthProfileEntry> profileEntriesToUse) {
        DepthProfileEntry depthEntry = null;
        for (DepthProfileEntry entry : profileEntriesToUse) {
            if (entry.getSectionDepthMin().equals(depth) && entry.getDeltaDepth1m() != null) {
                depthEntry = entry;
                break;
            }
        }

        return depthEntry;
    }


    /**
     * Set the section min and max depths and determine the 
     * section thickness for each section. A section is defined
     * by continuous rows that are or are not a thermocline.
     * 
     * This implements the following section of R code from the ATBD:
     * 
     *  data2$nextdepth2 <- c(data2$depth2[1:(nrow(data2)-1)+1], NA) 
     *  data2$nextdepth3 <- c(data2$depth3[1:(nrow(data2)-1)+1], NA)
     *  data2$sectionmin <- NA
     *  data2$sectionmax <- NA
     *
     *  data2$sectionmin[data2$thermocline] <- data2$depth3[data2$thermocline]
     *  data2$sectionmax[data2$thermocline] <- data2$nextdepth2[data2$thermocline]
     *  data2$sectionmin[!data2$thermocline] <- data2$depth2[!data2$thermocline]
     *  data2$sectionmax[!data2$thermocline] <- data2$nextdepth3[!data2$thermocline]
     *
     *  data2$sectionmax[nrow(data2)] <- max(data$depth)
     *  data2$sectionmin[1] <- data2$depth[1]
     *  data2$sectionthick <- data2$sectionmax - data2$sectionmin
     *  for(i in 2: (nrow(data2)-1)){
     *    if(data2$thermocline[i]==F & data2$sectionthick[i]<0.33*max(data$depth)){data2$thermocline[i] <- T}
     *  }
     * 
     * @param thermoclineChangeRows - the rows that represent the start or
     *          end of a thermocline section
     * @param maxSampleDepth - the max sampling depth
     * @param maxDepth - the max depth of the lake
     */
    private void determineSectionThickness(
            List<DepthProfileEntry> thermoclineChangeRows,
            Double maxSampleDepth, Double maxDepth) {
        
        
        // If only a single row then no need to do anything.
        if (thermoclineChangeRows.size() > 1) {
            // For each entry, set the "nextDepth2" and "nextDepth3"
            // to the corresponding depth values from the next entry.
            // For the last entry, do nothing because there is no
            // "next" entry for the last one.
            // Does this by starting with the next to last entry
            // and move back to the first entry.
            for (int i = thermoclineChangeRows.size() - 2; i >= 0; i--) {
                DepthProfileEntry currentEntry = thermoclineChangeRows.get(i);
                DepthProfileEntry nextEntry = thermoclineChangeRows.get(i+1);
                currentEntry.setNextDepth2(nextEntry.getDepth2());
                currentEntry.setNextDepth3(nextEntry.getDepth3());
            }
            
            // Now set the section min and max depths.
            for (DepthProfileEntry entry : thermoclineChangeRows) {
                if (entry.isThermocline()) {
                    entry.setSectionDepthMin(entry.getDepth3());
                    entry.setSectionDepthMax(entry.getNextDepth2());
                }
                else {
                    entry.setSectionDepthMin(entry.getDepth2());
                    entry.setSectionDepthMax(entry.getNextDepth3());
                }
            }
            
            // Fill in the first and last section depths.
            DepthProfileEntry firstEntry = thermoclineChangeRows.get(0); 
            firstEntry.setSectionDepthMin(firstEntry.getDepth());
            DepthProfileEntry lastEntry = thermoclineChangeRows.get(thermoclineChangeRows.size()-1); 
            lastEntry.setSectionDepthMax(maxSampleDepth);
            
            // Fill in the section thickness
            for (DepthProfileEntry entry : thermoclineChangeRows) {
                entry.setSectionThickness(entry.getSectionDepthMax() - entry.getSectionDepthMin());
            }
            
            // Now check for 2 metalimnions, i.e. is there a section that is 
            // not a thermocline between 2 thermoclines and that section is at least
            // 1/3 of the lake depth. If not, indicate that section is part of the 
            // previous thermocline.
            // To cover all cases, first check to see if there are only 2 
            // change rows. We know there are at least 2, but need to handle
            // the case of only 2 separately.
            Double thirdOfMax = maxDepth/3.0;
            if (thermoclineChangeRows.size() == 2) {
                // Only 2 rows, so do both.
                for (int i = 0; i <= thermoclineChangeRows.size() - 1; i++) {
                    DepthProfileEntry currentEntry = thermoclineChangeRows.get(i);
                    if (!currentEntry.isThermocline() && 
                            ((currentEntry.getSectionThickness() < thirdOfMax) || 
                             (currentEntry.getSectionThickness() < 2.0))) {
                        currentEntry.setThermocline(true);
                    }
                }
            }
            else {
                // More than 2 rows. Skip the first, because it will always be before
                // the first thermocline and don't bother with the last.
                for (int i = 1; i < thermoclineChangeRows.size() - 1; i++) {
                    DepthProfileEntry currentEntry = thermoclineChangeRows.get(i);
                    if (!currentEntry.isThermocline() && 
                            ((currentEntry.getSectionThickness() < thirdOfMax) || 
                             (currentEntry.getSectionThickness() < 2.0))) {
                        currentEntry.setThermocline(true);
                    }
                }
            }
        }
    }


    /**
     * Determine necessary values for entries to be used to calculate
     * the metalimnions and stratification values.
     * This implements the following section of R code from the ATBD:
     *  
     * depth <- as.numeric(pubProfileData$sampleDepth[pubProfileData$profileID==setGroup[k]])
     * temp <- as.numeric(pubProfileData$waterTemp[pubProfileData$profileID==setGroup[k]])
     * data<-data.frame(depth=depth, temp=temp)
     * data$depth2 <- c(NA, data$depth[2:nrow(data)-1])
     * data$temp2 <- c(NA, data$temp[2:nrow(data)-1])
     * data$deltaDepth <- data$depth - data$depth2
     * data$deltaTemp <- data$temp - data$temp2
     * data$deltaDepth1m <- ifelse(data$deltaDepth < 1, c(NA,NA,data$deltaDepth[3:nrow(data)] + data$deltaDepth[2:(nrow(data)-1)]), data$deltaDepth)
     * data$deltaTemp1m <- ifelse(data$deltaDepth < 1, c(NA,NA,data$deltaTemp[3:nrow(data)] + data$deltaTemp[2:(nrow(data)-1)]), data$deltaTemp)
     * data$depth3 <- data$depth - data$deltaDepth1m
     * data$tempDivideDepth <- data$deltaTemp1m/data$deltaDepth1m
     * data$thermocline[-1>data$tempDivideDepth]<-TRUE
     * data$thermocline[is.na(data$thermocline)]<-FALSE
     * 
     * @param depthProfileEntriesFullList - full list of profile data 
     *          (already sorted by depth)
     * @return a list of the profile data entries to use for calculation
     */
    private List<DepthProfileEntry> determineProfileEntries(
            List<DepthProfileEntry> depthProfileEntriesFullList) {
        
        List<DepthProfileEntry> entriesToUse = new ArrayList<>();
        
        // Create a list of just the activities that we can use for calculations.
        // We can't use activities with missing necessary data, or duplicates
        // that have different values.
        List<DepthProfileEntry> entriesToProcess = new ArrayList<>();
        List<Double> duplicateDepthList = new ArrayList<Double>();
        for (DepthProfileEntry entry : depthProfileEntriesFullList) {
            // If this activity has no depth or temp value, ignore it
            if (entry.getDepth() == null) continue;
            if (entry.getTemp() == null) continue;
            
            // If this is a duplicate with different values, then determine
            // if it should be used or not. If the water temps are the same
            // then it can be used, but only use 1 of the duplicates.
            if (entry.getDuplicateFlag() != null && entry.getDuplicateFlag() == 2.0) {
                if (useDuplicate(entry, depthProfileEntriesFullList)) {
                    if (duplicateDepthList.contains(entry.getDepth())) {
                        // Can use this duplicate, but already have one for this 
                        // depth, so skip this one.
                        continue;
                    }
                    else {
                        // Can use this duplicate and don't have one for this
                        // depth yet, so save it.
                        duplicateDepthList.add(entry.getDepth());
                    }
                }
                else {
                    // Can't use this duplicate, so skip it.
                    continue;
                }
            }
            
            entriesToProcess.add(entry);
        }
        
        log.debug("DepthProfileData entries: " + depthProfileEntriesFullList.size());
        log.debug("DepthProfileData entries being processed: " + entriesToProcess.size());
        
        DepthProfileEntry previousEntry = null;
        for (int i = 0; i < entriesToProcess.size(); i++) {
            DepthProfileEntry currentEntry = entriesToProcess.get(i);
            
            if (i > 0) {
                Double previousDepth = previousEntry.getDepth();
                Double previousTemp = previousEntry.getTemp();
                currentEntry.setDepth2(previousDepth);
                currentEntry.setTemp2(previousTemp);
                currentEntry.setDeltaDepth(currentEntry.getDepth() - previousDepth);
                currentEntry.setDeltaTemp(currentEntry.getTemp() - previousTemp);
                if (currentEntry.getDeltaDepth() < 1.0) {
                    if (currentEntry.getDeltaDepth() != null && previousEntry.getDeltaDepth() != null) {
                        Double deltaDepth1m = currentEntry.getDeltaDepth() + previousEntry.getDeltaDepth();
                        currentEntry.setDeltaDepth1m(deltaDepth1m);
                    }
                    if (currentEntry.getDeltaTemp() != null && previousEntry.getDeltaTemp() != null) {
                        Double deltaTemp1m = currentEntry.getDeltaTemp() + previousEntry.getDeltaTemp();
                        currentEntry.setDeltaTemp1m(deltaTemp1m);
                    }
                }
                else {
                    currentEntry.setDeltaDepth1m(currentEntry.getDeltaDepth());
                    currentEntry.setDeltaTemp1m(currentEntry.getDeltaTemp());
                }
                
                Double depth3 = (currentEntry.getDeltaDepth1m() != null) ? currentEntry.getDepth() - currentEntry.getDeltaDepth1m() : null; 
                currentEntry.setDepth3(depth3);
                Double deltaTempDivideDepth = null;
                if (currentEntry.getDeltaTemp1m() != null && currentEntry.getDeltaDepth1m() != null) {
                    deltaTempDivideDepth = currentEntry.getDeltaTemp1m()/currentEntry.getDeltaDepth1m();
                }
                currentEntry.setDeltaTempPerDepth(deltaTempDivideDepth);
                
                // Temperature should be going down for deeper depths, so the differences
                // should be negative. If temperatures are going up, even by large amounts,
                // they should be ignored. 
                if (deltaTempDivideDepth != null && deltaTempDivideDepth < -1.0) {
                    currentEntry.setThermocline(true);
                }
            }

            previousEntry = currentEntry;
            entriesToUse.add(currentEntry);
        }
        
        return entriesToUse;
    }


    /**
     * Method to determine whether to use this duplicate profile data in 
     * calculations or not. According to the ATBD, go ahead and use it
     * if the duplicates for the depth all have the same water temps. This means
     * that other values are different, but as long as the water temp 
     * is the same then we use it for calculations.
     * 
     * @param entry
     * @param depthProfileEntriesFullList
     * @return
     */
    private boolean useDuplicate(DepthProfileEntry entry,
            List<DepthProfileEntry> depthProfileEntriesFullList) {

        boolean useDup = true;

        Double depth = entry.getDepth();
        Double temp = entry.getTemp();

        for (DepthProfileEntry entry2 : depthProfileEntriesFullList) {
            if ((entry != entry2) && (depth.equals(entry2.getDepth()))) {
                if (!temp.equals(entry2.getTemp())) {
                    useDup = false;
                    break;
                }
            }
        }
        
        return useDup;
    }
}
