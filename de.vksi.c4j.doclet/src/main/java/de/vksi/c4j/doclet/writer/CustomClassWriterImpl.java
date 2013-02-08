package de.vksi.c4j.doclet.writer;

import com.sun.javadoc.ClassDoc;
import com.sun.tools.doclets.formats.html.ClassWriterImpl;
import com.sun.tools.doclets.internal.toolkit.util.ClassTree;

/**
 * Generate the Class Information Page.
 *
 * @author Atul M Dambalkar
 * @author Robert Field
 * @author modified by fmeyerer
 */
public class CustomClassWriterImpl extends ClassWriterImpl {

	public CustomClassWriterImpl(ClassDoc classDoc, ClassDoc prevClass, ClassDoc nextClass, ClassTree classTree)
			throws Exception {
		super(classDoc, prevClass, nextClass, classTree);
	}
	
	@Override
	public void writeClassSignature(String modifiers) {
		super.writeClassSignature(modifiers);
	}
}
