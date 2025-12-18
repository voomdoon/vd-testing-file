package de.voomdoon.testing.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Tests for {@link TempOutputDirectory}.
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
@ExtendWith(TempFileExtension.class)
class TempOutputDirectoryTest {

	/**
	 * Test class for {@link WithTempOutputDirectories}.
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	class WithTempOutputDirectoriesTest {

		/**
		 * Tests for {@code create=false}.
		 * 
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		@ExtendWith(TempFileExtension.class)
		@WithTempOutputDirectories(create = false)
		class Create_false_Test {

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_directoryDoesNotExists(@TempOutputDirectory File directory) throws Exception {
				assertThat(directory).doesNotExist();
			}
		}

		/**
		 * Tests for {@code create=true}.
		 * 
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		@ExtendWith(TempFileExtension.class)
		@WithTempOutputDirectories(create = true)
		class Create_true_Test {

			/**
			 * @since 0.1.0
			 */
			@Test
			void test_directoryExists(@TempOutputDirectory File directory) throws Exception {
				assertThat(directory).exists();
			}
		}
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_namePrefix(@TempOutputDirectory File directory) {
		assertThat(directory.getName()).startsWith("output-directory_");
	}

	/**
	 * @param directory
	 * @since 0.1.0
	 */
	@Test
	void test_parentIsNamedOutput(@TempOutputDirectory File directory) {
		assertThat(directory.getParentFile().getName()).isEqualTo("output");
	}

	/**
	 * @param directory
	 * @since 0.1.0
	 */
	@Test
	void test_twoAreNotEqual(@TempOutputDirectory File directory1, @TempOutputDirectory File directory2) {
		assertThat(directory1).isNotEqualTo(directory2);
	}
}
