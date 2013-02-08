package de.vksi.c4j.doclet.analyzer;

import static de.vksi.c4j.doclet.util.C4JDocletConstants.POST_CONDITION;
import static de.vksi.c4j.doclet.util.C4JDocletConstants.PRE_CONDITION;
import japa.parser.ast.stmt.IfStmt;
import japa.parser.ast.visitor.VoidVisitorAdapter;
import de.vksi.c4j.doclet.util.C4JConditions;

/**
 * Visits the if-statements of a given element
 * 
 * @author fmeyerer
 *
 */
public class IfStatementVisitor extends VoidVisitorAdapter<Object> {
	private C4JConditions conditions;
	
	public IfStatementVisitor() {
		super();
		conditions =  new C4JConditions();
	}

	@Override
	public void visit(IfStmt statement, Object obj) {
		AssertStatementVisitor assertionVisitor = new AssertStatementVisitor();
		statement.accept(assertionVisitor, null);
		
		if(statement.getCondition().toString().equals(PRE_CONDITION)){
			conditions.setPreConditions(assertionVisitor.getConditions());
		}else if(statement.getCondition().toString().equals(POST_CONDITION)){
			conditions.setPostConditions(assertionVisitor.getConditions());
		}
	}

	public C4JConditions getConditions() {
		return conditions;
	}
}
