package de.alpharogroup.c10n.iface.generator.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import c10n.annotations.De;
import c10n.annotations.En;
import c10n.annotations.Fr;
import c10n.annotations.It;
import c10n.annotations.Ja;
import c10n.annotations.Ko;
import c10n.annotations.Ru;
import c10n.annotations.Zh;
import de.alpharogroup.file.search.FileSearchUtils;
import de.alpharogroup.lang.PropertiesUtils;
import de.alpharogroup.locale.Locales;

public class PropertiesValidation
{

	@SuppressWarnings("serial")
	public static final Map<Locale, Class<?>> localeToannotation = new HashMap<Locale, Class<?>>()
	{
		{
			put(Locale.ENGLISH, En.class);
			put(Locale.GERMAN, De.class);
			put(Locale.FRENCH, Fr.class);
			put(Locale.ITALIAN, It.class);
			put(Locale.JAPANESE, Ja.class);
			put(Locale.KOREAN, Ko.class);
			put(Locales.RUSSIAN, Ru.class);
			put(Locale.CHINESE, Zh.class);
		}
	};

	public static List<File> findFiles(final File rootDir)
	{
		final String pattern = "*.properties";
		final List<File> foundedFiles = FileSearchUtils.findFilesRecursive(rootDir, pattern);
		return foundedFiles;
	}

	public static Map<String, List<Properties>> getMapForC10NInterfaceGeneration(
		final Collection<File> propertiesFiles) throws IOException
	{
		final Map<String, List<Properties>> propertiesWithAnnotationNames = new LinkedHashMap<String, List<Properties>>();
		for (final File propertiesFile : propertiesFiles)
		{
			final Properties properties = PropertiesUtils.loadProperties(propertiesFile);
			final Locale locale = PropertiesUtils.getLocale(propertiesFile);
			if (localeToannotation.containsKey(locale))
			{
				final Class<?> c10nAnnotation = localeToannotation.get(locale);
				if (!propertiesWithAnnotationNames.containsKey(c10nAnnotation.getSimpleName()))
				{
					propertiesWithAnnotationNames.put(c10nAnnotation.getSimpleName(),
						new ArrayList<Properties>());

				}
				propertiesWithAnnotationNames.get(c10nAnnotation.getSimpleName()).add(properties);
			}
		}
		return propertiesWithAnnotationNames;
	}

	public static Map<String, List<Properties>> getMapForC10NInterfaceGeneration(final File rootDir)
		throws IOException
	{
		final Collection<File> propertiesFiles = findFiles(rootDir);
		return getMapForC10NInterfaceGeneration(propertiesFiles);
	}

	public static Map<Properties, Locale> mapWithLocales(final Collection<File> propertiesFiles)
		throws IOException
	{
		final Map<Properties, Locale> propertiesToLocales = new HashMap<>();
		for (final File propertiesFile : propertiesFiles)
		{
			final Properties properties = PropertiesUtils.loadProperties(propertiesFile);
			final Locale locale = PropertiesUtils.getLocale(propertiesFile);
			propertiesToLocales.put(properties, locale);
		}
		return propertiesToLocales;
	}


	public static Collection<File> validate(final File rootDir) throws IOException
	{
		// 1. Replace all invalid characters.
		// Collection<File> propertiesFiles =
		// PropertiesNormalizer.findPropertiesFilesWithInvalidCharacters(rootDir);
		// 2. Find duplicate keys.
		// for (File propertiesFile : propertiesFiles) {
		// KeyValueLists keyValueLists =
		// DuplicatePropertiesKeyFinder.findDuplicateKeys(propertiesFile);
		// // TODO implement the algorithm for it...
		// }
		// // 3. Find duplicate values.
		// for (File propertiesFile : propertiesFiles) {
		// Properties properties = PropertiesUtils.loadProperties(propertiesFile);
		// Map<String, List<String>> redundantValues
		// =PropertiesUtils.findRedundantValues(properties);
		// // TODO implement the algorithm for it...
		// }
		// return propertiesFiles;
		return null;
	}

}