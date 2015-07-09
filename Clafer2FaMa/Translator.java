package Clafer2FaMa;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Translator {

	final static String FILE_INPUT_NAME = "c:\\Users\\Cristian\\git\\FaMA\\Metamodels\\FaMaFeatureModel\\src\\Clafer2FaMa\\linux.cfr";
	final static String FILE_OUTPUT_NAME = "c:\\Users\\Cristian\\git\\FaMA\\Metamodels\\FaMaFeatureModel\\src\\Clafer2FaMa\\linux.fama";

	
    final static Charset ENCODING = StandardCharsets.UTF_8;
    static Feature mainFeature;
    
    
	public static void main(String[] args) {
	    mainFeature = new Feature();
	    
	    try{
	        mainFeature.readFile(FILE_INPUT_NAME, ENCODING);
	        mainFeature.writeFaMa(FILE_OUTPUT_NAME);
	    }
	    catch(Exception ex){
	    	System.out.println(ex.toString());
	    }
	}
	
	 private static void log(Object aMsg){
		    System.out.println(String.valueOf(aMsg));
		  }
}
