/**
 * The list of cards are then encoded according to the following scheme:
 *
 * 1. Cards are grouped together based on how many copies of the card are in the deck (e.g., cards with three copies are grouped together,
 *    cards with two copies are grouped together, and cards with a single copy are grouped together).
 *
 * 2. Within those groups, lists of cards are created which share the same set AND faction.
 *
 * 3. The set/faction lists are ordered by increasing length. The contents of the set/faction lists are ordered alphanumerically.
 *
 * 4. Variable length integer (varints) (big endian) bytes for each ordered group of cards are written into the byte array according to the following convention:
 *    [how many lists of set/faction combination have three copies of a card]
 *        [how many cards within this set/faction combination follow]
 *            [set]
 *            [faction]
 *                [card number]
 *                [card number]
 *                ...
 *        [how many cards within this set/faction combination follow]
 *            [set]
 *            [faction]
 *                [card number]
 *                [card number]
 *                ...
 *    [repeat for the groups of two copies of a card]
 *    [repeat for the groups of a single copy of a card]
 *
 * 5. The resulting byte array is base32 encoded into a string.
 */
package ghosking.lormaster;

import org.apache.commons.codec.binary.Base32;

import java.util.*;

public class DeckCodeParser {


    private static final String                  SEPARATOR = "-";
    private static final char[]                  CHARS     = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();
    private static final int                     MASK      = CHARS.length - 1;
    private static final int                     SHIFT     = Integer.numberOfTrailingZeros(CHARS.length);
    private static final Map<Character, Integer> CHAR_MAP  = new HashMap<Character, Integer>()
    {{
        for (int i = 0; i < CHARS.length; i++)
        {
            put(CHARS[i], i);
        }
    }};
    public static String padLeft(String input, String val, int length)
    {
        StringBuilder sb = new StringBuilder(input);
        while (sb.length() < length)
        {
            sb.insert(0, val);
        }

        return sb.toString();
    }

    public static String encode(byte[] data)
    {
        if (data.length == 0)
        {
            return "";
        }

        if (data.length >= (1 << 28))
        {
            throw new ArrayIndexOutOfBoundsException("Array is too long for this");
        }

        int           outputLength = (data.length * 8 + SHIFT - 1) / SHIFT;
        StringBuilder result       = new StringBuilder(outputLength);

        int buffer   = data[0];
        int next     = 1;
        int bitsLeft = 8;
        while (bitsLeft > 0 || next < data.length)
        {
            if (bitsLeft < SHIFT)
            {
                if (next < data.length)
                {
                    buffer <<= 8;
                    buffer |= (data[next++] & 0xff);
                    bitsLeft += 8;
                } else
                {
                    int pad = SHIFT - bitsLeft;
                    buffer <<= pad;
                    bitsLeft += pad;
                }
            }
            int index = MASK & (buffer >>> (bitsLeft - SHIFT));
            bitsLeft -= SHIFT;
            result.append(CHARS[index]);
        }

        if (false)
        {
            int padding = 8 - (result.length() % 8);
            if (padding > 0)
            {
                result.append(padLeft("", "=", padding == 8 ? 0 : padding));
            }
        }

        return result.toString();
    }

    public static String encodeBoxed(List<Integer> result)
    {
        byte[] output = new byte[result.size()];

        for (int i = 0; i < result.size(); i++)
        {
            output[i] = result.get(i).byteValue();
        }

        return encode(output);
    }


    static final int excludingMostSignificantBit = 0x7f;
    static final int mostSignificantBit = 0x80;

    public static List<Integer> getVarInt(int value) {

        // Set up a byte buffer and initialize its ten slots to 0.
        ArrayList<Integer> byteBuffer = new ArrayList<>();
        for (int i = 0; i < 10; i++)
            byteBuffer.add(0);

        if (value == 0)
        {
            return Collections.singletonList(0);
        }

        // Maintain an effective size of the VarInt.
        int size = 0;
        while (value != 0) {

            // Break the binary value into groups of 7 bits starting from the lowest significant bit.
            int byteValue = value & excludingMostSignificantBit;
            value >>>= 7;

            if (value != 0)
                byteValue |= mostSignificantBit;

            byteBuffer.set(size, byteValue);
            size++;
        }

        return byteBuffer.subList(0, size);
    }

    public static ArrayList<Integer> encodeGroup(ArrayList<HashMap<String, ArrayList<String>>> group) {

        String[] factionsArr = { "DE", "FR", "IO", "NX", "PZ", "SI", "BW", "SH", "", "MT", "BC" };
        ArrayList<String> factions = new ArrayList(List.of(factionsArr));

        // Display which group is now being encoded.
        System.out.println();
        System.out.println("ENCODING GROUP: " + group);

        ArrayList<Integer> result = new ArrayList<>();

        int format = 1;
        int version = 1;
        int formatVersion = ((format << 4) | (version & 0xF));
        result.add(formatVersion);
        String deckCode = encodeBoxed(result);

        // Add the number of set/faction lists to the result.
        result.addAll(getVarInt(group.size()));
//        ArrayList<Integer> result = new ArrayList<>(getVarInt(group.size()));
        System.out.println("GROUP CONSISTS OF " + group.size() + " SET/FACTION LISTS");
        deckCode = encodeBoxed(result);

        // For each set/faction list in the group...
        for (HashMap<String, ArrayList<String>> setFactionGroup : group) {

            // Get the set/faction key from the hash map and the size of the list of card numbers for the set/faction.
            String setFaction = (String) setFactionGroup.keySet().toArray()[0];
//            result.addAll(getVarInt(setFactionGroup.get(setFaction).size()));
            result.addAll(getVarInt(6));
            deckCode = encodeBoxed(result);

            System.out.println("THERE ARE " + setFactionGroup.get(setFaction).size() + " CARDS IN THE SET/FACTION " +
                    "COMBINATION " + setFaction);

            // Parse the set/faction key for the individual set and faction integer representations and add those
            // to the result.
            int set = Integer.parseInt(setFaction.substring(0, 2));
            int faction = factions.indexOf(setFaction.substring(2, 4));
            result.addAll(getVarInt(set));
            deckCode = encodeBoxed(result);

            result.addAll(getVarInt(6));
            deckCode = encodeBoxed(result);

            // Then, for each card number in the set/faction list...
            for (String cardNumber : setFactionGroup.get(setFaction)) {
                result.addAll(getVarInt(Integer.parseInt(cardNumber)));
                System.out.println("CARD NUMBER: " + Integer.parseInt(cardNumber));
            }
        }

        return result;
    }

    public static void main(String[] args) {

        // Example deck: Plunder.
        String expectedDeckCode = "CECQMAQGCQQCELJ2HQAQGBQIAICQCAIGAECAMCQBAIAQEAQCAIDAEEQBAQAQUAIBAMARM";
        String encodedDeckCode;

        String[] deck = {
                "02BW032", "02BW032", "02BW032", // 3 copies of Gangplank
                "02FR002", "02FR002", "02FR002", // 3 copies of Sejuani
                "02BW060", "02BW060", "02BW060", // 3 copies of Crackshot Corsair
                "02BW058", "02BW058", "02BW058", // 3 copies of Jagged Butcher
                "03BW008", "03BW008", "03BW008", // 3 copies of Fortune Croaker
                "05FR006", "05FR006", "05FR006", // 3 copies of Tusk Speaker
                "02BW034", "02BW034", "02BW034", // 3 copies of Monkey Idol
                "05FR001", "05FR001", "05FR001", // 3 copies of Murkwolf Shaman
                "02BW002", "02BW002",            // 2 copies of The Dreadway
                "02BW020", "02BW020", "02BW020", // 3 copies of Warning Shot
                "02BW018", "02BW018",            // 2 copies of Parrrley
                "04FR010", "04FR010",            // 2 copies of Three Sisters
                "02BW045", "02BW045", "02BW045", // 3 copies of Make it Rain
                "04BW010", "04BW010", "04BW010", // 3 copies of Monster Harpoon
                "03FR022"                        // 1 copy of Feel The Rush
        };
        System.out.print("CARD CODES: ");
        for (String cardCode : deck)
            System.out.print(cardCode + " ");
        System.out.println();

        // Count how many times each card appears in the deck.
        HashMap<String, Integer> cardCounts = new HashMap<>();
        for (String cardCode : deck) {
            if (cardCounts.containsKey(cardCode))
                cardCounts.put(cardCode, cardCounts.get(cardCode) + 1);
            else
                cardCounts.put(cardCode, 1);
        }
        System.out.println("CARD COUNTS: " + cardCounts);

        // Group cards together based on how many copies of the card are in the deck (cards with three copies are grouped
        // together, cards with two copies are grouped together, and cards with a single copy are grouped together).
        HashMap<Integer, ArrayList<String>> cardsGroupedByCopies = new HashMap<>();
        cardsGroupedByCopies.put(3, new ArrayList<>());
        cardsGroupedByCopies.put(2, new ArrayList<>());
        cardsGroupedByCopies.put(1, new ArrayList<>());

        for (Map.Entry<String, Integer> cardCount : cardCounts.entrySet())
            cardsGroupedByCopies.get(cardCount.getValue()).add(cardCount.getKey());

        System.out.println("CARDS GROUPED BY COPIES: " + cardsGroupedByCopies);

        // Then, for each group, organize its contents into lists that share the same set number and faction.
        HashMap<Integer, ArrayList<HashMap<String, ArrayList<String>>>> formattedCardGroups = new HashMap<>();
        formattedCardGroups.put(3, new ArrayList<>());
        formattedCardGroups.put(2, new ArrayList<>());
        formattedCardGroups.put(1, new ArrayList<>());

        for (Map.Entry<Integer, ArrayList<String>> unformattedCardGroup : cardsGroupedByCopies.entrySet()) {

            ArrayList<HashMap<String, ArrayList<String>>> currentGroup = formattedCardGroups.get(unformattedCardGroup.getKey());
            for (String cardCode : unformattedCardGroup.getValue()) {
                String setFaction = cardCode.substring(0, 4);
                String cardNumber = cardCode.substring(4, 7);

                // Search the current group to see if this combination of set and faction is already in the group.
                int indexOfSetFaction = -1;
                for (int i = 0; i < currentGroup.size(); i++)
                    if (currentGroup.get(i).containsKey(setFaction))
                        indexOfSetFaction = i;
                // If it is not, create a new element to store this combination and card number.
                if (indexOfSetFaction == -1) {
                    currentGroup.add(new HashMap<>());
                    currentGroup.get(currentGroup.size() - 1).put(setFaction, new ArrayList<>());
                    currentGroup.get(currentGroup.size() - 1).get(setFaction).add(cardNumber);
                }
                // If it is, add the card number to the list of card numbers associated with the set and faction.
                else
                    currentGroup.get(indexOfSetFaction).get(setFaction).add(cardNumber);
            }
        }
        System.out.println("FORMATTED CARD GROUPS UNSORTED: " + formattedCardGroups);

        // These lists should then be ordered in increasing size and their contents ordered alphanumerically.
        for (Map.Entry<Integer, ArrayList<HashMap<String, ArrayList<String>>>> unsortedCardGroup : formattedCardGroups.entrySet()) {

            ArrayList<HashMap<String, ArrayList<String>>> currentGroup = formattedCardGroups.get(unsortedCardGroup.getKey());

            // Sort the current list increasing by size.
            for (int i = 0; i < currentGroup.size() - 1; i++) {

                int indexOfMin = i;
                for (int j = i + 1; j < currentGroup.size(); j++) {
                    // Since each hash map in this list is guaranteed to have exactly one key, it is safe to do this to get the key.
                    String keyJ = (String) currentGroup.get(j).keySet().toArray()[0];
                    String keyMin = (String) currentGroup.get(indexOfMin).keySet().toArray()[0];
                    int sizeAtJ = currentGroup.get(j).get(keyJ).size();
                    int sizeAtIndexOfMin = currentGroup.get(indexOfMin).get(keyMin).size();

                    if (sizeAtJ < sizeAtIndexOfMin)
                        indexOfMin = j;
                    else if (sizeAtJ == sizeAtIndexOfMin) {
                        if (keyJ.compareTo(keyMin) < 1)
                            indexOfMin = j;
                    }
                }
                // Swap the current element with the smallest.
                HashMap<String, ArrayList<String>> placeholder = currentGroup.get(i);
                currentGroup.set(i, currentGroup.get(indexOfMin));
                currentGroup.set(indexOfMin, placeholder);
            }

            // After sorting the list, sort the inner lists alphanumerically.
            for (HashMap<String, ArrayList<String>> stringArrayListHashMap : currentGroup) {

                String key = (String) stringArrayListHashMap.keySet().toArray()[0];
                ArrayList<String> setFactionCardNumbers = stringArrayListHashMap.get(key);
                for (int j = 0; j < setFactionCardNumbers.size() - 1; j++) {

                    int indexOfMin = j;
                    for (int k = j + 1; k < setFactionCardNumbers.size(); k++)
                        if (setFactionCardNumbers.get(k).compareTo(setFactionCardNumbers.get(indexOfMin)) < 1)
                            indexOfMin = k;
                    String placeholder = setFactionCardNumbers.get(j);
                    setFactionCardNumbers.set(j, setFactionCardNumbers.get(indexOfMin));
                    setFactionCardNumbers.set(indexOfMin, placeholder);
                }
            }
        }
        System.out.println("FORMATTED CARD GROUPS SORTED: " + formattedCardGroups);


        // Create an integer array to store the resulting VarInts before encoding.
        ArrayList<Integer> result = new ArrayList<>();

        int format = 1;
        int version = 1;
        int formatVersion = ((format << 4) | (version & 0xF));
        result.add(formatVersion);

        result.addAll(encodeGroup(formattedCardGroups.get(3)));
        result.addAll(encodeGroup(formattedCardGroups.get(2)));
        result.addAll(encodeGroup(formattedCardGroups.get(1)));
        System.out.println();

//        Base32 enc = new Base32();
//        byte[] output = new byte[result.size()];
//
//        for (int i = 0; i < result.size(); i++)
//            output[i] = result.get(i).byteValue();
//
//        System.out.println(enc.encodeAsString(output));

        System.out.println("EXPECTED DECK CODE: " + expectedDeckCode);
        System.out.println("ENCODED DECK CODE:  " + encodeBoxed(result));
//        System.out.println(encodeBoxed(result));
//        System.out.println(expectedDeckCode);
    }
}
