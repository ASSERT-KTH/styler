package eu.dzhw.fdz.metadatamanagement.instrumentmanagement.domain.validation;

import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import eu.dzhw.fdz.metadatamanagement.common.domain.projections.IdAndVersionProjection;
import eu.dzhw.fdz.metadatamanagement.instrumentmanagement.domain.Instrument;
import eu.dzhw.fdz.metadatamanagement.instrumentmanagement.repository.InstrumentRepository;

/**
 * Validates the uniqueness of number.
 */
public class ValidUniqueInstrumentNumberValidator
    implements ConstraintValidator<ValidUniqueInstrumentNumber, Instrument> {
  
  @Autowired
  private InstrumentRepository instrumentRepository;

  /*
   * (non-Javadoc)
   * 
   * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
   */
  @Override
  public void initialize(ValidUniqueInstrumentNumber constraintAnnotation) {}

  /*
   * (non-Javadoc)
   * 
   * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
   * javax.validation.ConstraintValidatorContext)
   */
  @Override
  public boolean isValid(Instrument instrument, ConstraintValidatorContext context) {

    if (instrument.isShadow()) {
      return true;
    } else {
      if (instrument.getNumber() == null
          || StringUtils.isEmpty(instrument.getDataAcquisitionProjectId())) {
        return true;
      }
      List<IdAndVersionProjection> instruments = instrumentRepository
          .findIdsByNumberAndDataAcquisitionProjectId(instrument.getNumber(),
              instrument.getDataAcquisitionProjectId());
      if (instruments.size() > 1) {
        return false;
      }
      if (instruments.size() == 1) {
        return instruments.get(0).getId().equals(instrument.getId());
      }
      return true;
    }
  }
}
