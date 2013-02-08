package de.vksi.c4j.doclet.writer;

import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.tools.doclets.formats.html.ConstructorWriterImpl;
import com.sun.tools.doclets.formats.html.SubWriterHolderWriter;

import de.vksi.c4j.doclet.analyzer.ConditionExtractor;
import de.vksi.c4j.doclet.analyzer.ConditionWriter;
import de.vksi.c4j.doclet.util.TargetContractMap;

/**
 * Writes constructor documentation (with contract information as appropriated).
 *
 * @author Robert Field
 * @author Atul M Dambalkar
 * @author fmeyerer
 */
public class CustomConstructorWriterImpl extends ConstructorWriterImpl{
	private TargetContractMap targetContractMap;
	
	public CustomConstructorWriterImpl(SubWriterHolderWriter writer, ClassDoc classDoc,
			TargetContractMap targetContractMap) {
		super(writer, classDoc);
		this.targetContractMap = targetContractMap;
	}
	
	@Override
	public void writeTags(ConstructorDoc constructor) {
		if(!targetContractMap.isContractClass(classdoc)){
			ConditionExtractor conditionExtractor = new ConditionExtractor(classdoc, targetContractMap, this.writer.configuration());
			conditionExtractor.extractConditionsOf(constructor);
			
			ConditionWriter condWriter = new ConditionWriter(writer);
			condWriter.writeConditionsOf(conditionExtractor);
		}
		super.writeTags(constructor);
	}
}
