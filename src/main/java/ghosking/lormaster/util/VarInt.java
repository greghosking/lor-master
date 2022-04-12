package ghosking.lormaster.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Derived from https://github.com/RiotGames/LoRDeckCodes/blob/main/LoRDeckCodes/VarintTranslator.cs.
 */
public class VarInt {

    private static final int ALL_BUT_MSB = 0x7F;
    private static final int JUST_MSB = 0x80;

    public static int pop(List<Byte> bytes) {
        int result = 0;
        int currentShift = 0;
        int bytesPopped = 0;

        for (int i = 0; i < bytes.size(); i++) {
            bytesPopped++;
            int current = bytes.get(i) & ALL_BUT_MSB;
            result |= current << currentShift;

            if ((bytes.get(i) & JUST_MSB) != JUST_MSB) {
                bytes.subList(0, bytesPopped).clear();
                return result;
            }

            currentShift += 7;
        }

        throw new IllegalArgumentException("Byte array did not contain valid VarInts.");
    }

    public static List<Integer> get(int value) {
        Integer[] data = new Integer[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        List<Integer> buffer = new ArrayList<>(Arrays.asList(data));
        int currentIndex = 0;

        if (value == 0) {
            List<Integer> result = new ArrayList<>();
            result.add(0);
            return result;
        }

        while (value != 0) {
            int byteVal = value & ALL_BUT_MSB;
            value >>>= 7;

            if (value != 0) byteVal |= JUST_MSB;

            buffer.set(currentIndex++, byteVal);
        }

        return buffer.subList(0, currentIndex);
    }
}
