package de.vksi.c4j.doclet.analyzer;

import java.util.List;

import com.sun.tools.doclets.formats.html.SubWriterHolderWriter;

/**
 * Writes conditions in HTML format
 * 
 * @author fmeyerer
 * 
 */
public class ConditionWriter {
	private SubWriterHolderWriter writer;

	public ConditionWriter(SubWriterHolderWriter writer) {
		this.writer = writer;
	}

	public void writeConditionsOf(ConditionExtractor conditionExtractor) {
		printConditions(conditionExtractor);
	}
	
	private void printConditions(ConditionExtractor ce) {
		if (!ce.getInvariantConditions().isEmpty()) {
			printConditions(ce.getInvariantConditions());
		}
		if (!ce.getPreConditions().isEmpty()) {
			printConditionHeader("Precondition");
			printConditions(ce.getPreConditions());
			writer.br();
			printConditionFooter();
		}
		if (!ce.getPostConditions().isEmpty()) {
			printConditionHeader("Postcondition");
			printConditions(ce.getPostConditions());
			writer.br();
			printConditionFooter();
		}
	}

	private void printConditionHeader(String kindOfCondition) {
		writer.dd();
		writer.dl();
		writer.dt();
		if (!kindOfCondition.isEmpty()) {
			writer.bold(kindOfCondition);
		}
		writer.dd();
	}

	private void printConditions(List<String> conditions) {
		writer.code();
		for (String string : conditions) {
			writer.write(string);
			writer.br();
		}
		writer.codeEnd();
	}

	private void printConditionFooter() {
		writer.ddEnd();
		writer.dlEnd();
	}
}
