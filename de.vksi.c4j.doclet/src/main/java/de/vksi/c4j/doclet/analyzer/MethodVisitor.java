package de.vksi.c4j.doclet.analyzer;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

import com.sun.javadoc.MethodDoc;

import de.vksi.c4j.doclet.util.C4JConditions;

/**
 * Visits the methods of a given class
 * 
 * @author fmeyerer
 * 
 */
public class MethodVisitor extends VoidVisitorAdapter<Object> {

	private static final int METHOD_PARAMETER = 1;
	private static final int METHOD_NAME = 0;
	private C4JConditions conditions;
	private MethodDoc targetMethod;

	public MethodVisitor(MethodDoc targetMethod) {
		super();
		this.targetMethod = targetMethod;
		conditions = new C4JConditions();
	}

	@Override
	public void visit(MethodDeclaration currentMethod, Object obj) {
		if (matchesTargetMethod(currentMethod)) {
			IfStatementVisitor ifVisitor = new IfStatementVisitor();
			ifVisitor.visit(currentMethod, null);
			conditions = ifVisitor.getConditions();
		}
	}

	private boolean matchesTargetMethod(MethodDeclaration currentMethod) {
		if (targetMethod != null) {
			String[] givenMethodSignatur = getMethodSignatur(targetMethod);

			return currentMethod.getName().equals(
					givenMethodSignatur[METHOD_NAME])
					&& parametersToString(currentMethod).equals(
							givenMethodSignatur[METHOD_PARAMETER]);
		}
		return false;
	}

	private String parametersToString(MethodDeclaration method) {
		List<Parameter> parameters = method.getParameters();
		List<String> paramList = new ArrayList<String>();
		if (parameters != null) {
			for (Parameter parameter : parameters) {
				paramList.add(parameter.getType().toString());
			}
		}

		String paramString = paramList.toString();
		paramString = paramString.replace("[", "");
		paramString = paramString.replace("]", "");

		return paramString;
	}

	// TODO: write unit tests to check differend kinds of signatures
	private String[] getMethodSignatur(MethodDoc method) {
		if (method != null) {
			String parameter = method.flatSignature();
			parameter = parameter.replace("(", "");
			parameter = parameter.replace(")", "");
			return new String[] { method.name(), parameter };
		}
		return new String[] { "", "" };
	}

	public C4JConditions getConditions() {
		return conditions;
	}

}
