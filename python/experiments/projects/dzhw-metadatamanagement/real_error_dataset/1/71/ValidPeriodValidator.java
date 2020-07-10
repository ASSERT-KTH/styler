package eu.dzhw.fdz.metadatamanagement.common.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import eu.dzhw.fdz.metadatamanagement.common.domain.Period;

/**
 * Validate that the begin of a period is less than or equal to the end.
 * @author René Reitmann
 */
public class ValidPeriodValidator implements ConstraintValidator<ValidPeriod, Period> {

  /*
   * (non-Javadoc)
   *
   * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
   */
  @Override
  public void initialize(ValidPeriod constraintAnnotation) {}

  /*
   * (non-Javadoc)
   *
   * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
   * javax.validation.ConstraintValidatorContext)
   */
  @Override
  public boolean isValid(Period period, ConstraintValidatorContext context) {

    // no period, no valid value.
    if (period == null) {
      return false;
    }

    // if one end of the period is not set than it is not valid
    if (period.getStart() == null || period.getEnd() == null) {
      return false;
    }

    return period.getStart().isBefore(period.getEnd()) || period.getStart().equals(period.getEnd());
  }
}
