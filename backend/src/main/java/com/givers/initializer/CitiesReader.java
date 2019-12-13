package com.givers.initializer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CitiesReader {
   private static final String CITIES_FILE_NAME = "cities.csv";
   
   
   // get file from classpath, resources folder
   private File getFileFromResources() {
       ClassLoader classLoader = getClass().getClassLoader();
       URL resource = classLoader.getResource(CITIES_FILE_NAME);
       
       if (resource == null) {
           throw new IllegalArgumentException("File can not be found!");
       } else {
           return new File(resource.getFile());
       }
   }
   
	public final List<String> readCities() {
		String line = "";
		BufferedReader br = null;
		List<String> cities = new ArrayList<>();
		try {
			br = new BufferedReader(new FileReader(this.getFileFromResources()));

			while ((line = br.readLine()) != null) {
				cities.add(line.trim().split(",")[0].replaceAll("\"", ""));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return cities;
	}
}
