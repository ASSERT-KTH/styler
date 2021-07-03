package eu.dzhw.fdz.metadatamanagement.conceptmanagement.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import eu.dzhw.fdz.metadatamanagement.common.domain.I18nString;
import eu.dzhw.fdz.metadatamanagement.conceptmanagement.domain.ConceptAttachmentMetadata;
import eu.dzhw.fdz.metadatamanagement.conceptmanagement.domain.ConceptAttachmentTypes;

/**
 * Validates the type of an {@link ConceptAttachmentMetadata}.
 */
public class ValidConceptAttachmentTypeValidator
    implements ConstraintValidator<ValidConceptAttachmentType, I18nString> {

  /*
   * (non-Javadoc)
   * 
   * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
   */
  @Override
  public void initialize(ValidConceptAttachmentType constraintAnnotation) {}

  /*
   * (non-Javadoc)
   * 
   * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
   * javax.validation.ConstraintValidatorContext)
   */
  @Override
  public boolean isValid(I18nString type, ConstraintValidatorContext context) {
    return ConceptAttachmentTypes.ALL.contains(type);
  }

}
