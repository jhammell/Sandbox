/**
 * 
 */
package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.testng.annotations.*;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

/**
 * @author bpenn
 *
 */
public class ValidateAndCopyTextFromL0toL1_Test {
	private DPMSMethActivity L0inputActy = new DPMSMethActivity();
	private DPMSMethActivity L1inputActy = new DPMSMethActivity();
	Calendar cal = Calendar.getInstance();
	private Date tranTime;
	private Date tranTime2;
	private ArrayList<String> techIdList = new ArrayList<String>();
	private Long L0valId;
	private Long L1valId;
	private Long L1valTypeId = 10L;

	@BeforeMethod
	@BeforeClass
	public void setUp() throws Exception {
		
		BasicConfigurator.configure();
		techIdList.add("D10KMT05");
		techIdList.add("D10BJP03");
		techIdList.add("C03ZLY04");
		techIdList.add("C03MDR02");

	}

	@Test
	public void testValidateAndCopyTextFromL0toL1_Test() {
		ArrayList<Long> fieldList = new ArrayList<Long>();
		fieldList.add(101L);
		fieldList.add(102L);

		ArrayList<Long> qaQcValIdList = new ArrayList<Long>();
		qaQcValIdList.add(1L);
		qaQcValIdList.add(2L);
		DPMSMethStreamData qaQcMethStream = new DPMSMethStreamData();

		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.MAY, 21); //Year, month and day of month
		tranTime = cal.getTime();
		cal.set(2014, Calendar.APRIL, 1);
		tranTime2 = cal.getTime();
		
		//L0 data
		DPMSMethStreamData msA = new DPMSMethStreamData();
		L0inputActy.getMethStreams().add(msA);
		DPMSMStreamReadout rdotA1 = new DPMSMStreamReadout();
		msA.getMSReadouts().add(rdotA1);
		rdotA1.setReadoutTranTime(tranTime);
		rdotA1.setValueStringForValueId(fieldList.get(0), "D10BJP03", true);
		//rdotA1.setValueStringForValueId(fieldList.get(0), "D10FPP01", true);
		
		//L1 data  create blank MethStream data with correct L1valTypeId
		DPMSMethStreamData msB = new DPMSMethStreamData();
		msB.setMStreamValTypeID(L1valTypeId);
		L1inputActy.getMethStreams().add(msB);
		DPMSMStreamReadout rdotB1 = new DPMSMStreamReadout();
		msB.getMSReadouts().add(rdotB1);
		
		L0valId = fieldList.get(0);
		L1valId = L0valId;
		
		ValidateAndCopyTextFromL0toL1 qaQc = new ValidateAndCopyTextFromL0toL1(fieldList, L0valId, L0valId, fieldList, L1valTypeId, L0inputActy, L1inputActy, techIdList);
		qaQc.runAlgorithm();
		

	}
}
