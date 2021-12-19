/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class com.bld.processor.JpaProcessor.java
 */
package com.bld.processor;

import java.lang.annotation.Annotation;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.persistence.Entity;

import com.bld.processor.build.ClassBuilding;

import bld.commons.classes.generator.ClassesGenerator;
import bld.commons.classes.generator.config.ConfigurationClassGenerator;
import bld.commons.classes.generator.impl.ClassesGeneratorImpl;
import bld.commons.classes.model.ModelClasses;
import bld.commons.processor.annotations.QueryBuilder;

/**
 * The Class JpaProcessor.
 */
@SupportedAnnotationTypes({ "javax.persistence.Entity", "bld.commons.processor.annotations.QueryBuilder" })
//@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class JpaProcessor extends AbstractProcessor {

	/** The model classes. */
	private ModelClasses modelClasses = new ModelClasses();

	/**
	 * Process.
	 *
	 * @param annotations the annotations
	 * @param roundEnv the round env
	 * @return true, if successful
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		try {
			this.runAnnotation(roundEnv, Entity.class);
			this.runAnnotationWithGeneration(roundEnv, QueryBuilder.class);

		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}

		return true;
	}

	/**
	 * Run annotation with generation.
	 *
	 * @param roundEnv the round env
	 * @param classAnnotation the class annotation
	 * @throws Exception the exception
	 */
	private void runAnnotationWithGeneration(RoundEnvironment roundEnv, Class<? extends Annotation> classAnnotation) throws Exception {
		this.runAnnotation(roundEnv, classAnnotation);
		ClassesGenerator generatorClass = new ClassesGeneratorImpl(ConfigurationClassGenerator.configClassGenerator("/template"));
		generatorClass.writeClass(modelClasses, this.processingEnv);
	}

	/**
	 * Run annotation.
	 *
	 * @param roundEnv the round env
	 * @param classAnnotation the class annotation
	 * @throws Exception the exception
	 */
	private void runAnnotation(RoundEnvironment roundEnv, Class<? extends Annotation> classAnnotation) throws Exception {
		modelClasses = new ModelClasses();
		Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(classAnnotation);
		Set<TypeElement> types = ElementFilter.typesIn(annotatedElements);

		for (TypeElement type : types) {
			Annotation annotation = type.getAnnotation(classAnnotation);
			if (annotation != null) {
				if (Entity.class.isAssignableFrom(classAnnotation)) {
					ClassBuilding.generateJpaClass(modelClasses, type);
				} else if (QueryBuilder.class.isAssignableFrom(classAnnotation)) {
					AnnotationMirror annotationMirror=null;
					for(AnnotationMirror am:type.getAnnotationMirrors()) {
						System.out.println(classAnnotation.getName()+"=="+am.getAnnotationType().toString());
						if(classAnnotation.getName().equals(am.getAnnotationType().toString())) {
							System.out.println("Set Annotation ");
							annotationMirror=am;
							break;
						}
					}
						
					for (TypeMirror typeMirror : type.getInterfaces()) {
						String servicePackage = typeMirror.toString().substring(0, typeMirror.toString().lastIndexOf("."));
						if (typeMirror instanceof DeclaredType) {
							TypeElement te = (TypeElement) this.processingEnv.getTypeUtils().asElement(typeMirror);
							for (TypeMirror tm : te.getInterfaces()) {
								if (typeMirror instanceof DeclaredType) {
									DeclaredType dclt = (DeclaredType) tm;
									TypeMirror argument = dclt.getTypeArguments().get(0);
									TypeElement typeArgument = (TypeElement) this.processingEnv.getTypeUtils().asElement(argument);
									ClassBuilding.generateQueryClass(modelClasses, typeArgument, servicePackage, this.processingEnv, type.getAnnotation(QueryBuilder.class),annotationMirror,type);
								}
							}
						}
					}
				}

			}

		}
	}

}
