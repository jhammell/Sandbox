package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
//import org.junit.Before;
//import org.junit.Test;
import org.testng.annotations.*;
import org.neoninc.dpms.datastructures.DPMSMStreamReadout;
import org.neoninc.dpms.datastructures.DPMSMethActivity;
import org.neoninc.dpms.datastructures.DPMSMethStreamData;

public class IdQualFieldsQAQC_Test {

	@BeforeMethod
	@BeforeClass
	public void setUp() throws Exception {
		
		BasicConfigurator.configure();
	}
	
	@Test
	public void runTest() {
		ArrayList<Long> fieldList = new ArrayList<Long>();
		fieldList.add(163L);
		fieldList.add(162L);
		fieldList.add(160L);
		fieldList.add(161L);
		fieldList.add(159L);
		// lookup table for identificationQualifier
		ArrayList<String> IdentificationQualifierLookupTable = new ArrayList<String> ();
		IdentificationQualifierLookupTable.add("CS");
		IdentificationQualifierLookupTable.add("AS");
		IdentificationQualifierLookupTable.add("CG");
		IdentificationQualifierLookupTable.add("AG");
		IdentificationQualifierLookupTable.add("CB");
		IdentificationQualifierLookupTable.add("AB");
		IdentificationQualifierLookupTable.add("CF");
		IdentificationQualifierLookupTable.add("AF");
		IdentificationQualifierLookupTable.add("AV");
		IdentificationQualifierLookupTable.add("CV");
		//lookup table for taxonID
		ArrayList<String> TaxonIdLookupTable = new ArrayList<String> ();
		TaxonIdLookupTable.add("AMHA");
		TaxonIdLookupTable.add("AMIN");
		TaxonIdLookupTable.add("AMLE");
		TaxonIdLookupTable.add("AMSP");
		TaxonIdLookupTable.add("BATA");
		TaxonIdLookupTable.add("BLBR");
		TaxonIdLookupTable.add("BLCA");
		TaxonIdLookupTable.add("BLHY");
		TaxonIdLookupTable.add("BLSP");
		TaxonIdLookupTable.add("BRID");
		TaxonIdLookupTable.add("CHBA");
		TaxonIdLookupTable.add("CHCA");
		TaxonIdLookupTable.add("CHER");
		TaxonIdLookupTable.add("CHFO");
		TaxonIdLookupTable.add("CHHI");
		TaxonIdLookupTable.add("CHIN");
		TaxonIdLookupTable.add("CHPE");
		TaxonIdLookupTable.add("CHSP");
		TaxonIdLookupTable.add("COCR");
		TaxonIdLookupTable.add("CRPA");
		TaxonIdLookupTable.add("CYGU");
		TaxonIdLookupTable.add("CYLU");
		TaxonIdLookupTable.add("CYSP");
		TaxonIdLookupTable.add("DIGR");
		TaxonIdLookupTable.add("DIDE");
		TaxonIdLookupTable.add("DIHE");
		TaxonIdLookupTable.add("DIME");
		TaxonIdLookupTable.add("DIMI");
		TaxonIdLookupTable.add("DIOR");
		TaxonIdLookupTable.add("DPSP");
		TaxonIdLookupTable.add("DISP");
		TaxonIdLookupTable.add("GEAR");
		TaxonIdLookupTable.add("GEBR");
		TaxonIdLookupTable.add("GEBU");
		TaxonIdLookupTable.add("GEPI");
		TaxonIdLookupTable.add("GESP");
		TaxonIdLookupTable.add("GLSA");
		TaxonIdLookupTable.add("GLSP");
		TaxonIdLookupTable.add("GLVO");
		TaxonIdLookupTable.add("LECU");
		TaxonIdLookupTable.add("LESI");
		TaxonIdLookupTable.add("LESP");
		TaxonIdLookupTable.add("LETR");
		TaxonIdLookupTable.add("LEAL");
		TaxonIdLookupTable.add("LEAM");
		TaxonIdLookupTable.add("LECA");
		TaxonIdLookupTable.add("LPSP");
		TaxonIdLookupTable.add("LETO");
		TaxonIdLookupTable.add("MIME");
		TaxonIdLookupTable.add("MICA");
		TaxonIdLookupTable.add("MICC");
		TaxonIdLookupTable.add("MICH");
		TaxonIdLookupTable.add("MILO");
		TaxonIdLookupTable.add("MIMI");
		TaxonIdLookupTable.add("MIMG");
		TaxonIdLookupTable.add("MIMO");
		TaxonIdLookupTable.add("MIOC");
		TaxonIdLookupTable.add("MIOE");
		TaxonIdLookupTable.add("MIOR");
		TaxonIdLookupTable.add("MIPE");
		TaxonIdLookupTable.add("MIPI");
		TaxonIdLookupTable.add("MIRI");
		TaxonIdLookupTable.add("MISP");
		TaxonIdLookupTable.add("MITO");
		TaxonIdLookupTable.add("MIXA");
		TaxonIdLookupTable.add("MUMU");
		TaxonIdLookupTable.add("MUER");
		TaxonIdLookupTable.add("MUFR");
		TaxonIdLookupTable.add("MUNI");
		TaxonIdLookupTable.add("MUSP");
		TaxonIdLookupTable.add("MYGA");
		TaxonIdLookupTable.add("MYRU");
		TaxonIdLookupTable.add("MYSP");
		TaxonIdLookupTable.add("NAIN");
		TaxonIdLookupTable.add("NEAN");
		TaxonIdLookupTable.add("NEAL");
		TaxonIdLookupTable.add("NECI");
		TaxonIdLookupTable.add("NEFL");
		TaxonIdLookupTable.add("NEFU");
		TaxonIdLookupTable.add("NELE");
		TaxonIdLookupTable.add("NEMA");
		TaxonIdLookupTable.add("NEME");
		TaxonIdLookupTable.add("NEMI");
		TaxonIdLookupTable.add("NESP");
		TaxonIdLookupTable.add("NEGI");
		TaxonIdLookupTable.add("NOCR");
		TaxonIdLookupTable.add("OCNU");
		TaxonIdLookupTable.add("ONZI");
		TaxonIdLookupTable.add("ONAR");
		TaxonIdLookupTable.add("ONLE");
		TaxonIdLookupTable.add("ONSP");
		TaxonIdLookupTable.add("ONTO");
		TaxonIdLookupTable.add("ORPA");
		TaxonIdLookupTable.add("PABR");
		TaxonIdLookupTable.add("PEAM");
		TaxonIdLookupTable.add("PEFA");
		TaxonIdLookupTable.add("PEFV");
		TaxonIdLookupTable.add("PEFL");
		TaxonIdLookupTable.add("PEPA");
		TaxonIdLookupTable.add("PGSP");
		TaxonIdLookupTable.add("PEAT");
		TaxonIdLookupTable.add("PEBO");
		TaxonIdLookupTable.add("PECA");
		TaxonIdLookupTable.add("PECR");
		TaxonIdLookupTable.add("PEER");
		TaxonIdLookupTable.add("PEGO");
		TaxonIdLookupTable.add("PELE");
		TaxonIdLookupTable.add("PEMA");
		TaxonIdLookupTable.add("PEME");
		TaxonIdLookupTable.add("PENA");
		TaxonIdLookupTable.add("PEPO");
		TaxonIdLookupTable.add("PESP");
		TaxonIdLookupTable.add("PETR");
		TaxonIdLookupTable.add("PHIN");
		TaxonIdLookupTable.add("POFL");
		TaxonIdLookupTable.add("RAEX");
		TaxonIdLookupTable.add("RANO");
		TaxonIdLookupTable.add("RARA");
		TaxonIdLookupTable.add("RASP");
		TaxonIdLookupTable.add("REFU");
		TaxonIdLookupTable.add("REHU");
		TaxonIdLookupTable.add("REME");
		TaxonIdLookupTable.add("REMO");
		TaxonIdLookupTable.add("RESP");
		TaxonIdLookupTable.add("SCAQ");
		TaxonIdLookupTable.add("SCLA");
		TaxonIdLookupTable.add("SCOR");
		TaxonIdLookupTable.add("SNSP");
		TaxonIdLookupTable.add("SCTO");
		TaxonIdLookupTable.add("SCAB");
		TaxonIdLookupTable.add("SCCN");
		TaxonIdLookupTable.add("SCCA");
		TaxonIdLookupTable.add("SCGR");
		TaxonIdLookupTable.add("SCNI");
		TaxonIdLookupTable.add("SCSP");
		TaxonIdLookupTable.add("SIAR");
		TaxonIdLookupTable.add("SIFU");
		TaxonIdLookupTable.add("SIHI");
		TaxonIdLookupTable.add("SIOC");
		TaxonIdLookupTable.add("SISP");
		TaxonIdLookupTable.add("SOAR");
		TaxonIdLookupTable.add("SOAZ");
		TaxonIdLookupTable.add("SOBA");
		TaxonIdLookupTable.add("SOBE");
		TaxonIdLookupTable.add("SOCI");
		TaxonIdLookupTable.add("SODI");
		TaxonIdLookupTable.add("SOFU");
		TaxonIdLookupTable.add("SOHA");
		TaxonIdLookupTable.add("SOHO");
		TaxonIdLookupTable.add("SOLO");
		TaxonIdLookupTable.add("SOLY");
		TaxonIdLookupTable.add("SOME");
		TaxonIdLookupTable.add("SOMO");
		TaxonIdLookupTable.add("SONA");
		TaxonIdLookupTable.add("SOOR");
		TaxonIdLookupTable.add("SOPA");
		TaxonIdLookupTable.add("SOPR");
		TaxonIdLookupTable.add("SOSP");
		TaxonIdLookupTable.add("SOTR");
		TaxonIdLookupTable.add("SOTU");
		TaxonIdLookupTable.add("SOUG");
		TaxonIdLookupTable.add("SOVA");
		TaxonIdLookupTable.add("SOYU");
		TaxonIdLookupTable.add("SPAR");
		TaxonIdLookupTable.add("SPBE");
		TaxonIdLookupTable.add("SPBD");
		TaxonIdLookupTable.add("SPEL");
		TaxonIdLookupTable.add("SPFR");
		TaxonIdLookupTable.add("SPLA");
		TaxonIdLookupTable.add("SPMO");
		TaxonIdLookupTable.add("SPPA");
		TaxonIdLookupTable.add("SPRI");
		TaxonIdLookupTable.add("SPSA");
		TaxonIdLookupTable.add("SMSP");
		TaxonIdLookupTable.add("SPSP");
		TaxonIdLookupTable.add("SPTE");
		TaxonIdLookupTable.add("SPTR");
		TaxonIdLookupTable.add("SPVA");
		TaxonIdLookupTable.add("SYAQ");
		TaxonIdLookupTable.add("SYAU");
		TaxonIdLookupTable.add("SYBA");
		TaxonIdLookupTable.add("SYFL");
		TaxonIdLookupTable.add("SYNU");
		TaxonIdLookupTable.add("SYOB");
		TaxonIdLookupTable.add("SYPA");
		TaxonIdLookupTable.add("SLSP");
		TaxonIdLookupTable.add("SYTR");
		TaxonIdLookupTable.add("SYBO");
		TaxonIdLookupTable.add("SYCO");
		TaxonIdLookupTable.add("SYSP");
		TaxonIdLookupTable.add("TAAM");
		TaxonIdLookupTable.add("TACI");
		TaxonIdLookupTable.add("TADO");
		TaxonIdLookupTable.add("TAME");
		TaxonIdLookupTable.add("TAMI");
		TaxonIdLookupTable.add("TAQU");
		TaxonIdLookupTable.add("TARU");
		TaxonIdLookupTable.add("TMSP");
		TaxonIdLookupTable.add("TASP");
		TaxonIdLookupTable.add("TAST");
		TaxonIdLookupTable.add("TATO");
		TaxonIdLookupTable.add("TAUM");
		TaxonIdLookupTable.add("TSDO");
		TaxonIdLookupTable.add("TAHU");
		TaxonIdLookupTable.add("TSSP");
		TaxonIdLookupTable.add("THBO");
		TaxonIdLookupTable.add("THID");
		TaxonIdLookupTable.add("THSP");
		TaxonIdLookupTable.add("THTA");
		TaxonIdLookupTable.add("THUM");
		TaxonIdLookupTable.add("ZAHP");
		TaxonIdLookupTable.add("ZAHU");
		TaxonIdLookupTable.add("ZAPR");
		TaxonIdLookupTable.add("ZASP");
		TaxonIdLookupTable.add("ZATR");
				
		long qaQcValId = 1L;
		String plotID = "HARV_001";
//		String trapCoordinate = "A1";
		long typeId = 100L;
		Double valId = 150D;
		long typeId1 = 110L;
		Double valId1 = 130D;
		long valTypeId = 230L;  //Taxon ID
		long valTypeId1 = 240L; //Identification Qualifier
		
		DPMSMethStreamData qaQcMethStream = new DPMSMethStreamData();

		Calendar cal = Calendar.getInstance();
		cal.set(2014, Calendar.MAY, 21); //Year, month and day of month
		Date startTime = cal.getTime();
		cal.set(2014, Calendar.APRIL, 1);
		Date startTime2 = cal.getTime();
		

		DPMSMethActivity inputActy = new DPMSMethActivity();
			
		//Create entries for inputActy
		DPMSMethStreamData msA = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msA);
		DPMSMStreamReadout rdotA1 = new DPMSMStreamReadout();
		msA.getMSReadouts().add(rdotA1);
		rdotA1.setReadoutStartTime(startTime);
		//rdotA1.setValueForValueId(fieldList.get(0), valId, true);
		rdotA1.setValueStringForValueId(valTypeId, "ZAHU", true);
		DPMSMStreamReadout rdotA2 = new DPMSMStreamReadout();
		msA.getMSReadouts().add(rdotA2);
		rdotA2.setReadoutStartTime(startTime);
		//rdotA2.setValueForValueId(fieldList.get(0), valId1, true);
		rdotA2.setValueStringForValueId(valTypeId, "A1", true);
		
		//Create entries for inputActy
		DPMSMethStreamData msB = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msB);
		DPMSMStreamReadout rdotB1 = new DPMSMStreamReadout();
		msB.getMSReadouts().add(rdotB1);
		rdotB1.setReadoutStartTime(startTime);
		//rdotB1.setValueForValueId(fieldList.get(1), valId, true);
		rdotB1.setValueStringForValueId(valTypeId1, "CS", true);
		DPMSMStreamReadout rdotB2 = new DPMSMStreamReadout();
		msB.getMSReadouts().add(rdotB2);
		rdotB2.setReadoutStartTime(startTime);
		//rdotB2.setValueForValueId(fieldList.get(4), valId1, true);
		rdotB2.setValueStringForValueId(valTypeId1, "J5", true);
		
		//Create entries for inputActy
		DPMSMethStreamData msC = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msC);
		DPMSMStreamReadout rdotC1 = new DPMSMStreamReadout();
		msC.getMSReadouts().add(rdotC1);
		rdotC1.setReadoutStartTime(startTime);
		//rdotC1.setValueForValueId(fieldList.get(3), valId, true);
		rdotC1.setValueStringForValueId(valTypeId, "HARV_001", true);
		DPMSMStreamReadout rdotC2 = new DPMSMStreamReadout();
		msC.getMSReadouts().add(rdotC2);
		rdotC2.setReadoutStartTime(startTime);
		//rdotC2.setValueForValueId(fieldList.get(3), valId1, true);
		rdotC2.setValueStringForValueId(valTypeId1, "C6", true);
		
		//Create entries for inputActy
		DPMSMethStreamData msD = new DPMSMethStreamData();
		inputActy.getMethStreams().add(msD);
		DPMSMStreamReadout rdotD1 = new DPMSMStreamReadout();
		msD.getMSReadouts().add(rdotD1);
		rdotD1.setReadoutStartTime(startTime);
		//rdotD1.setValueForValueId(valTypeId1, valId1, true);
		rdotD1.setValueStringForValueId(fieldList.get(2),"CS" , true);
		DPMSMStreamReadout rdotD2 = new DPMSMStreamReadout();
		msD.getMSReadouts().add(rdotD2);
		rdotD2.setReadoutStartTime(startTime);
		//rdotD2.setValueForValueId(fieldList.get(2), valId1, true);
		rdotD2.setValueStringForValueId(valTypeId1,"G7" , true);

//		//Create entries for inputActy
//		DPMSMethStreamData msE = new DPMSMethStreamData();
//		inputActy.getMethStreams().add(msE);
//		DPMSMStreamReadout rdotE1 = new DPMSMStreamReadout();
//		msE.getMSReadouts().add(rdotE1);
//		rdotE1.setReadoutStartTime(startTime);
//		rdotE1.setValueForValueId(valTypeId, valId, true);
//		rdotE1.setValueStringForValueId(fieldList.get(0), "ZATR", true);
//		DPMSMStreamReadout rdotE2 = new DPMSMStreamReadout();
//		msE.getMSReadouts().add(rdotE2);
//		rdotE2.setReadoutStartTime(startTime);
//		rdotE2.setValueForValueId(fieldList.get(0), valId1, true);
//		rdotE2.setValueStringForValueId(valTypeId1, "F4", true);
		

		IdQualFieldsQAQC qaQc = new IdQualFieldsQAQC(fieldList, inputActy, TaxonIdLookupTable, valTypeId, fieldList.get(0), qaQcMethStream, qaQcValId);
		qaQc.runAlgorithm();
		qaQc = new IdQualFieldsQAQC(fieldList, inputActy, IdentificationQualifierLookupTable, valTypeId1, fieldList.get(1), qaQcMethStream, qaQcValId);
		qaQc.runAlgorithm();		
	}

}
