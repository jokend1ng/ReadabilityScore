package readability;

import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Pattern;


public class Main {

    public static void main(String[] args) {
        try {
            String filePath;
            if (args.length > 0) {
                filePath = args[0];
            } else {
                System.out.print("Enter a File Path: ");
                filePath = new Scanner(System.in).next();
            }
            String text = readFileAsString(filePath).toLowerCase();
            runReadabilityCalculator(text);
        } catch (IOException exception) {
            System.out.println(exception.getMessage() + " was not found");
        } finally {
            System.out.println("\nThanks for using my Readability Calculator");
        }
    }

    public static void runReadabilityCalculator(String text) {
        try {
            // The lines below retrieve basic information about the text
            int sentenceCount = text.split("[.?!][\\s]+").length;
            int wordCount = text.split("[\\s]+").length;


            int charCount = text.replaceAll("[\\s]+", "").length();
            int syllableCount = 0;
            int polySyllableCount = 0;
            for (String word : text.split("[,:'\"]?[\\s]+[,:'\"]?")) {
                if (word.charAt(word.length() - 1) == 'e') {
                    word = word.substring(0, word.length() - 1);
                }
                int syllables = (int) Pattern.compile("[aeiouy]+").matcher(word).results().count();
                polySyllableCount += syllables >= 3 ? 1 : 0;
                syllableCount += syllables == 0 ? 1 : syllables;
            }

            double scoreARI = (4.71 * charCount / wordCount) +
                    (0.5 * wordCount / sentenceCount) - 21.43;
            double scoreFK = (0.39 * wordCount / sentenceCount) +
                    (11.8 * syllableCount / wordCount) - 15.59;
            double scoreSMOG = 1.043 * Math.sqrt(polySyllableCount * 30f / sentenceCount) + 3.1291;
            double scoreCL = 0.0588 * ((double) charCount / wordCount * 100) -
                    0.296 * ((double) sentenceCount / wordCount * 100) - 15.8;

            // The lines below retrieve the age based on the different readability scores
            int ageARI = getAge(scoreARI);
            int ageFK = getAge(scoreFK);
            int ageSMOG = getAge(scoreSMOG);
            int ageCL = getAge(scoreCL);

            // The lines below print out information about the calculations
            System.out.println("Words: " + wordCount);
            System.out.println("Sentences: " + sentenceCount);
            System.out.println("Characters: " + charCount);
            System.out.println("Syllables: " + syllableCount);
            System.out.println("Polysyllables: " + polySyllableCount);
            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            String desiredScore = new Scanner(System.in).next();
            System.out.println();

            switch (desiredScore.toLowerCase()) {
                case "all" -> {
                    System.out.printf("Automated Readability Index: %f (about %s year olds)",
                            scoreARI, ageARI);
                    System.out.printf("\nFlesch-Kincaid readability tests: %f (about %s year olds)",
                            scoreFK, ageFK);
                    System.out.printf("\nSimple Measure of Gobbledygook: %f (about %s year olds)",
                            scoreSMOG, ageSMOG);
                    System.out.printf("\nColeman-Liau index: %f (about %s year olds)",
                            scoreCL, ageCL);
                    double avg = (ageARI + ageARI + ageSMOG + ageCL) / 4f;
                    System.out.printf("\n\nThis text should be understood in average by %f year olds.", avg);
                }
                case "ari" -> System.out.printf("Automated Readability Index: %f (about %s year olds)",
                        scoreARI, ageARI);
                case "fk" -> System.out.printf("Flesch-Kincaid readability tests: %f (about %s year olds)",
                        scoreFK, ageFK);
                case "smog" -> System.out.printf("Simple Measure of Gobbledygook: %f (about %s year olds)",
                        scoreSMOG, ageSMOG);
                case "cl" -> System.out.printf("Coleman-Liau index: %f (about %s year olds)",
                        scoreCL, ageCL);
                default -> System.out.println("I can't calculate that :(");
            }
        } catch (ArithmeticException exception) {
            System.out.println("Seems like the file was empty or there was something wrong with the arithmetic");
        }
    }


    public static String readFileAsString(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }


    public static int getAge(double grade) {
        int score = (int) Math.round(grade);
        if (score < 3) {
            return (score + 5);
        } else if (score < 13) {
            return score + 6;
        } else if (score < 14) {
            return score + 11;
        } else {
            return 30;
        }
    }
}