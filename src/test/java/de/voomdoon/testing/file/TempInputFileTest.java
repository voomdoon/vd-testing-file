package de.voomdoon.testing.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link TempInputFile}.
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
@ExtendWith(TempFileExtension.class)
class TempInputFileTest {

	/**
	 * Tests for {@link WithTempInputFilesTest}
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	class WithTempInputFilesTest {

		/**
		 * Test for {@code extension}.
		 *
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		@WithTempInputFiles(extension = "txt")
		class ExtensionTest {

			/**
			 * @param file
			 *            {@link File}
			 * @since 0.1.0
			 */
			@Test
			void test_File_hasConfiguredExtension(@TempInputFile File file) {
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
	void test_File_isAtSomeInputDirectory(@TempInputFile File file) {
		assertThat(file.getParent()).contains("input");
	}
}
