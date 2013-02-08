package de.vksi.c4j.doclet.analyzer;

import japa.parser.ast.body.ConstructorDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

import com.sun.javadoc.ConstructorDoc;

import de.vksi.c4j.doclet.util.C4JConditions;

/**
 * Visits the constructors of a given class
 * 
 * @author fmeyerer
 *
 */
public class ConstructorVisitor extends VoidVisitorAdapter<Object> {

	private static final int CONSTRUCTOR_PARAMETER = 1;
	private C4JConditions conditions;
	
	public ConstructorVisitor() {
		super();
		conditions = new C4JConditions();
	}

	@Override
	public void visit(ConstructorDeclaration visitedConstructor, Object constructor) {
		if (!(constructor instanceof ConstructorDoc))
			return;

		ConstructorDoc givenContructor = (ConstructorDoc) constructor;

		String[] givenConstructorSignatur = getConstructorSignatur(givenContructor);
		
		if (parametersToString(visitedConstructor).equals(givenConstructorSignatur[CONSTRUCTOR_PARAMETER])) {
			IfStatementVisitor ifVisitor = new IfStatementVisitor();
			ifVisitor.visit(visitedConstructor, null);
			conditions = ifVisitor.getConditions();
		}
	}

	private String parametersToString(ConstructorDeclaration constructor) {
		List<Parameter> parameters = constructor.getParameters();
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

	public C4JConditions getConditions() {
		return conditions;
	}
	
	// TODO: write unit tests to check differend kinds of signatures
	private String[] getConstructorSignatur(ConstructorDoc method) {
		if (method != null) {
			String parameter = method.flatSignature();
			parameter = parameter.replace("(", "");
			parameter = parameter.replace(")", "");
			return new String[] { method.name(), parameter };
		}
		return new String[] {"", ""};
	}
}