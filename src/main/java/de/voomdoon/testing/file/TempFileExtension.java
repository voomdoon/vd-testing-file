package de.voomdoon.testing.file;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * JUnit extension that injects temporary file or directory instances into test method parameters.
 *
 * Behavior:
 * <ul>
 * <li>Injected files and directories are <strong>not</strong> created automatically. Only the parent directories are
 * created.</li>
 * <li>All resources are unique per injection and isolated to the current test method.</li>
 * <li>All resources are automatically deleted after the test completes.</li>
 * <li>If a file or directory cannot be deleted, an exception is thrown.</li>
 * <li>Cleanup may not occur if a test fails abnormally or the JVM is forcibly terminated.</li>
 * <li>Each parameter must be annotated with exactly one supported annotation.</li>
 * </ul>
 * 
 * <p>
 * <strong>Supported annotations:</strong>
 * </p>
 * <ul>
 * <li>{@link TempFile}</li>
 * <li>{@link TempInputFile}</li>
 * <li>{@link TempInputDirectory}</li>
 * <li>{@link TempOutputFile}</li>
 * <li>{@link TempOutputDirectory}</li>
 * </ul>
 * 
 * <p>
 * <strong>Supported parameter types:</strong>
 * </p>
 * <ul>
 * <li>{@link java.io.File}</li>
 * <li>{@link java.nio.file.Path}</li>
 * <li>{@link java.lang.String}</li>
 * </ul>
 * 
 * <p>
 * <strong>Example usage:</strong>
 * </p>
 * 
 * <pre>
 * &#64;ExtendWith(TempFileExtension.class)
 * class MyTest {
 *
 * 	&#64;Test
 * 	void testWithTempFile(@TempFile File tempFile) {
 * 		// use tempFile here
 * 	}
 * }
 * </pre>
 *
 * <p>
 * <strong>Special behavior (like file name extensions or directory creation) can be configured using the following
 * annotations:</strong>
 * <ul>
 * <li>{@link WithTempInputFiles}</li>
 * <li>{@link WithTempInputDirectories}</li>
 * <li>{@link WithTempOutputFiles}</li>
 * <li>{@link WithTempOutputDirectories}</li>
 * </ul>
 * </p>
 *
 * <p>
 * Directory structure:
 * </p>
 * 
 * <pre>
 * target
 * └── test-temp
 *     ├── file_1.tmp (by TempFile)
 *     ├── file_2.tmp
 *     ├── input
 *     │   ├── 0
 *     │   │   ├── input_1.tmp (by TempInputFile)
 *     │   │   ├── input_2.tmp
 *     │   │   └── ...
 *     │   ├── input-directory_1 (by TempInputDirectory)
 *     │   ├── input-directory_2
 *     │   └── ...
 *     └── output
 *         ├── 0
 *         │   ├── output_1.tmp (by TempOutputFile)
 *         │   ├── output_2.tmp
 *         │	└── ...
 *         ├── output-directory_1 (by TempOutputDirectory)
 *         ├── output-directory_2
 *         └── ...
 * </pre>
 *
 * @author André Schulz
 * 
 * @since 0.1.0
 */
public class TempFileExtension implements ParameterResolver, AfterEachCallback {

	/**
	 *
	 * @author André Schulz
	 *
	 * @since 0.1.0
	 */
	enum FileType {

		/**
		 * @since 0.1.0
		 */
		DEFAULT,

		/**
		 * @since 0.1.0
		 */
		INPUT,

		/**
		 * @since 0.1.0
		 */
		INPUT_DIRECTORY,

		/**
		 * @since 0.1.0
		 */
		OUTPUT,

		/**
		 * @since 0.1.0
		 */
		OUTPUT_DIRECTORY,

		;

		/**
		 * @return
		 * @since 0.1.0
		 */
		private boolean isFile() {
			return switch (this) {
				case DEFAULT, INPUT, OUTPUT -> true;
				case INPUT_DIRECTORY, OUTPUT_DIRECTORY -> false;
			};
		}
	}

	public static final String STORE_KEY_ROOT = "temp-files";

	/**
	 * @since 0.1.0
	 */
	private static final Map<FileType, String> STORE_KEYS = Map.of(//
			FileType.DEFAULT, STORE_KEY_ROOT, //
			FileType.INPUT, STORE_KEY_ROOT + "/input", //
			FileType.INPUT_DIRECTORY, STORE_KEY_ROOT + "/input-directory", //
			FileType.OUTPUT, STORE_KEY_ROOT + "/output", //
			FileType.OUTPUT_DIRECTORY, STORE_KEY_ROOT + "/output-directory");

	/**
	 * @since 0.1.0
	 */
	private Path tempDirectory = Path.of("target", "test-temp");

	/**
	 * @since 0.1.0
	 */
	private Path tempInputDirectory = tempDirectory.resolve("input/common");

	/**
	 * @since 0.1.0
	 */
	private Path tempOutputDirectory = tempDirectory.resolve("output/common");

	/**
	 * @since 0.1.0
	 */
	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		deleteDirectory(tempDirectory.toFile());
	}

	/**
	 * @since 0.1.0
	 */
	@Override
	public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		FileType fileType = getFileType(parameterContext);
		Path file = getNext(extensionContext, fileType);

		maybeCreateDirectories(extensionContext, fileType, file);

		return getResultType(parameterContext, file);
	}

	/**
	 * @since 0.1.0
	 */
	@Override
	public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
			throws ParameterResolutionException {
		return supportsType(parameterContext) && supportsAnnotation(parameterContext);
	}

	/**
	 * @param directory
	 *            {@link File}
	 * @since 0.1.0
	 */
	private void deleteDirectory(File directory) throws IOException {
		if (directory.listFiles() != null) {
			for (File file : directory.listFiles()) {
				if (file.isDirectory()) {
					deleteDirectory(file);
				} else {
					Files.delete(file.toPath());
				}
			}
		}

		Files.deleteIfExists(directory.toPath());
	}

	/**
	 * @param context
	 *            {@link ExtensionContext}
	 * @param type
	 *            {@link FileType}
	 * @return {@link String}
	 * @since 0.1.0
	 */
	private String getConfiguredFileNameExtension(ExtensionContext context, FileType type) {
		Class<?> testClass = context.getRequiredTestClass();

		return switch (type) {
			case INPUT -> {
				WithTempInputFiles config = testClass.getAnnotation(WithTempInputFiles.class);
				yield (config != null) ? config.extension() : "tmp";
			}
			case OUTPUT -> {
				WithTempOutputFiles config = testClass.getAnnotation(WithTempOutputFiles.class);
				yield (config != null) ? config.extension() : "tmp";
			}
			default -> "tmp";
		};
	}

	/**
	 * @param type
	 *            {@link FileType}
	 * @return {@link Path}
	 * @since 0.1.0
	 */
	private Path getDirectoryFor(FileType type) {
		return switch (type) {
			case INPUT -> tempInputDirectory;
			case OUTPUT -> tempOutputDirectory;
			default -> tempDirectory;
		};
	}

	/**
	 * @param parameterContext
	 *            {@link ParameterContext}
	 * @return {@link FileType}
	 * @since 0.1.0
	 */
	private FileType getFileType(ParameterContext parameterContext) {
		if (parameterContext.isAnnotated(TempInputFile.class)) {
			return FileType.INPUT;
		} else if (parameterContext.isAnnotated(TempInputDirectory.class)) {
			return FileType.INPUT_DIRECTORY;
		} else if (parameterContext.isAnnotated(TempOutputFile.class)) {
			return FileType.OUTPUT;
		} else if (parameterContext.isAnnotated(TempOutputDirectory.class)) {
			return FileType.OUTPUT_DIRECTORY;
		} else if (parameterContext.isAnnotated(TempFile.class)) {
			return FileType.DEFAULT;
		} else {
			throw new UnsupportedOperationException("Method 'getFileType' not implemented for "
					+ Arrays.stream(parameterContext.getAnnotatedElement().getAnnotations())
							.map(Annotation::annotationType).toList());
		}
	}

	/**
	 * @param context
	 *            {@link ExtensionContext}
	 * @param type
	 *            {@link FileType}
	 * @return {@link Path}
	 * @since 0.1.0
	 */
	private Path getNext(ExtensionContext context, FileType type) {
		List<Path> files = getOrCreateFiles(context, type);
		Path dir = getDirectoryFor(type);

		String prefix = switch (type) {
			case INPUT -> "input";
			case INPUT_DIRECTORY -> "input/input-directory";
			case OUTPUT -> "output";
			case OUTPUT_DIRECTORY -> "output/output-directory";
			case DEFAULT -> "file";
			default -> throw new UnsupportedOperationException("Method 'getNext' not implemented yet");
		};

		String suffix = type.isFile() ? "." + getConfiguredFileNameExtension(context, type) : "";

		Path result;
		int i = 1;

		do {
			String fileName = String.format("%s_%d%s", prefix, i++, suffix);
			result = dir.resolve(fileName);
		} while (files.contains(result));

		files.add(result);

		return result;
	}

	/**
	 * Returns the list of all files for the given {@link FileType} from the store. If no list exists yet, a new one is
	 * created.
	 * 
	 * @param context
	 *            {@link ExtensionContext}
	 * @param type
	 *            {@link FileType}
	 * @return {@link List} of all files as {@link Path}
	 * @since 0.1.0
	 */
	private List<Path> getOrCreateFiles(ExtensionContext context, FileType type) {
		String key = STORE_KEYS.get(type);

		@SuppressWarnings("unchecked")
		List<Path> result = getStore(context).computeIfAbsent(key, k -> Collections.synchronizedList(new ArrayList<>()),
				List.class);

		return result;
	}

	/**
	 * Returns an object of the type configured in the parameter context.
	 * 
	 * @param parameterContext
	 *            {@link ParameterContext}
	 * @param file
	 *            {@link Path}
	 * @return Object of type configured in parameterContext
	 * @since 0.1.0
	 */
	private Object getResultType(ParameterContext parameterContext, Path file) {
		Class<?> resultType = parameterContext.getParameter().getType();

		if (File.class.equals(resultType)) {
			return file.toFile();
		} else if (Path.class.equals(resultType)) {
			return file;
		} else if (String.class.equals(resultType)) {
			return file.toString();
		}

		throw new ParameterResolutionException("Unsupported parameter type: " + resultType);
	}

	/**
	 * Returns the store for the given extension context.
	 * 
	 * @param context
	 *            {@link ExtensionContext}
	 * @return {@link ExtensionContext.Store}
	 * @since 0.1.0
	 */
	private ExtensionContext.Store getStore(ExtensionContext context) {
		return context.getStore(ExtensionContext.Namespace.create(getClass(), context));
	}

	/**
	 * Returns whether directory creation is configured for the given {@link FileType}.
	 * 
	 * @param context
	 *            {@link ExtensionContext}
	 * @param fileType
	 *            {@link FileType}
	 * @return {@code true} if directory creation is configured, else {@code false}
	 * @since 0.1.0
	 */
	private boolean isDirectoryCreationConfigured(ExtensionContext context, FileType fileType) {
		Class<?> testClass = context.getRequiredTestClass();

		return switch (fileType) {
			case INPUT_DIRECTORY -> {
				WithTempInputDirectories config = testClass.getAnnotation(WithTempInputDirectories.class);
				yield (config != null) ? config.create() : false;
			}
			case OUTPUT_DIRECTORY -> {
				WithTempOutputDirectories config = testClass.getAnnotation(WithTempOutputDirectories.class);
				yield (config != null) ? config.create() : false;
			}
			default -> false;
		};
	}

	/**
	 * Makes sure that all necessary directories are created if configured.
	 * 
	 * @param extensionContext
	 *            {@link ExtensionContext}
	 * @param fileType
	 *            {@link FileType}
	 * @param file
	 *            {@link Path} representing the file
	 * @since 0.1.0
	 */
	private void maybeCreateDirectories(ExtensionContext extensionContext, FileType fileType, Path file) {
		maybeCreateDirectory(extensionContext, fileType, file);

		Path baseDir = getDirectoryFor(fileType);

		try {
			Files.createDirectories(baseDir);
		} catch (IOException e) {
			throw new ParameterResolutionException("Failed to create temp directory: " + baseDir, e);
		}
	}

	/**
	 * Makes sure that the given directory is created if configured.
	 * 
	 * @param context
	 *            {@link ExtensionContext}
	 * @param fileType
	 *            {@link FileType}
	 * @param file
	 *            {@link Path} representing the file
	 * @since 0.1.0
	 */
	private void maybeCreateDirectory(ExtensionContext context, FileType fileType, Path file) {
		if (!fileType.isFile() && isDirectoryCreationConfigured(context, fileType)) {
			try {
				Files.createDirectories(file);
			} catch (IOException e) {
				throw new RuntimeException("Error at 'maybeCreateDirectory': " + e.getMessage(), e);
			}
		}
	}

	/**
	 * @param parameterContext
	 *            {@link ParameterContext}
	 * @return {@code true} if parameter is annotated with supported annotation, else {@code false}
	 * @since 0.1.0
	 */
	private boolean supportsAnnotation(ParameterContext parameterContext) {
		boolean isAnnotated = //
				parameterContext.isAnnotated(TempFile.class) //
						|| parameterContext.isAnnotated(TempInputFile.class)//
						|| parameterContext.isAnnotated(TempInputDirectory.class)//
						|| parameterContext.isAnnotated(TempOutputFile.class)//
						|| parameterContext.isAnnotated(TempOutputDirectory.class);
		return isAnnotated;
	}

	/**
	 * @param parameterContext
	 *            {@link ParameterContext}
	 * @return {@code true} if parameter type is supported, else {@code false}
	 * @since 0.1.0
	 */
	private boolean supportsType(ParameterContext parameterContext) {
		boolean isSupportedType = //
				File.class.isAssignableFrom(parameterContext.getParameter().getType())
						|| Path.class.isAssignableFrom(parameterContext.getParameter().getType())
						|| String.class.isAssignableFrom(parameterContext.getParameter().getType());
		return isSupportedType;
	}
}
