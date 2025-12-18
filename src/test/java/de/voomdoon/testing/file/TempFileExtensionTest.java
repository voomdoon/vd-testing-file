package de.voomdoon.testing.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.lang.reflect.Parameter;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.mockito.Mockito;

/**
 * Tests for {@link TempFileExtension}.
 *
 * @author André Schulz
 *
 * @since 0.1.0
 */
class TempFileExtensionTest {

	/**
	 * Tests for {@link TempFileExtension#afterEach(ExtensionContext)}.
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	class AfterEachTest {

		/**
		 * @param file
		 *            {@link File}
		 * @since 0.1.0
		 */
		public void dummyMethod(@TempFile File file) {
		}

		/**
		 * @throws Exception
		 * @since 0.1.0
		 */
		@Test
		void test_File_isDeleted() throws Exception {
			TempFileExtension extension = new TempFileExtension();

			ParameterContext parameterContext = mock(ParameterContext.class);

			ExtensionContext extensionContext = mock(ExtensionContext.class);
			ExtensionContext.Store store = mock(ExtensionContext.Store.class);
			when(extensionContext.getStore(any())).thenReturn(store);
			Mockito.doReturn(AfterEachTest.class).when(extensionContext).getRequiredTestClass();

			List<Path> trackedFiles = new ArrayList<>();

			when(store.get(eq(TempFileExtension.STORE_KEY_ROOT), eq(List.class))).thenReturn(trackedFiles);
			when(store.computeIfAbsent(eq(TempFileExtension.STORE_KEY_ROOT), any(), eq(List.class)))
					.thenReturn(trackedFiles);

			Parameter parameter = AfterEachTest.class.getDeclaredMethod("dummyMethod", File.class).getParameters()[0];

			when(parameterContext.getParameter()).thenReturn(parameter);
			when(parameterContext.isAnnotated(TempFile.class)).thenReturn(true);

			File file = (File) extension.resolveParameter(parameterContext, extensionContext);

			file.createNewFile();

			extension.afterEach(extensionContext);

			assertThat(file).doesNotExist();
		}

		/**
		 * @throws SecurityException
		 * @throws NoSuchMethodException
		 * @since 0.1.0
		 */
		@Test
		void test_noFile_passes() throws NoSuchMethodException, SecurityException {
			TempFileExtension extension = new TempFileExtension();

			ParameterContext parameterContext = mock(ParameterContext.class);
			ExtensionContext extensionContext = mock(ExtensionContext.class);

			ExtensionContext.Store store = mock(ExtensionContext.Store.class);
			when(extensionContext.getStore(any())).thenReturn(store);

			List<Path> trackedFiles = new ArrayList<>();

			when(store.get(eq(TempFileExtension.STORE_KEY_ROOT), eq(List.class))).thenReturn(trackedFiles);
			when(store.computeIfAbsent(eq(TempFileExtension.STORE_KEY_ROOT), any(), eq(List.class)))
					.thenReturn(trackedFiles);

			Parameter parameter = AfterEachTest.class.getDeclaredMethod("dummyMethod", File.class).getParameters()[0];

			when(parameterContext.getParameter()).thenReturn(parameter);
			when(parameterContext.isAnnotated(TempFile.class)).thenReturn(true);

			assertDoesNotThrow(() -> extension.afterEach(extensionContext));
		}
	}

	/**
	 * Tests for {@link TempFileExtension#resolveParameter(ParameterContext, ExtensionContext)}.
	 * 
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	@ExtendWith(TempFileExtension.class)
	class ResolveParameterTest {

		/**
		 * @since 0.1.0
		 */
		@Test
		void test() {
			TempFileExtension extension = new TempFileExtension();
			ParameterContext parameterContext = mockParameterContext();
			ExtensionContext extensionContext = mockExtensionContext();

			Object actual1 = extension.resolveParameter(parameterContext, extensionContext);
			Object actual2 = extension.resolveParameter(parameterContext, extensionContext);

			assertThat(actual1).asString().endsWith("_1.tmp");
			assertThat(actual2).asString().endsWith("_2.tmp");
		}

		/**
		 * @since 0.1.0
		 */
		private ExtensionContext mockExtensionContext() {
			ExtensionContext extensionContext = mock(ExtensionContext.class);
			Store store = mock(ExtensionContext.Store.class);
			when(store.computeIfAbsent(any(), any(), eq(List.class))).thenReturn(new ArrayList<Path>());
			when(extensionContext.getStore(any())).thenReturn(store);
			return extensionContext;
		}

		/**
		 * @since 0.1.0
		 */
		private ParameterContext mockParameterContext() {
			ParameterContext parameterContext = mock(ParameterContext.class);
			Parameter parameter = mock(Parameter.class);
			doReturn(String.class).when(parameter).getType();
			when(parameterContext.getParameter()).thenReturn(parameter);
			when(parameterContext.isAnnotated(TempFile.class)).thenReturn(true);
			return parameterContext;
		}
	}

	/**
	 * Tests for {@link TempFileExtension#supportsParameter(ParameterContext, ExtensionContext)}.
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	class SupportsParameterTest {

		/**
		 * @since 0.1.0
		 */
		@Test
		void test_false_forNotSupportedType() {
			TempFileExtension extension = new TempFileExtension();
			ParameterContext parameterContext = mock(ParameterContext.class);
			Parameter parameter = mock(Parameter.class);
			doReturn(String.class).when(parameter).getType();
			when(parameterContext.getParameter()).thenReturn(parameter);
			ExtensionContext extensionContext = mock(ExtensionContext.class);

			boolean actual = extension.supportsParameter(parameterContext, extensionContext);

			assertThat(actual).isFalse();
		}
	}

	/**
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	@Nested
	@ExtendWith(TempFileExtension.class)
	class UseMultipleParameterAnotationsTest {

		/**
		 * @param tempfile
		 *            {@link File}
		 * @param tempInputFile
		 *            {@link File}
		 * @param tempOutputFile
		 *            {@link File}
		 * @throws Exception
		 * @since 0.1.0
		 */
		@Test
		void testTempFileAndTempInputFileANDTempOutputFile(@TempFile File tempfile, @TempInputFile File tempInputFile,
				@TempOutputFile File tempOutputFile) throws Exception {
			assertThat(tempfile).isNotNull();

			assertThat(tempInputFile).isNotNull();
			assertThat(tempInputFile).isNotEqualTo(tempfile);

			assertThat(tempOutputFile).isNotNull();
			assertThat(tempOutputFile).isNotEqualTo(tempfile);
		}
	}
}
