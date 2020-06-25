package meintagebuch;

import com.openpojo.reflection.PojoClass;
import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.NoPublicFieldsExceptStaticFinalRule;
import com.openpojo.validation.rule.impl.NoStaticExceptFinalRule;
import org.junit.Test;

public class PhrasesAndConstantsTest {
  @Test
  public void validateConstants() throws Exception {

    PojoClass activityPojo = PojoClassFactory.getPojoClass(PhrasesAndConstants.class);

    Validator validator = ValidatorBuilder.create()
        .with(new NoStaticExceptFinalRule())
        .with(new NoPublicFieldsExceptStaticFinalRule())
        .build();

    // Start the Test
    validator.validate(activityPojo);
  }
}
