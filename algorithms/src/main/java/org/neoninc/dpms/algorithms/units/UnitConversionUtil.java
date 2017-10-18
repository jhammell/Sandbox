package org.neoninc.dpms.algorithms.units;

public class UnitConversionUtil {
	public static final double CONSTANT_FROM_DEGREE_TO_KELVIN = 273.15;
	public static final double COEF_FROM_KILOUNIT_TO_UNIT = 1E3;
	public static final double COEF_FROM_MILLIUNIT_TO_UNIT = 1E-3;
	public static final double COEF_FROM_MICROUNIT_TO_UNIT = 1E-6;
	
	public enum UnitConvertMethod {
		DEGREE_TO_KELVIN,
		KPA_TO_PA,
		MILLIMOL_TO_MOL,
		MICROMOL_TO_MOL
		
	}

	public static Double unitConversion(double valueIn, UnitConvertMethod convertMethod) {
		Double valueOut = null;
		
		switch (convertMethod) {
		case DEGREE_TO_KELVIN:
			valueOut = valueIn + CONSTANT_FROM_DEGREE_TO_KELVIN;
			break;
		case KPA_TO_PA:
			valueOut = valueIn * COEF_FROM_KILOUNIT_TO_UNIT;
			break;
		case MILLIMOL_TO_MOL:
			valueOut = valueIn * COEF_FROM_MILLIUNIT_TO_UNIT;
			break;
		case MICROMOL_TO_MOL:
			valueOut = valueIn * COEF_FROM_MICROUNIT_TO_UNIT;
			break;
		default:
			valueOut = valueIn;
		}
		
		return valueOut;
	}

}
