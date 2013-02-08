package de.vksi.c4j.doclet.writer;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.tools.doclets.formats.html.MethodWriterImpl;
import com.sun.tools.doclets.formats.html.SubWriterHolderWriter;

import de.vksi.c4j.doclet.analyzer.ConditionExtractor;
import de.vksi.c4j.doclet.analyzer.ConditionWriter;
import de.vksi.c4j.doclet.util.TargetContractMap;

/**
 * Writes method documentation in HTML format.
 * (Writes contract information as appropriated) 
 * @author Robert Field
 * @author Atul M Dambalkar
 * @author Jamie Ho (rewrite)
 * @author fmeyerer
 */
public class CustomMethodWriterImpl extends MethodWriterImpl {
	private TargetContractMap targetContractMap;

	public CustomMethodWriterImpl(SubWriterHolderWriter writer, ClassDoc classDoc,
			TargetContractMap targetContractMap) {
		super(writer, classDoc);
		this.targetContractMap = targetContractMap;
	}

	@Override
	public void writeSignature(MethodDoc method) {
		super.writeSignature(method);
	}

	@Override
	public void writeTags(MethodDoc method) {
		if(!targetContractMap.isContractClass(classdoc)){
			ConditionExtractor conditionExtractor = new ConditionExtractor(classdoc, targetContractMap, this.writer.configuration());
			conditionExtractor.extractConditionsOf(method);
			
			ConditionWriter condWriter = new ConditionWriter(writer);
			condWriter.writeConditionsOf(conditionExtractor);
		}
		super.writeTags(method);
		
	}
}
