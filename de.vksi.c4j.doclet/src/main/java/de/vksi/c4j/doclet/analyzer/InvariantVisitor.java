package de.vksi.c4j.doclet.analyzer;

import static de.vksi.c4j.doclet.util.C4JDocletConstants.ANNOTATION_CLASS_INVARIANT;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

import de.vksi.c4j.doclet.util.C4JConditions;

/**
 * @author fmeyerer
 *
 */
public class InvariantVisitor extends VoidVisitorAdapter<Object> {

	private C4JConditions conditions;

	public InvariantVisitor() {
		super();
		conditions = new C4JConditions();
	}

	public void visit(CompilationUnit cu) {
		visit(cu, null);
	}
	
	@Override
	public void visit(MethodDeclaration method, Object obj) {
		
		if (isInvariant(method)) {
			AssertStatementVisitor assertionVisitor = new AssertStatementVisitor();
			assertionVisitor.visit(method);
			conditions.setInvariantConditions(assertionVisitor.getConditions());
		}
	}

	private boolean isInvariant(MethodDeclaration method) {
		List<AnnotationExpr> annotations = method.getAnnotations();

		String classInvariantAnnotaion = getSimpleName(ANNOTATION_CLASS_INVARIANT);

		for (AnnotationExpr annotation : annotations) {
			if (classInvariantAnnotaion.equals(annotation.getName().toString()))
				return true;
		}

		return false;
	}

	private String getSimpleName(String qualifiedAnnotationName) {
		int lastIndexOfDot = qualifiedAnnotationName.lastIndexOf(".");
		if (lastIndexOfDot >= 0)
			return qualifiedAnnotationName.substring(lastIndexOfDot + 1, qualifiedAnnotationName.length());

		return qualifiedAnnotationName;
	}

	public C4JConditions getConditions() {
		return conditions;
	}
}
