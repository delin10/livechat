package nil.ed.livechat.common.validator.annotation;

import javax.validation.Constraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.validator.internal.constraintvalidators.bv.NotNullValidator;
import org.hibernate.validator.internal.constraintvalidators.bv.size.SizeValidatorForCharSequence;

/**
 * Created at 2020-03-08
 *
 * @author lidelin
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {NotNullValidator.class, SizeValidatorForCharSequence.class})
@Size(min = 6)
@NotNull
public @interface PwdValidated {
}
