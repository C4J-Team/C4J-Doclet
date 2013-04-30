package de.vksi.c4j.doclet;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.sun.javadoc.RootDoc;

import de.vksi.c4j.doclet.util.RootDocFactory;


public class C4JDocletTest {

	private static final String PACKAGE1 = "externalContract";
	private static final String PACKAGE2 = "internalContract";
	private static final String PACKAGE3 = "package4contracts";
	private static final String PACKAGE4 = "package4classes";
	private static final String PACKAGE5 = "inheritanceWithClasses";
	private static final String PACKAGE6 = "inheritanceWithInterfaces";
	//make sure that the test resources can be found cross platform
	private static final String PATH_TO_TARGETS = "test" + File.separator + "resources" + File.separator
			+ "main" + File.separator + "java"; 
	private static final String PATH_TO_CONTRACTS = "test" + File.separator + "resources" + File.separator
			+ "contract" + File.separator + "java";
	private static final String OUTPUT_DESTINATION = "test" + File.separator + "output";
//	private static final String OUTPUT_DESTINATION = "C:" + File.separator + "Temp";
	
	private File[] sourcePath;
	private RootDoc rootDoc;
	
	@Before
	public void setUp() {
		this.sourcePath = new File[] { new File(PATH_TO_TARGETS), new File(PATH_TO_CONTRACTS) };
		rootDoc = RootDocFactory.createRootDoc(OUTPUT_DESTINATION, this.sourcePath, PACKAGE1, PACKAGE2, PACKAGE3, PACKAGE4, PACKAGE5, PACKAGE6);
	}

	@Test
	public void testStart() {
		C4JDoclet.start(this.rootDoc);
	}
}
