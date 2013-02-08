package de.vksi.c4j.doclet.builder;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.Tag;
import com.sun.tools.doclets.formats.html.SubWriterHolderWriter;
import com.sun.tools.doclets.internal.toolkit.AnnotationTypeWriter;
import com.sun.tools.doclets.internal.toolkit.ClassWriter;
import com.sun.tools.doclets.internal.toolkit.Configuration;
import com.sun.tools.doclets.internal.toolkit.MemberSummaryWriter;
import com.sun.tools.doclets.internal.toolkit.builders.AbstractMemberBuilder;
import com.sun.tools.doclets.internal.toolkit.util.DocFinder;
import com.sun.tools.doclets.internal.toolkit.util.Util;
import com.sun.tools.doclets.internal.toolkit.util.VisibleMemberMap;

import de.vksi.c4j.doclet.analyzer.ConditionExtractor;
import de.vksi.c4j.doclet.util.TargetContractMap;
import de.vksi.c4j.doclet.writer.ClassInvariantWriterImpl;

/**
 * Builds the member summary.
 *
 * This code is not part of an API.
 * It is implementation that is subject to change.
 * Do not use it as an API
 *
 * @author Jamie Ho, modified by fmeyerer
 * @since 1.5
 */
public class CustomMemberSummaryBuilder extends AbstractMemberBuilder {

	private TargetContractMap targetContractMap;
	/**
	 * The XML root for this builder.
	 */
	public static final String NAME = "MemberSummary";

	/**
	 * The visible members for the given class.
	 */
	private VisibleMemberMap[] visibleMemberMaps;

	/**
	 * The member summary writers for the given class.
	 */
	private MemberSummaryWriter[] memberSummaryWriters;

	/**
	 * The type being documented.
	 */
	private ClassDoc classDoc;
	private ClassWriter classWriter;

	private CustomMemberSummaryBuilder(Configuration configuration) {
		super(configuration);
	}

	/**
	 * Construct a new MemberSummaryBuilder.
	 * 
	 * @param classWriter
	 *            the writer for the class whose members are being summarized.
	 * @param configuration
	 *            the current configuration of the doclet.
	 * @param targetContractMap
	 *            the map contains target-contract pairs, where the targets are
	 *            keys and the contracts are values.
	 */
	public static CustomMemberSummaryBuilder getInstance(ClassWriter classWriter,
			Configuration configuration, TargetContractMap targetContractMap) throws Exception {
		CustomMemberSummaryBuilder builder = new CustomMemberSummaryBuilder(configuration);
		builder.classDoc = classWriter.getClassDoc();
		builder.classWriter = classWriter;
		builder.targetContractMap = targetContractMap;
		builder.init(classWriter);
		return builder;
	}

	/**
	 * Construct a new MemberSummaryBuilder.
	 * 
	 * @param annotationTypeWriter
	 *            the writer for the class whose members are being summarized.
	 * @param configuration
	 *            the current configuration of the doclet.
	 */
	public static CustomMemberSummaryBuilder getInstance(AnnotationTypeWriter annotationTypeWriter,
			Configuration configuration) throws Exception {
		CustomMemberSummaryBuilder builder = new CustomMemberSummaryBuilder(configuration);
		builder.classDoc = annotationTypeWriter.getAnnotationTypeDoc();
		builder.init(annotationTypeWriter);
		return builder;
	}

	private void init(Object writer) throws Exception {
		visibleMemberMaps = new VisibleMemberMap[VisibleMemberMap.NUM_MEMBER_TYPES];
		for (int i = 0; i < VisibleMemberMap.NUM_MEMBER_TYPES; i++) {
			visibleMemberMaps[i] = new VisibleMemberMap(classDoc, i, configuration.nodeprecated);
		}
		memberSummaryWriters = new MemberSummaryWriter[VisibleMemberMap.NUM_MEMBER_TYPES];
		for (int i = 0; i < VisibleMemberMap.NUM_MEMBER_TYPES; i++) {
			if (classDoc.isAnnotationType()) {
				memberSummaryWriters[i] = visibleMemberMaps[i].noVisibleMembers() ? null : configuration
						.getWriterFactory().getMemberSummaryWriter((AnnotationTypeWriter) writer, i);
			} else {
				memberSummaryWriters[i] = visibleMemberMaps[i].noVisibleMembers() ? null : configuration
						.getWriterFactory().getMemberSummaryWriter((ClassWriter) writer, i);
			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public String getName() {
		return NAME;
	}

	/**
	 * Return the specified visible member map.
	 * 
	 * @param type
	 *            the type of visible member map to return.
	 * @return the specified visible member map.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the type is invalid.
	 * @see VisibleMemberMap
	 */
	public VisibleMemberMap getVisibleMemberMap(int type) {
		return visibleMemberMaps[type];
	}

	/**
	 * Return the specified member summary writer.
	 * 
	 * @param type
	 *            the type of member summary writer to return.
	 * @return the specified member summary writer.
	 * @throws ArrayIndexOutOfBoundsException
	 *             when the type is invalid.
	 * @see VisibleMemberMap
	 */
	public MemberSummaryWriter getMemberSummaryWriter(int type) {
		return memberSummaryWriters[type];
	}

	/**
	 * Returns a list of methods that will be documented for the given class.
	 * This information can be used for doclet specific documentation
	 * generation.
	 * 
	 * @param classDoc
	 *            the {@link ClassDoc} we want to check.
	 * @param type
	 *            the type of members to return.
	 * @return a list of methods that will be documented.
	 * @see VisibleMemberMap
	 */
	@SuppressWarnings("rawtypes")
	public List members(int type) {
		return visibleMemberMaps[type].getLeafClassMembers(configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("rawtypes")
	public void invokeMethod(String methodName, Class[] paramClasses, Object[] params) throws Exception {
		if (DEBUG) {
			configuration.root.printError("DEBUG: " + this.getClass().getName() + "." + methodName);
		}
		Method method = this.getClass().getMethod(methodName, paramClasses);
		method.invoke(this, params);
	}

	/**
	 * Return true it there are any members to summarize.
	 * 
	 * @return true if there are any members to summarize.
	 */
	public boolean hasMembersToDocument() {
		if (classDoc instanceof AnnotationTypeDoc) {
			return ((AnnotationTypeDoc) classDoc).elements().length > 0;
		}
		for (int i = 0; i < VisibleMemberMap.NUM_MEMBER_TYPES; i++) {
			VisibleMemberMap members = visibleMemberMaps[i];
			if (!members.noVisibleMembers()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Build the summary for the enum constants.
	 */
	public void buildEnumConstantsSummary() {
		buildSummary(memberSummaryWriters[VisibleMemberMap.ENUM_CONSTANTS],
				visibleMemberMaps[VisibleMemberMap.ENUM_CONSTANTS]);
	}

	/**
	 * Build the summary for the optional members.
	 */
	public void buildAnnotationTypeOptionalMemberSummary() {
		buildSummary(memberSummaryWriters[VisibleMemberMap.ANNOTATION_TYPE_MEMBER_OPTIONAL],
				visibleMemberMaps[VisibleMemberMap.ANNOTATION_TYPE_MEMBER_OPTIONAL]);
	}

	/**
	 * Build the summary for the optional members.
	 */
	public void buildAnnotationTypeRequiredMemberSummary() {
		buildSummary(memberSummaryWriters[VisibleMemberMap.ANNOTATION_TYPE_MEMBER_REQUIRED],
				visibleMemberMaps[VisibleMemberMap.ANNOTATION_TYPE_MEMBER_REQUIRED]);
	}

	/**
	 * Build the summary for the fields.
	 */
	public void buildFieldsSummary() {
		buildSummary(memberSummaryWriters[VisibleMemberMap.FIELDS],
				visibleMemberMaps[VisibleMemberMap.FIELDS]);
	}

	/**
	 * Build the inherited summary for the fields.
	 */
	public void buildFieldsInheritedSummary() {
		buildInheritedSummary(memberSummaryWriters[VisibleMemberMap.FIELDS],
				visibleMemberMaps[VisibleMemberMap.FIELDS]);
	}

	/**
	 * Build the summary for the nested classes.
	 */
	public void buildNestedClassesSummary() {
		buildSummary(memberSummaryWriters[VisibleMemberMap.INNERCLASSES],
				visibleMemberMaps[VisibleMemberMap.INNERCLASSES]);
	}

	/**
	 * Build the inherited summary for the nested classes.
	 */
	public void buildNestedClassesInheritedSummary() {
		buildInheritedSummary(memberSummaryWriters[VisibleMemberMap.INNERCLASSES],
				visibleMemberMaps[VisibleMemberMap.INNERCLASSES]);
	}

	/**
	 * Build the method summary.
	 */
	public void buildMethodsSummary() {
		buildSummary(memberSummaryWriters[VisibleMemberMap.METHODS],
				visibleMemberMaps[VisibleMemberMap.METHODS]);
	}

	/**
	 * Build the inherited method summary.
	 */
	public void buildMethodsInheritedSummary() {
		buildInheritedSummary(memberSummaryWriters[VisibleMemberMap.METHODS],
				visibleMemberMaps[VisibleMemberMap.METHODS]);
	}

	/**
	 * Build the constructor summary.
	 */
	public void buildConstructorsSummary() {
		buildSummary(memberSummaryWriters[VisibleMemberMap.CONSTRUCTORS],
				visibleMemberMaps[VisibleMemberMap.CONSTRUCTORS]);
	}

	/**
	 * Build the class invariant summary.
	 */
	public void buildClassInvariantSummary() {
		ConditionExtractor conditionExtractor = new ConditionExtractor(classDoc, targetContractMap, this.configuration);
		conditionExtractor.extractConditionsOf(classDoc);
		List<String> invariantConditions = conditionExtractor.getInvariantConditions();
		if (!invariantConditions.isEmpty()) {
			ClassInvariantWriterImpl writer = new ClassInvariantWriterImpl((SubWriterHolderWriter) classWriter,
					classWriter.getClassDoc(), conditionExtractor);
			writer.writeMemberSummaryHeader(classDoc);
			writer.writeMemberSummary();
			writer.writeMemberSummaryFooter(classDoc);
		}
	}

	/**
	 * Build the member summary for the given members.
	 * 
	 * @param writer
	 *            the summary writer to write the output.
	 * @param visibleMemberMap
	 *            the given members to summarize.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void buildSummary(MemberSummaryWriter writer, VisibleMemberMap visibleMemberMap) {
		List members = new ArrayList(visibleMemberMap.getLeafClassMembers(configuration));
		if (members.size() > 0) {
			writer.writeMemberSummaryHeader(classDoc);
			writeMemberSummary(writer, members);
			writer.writeMemberSummaryFooter(classDoc);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void writeMemberSummary(MemberSummaryWriter writer, List members) {
		Collections.sort(members);
		
		for (int i = 0; i < members.size(); i++) {
			ProgramElementDoc member = (ProgramElementDoc) members.get(i);
			Tag[] firstSentenceTags = member.firstSentenceTags();
			
			firstSentenceTags = getInheritedComments(member, firstSentenceTags);
			
			boolean isFirst = i == 0;
			boolean isLast = i == members.size() - 1;
			writer.writeMemberSummary(classDoc, member, firstSentenceTags, isFirst, isLast);
		}
	}

	private Tag[] getInheritedComments(ProgramElementDoc member, Tag[] firstSentenceTags) {
		if (member instanceof MethodDoc && firstSentenceTags.length == 0) {
			DocFinder.Output inheritedDoc = DocFinder.search(new DocFinder.Input((MethodDoc) member));
			if (inheritedDoc.holder != null && inheritedDoc.holder.firstSentenceTags().length > 0) {
				firstSentenceTags = inheritedDoc.holder.firstSentenceTags();
			}
		}
		return firstSentenceTags;
	}

	/**
	 * Build the inherited member summary for the given methods.
	 * 
	 * @param writer
	 *            the writer for this member summary.
	 * @param visibleMemberMap
	 *            the map for the members to document.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void buildInheritedSummary(MemberSummaryWriter writer, VisibleMemberMap visibleMemberMap) {
		List<ClassDoc> visibleClassesList = visibleMemberMap.getVisibleClassesList();

		for (ClassDoc inhclass : visibleClassesList) {
			if (!(inhclass.isPublic() || Util.isLinkable(inhclass, configuration))) {
				continue;
			}
			if (inhclass == classDoc) {
				continue;
			}
			List inhmembers = visibleMemberMap.getMembersFor(inhclass);
			if (inhmembers.size() > 0) {
				writer.writeInheritedMemberSummaryHeader(inhclass);
				writeInheritedMember(writer, inhclass, inhmembers);
				writer.writeInheritedMemberSummaryFooter(inhclass);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void writeInheritedMember(MemberSummaryWriter writer, ClassDoc inhclass, List inhmembers) {
		Collections.sort(inhmembers);
		for (int j = 0; j < inhmembers.size(); ++j) {
			ClassDoc clazz = getClassTheInheritedMemberBelongsTo(inhclass);
			boolean isFirst = j == 0;
			boolean isLast = j == inhmembers.size() - 1;
			writer.writeInheritedMemberSummary(clazz, (ProgramElementDoc) inhmembers.get(j), isFirst, isLast);
		}
	}

	private ClassDoc getClassTheInheritedMemberBelongsTo(ClassDoc inhclass) {
		return inhclass.isPackagePrivate() && !Util.isLinkable(inhclass, configuration) ? classDoc : inhclass;
	}
}
