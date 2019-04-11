/*******************************************************************************
 * Copyright (c) 2009, 2019 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Evgeny Mandrikov - initial API and implementation
 *
 *******************************************************************************/
package org.jacoco.core.internal.analysis.filter;


import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.List;

/**
 * Filters classes and methods annotated with
 * {@link java.lang.annotation.RetentionPolicy#RUNTIME runtime visible} and
 * {@link java.lang.annotation.RetentionPolicy#CLASS invisible} annotation whose
 * simple name contains <code>Generated</code>.
 */
public final class WhiteListFilter implements IFilter {

	public void filter(final MethodNode methodNode,
			final IFilterContext context, final IFilterOutput output) {

		boolean testRequired=false;

		for (String annotation : context.getClassAnnotations()) {
			if (matches(annotation)) {
				testRequired=true;
				break;
			}
		}

		if(!testRequired){
			output.ignore(methodNode.instructions.getFirst(),
					methodNode.instructions.getLast());
			return;
		}

		if ( (methodNode.access & Opcodes.ACC_PUBLIC) == 0 ) {
			output.ignore(methodNode.instructions.getFirst(),
					methodNode.instructions.getLast());
		}
	}

	private static boolean matches(final String annotation) {
		final String name = annotation
				.substring(Math.max(annotation.lastIndexOf('/'),
						annotation.lastIndexOf('$')) + 1);
		return name.contains("TestRequired");
	}

	private static boolean presentIn(final List<AnnotationNode> annotations) {
		if (annotations != null) {
			for (AnnotationNode annotation : annotations) {
				if (matches(annotation.desc)) {
					return true;
				}
			}
		}
		return false;
	}

}
