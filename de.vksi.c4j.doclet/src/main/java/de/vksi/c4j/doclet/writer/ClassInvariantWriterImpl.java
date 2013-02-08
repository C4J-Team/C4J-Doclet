package de.vksi.c4j.doclet.writer;

import com.sun.javadoc.ClassDoc;
import com.sun.tools.doclets.formats.html.MethodWriterImpl;
import com.sun.tools.doclets.formats.html.SubWriterHolderWriter;

import de.vksi.c4j.doclet.analyzer.ConditionExtractor;
import de.vksi.c4j.doclet.analyzer.ConditionWriter;

/**
 * Writes class-invariant documentation in HTML format.
 * 
 * @author fmeyerer
 *
 */
public class ClassInvariantWriterImpl extends MethodWriterImpl {
	private ConditionWriter condWriter;
	private ConditionExtractor conditionExtractor;
	
	public ClassInvariantWriterImpl(SubWriterHolderWriter writer, ClassDoc classDoc, ConditionExtractor conditionExtractor) {
		super(writer, classDoc);

		this.conditionExtractor = conditionExtractor;
		condWriter = new ConditionWriter(writer);
	}
	
	public void writeMemberSummary() {
			writer.trBgcolorStyle("white", "TableRowColor");
			writer.printTypeSummaryHeader();
			writer.space();
			writer.table(0, 0, 0);
			writer.trAlignVAlign("right", "");
			writer.tdNowrap();
			writer.font("-1");
			writer.code();
			writer.print("Invariants");
			writer.codeEnd();
			writer.fontEnd();
			writer.tdEnd();
			writer.trEnd();
			writer.tableEnd();
			writer.printTypeSummaryFooter();
			writer.summaryRow(0);
			writer.code();
			writeInvariants();
			writer.codeEnd();
			writer.println();
			writer.br();
			writer.printNbsps();
			writer.summaryRowEnd();
			writer.trEnd();
	}

    /**
     * Write the class-invariant summary header for the given class.
     *
     * @param classDoc the class the summary belongs to.
     */
	public void writeMemberSummaryHeader(ClassDoc classDoc) {
		writer.println();
		writer.println("<!-- ========== CLASS INVARIANT SUMMARY =========== -->");
		writer.println();
		writer.printSummaryHeader(this, classDoc);
	}

	/**
	 * Write the class-invariant summary footer for the given class.
    *
     * @param classDoc the class the summary belongs to.
     */
	public void writeMemberSummaryFooter(ClassDoc classDoc) {
		writer.printSummaryFooter(this, classDoc);
	}

	/**
	 * Write class-invariant summary lable
	 */
	public void printSummaryLabel(ClassDoc cd) {
		writer.bold("Class Invariants Summary");
	}

	private void writeInvariants() {
		condWriter.writeConditionsOf(conditionExtractor);
	}
}
