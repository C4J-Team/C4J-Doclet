package de.vksi.c4j.doclet.writer;

import java.io.IOException;
import java.util.List;

import com.sun.javadoc.ClassDoc;
import com.sun.tools.doclets.formats.html.AllClassesFrameWriter;
import com.sun.tools.doclets.formats.html.ConfigurationImpl;
import com.sun.tools.doclets.internal.toolkit.util.DocletAbortException;
import com.sun.tools.doclets.internal.toolkit.util.IndexBuilder;

import de.vksi.c4j.doclet.util.C4JContractFilter;
import de.vksi.c4j.doclet.util.TargetContractMap;

/**
 * Generate the file with list of all the classes in this run. This page will be
 * used in the left-hand bottom frame, when "All Classes" link is clicked in
 * the left-hand top frame. The name of the generated file is
 * "allclasses-frame.html".
 *
 * @author Atul M Dambalkar
 * @author Doug Kramer
 * @author modified by fmeyerer
 */
public class CustomAllClassesFrameWriter extends AllClassesFrameWriter {
	private static final String DOCLET_EXCEPTION_ENCOUNTERED = "doclet.exception_encountered";
	private TargetContractMap targetContractMap;
	
	public CustomAllClassesFrameWriter(ConfigurationImpl configuration, String filename,
			IndexBuilder indexbuilder) throws IOException {
		this(configuration, filename, indexbuilder, null);
	}

	public CustomAllClassesFrameWriter(ConfigurationImpl configuration, String filename,
			IndexBuilder indexbuilder, TargetContractMap targetContractMap) throws IOException {
		super(configuration, filename, indexbuilder);
		this.targetContractMap = (targetContractMap != null) ? targetContractMap : new TargetContractMap();
	}

	/**
	 * Create CustomAllClassesFrameWriter object. Then use it to generate the
	 * "allclasses-frame.html" file. Generate the file in the current or the
	 * destination directory.
	 * 
	 * @param indexbuilder
	 *            IndexBuilder object for all classes index.
	 * @throws DocletAbortException
	 */
	public static void generate(ConfigurationImpl configuration, IndexBuilder indexbuilder,
			TargetContractMap targetContractMap) {
		CustomAllClassesFrameWriter allclassgen;
		String filename = OUTPUT_FILE_NAME_FRAMES;
		try {
			allclassgen = new CustomAllClassesFrameWriter(configuration, filename, indexbuilder, targetContractMap);
			allclassgen.generateAllClassesFile(true);
			allclassgen.close();
			filename = OUTPUT_FILE_NAME_NOFRAMES;
			allclassgen = new CustomAllClassesFrameWriter(configuration, filename, indexbuilder, targetContractMap);
			allclassgen.generateAllClassesFile(false);
			allclassgen.close();
		} catch (IOException exc) {
			configuration.standardmessage.error(DOCLET_EXCEPTION_ENCOUNTERED, exc.toString(), filename);
			throw new DocletAbortException();
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void generateContents(List classlist, boolean wantFrames) {

		ClassDoc[] classes = (ClassDoc[]) classlist.toArray(new ClassDoc[classlist.size()]);
		List<ClassDoc> filteredClasses = C4JContractFilter.removeContractClasses(classes, targetContractMap);
		
		super.generateContents(filteredClasses, wantFrames);
	}
}
