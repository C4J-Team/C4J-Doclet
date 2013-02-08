package de.vksi.c4j.doclet;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.sun.javadoc.RootDoc;

import de.vksi.c4j.doclet.util.EasyDoclet;


public class C4JDocletTest {

	private static final String PACKAGE1 = "externalContract";
	private static final String PACKAGE2 = "internalContract";
	private static final String PACKAGE3 = "package4contracts";
	private static final String PACKAGE4 = "package4classes";
	private static final String PACKAGE5 = "inheritance";
	//make sure that the test resources can be found cross platform
	private static final String PATH_TO_TARGETS = "test" + File.separator + "resources" + File.separator
			+ "main" + File.separator + "java"; 
	private static final String PATH_TO_CONTRACTS = "test" + File.separator + "resources" + File.separator
			+ "contract" + File.separator + "java";
	
	private File[] sourcePath;
	private RootDoc doc;

	@Before
	public void setUp() {
		this.sourcePath = new File[] { new File(PATH_TO_TARGETS), new File(PATH_TO_CONTRACTS) };
//		EasyDoclet doclet = new EasyDoclet(this.sourcePath, PACKAGE1, PACKAGE2, PACKAGE3, PACKAGE4, PACKAGE5);
		EasyDoclet doclet = new EasyDoclet(this.sourcePath, PACKAGE1);
		doc = doclet.getRootDoc();
		
		System.out.println();
	}

	@Test
	public void testStart() {
		System.out.println();
		C4JDoclet.start(this.doc);
		System.out.println();
	}
}
