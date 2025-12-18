package de.voomdoon.testing.file;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configures handling of {@link TempOutputFile}.
 * 
 * @author André Schulz
 *
 * @since 0.1.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WithTempOutputFiles {

	/**
	 * File extension to use for {@link TempOutputFile}s.
	 * 
	 * @return the file extension (default is {@code tmp})
	 * @since 0.1.0
	 */
	String extension() default "tmp";
}
