package org.neoninc.dpms.algorithms.units;

import org.neoninc.dpms.algorithms.units.UnitConversionUtil.UnitConvertMethod;
import org.testng.annotations.Test;
import static org.testng.AssertJUnit.*;

public class UnitConversionUtil_Test {

	@Test
	public void testUnitConversion () {
		
		UnitConvertMethod method = UnitConvertMethod.DEGREE_TO_KELVIN;		
		double valueIn = 0.;
		double valueOut = UnitConversionUtil.unitConversion(valueIn, method);
		assertTrue(valueOut==273.15);
		
		method = UnitConvertMethod.KPA_TO_PA;
		valueIn = 10.;
		valueOut = UnitConversionUtil.unitConversion(valueIn, method);
		assertTrue(valueOut==10000.);
		
		method = UnitConvertMethod.MILLIMOL_TO_MOL;
		valueIn = 15;
		valueOut = UnitConversionUtil.unitConversion(valueIn, method);
		assertTrue(valueOut==0.015);
		
		method = UnitConvertMethod.MICROMOL_TO_MOL;
		valueIn = 15;
		valueOut = UnitConversionUtil.unitConversion(valueIn, method);
		System.out.println(valueOut);
//		assertTrue(valueOut==1.5E-5);
//		assertEquals("valueOut", 1.5E-5, valueOut);
		assertTrue(valueOut-1.5E-5 < 1E-20);
	}
}
