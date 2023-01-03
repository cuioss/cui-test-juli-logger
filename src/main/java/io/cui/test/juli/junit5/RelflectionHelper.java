package io.cui.test.juli.junit5;

import static java.util.Objects.requireNonNull;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.experimental.UtilityClass;

/**
 * Simple utility method for handling reflection related stuff
 *
 * @author Oliver Wolff
 *
 */
@UtilityClass
class RelflectionHelper {

    /**
     * Helper class for extracting <em>all</em> annotations of a given class including from their
     * ancestors.
     *
     * @param annotatedType the (possibly) annotated type. If it is null or
     *            {@link Object#getClass()} it will return an empty list
     * @param annotation the annotation to be extracted, must not be null
     * @return an immutable List with all annotations found at the given object or one of its
     *         ancestors. May be empty but never null
     */
    static <A extends Annotation> List<A> extractAllAnnotations(
            final Class<?> annotatedType,
            final Class<A> annotation) {
        if (null == annotatedType || Object.class.equals(annotatedType.getClass())) {
            return Collections.emptyList();
        }
        final List<A> builder = new ArrayList<>();
        builder.addAll(Arrays.asList(annotatedType.getAnnotationsByType(annotation)));
        builder.addAll(extractAllAnnotations(annotatedType.getSuperclass(), annotation));
        return builder;
    }

    /**
     * Helper class for extracting an annotation of a given class including from their
     * ancestors.
     *
     * @param annotatedType the (possibly) annotated type. If it is null or
     *            {@link Object#getClass()} {@link Optional#empty()}
     * @param annotation the annotation to be extracted, must not be null
     * @return an {@link Optional} on the annotated Object if the annotation can be found. In case
     *         the annotation is found multiple times the first element will be returned.
     */
    static <A extends Annotation> Optional<A> extractAnnotation(
            final Class<?> annotatedType, final Class<A> annotation) {
        requireNonNull(annotation);
        final List<A> extracted = extractAllAnnotations(annotatedType, annotation);
        if (extracted.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(extracted.iterator().next());
    }
}
