package fr.upem.lgtools.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.*;

public class ExternalData {
    Map<String, Double> compositionality = new HashMap<String, Double>();

    public ExternalData(String filename) {

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));

            String currentLine;
            while ((currentLine = reader.readLine()) != null) { 
                Scanner s = new Scanner(currentLine).useDelimiter("\t");
		        compositionality.put(s.next(), s.nextDouble());
            }

        } catch (IOException e) {
                e.printStackTrace();
        }

    }
}
