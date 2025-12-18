package de.voomdoon.testing.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;

import java.io.File;
import java.io.IOException;
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
	void test_File(@TempFile File file) {
		assertThat(file).isNotNull();
	}

	/**
	 * @throws IOException
	 * @since 0.1.0
	 */
	@Test
	void test_File_canBeCreated(@TempFile File file) throws IOException {
		boolean success = file.createNewFile();

		assertThat(success).isTrue();
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_File_isNotCreated(@TempFile File file) {
		assumeThat(file).isNotNull();

		assertThat(file).doesNotExist();
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_File_twoAreDifferent(@TempFile File file1, @TempFile File file2) {
		assertThat(file1).isNotEqualTo(file2);
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_Path(@TempFile Path path) {
		assertThat(path).isNotNull();
	}

	/**
	 * @since 0.1.0
	 */
	@Test
	void test_Path(@TempFile String string) {
		assertThat(string).endsWith(".tmp");
	}
}