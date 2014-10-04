package de.alpharogroup.c10n.iface.generator;


import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import net.sourceforge.jaulp.collections.SortedProperties;
import net.sourceforge.jaulp.file.search.PathFinder;

import org.testng.annotations.Test;

import c10n.C10N;
import c10n.C10NConfigBase;
import c10n.annotations.DefaultC10NAnnotations;
import de.alpharogroup.c10n.iface.generator.util.PropertiesValidation;


public class C10NInterfaceGeneratorTest {

	@Test(enabled=true)
	public void testGenerate() throws IOException {
		Map<String, Properties> propertiesWithAnnotationNames = new LinkedHashMap<String, Properties>();
		Properties enProperties = new SortedProperties();

		enProperties.put("com", "Hello, {0} {1} {2}!");
		enProperties.put("com.example.gui.window.title", "Hello, {0}!");
		enProperties.put("com.example.gui.window.buttons.ok", "OK");
		enProperties.put("com.example.gui.window.buttons.cancel", "Cancel");
		
		String localeAnnotationName = "En";
		propertiesWithAnnotationNames.put(localeAnnotationName, enProperties);
		
		Properties ruProperties = new SortedProperties();

		ruProperties.put("com", "Привет, {0} {1} {2}!");
		ruProperties.put("com.example.gui.window.title", "Привет, {0}!");
		ruProperties.put("com.example.gui.window.buttons.ok", "Да");
		ruProperties.put("com.example.gui.window.buttons.cancel", "Отмена");

		localeAnnotationName = "Ru";
		propertiesWithAnnotationNames.put(localeAnnotationName, ruProperties);
		
		C10NInterfaceGenerator generator = new C10NInterfaceGenerator(propertiesWithAnnotationNames, "src/main/java/");
		generator.generate();	

	}

	@Test(enabled=false)
	public void testProperties() throws IOException {
		File rootDir = PathFinder.getSrcTestResourcesDir();
		
		Map<String, List<Properties>> propertiesWithAnnotationNames = PropertiesValidation
				.getMapForC10NInterfaceGeneration(rootDir);
		for(Entry<String, List<Properties>> entry : propertiesWithAnnotationNames.entrySet()){
			String key = entry.getKey();
			List<Properties> propertiesList = entry.getValue();
			for(Properties p : propertiesList){
				Map<String, Properties> propWithAnnotationNames = new LinkedHashMap<String, Properties>();
				propWithAnnotationNames.put(key, p);
				C10NInterfaceGenerator generator = new
				 C10NInterfaceGenerator(propWithAnnotationNames,
				 "src/main/generated/");
				 generator.generate();
			}
		}		 
	}

	public void testMessage(){

		C10N.configure(new C10NConfigBase(){
		    @Override
		    public void configure() {
		      install(new DefaultC10NAnnotations());

		    }
		  });
		// uncomment if intefaces are generated...
//		Window msg = C10N.get(Window.class, Locale.ENGLISH);
//		String result = msg.title("bla");
//		System.out.println(result);
//		
//		Buttons buttons = C10N.get(Buttons.class, Locale.ENGLISH);
//		result = buttons.cancel();
//		System.out.println(result);
//		
//		buttons = C10N.get(Buttons.class, new Locale("ru"));
//		
//		result = buttons.cancel();
//		System.out.println(result);


	}

}

