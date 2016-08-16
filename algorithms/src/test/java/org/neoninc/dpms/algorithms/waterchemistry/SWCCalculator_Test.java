package org.neoninc.dpms.algorithms.waterchemistry;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.neoninc.dpms.algorithms.waterchemistry.WaterChemistryCalculator;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

public class SWCCalculator_Test {

    public static String BOGUS_TYPE_STRING = "BOGUS";
    public static Double WATER_TEMP_1 = new Double(8.1);
    public static Double WATER_TEMP_2 = new Double(15.5);
    public static Double CONDUNCTANCE_1 = new Double(1024.0);
    public static Double CONDUNCTANCE_2 = new Double(483.9);
    public static Double PH_1 = new Double(5.97);
    public static Double PH_2 = new Double(8.43);
    public static Double SAMPLE_VOLUME_1 = new Double(50.0);
    public static Double SAMPLE_VOLUME_2 = new Double(100.0);
    public static Double NORMALITY_1 = new Double(1.6);
    public static Double NORMALITY_2 = new Double(0.16);
    public static String TITRATION_CURVE1 = "0,7.5;25,7.25;50,6.75;75,6.5;100,6.0;112,5.58;120,5.45;123,5.0;125,4.9;";
    public static String TITRATION_CURVE2 = "0,7.9;25,7.36;50,7.1;75,6.88;100,6.71;125,6.58;150,6.45;175,6.3;200,6.13;225,5.95;250,5.7;265,5.54;275,5.35;278,5.27;281,5.19";
    public static String TITRATION_CURVE3 = "0,8.26;3,8.22;6,8.15;9,8.05;13,7.88;16,7.76;19,7.66;22,7.59;25,7.53;28,7.47;31,7.4;34,7.36;37,7.31;40,7.27;50,7.13;60,7.05;81,6.86;124,6.59;155,6.44;205,6.18;255,5.95;295,5.7;315,5.55;325,5.45;329,5.42;333,5.38;337,5.34;341,5.29;344,5.23;347,5.21;350,5.17;353,5.1;356,5.05;359,4.97;362,4.91;365,4.84;368,4.76;371,4.64;375,4.5;378,4.34;381,4.19;384,4.05;387,3.95;390,3.83;393,3.74;396,3.65;408,3.41;428,3.18;";
    public static String TITRATION_CURVE_MULTI_PEAKS = "0,8.72;5,8.65;10,8.57;15,8.41;18,8.3;21,8.17;24,8.04;27,7.92;30,7.83;35,7.64;40,7.5;50,7.32;65,7.09;90,6.85;125,6.61;175,6.32;225,6.04;255,5.77;275,5.56;290,5.37;300,5.19;310,4.98;315,4.73;318,4.61;321,4.48;324,4.33;327,4.18;330,4.07;333,3.94;340,3.71;345,3.59;355,3.44;375,3.2;400,3.03;";
    public static String TITRATION_CURVE_HIGH_PH_PEAK = "0,8.97;5,8.91;10,8.76;15,8.61;20,8.4;25,8.18;28,7.98;31,7.85;35,7.7;38,7.62;50,7.37;60,7.2;70,7.08;85,6.94;100,6.8;125,6.63;150,6.48;180,6.31;220,6.05;260,5.78;290,5.47;300,5.32;310,5.12;315,4.99;320,4.83;325,4.62;328,4.43;331,4.29;334,4.1;337,3.94;340,3.82;350,3.54;355,3.45;365,3.31;380,3.14;395,3.01;";
    public static String TITRATION_CURVE_PH_INCREASE = "0,7.66;25,7.03;50,6.67;75,6.42;100,5.17;125,5.89;150,5.46;175,4.11;200,3.33;203,3.29;206,3.26;209,3.21;212,3.17;215,2.14;218,3.11;221,3.08;224,3.05;227,3.03;230,3.01;233,2.99;236,2.97;";
    public static String TITRATION_CURVE_PH_INCREASE_AT_START = "0,7.46;3,7.57;6,7.6;12,7.56;18,7.67;27,7.51;36,7.38;48,7.33;60,7.21;75,7.15;90,7.08;108,6.96;126,6.87;147,6.8;168,6.74;198,6.6;218,6.54;260,6.4;302,6.25;350,6.05;398,5.85;431,5.6;443,5.51;449,5.46;452,5.41;455,5.36;458,5.34;461,5.31;464,5.28;467,5.23;470,5.18;476,5.03;482,4.85;512,4.07;542,3.77;572,3.59;587,3.53;599,3.48;";
    public static String INVALID_TITRATION_CURVE1 = "0,7.5;25,7.25;";
    
    private WaterChemistryCalculator calculator;
    
    @BeforeTest
    private void setup () {
        calculator = new WaterChemistryCalculator();
    }
    
    @Test(enabled=false)
    public void testParseTitrationTest() {
        List<Pair<Double, Double>> curve = null;
        String titrationData = "0,8.09;25,7.25;50,6.94;75,6.71;100,6.53;125,6.35;150,6.19;175,6.01;200,5.8;225,5.43;245,4.86;248,4.69;251,4.49;254,4.26;257,4.07;260,3.91;265,3.72";
        curve = calculator.getTitrationCurve(titrationData);
        for (Pair<Double,Double> point : curve) {
            System.out.println (point.getLeft() + "\t" + point.getRight());
        }
    }
    
    @Test
    public void testParseTitrationDataNull() {
        List<Pair<Double, Double>> curve = null;
        curve = calculator.getTitrationCurve(null);
        assertTrue("Unexpected values returned when null passed", curve.isEmpty());
        
        curve = calculator.getTitrationCurve("");
        assertTrue("Unexpected values returned when empty string passed", curve.isEmpty());
    }
    
    @Test
    public void testParseTitrationData() {
        List<Pair<Double, Double>> curve = null;
        curve = calculator.getTitrationCurve(TITRATION_CURVE1);
        assertEquals("Unexpected number of values returned", 9, curve.size());
        
        curve = calculator.getTitrationCurve(TITRATION_CURVE2);
        assertEquals("Unexpected number of values returned", 15, curve.size());
    }
    
    @Test
    public void testGetCounterReadingEmpty() {
        List<Pair<Double, Double>> curve = null;
        curve = calculator.getTitrationCurve("");

        Double counterAverage = calculator.getCounterAverageAtPeak(curve);
        assertEquals("Incorrect counter average", 0.0, counterAverage);
    }
    
    @Test
    public void testGetCounterReading() {
        List<Pair<Double, Double>> curve = null;
        curve = calculator.getTitrationCurve(TITRATION_CURVE1);
        assertEquals("Unexpected number of values returned", 9, curve.size());
        
        Double counterAverage = calculator.getCounterAverageAtPeak(curve);
        assertEquals("Incorrect counter average", 121.5, counterAverage);
    }
    
    @Test
    public void testGetCounterReadingWithMultiplePeaks() {
        List<Pair<Double, Double>> curve = null;
        curve = calculator.getTitrationCurve(TITRATION_CURVE_MULTI_PEAKS);
        assertEquals("Unexpected number of values returned", 34, curve.size());
        
        Double counterAverage = calculator.getCounterAverageAtPeak(curve);
        assertEquals("Incorrect counter average", 319.0, counterAverage);
    }
    
    @Test
    public void testGetCounterReadingWithHighPHPeak() {
        List<Pair<Double, Double>> curve = null;
        curve = calculator.getTitrationCurve(TITRATION_CURVE_HIGH_PH_PEAK);
        assertEquals("Unexpected number of values returned", 36, curve.size());
        
        Double counterAverage = calculator.getCounterAverageAtPeak(curve);
        assertEquals("Incorrect counter average", 329.5, counterAverage);
    }
    
    @Test
    public void testGetCounterReadingWithPHIncrease() {
        List<Pair<Double, Double>> curve = null;
        curve = calculator.getTitrationCurve(TITRATION_CURVE_PH_INCREASE);
        assertEquals("Unexpected number of values returned", 19, curve.size());
        
        Double counterAverage = calculator.getCounterAverageAtPeak(curve);
        assertEquals("Incorrect counter average", 162.5, counterAverage);
    }
    
    @Test
    public void testCalculateIPTANC() {

        Map<String,Double> values = null;
        values = calculator.calculate(TITRATION_CURVE1, WaterChemistryCalculator.WC_METHOD_TYPE_IPT, WaterChemistryCalculator.WC_SAMPLE_TYPE_ANC, 
                SAMPLE_VOLUME_1, NORMALITY_1, WATER_TEMP_1, CONDUNCTANCE_1);
        assertNotNull("Calculation failed", values);
        assertTrue("MeqPerL Values do not match, expected <4.9086>, but was <" + values.get(WaterChemistryCalculator.MEQ_PER_L_KEY) + ">",
                valueWithinTolerance(4.9086, values.get(WaterChemistryCalculator.MEQ_PER_L_KEY), .0001));
        assertTrue("MgPerL Values do not match, expected <246.0>, but was <" + values.get(WaterChemistryCalculator.MG_PER_L_KEY) + ">",
                valueWithinTolerance(246.0, values.get(WaterChemistryCalculator.MG_PER_L_KEY), .5));
    }
    
    @Test
    public void testCalculateIPTALK() {

        Map<String,Double> values = null;
        values = calculator.calculate(TITRATION_CURVE1, WaterChemistryCalculator.WC_METHOD_TYPE_IPT, WaterChemistryCalculator.WC_SAMPLE_TYPE_ALK, 
                SAMPLE_VOLUME_2, NORMALITY_1, WATER_TEMP_1, CONDUNCTANCE_1);
        assertNotNull("Calculation failed", values);
        assertTrue("MeqPerL Values do not match, expected <2.4543>, but was <" + values.get(WaterChemistryCalculator.MEQ_PER_L_KEY) + ">",
                valueWithinTolerance(2.4543, values.get(WaterChemistryCalculator.MEQ_PER_L_KEY), .0001));
        assertTrue("MgPerL Values do not match, expected <123.0>, but was <" + values.get(WaterChemistryCalculator.MG_PER_L_KEY) + ">",
                valueWithinTolerance(123.0, values.get(WaterChemistryCalculator.MG_PER_L_KEY), .5));
    }
    
    @Test
    public void testCalculateIPTHighPhPeak() {

        Map<String,Double> values = null;
        values = calculator.calculate(TITRATION_CURVE_HIGH_PH_PEAK, WaterChemistryCalculator.WC_METHOD_TYPE_IPT, WaterChemistryCalculator.WC_SAMPLE_TYPE_ALK, 
                SAMPLE_VOLUME_2, NORMALITY_1, WATER_TEMP_1, CONDUNCTANCE_1);
        assertNotNull("Calculation failed", values);
        assertTrue("MeqPerL Values do not match, expected <6.6559>, but was <" + values.get(WaterChemistryCalculator.MEQ_PER_L_KEY) + ">",
                valueWithinTolerance(6.6559, values.get(WaterChemistryCalculator.MEQ_PER_L_KEY), .0001));
        assertTrue("MgPerL Values do not match, expected <333.0>, but was <" + values.get(WaterChemistryCalculator.MG_PER_L_KEY) + ">",
                valueWithinTolerance(333.0, values.get(WaterChemistryCalculator.MG_PER_L_KEY), .5));
    }
    
    @Test
    public void testCalculateIPTPHIncrease() {

        Map<String,Double> values = null;
        values = calculator.calculate(TITRATION_CURVE_PH_INCREASE, WaterChemistryCalculator.WC_METHOD_TYPE_IPT, WaterChemistryCalculator.WC_SAMPLE_TYPE_ALK, 
                SAMPLE_VOLUME_2, NORMALITY_1, WATER_TEMP_1, CONDUNCTANCE_1);
        assertNotNull("Calculation failed", values);
        assertTrue("MeqPerL Values do not match, expected <3.2825>, but was <" + values.get(WaterChemistryCalculator.MEQ_PER_L_KEY) + ">",
                valueWithinTolerance(3.2825, values.get(WaterChemistryCalculator.MEQ_PER_L_KEY), .0001));
        assertTrue("MgPerL Values do not match, expected <164.0>, but was <" + values.get(WaterChemistryCalculator.MG_PER_L_KEY) + ">",
                valueWithinTolerance(164.0, values.get(WaterChemistryCalculator.MG_PER_L_KEY), .5));
    }
    
    @Test
    public void testCalculateInvalidSampleType() {

        Map<String,Double> values = null;
        values = calculator.calculate(TITRATION_CURVE1, WaterChemistryCalculator.WC_METHOD_TYPE_IPT, BOGUS_TYPE_STRING, 
                SAMPLE_VOLUME_1, NORMALITY_1, WATER_TEMP_1, CONDUNCTANCE_1);
        assertNull("Calculation successful even though sample type is invalid", values);
    }
    
    @Test
    public void testCalculateInvalidMethod() {

        Map<String,Double> values = null;
        values = calculator.calculate(TITRATION_CURVE1, BOGUS_TYPE_STRING, WaterChemistryCalculator.WC_SAMPLE_TYPE_ANC, 
                SAMPLE_VOLUME_1, NORMALITY_1, WATER_TEMP_1, CONDUNCTANCE_1);
        assertNull("Calculation successful even though method type is invalid", values);
    }
    
    @Test(enabled=false)
    public void testCalculateGRAN_ANC() {

        Map<String,Double> values = null;
        values = calculator.calculate(TITRATION_CURVE3, WaterChemistryCalculator.WC_METHOD_TYPE_GRAN, WaterChemistryCalculator.WC_SAMPLE_TYPE_ANC, 
                SAMPLE_VOLUME_1, NORMALITY_1, WATER_TEMP_1, CONDUNCTANCE_1);
        assertNotNull("Calculation failed", values);
        assertEquals("Incorrect MeqPerL value", 15.344, values.get(WaterChemistryCalculator.MEQ_PER_L_KEY));
        assertEquals("Incorrect MgPerL value", 768.0, values.get(WaterChemistryCalculator.MG_PER_L_KEY));
    }
    
    @Test(enabled=false)
    public void testCalculateGRAN_ALK() {

        Map<String,Double> values = null;
        values = calculator.calculate(TITRATION_CURVE3, WaterChemistryCalculator.WC_METHOD_TYPE_GRAN, WaterChemistryCalculator.WC_SAMPLE_TYPE_ALK, 
                SAMPLE_VOLUME_1, NORMALITY_1, WATER_TEMP_1, CONDUNCTANCE_1);
        assertNotNull("Calculation failed", values);
        assertEquals("Incorrect MeqPerL value", 15.344, values.get(WaterChemistryCalculator.MEQ_PER_L_KEY));
        assertEquals("Incorrect MgPerL value", 768.0, values.get(WaterChemistryCalculator.MG_PER_L_KEY));
    }
    
    @Test(enabled=false)
    public void testCalculateGRAN_ANC2() {

        Map<String,Double> values = null;
        values = calculator.calculate(TITRATION_CURVE2, WaterChemistryCalculator.WC_METHOD_TYPE_GRAN, WaterChemistryCalculator.WC_SAMPLE_TYPE_ANC, 
                SAMPLE_VOLUME_1, NORMALITY_1, WATER_TEMP_1, CONDUNCTANCE_1);
        assertNotNull("Calculation failed", values);
        assertEquals("Incorrect MeqPerL value", 12.027, values.get(WaterChemistryCalculator.MEQ_PER_L_KEY));
        assertEquals("Incorrect MgPerL value", 602.0, values.get(WaterChemistryCalculator.MG_PER_L_KEY));
    }
    
    @Test(enabled=false)
    public void testCalculateGRAN_InvalidData() {

        Map<String,Double> values = null;
        values = calculator.calculate(INVALID_TITRATION_CURVE1, WaterChemistryCalculator.WC_METHOD_TYPE_GRAN, WaterChemistryCalculator.WC_SAMPLE_TYPE_ANC, 
                SAMPLE_VOLUME_1, NORMALITY_1, WATER_TEMP_1, CONDUNCTANCE_1);
        assertNotNull("Calculation failed", values);
        assertNull("Unexpected meqL value returned for invalid data.", values.get(WaterChemistryCalculator.MEQ_PER_L_KEY));
        assertNull("Unexpected mgL value returned for invalid data.", values.get(WaterChemistryCalculator.MG_PER_L_KEY));
    }
    
    @Test(enabled=false)
    public void testCalculateGRAN_PHIncrease() {

        Map<String,Double> values = null;
        values = calculator.calculate(TITRATION_CURVE_PH_INCREASE_AT_START, WaterChemistryCalculator.WC_METHOD_TYPE_GRAN, WaterChemistryCalculator.WC_SAMPLE_TYPE_ALK, 
                SAMPLE_VOLUME_1, NORMALITY_2, WATER_TEMP_1, CONDUNCTANCE_1);
        assertNotNull("Calculation failed", values);
        assertEquals("Incorrect MeqPerL value", 1.947, values.get(WaterChemistryCalculator.MEQ_PER_L_KEY));
        assertEquals("Incorrect MgPerL value", 97.0, values.get(WaterChemistryCalculator.MG_PER_L_KEY));
    }
    
    @Test
    public void testRoundMgL() {

        Double roundedValue = calculator.roundMgPerLValue(1111.12345);
        assertEquals("Incorrect rounded large value", 1111.123, roundedValue);
        roundedValue = calculator.roundMgPerLValue(1000.123885);
        assertEquals("Incorrect rounded large value", 1000.124, roundedValue);
        roundedValue = calculator.roundMgPerLValue(500.98723);
        assertEquals("Incorrect rounded medium value", 501.0, roundedValue);
        roundedValue = calculator.roundMgPerLValue(50.298723);
        assertEquals("Incorrect rounded small value", 50.0, roundedValue);
    }
    
    @Test
    public void testRoundMeqL() {

        Double roundedValue = calculator.roundMeqPerLValue(32.12345);
        assertEquals("Incorrect rounded large value", 32.123, roundedValue);
        roundedValue = calculator.roundMeqPerLValue(20.123885);
        assertEquals("Incorrect rounded large value", 20.124, roundedValue);
        roundedValue = calculator.roundMeqPerLValue(2.98723);
        assertEquals("Incorrect rounded medium value", 2.987, roundedValue);
        roundedValue = calculator.roundMeqPerLValue(2.123723);
        assertEquals("Incorrect rounded medium value", 2.124, roundedValue);
        roundedValue = calculator.roundMeqPerLValue(1.298723);
        assertEquals("Incorrect rounded small value", 1.299, roundedValue);
    }
    
    private boolean valueWithinTolerance (double actualValue, double expectedValue, double tolerance) {
        boolean withinTolerance = false;
        
        if (actualValue == expectedValue) {
            withinTolerance = true;
        }
        else {
            double diff = Math.abs(actualValue - expectedValue);
            if (diff <= tolerance) {
                withinTolerance = true;
            }
        }
        
        return withinTolerance;
    }
}
