package org.neoninc.dpms.algorithms.fsu.obsolete;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import java.util.ArrayList;

import org.apache.log4j.BasicConfigurator;
//import org.junit.Before;
//import org.junit.Test;
import org.testng.annotations.*;

public class taxonIDDomainStatusQF_Test {


	@BeforeMethod
	@BeforeClass
	public void setUp() throws Exception {
		
		BasicConfigurator.configure();
	}
	
	@Test
	public void runTest() {
		
		Integer nSpecies = 215;
		
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
				
		taxonIDDomainStatusQF qaQc = new taxonIDDomainStatusQF();
		
		Double val = Math.random()*nSpecies;
		Integer spec = (int) val.doubleValue();
		System.out.println("Species = "+TaxonIdLookupTable.get(spec));
		for(int dom=0;dom<qaQc.getnDomains();dom++){
				System.out.print(qaQc.getIndividualDomainNativeStatusCode(spec, dom)+", ");
		}
		System.out.println();
		Integer[] domains = new Integer[nSpecies];
		domains = qaQc.getDomainNativeStatusCodes(spec);
		for(int dom=0;dom<qaQc.getnDomains();dom++){
			System.out.print(domains[dom]+", ");
		}
	}

}
