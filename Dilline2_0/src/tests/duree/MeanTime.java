package tests.duree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MeanTime {


    public static void calculateAndPrintMeanTime(String filePath) {
        double times = 0;
        int cpt = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length > 1) {  // Assure that there is a number before "secondes"
                    try {
                        double time = Double.parseDouble(parts[0]);
                        times += time;
                        cpt++;
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid number format: " + parts[0]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return; // Return early if there's an error reading the file
        }

        if (cpt > 0) {
            double mean = times / cpt;
            System.out.println("Le temps moyen est: " + mean + " secondes");
        } else {
            System.out.println("Aucune donnée valide pour calculer le temps moyen.");
        }
    }

    public static void main(String[] args) {
        MeanTime.calculateAndPrintMeanTime("/home/fan/git/Projet_CPS_Diline/Dilline2_0/execution_times_emptyC.txt");
        MeanTime.calculateAndPrintMeanTime("/home/fan/git/Projet_CPS_Diline/Dilline2_0/execution_times_directionalC.txt");
        MeanTime.calculateAndPrintMeanTime("/home/fan/git/Projet_CPS_Diline/Dilline2_0/execution_times_floodingC.txt");

    }
}
