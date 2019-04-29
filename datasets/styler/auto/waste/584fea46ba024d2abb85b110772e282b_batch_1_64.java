package com.google.auto.value.extension;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

/**
 * An AutoValueExtension allows for extra functionality to be created during the generation
 * of an AutoValue class.
 *
 * <p>NOTE: The design of this interface is not final and subject to change.
 *
 * <p>Extensions are discovered at compile time using the {@link java.util.ServiceLoader} APIs,
 * allowing them to run without any additional annotations.
 *
 * <p>Extensions can extend the AutoValue implementation by generating subclasses of the AutoValue
 * generated class. Its not guaranteed that an Extension's generated class will be the final
 * class in the inheritance hierarchy, unless it's {@link #mustBeFinal()} method 
 * returns true, and only one Extension at a time can return true for a given context.  Only
 * generated classes that will be the final class in the inheritance hierarchy can be declared 
 * final.  All others should be declared abstract.
 *
 * <p>Each Extension must also be sure to generate a constructor with arguments corresponding to
 * all properties in {@link com.google.auto.value.AutoValueExtension.Context#properties()}, in
 * order.  This constructor must have at least package visibility.
 */
public abstract class AutoValueExtension {

  /**
   * The context of the generation cycle.
   */
  public interface Context {

    /**
     * The processing environment of this generation cycle.
     *
     * @return The ProcessingEnvironment of this generation cycle.
     */
    ProcessingEnvironment processingEnvironment();

    /**
     * The package name of the classes to be generated.
     *
     * @return The package name of the classes to be generated.
     */
    String packageName();

    /**
     * The annotated class that this generation cycle is based on.
     *
     * <p>Given {@code @AutoValue public class Foo {...}}, this will be {@code Foo}.
     *
     * @return The annotated class.
     */
    TypeElement autoValueClass();

    /**
     * The ordered collection of properties to be generated by AutoValue.
     *
     * @return The ordered collection of properties.
     */
    Map<String, ExecutableElement> properties();
  }

  /**
   * Determines whether this extension applies to the given context.
   *
   * @param context The Context of the code generation for this class.
   * @return True if this extension should be applied in the given context.
   */
  public boolean applicable(Context context) {
    return false;
  }

  /**
   * Denotes that the class generated by this Extension must be the final class
   * in the inheritance hierarchy.  Only one extension may be the final class, so
   * this should be used sparingly.
   *
   * @param context The {@link com.google.auto.value.AutoValueExtension.Context} of the code
   *                generation for this class.
   * @return True if the resulting class must be the final class in the inheritance hierarchy.
   */
  public boolean mustBeFinal(Context context) {
    return false;
  }

  /**
   * Returns a non-null collections of property names from <code>context.properties()</code> that
   * this extension intends to implement.  This will prevent AutoValue from generating an
   * implementation and remove the supplied properties from builders, constructors, toString() and
   * hashcode(). Defaults to an empty set.
   *
   * @param context The {@link com.google.auto.value.AutoValueExtension.Context} of the code
   *                generation for this class.
   * @return A collection of property names that this extension intends to implement.
   */
  public Collection<String> consumeProperties(Context context) {
    return Collections.emptySet();
  }

  /**
   * Generates the source code of the class named <code>className</code> to extend
   * <code>classToExtend</code>, with the original annotated class of
   * <code>classToImplement</code>.  The generated class should be final if <code>isFinal</code>
   * is true, otherwise it should be abstract.
   *
   * @param context The {@link com.google.auto.value.AutoValueExtension.Context} of the code
   *                generation for this class.
   * @param className The name of the resulting class. The returned code will be written to a
   *                  file named accordingly.
   * @param classToExtend The direct parent of the generated class. Could be the AutoValue
   *                      generated class, or a class generated as the result of another
   *                      extension.
   * @param isFinal True if this class is the last class in the chain, meaning it should be
   *                marked as final, otherwise it should be marked as abstract.
   * @return The source code of the generated class
   */
  public
  abstract
  String
  generateClass
  (
  Context
  context
  ,
  String
  className
  ,
  String
  classToExtend
  ,
  boolean
}

