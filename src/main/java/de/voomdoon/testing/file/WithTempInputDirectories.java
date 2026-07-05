package de.voomdoon.testing.file;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configures handling of {@link TempInputDirectory}.
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WithTempInputDirectories {

	/**
	 * Determines whether input directories are created automatically.
	 *
	 * @return {@code true} results in creation of the directory
	 * @since 0.1.0
	 */
	boolean create() default false;
}
