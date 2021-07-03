package eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.FilterExpressionLanguages;

/**
 * Validator for the filter expression languages of a variable. Only valued from the
 * {@link FilterExpressionLanguages} class are allowed.
 * 
 * @author Daniel Katzberg
 *
 */
public class ValidFilterExpressionLanguageValidator
    implements ConstraintValidator<ValidFilterExpressionLanguage, String> {

  /*
   * (non-Javadoc)
   * 
   * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
   */
  @Override
  public void initialize(ValidFilterExpressionLanguage constraintAnnotation) {}

  /*
   * (non-Javadoc)
   * 
   * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
   * javax.validation.ConstraintValidatorContext)
   */
  @Override
  public boolean isValid(String filterExpressionLanguage, ConstraintValidatorContext context) {

     // expression language is okay
    return FilterExpressionLanguages.ALL.contains(filterExpressionLanguage);
  }

}
