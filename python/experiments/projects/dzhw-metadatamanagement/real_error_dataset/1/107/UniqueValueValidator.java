package eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.validation;

import java.util.HashSet;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.ValidResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * Ensure that value.valueClass is unique within the variable.
 * 
 * @author René Reitmann
 */
@Slf4j
public class UniqueValueValidator
    implements ConstraintValidator<UniqueValue, List<ValidResponse>> {
  /*
   * (non-Javadoc)
   * 
   * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
   */
  @Override
  public void initialize(UniqueValue constraintAnnotation) {}

  /*
   * (non-Javadoc)
   * 
   * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
   * javax.validation.ConstraintValidatorContext)
   */
  @Override
  public boolean isValid(List<ValidResponse> validResponses, ConstraintValidatorContext context) {
    if (validResponses == null) {
      return true;
    }
    
    HashSet<String> classes = new HashSet<>();
    
    for (ValidResponse validResponse : validResponses) {
      if (validResponse.getValue() != null) {
        if (classes.contains(validResponse.getValue())) {
          log.debug("Duplicate validResponse.value found: " + validResponse.getValue());
          return false;
        }
        classes.add(validResponse.getValue());
      }
    }
    
    return true;
  }
}
