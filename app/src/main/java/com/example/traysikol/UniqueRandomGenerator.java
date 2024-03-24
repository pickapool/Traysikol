package com.example.traysikol;

import java.util.Random;

public class UniqueRandomGenerator {
    private int previousNumber;
    private Random random;


    public UniqueRandomGenerator() {
        random = new Random();
        previousNumber = -1; // Initialize to an invalid number
    }

    public int generateUniqueRandom() {
        int currentNumber;
        do {
            currentNumber = random.nextInt(4) + 1; // Generates random number between 1 and 4
        } while (currentNumber == previousNumber); // Loop until a different number is generated

        previousNumber = currentNumber; // Update the previous number
        return currentNumber;
    }
}
