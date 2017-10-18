package org.neoninc.dpms.algorithms.depthprofile;

import java.util.Comparator;

public class DepthProfileEntryComparator implements Comparator<DepthProfileEntry> {

    @Override
    public int compare(DepthProfileEntry o1, DepthProfileEntry o2) {

        if ( (o1 == null) && (o2 != null) ) return -1;
        if ( (o1 != null) && (o2 == null) ) return 1;
        if ( (o1 == null) && (o2 == null) ) return 0;

        return compareDoubles (o1.getDepth(), o2.getDepth());
    }

    private int compareDoubles (Double d1, Double d2) {
        if (d1 == null && d2 == null) return 0;
        if (d1 == null) return -1;
        if (d2 == null) return 1;
        return d1.compareTo(d2);
    }

}
