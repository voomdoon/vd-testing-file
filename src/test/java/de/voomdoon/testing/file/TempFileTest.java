package de.voomdoon.testing.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * DOCME add JavaDoc for UseTempFilesTest
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
@ExtendWith(TempFileExtension.class)
class TempFileTest {

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_File(@TempFile File file) throws Exception {
		assertThat(file).isNotNull();
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_File_canBeCreated(@TempFile File file) throws Exception {
		boolean success = file.createNewFile();

		assertThat(success).isTrue();
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_File_isNotCreated(@TempFile File file) throws Exception {
		assumeThat(file).isNotNull();

		assertThat(file).doesNotExist();
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_File_twoAreDifferent(@TempFile File file1, @TempFile File file2) throws Exception {
		assertThat(file1).isNotEqualTo(file2);
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_Path(@TempFile Path path) throws Exception {
		assertThat(path).isNotNull();
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_Path(@TempFile String string) throws Exception {
		assertThat(string).endsWith(".tmp");
	}
}