package de.alpharogroup.c10n.iface.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import c10n.annotations.De;
import c10n.annotations.En;
import c10n.annotations.Fr;
import c10n.annotations.It;
import c10n.annotations.Ja;
import c10n.annotations.Ko;
import c10n.annotations.Ru;
import c10n.annotations.Zh;

import com.google.common.io.Files;

import de.alpharogroup.file.FileExtension;
import de.alpharogroup.lang.PropertiesUtils;
import de.alpharogroup.lang.model.AnnotationModel;
import de.alpharogroup.lang.model.ClassModel;
import de.alpharogroup.lang.model.MethodModel;
import de.alpharogroup.locale.Locales;
import de.alpharogroup.string.StringExtensions;

public class C10NInterfaceGenerator
{
	// Holds the data for the inteface creation.
	Map<String, ClassModel> interfaces = new LinkedHashMap<String, ClassModel>();

	// Holds the AnnotationName for the locale and the corresponding properties object.
	Map<String, Properties> propertiesWithAnnotationNames;

	@SuppressWarnings("serial")
	Map<String, Class<?>> stringToAnnotationClass = new HashMap<String, Class<?>>()
	{
		{
			put("En", En.class);
			put("De", De.class);
			put("Fr", Fr.class);
			put("It", It.class);
			put("Ja", Ja.class);
			put("Ko", Ko.class);
			put("Ru", Ru.class);
			put("Zh", Zh.class);
		}
	};
	@SuppressWarnings("serial")
	Map<Class<?>, Locale> annotationToLocale = new HashMap<Class<?>, Locale>()
	{
		{
			put(En.class, Locale.ENGLISH);
			put(De.class, Locale.GERMAN);
			put(Fr.class, Locale.FRENCH);
			put(It.class, Locale.ITALIAN);
			put(Ja.class, Locale.JAPANESE);
			put(Ko.class, Locale.KOREAN);
			put(Ru.class, Locales.RUSSIAN);
			put(Zh.class, Locale.CHINESE);
		}
	};
	@SuppressWarnings("serial")
	Map<Locale, Class<?>> localeToannotation = new HashMap<Locale, Class<?>>()
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

	String sourceFolderForInterfaces;

	public C10NInterfaceGenerator(final Map<String, Properties> propertiesWithAnnotationNames,
		final String sourceFolderForInterfaces)
	{
		if ((propertiesWithAnnotationNames == null) || propertiesWithAnnotationNames.isEmpty())
		{
			throw new IllegalArgumentException(
				"propertiesWithAnnotationNames should not be null or empty.");
		}
		if ((sourceFolderForInterfaces == null) || sourceFolderForInterfaces.isEmpty())
		{
			throw new IllegalArgumentException(
				"sourceFolderForInterfaces should not be null or empty.");
		}
		this.propertiesWithAnnotationNames = propertiesWithAnnotationNames;
		this.sourceFolderForInterfaces = sourceFolderForInterfaces;
		init();
	}

	public void generate() throws IOException
	{
		for (final Map.Entry<String, ClassModel> entry : interfaces.entrySet())
		{
			final ClassModel classModel = entry.getValue();

			final VelocityContext context = new VelocityContext();
			context.put("model", classModel);
			final Template daoClassTemplate = Velocity
				.getTemplate("src/main/resources/c10nInterface.vm");
			System.out.println(classModel);

			final String packageAndClassName = classModel.getClassName().equalsIgnoreCase(
				classModel.getPackageName()) ? classModel.getPackageName() + "/"
				+ classModel.getClassName() : classModel.getPackageName().replace('.', '/') + "/"
				+ classModel.getClassName();
			final File classFile = new File(sourceFolderForInterfaces + packageAndClassName
				+ FileExtension.JAVA.getExtension());
			Files.createParentDirs(classFile);
			if (!classFile.exists())
			{
				classFile.createNewFile();
			}
			final BufferedWriter writer = new BufferedWriter(new FileWriter(classFile));

			daoClassTemplate.merge(context, writer);
			writer.flush();
			writer.close();

		}
	}

	private void getClassModels(final Map<String, List<String>> matchedPrefixes,
		final String localeAnnotationName, final Map<String, ClassModel> interfaces,
		final Properties ruProperties)
	{
		for (final Map.Entry<String, List<String>> entry : matchedPrefixes.entrySet())
		{
			final String key = entry.getKey();
			final List<String> propertyKeyValues = entry.getValue();
			ClassModel classModel = null;
			Map<String, MethodModel> methods = null;
			final int lastIndex = key.lastIndexOf(".");
			String packageName = "";
			String interfaceName = null;
			String methodName = "";
			MethodModel method = null;
			if (0 < lastIndex)
			{
				interfaceName = StringExtensions.firstCharacterToUpperCase(key.substring(
					lastIndex + 1, key.length()));
				if (interfaces.containsKey(interfaceName))
				{
					classModel = interfaces.get(interfaceName);
					methods = classModel.getMethods();
					for (final String value : propertyKeyValues)
					{
						methodName = value.substring(key.length() + 1, value.length());

						method = methods.get(methodName);
						if (method != null)
						{
							final String propertyValue = (String)ruProperties.get(value);
							final AnnotationModel annotation = new AnnotationModel();
							method.getMethodAnnotations().add(annotation);
							annotation.setName(localeAnnotationName);
							annotation.setValue(propertyValue);
							method.setParameters(PropertiesUtils
								.getPropertyParameters(propertyValue));
						}
						else
						{
							System.err.println("Method does not exists in classmodel...\n"
								+ "classname:" + classModel.getClassName() + "\n" + "packageName:"
								+ classModel.getPackageName());
						}
					}
				}
				else
				{
					classModel = new ClassModel();
					classModel.setClassName(interfaceName);
					methods = new LinkedHashMap<String, MethodModel>();
					classModel.setMethods(methods);
					packageName = key.substring(0, lastIndex);
					classModel.setPackageName(packageName);
					for (final String value : propertyKeyValues)
					{
						methodName = value.substring(key.length() + 1, value.length());
						method = new MethodModel();
						method.setMethodAnnotations(new ArrayList<AnnotationModel>());
						methods.put(methodName, method);
						method.setMethodName(methodName);
						final String propertyValue = (String)ruProperties.get(value);
						final AnnotationModel annotation = new AnnotationModel();
						method.getMethodAnnotations().add(annotation);
						annotation.setName(localeAnnotationName);
						annotation.setValue(propertyValue);
						method.setParameters(PropertiesUtils.getPropertyParameters(propertyValue));
					}
					final String cn = StringExtensions.firstCharacterToUpperCase(classModel
						.getClassName());
					classModel.setClassName(cn);
					interfaces.put(interfaceName, classModel);
				}

			}
			else
			{
				interfaceName = key;
				if (interfaces.containsKey(interfaceName))
				{
					classModel = interfaces.get(interfaceName);
					packageName = interfaceName.toLowerCase();
					classModel.setPackageName(packageName);
					methods = classModel.getMethods();
					for (final String value : propertyKeyValues)
					{
						methodName = value;
						method = methods.get(methodName);
						final String propertyValue = (String)ruProperties.get(value);
						final AnnotationModel annotation = new AnnotationModel();
						method.getMethodAnnotations().add(annotation);
						annotation.setName(localeAnnotationName);
						annotation.setValue(propertyValue);
						method.setParameters(PropertiesUtils.getPropertyParameters(propertyValue));
					}
				}
				else
				{
					interfaceName = key;
					classModel = new ClassModel();
					packageName = interfaceName.toLowerCase();
					classModel.setPackageName(packageName);
					methods = new LinkedHashMap<String, MethodModel>();
					classModel.setMethods(methods);
					classModel.setClassName(interfaceName);
					for (final String value : propertyKeyValues)
					{
						methodName = value;
						method = new MethodModel();
						method.setMethodAnnotations(new ArrayList<AnnotationModel>());
						methods.put(methodName, method);
						method.setMethodName(methodName);
						final String propertyValue = (String)ruProperties.get(value);
						final AnnotationModel annotation = new AnnotationModel();
						method.getMethodAnnotations().add(annotation);
						annotation.setName(localeAnnotationName);
						annotation.setValue(propertyValue);
						method.setParameters(PropertiesUtils.getPropertyParameters(propertyValue));
					}
					final String cn = StringExtensions.firstCharacterToUpperCase(classModel
						.getClassName());
					classModel.setClassName(cn);
					interfaces.put(interfaceName, classModel);
				}
			}
		}
	}


	private void init()
	{
		Map<String, List<String>> matchedPrefixes;
		for (final Map.Entry<String, Properties> entry : this.propertiesWithAnnotationNames
			.entrySet())
		{
			final String key = entry.getKey();
			final Properties value = entry.getValue();
			matchedPrefixes = PropertiesUtils.getMatchedPrefixLists(value);
			getClassModels(matchedPrefixes, key, interfaces, value);
		}
	}
}
