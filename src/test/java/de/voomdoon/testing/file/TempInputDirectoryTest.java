package de.voomdoon.testing.file;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * Test class for {@link TempInputDirectory}.
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
@ExtendWith(TempFileExtension.class)
class TempInputDirectoryTest {

	/**
	 * Test class for {@link WithTempInputDirectories}.
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	class WithTempInputDirectoriesTest {

		/**
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		@ExtendWith(TempFileExtension.class)
		@WithTempInputDirectories(create = false)
		class Create_false_Test {

			/**
			 * @param directory
			 *            {@link File}
			 * @since 0.1.0
			 */
			@Test
			void test_directoryDoesNotExists(@TempInputDirectory File directory) throws Exception {
				assertThat(directory).doesNotExist();
			}
		}

		/**
		 * @author André Schulz
		 *
		 * @since 0.1.0
		 */
		@Nested
		@ExtendWith(TempFileExtension.class)
		@WithTempInputDirectories(create = true)
		class Create_true_Test {

			/**
			 * @param directory
			 *            {@link File}
			 * @since 0.1.0
			 */
			@Test
			void test_directoryExists(@TempInputDirectory File directory) throws Exception {
				assertThat(directory).exists();
			}
		}
	}

	/**
	 * @param directory
	 *            {@link File}
	 * @since 0.1.0
	 */
	@Test
	void test_isNotCreated(@TempInputDirectory File directory) {
		assertThat(directory).doesNotExist();
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_namePrefix(@TempInputDirectory File directory) {
		assertThat(directory.getName()).startsWith("input-directory_");
	}

	/**
	 * @param directory
	 *            {@link File}
	 * @since 0.1.0
	 */
	@Test
	void test_parentIsNamedInput(@TempInputDirectory File directory) {
		assertThat(directory.getParentFile().getName()).isEqualTo("input");
	}

	/**
	 * @param directory1
	 *            {@link File}
	 * @param directory2
	 *            {@link File}
	 * @since 0.1.0
	 */
	@Test
	void test_twoAreNotEqual(@TempInputDirectory File directory1, @TempInputDirectory File directory2) {
		assertThat(directory1).isNotEqualTo(directory2);
	}
}
