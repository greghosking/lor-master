package ghosking.lormaster.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Derived from https://github.com/RiotGames/LoRDeckCodes/blob/main/LoRDeckCodes/Base32.cs.
 */
public final class Base32 {

    private static final ArrayList<Character> CHARACTERS = new ArrayList<>(
            Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                          'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '2', '3', '4', '5', '6', '7')
    );
    private static final int MASK = CHARACTERS.size() - 1;
    private static final int SHIFT = Integer.numberOfTrailingZeros(CHARACTERS.size());
    private static final HashMap<Character, Integer> CHARACTER_MAP = new HashMap<>() {{
        for (int i = 0; i < CHARACTERS.size(); i++) {
            put(CHARACTERS.get(i), i);
        }
    }};
    private static final String SEPARATOR = "-";

    public static String encode(byte[] data) {
        if (data.length == 0) {
            return "";
        }

        if (data.length >= (1 << 28)) {
            throw new ArrayIndexOutOfBoundsException("Array is too long for this operation.");
        }

        int outputLength = (data.length * 8 + SHIFT - 1) / SHIFT;
        StringBuilder result = new StringBuilder(outputLength);

        int buffer = data[0];
        int next = 1;
        int bitsRemaining = 8;
        while (bitsRemaining > 0 || next < data.length) {
            if (bitsRemaining < SHIFT) {
                if (next < data.length) {
                    buffer <<= 8;
                    buffer |= (data[next++] & 0xff);
                    bitsRemaining += 8;
                }
                else {
                    int pad = SHIFT - bitsRemaining;
                    buffer <<= pad;
                    bitsRemaining += pad;
                }
            }
            int index = MASK & (buffer >>> (bitsRemaining - SHIFT));
            bitsRemaining -= SHIFT;
            result.append(CHARACTERS.get(index));
        }

        return result.toString();
    }

    public static byte[] decode(String code) {
        String encoded = code.trim().replace(SEPARATOR, "");
        encoded = encoded.replaceAll("[=]*$", "").toUpperCase();

        if (encoded.length() == 0) {
            return new byte[0];
        }

        int encodedLength = encoded.length();
        int decodedLength = encodedLength * SHIFT / 8;
        byte[] result = new byte[decodedLength];

        int buffer = 0;
        int next = 0;
        int bitsRemaining = 0;
        for (char c : encoded.toCharArray()) {
            if (!CHARACTER_MAP.containsKey(c)) {
                throw new IllegalArgumentException("Illegal character: " + c);
            }
            buffer <<= SHIFT;
            buffer |= CHARACTER_MAP.get(c) & MASK;
            bitsRemaining += SHIFT;
            if (bitsRemaining >= 8) {
                result[next++] = (byte) (buffer >>> (bitsRemaining - 8));
                bitsRemaining -= 8;
            }
        }

        return result;
    }
}
