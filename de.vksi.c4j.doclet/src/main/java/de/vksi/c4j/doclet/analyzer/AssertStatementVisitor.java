package de.vksi.c4j.doclet.analyzer;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.stmt.AssertStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Visits Assertion-statements.
 * 
 * @author fmeyerer
 */
public class AssertStatementVisitor extends VoidVisitorAdapter<List<String>> {

	private List<String> conditions;
	private static final String TARGET = "target."; //TODO: better solution: search for @Target-Ref and get the value name
	
	public AssertStatementVisitor() {
		super();
		conditions = new ArrayList<String>();
	}
	
	public void visit(MethodDeclaration method) {
		visit(method, null);
	}
	
	public void visit(AssertStmt assertStmt, List<String> arg) {
		String check = assertStmt.getCheck().toString();
		check = check.replace(TARGET, "");
		conditions.add(check + " : " + assertStmt.getMessage());
	}

	public List<String> getConditions() {
		return conditions;
	}
}
