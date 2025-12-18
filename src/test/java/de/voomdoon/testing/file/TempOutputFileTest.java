package de.voomdoon.testing.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link TempOutputFile}.
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
@ExtendWith(TempFileExtension.class)
class TempOutputFileTest {

	/**
	 * Tests for {@link WithTempOutputFilesTest}
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	class WithTempOutputFilesTest {

		/**
		 * Test for {@code extension}.
		 *
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		@WithTempOutputFiles(extension = "txt")
		class ExtensionTest {

			/**
			 * @param file
			 *            {@link File}
			 * @since 0.1.0
			 */
			@Test
			void test_File_hasConfiguredExtension(@TempOutputFile File file) {
				assertThat(file.getName()).endsWith(".txt");
			}
		}
	}

	/**
	 * @param file
	 *            {@link File}
	 * @since 0.1.0
	 */
	@Test
	void test_File_isAtSomeOutputDirectory(@TempOutputFile File file) {
		assertThat(file.getParent()).contains("output");
	}
}
