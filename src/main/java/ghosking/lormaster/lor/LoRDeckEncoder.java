package ghosking.lormaster.lor;

import ghosking.lormaster.util.Base32;
import ghosking.lormaster.util.VarInt;

import java.util.ArrayList;
import java.util.List;

/**
 * Derived from https://github.com/RiotGames/LoRDeckCodes/blob/main/LoRDeckCodes/LoRDeckEncoder.cs.
 */
public class LoRDeckEncoder {

    /**
     * Encode an LoRDeck to a deck code string using Base32 encoding.
     * @param deck The deck to be encoded.
     * @return A deck code string that can be shared or used in-game.
     */
    public static String encode(LoRDeck deck) {
        List<Integer> result = new ArrayList<>();

        // Get the format and version and add those to the beginning of the result.
        int format = 1;
        int version = getMaxVersion(deck);
        int formatAndVersion = ((format << 4) | (version & 0xF));
        result.add(formatAndVersion);

        // Organize the cards into separate ArrayLists for cards with three copies,
        // two copies, one copy, and more than three copies (n copies).
        List<LoRDeck.LoRCardCodeAndCount> of3 = new ArrayList<>();
        List<LoRDeck.LoRCardCodeAndCount> of2 = new ArrayList<>();
        List<LoRDeck.LoRCardCodeAndCount> of1 = new ArrayList<>();
        List<LoRDeck.LoRCardCodeAndCount> ofN = new ArrayList<>();
        for (LoRDeck.LoRCardCodeAndCount cardCodeAndCount : deck.getCards()) {
            if (cardCodeAndCount.getCount() == 3) {
                of3.add(cardCodeAndCount);
            }
            else if (cardCodeAndCount.getCount() == 2) {
                of2.add(cardCodeAndCount);
            }
            else if (cardCodeAndCount.getCount() == 1) {
                of1.add(cardCodeAndCount);
            }
             else {
                 ofN.add(cardCodeAndCount);
             }
        }

        List<List<LoRDeck.LoRCardCodeAndCount>> groupedOf3s = getGroupedOfs(of3);
        List<List<LoRDeck.LoRCardCodeAndCount>> groupedOf2s = getGroupedOfs(of2);
        List<List<LoRDeck.LoRCardCodeAndCount>> groupedOf1s = getGroupedOfs(of1);

        sortGroupOf(groupedOf3s);
        sortGroupOf(groupedOf2s);
        sortGroupOf(groupedOf1s);
        sortNOfs(ofN);

        result.addAll(encodeGroupOf(groupedOf3s));
        result.addAll(encodeGroupOf(groupedOf2s));
        result.addAll(encodeGroupOf(groupedOf1s));
        result.addAll(encodeNOfs(ofN));

        // Store the result in a byte array to be passed into Base32.encode().
        byte[] resultBytes = new byte[result.size()];
        for (int i = 0; i < result.size(); i++)
            resultBytes[i] = result.get(i).byteValue();

        return Base32.encode(resultBytes);
    }

    /**
     * Decode a deck code string into an LoRDeck using Base32 decoding.
     * @param code The deck code to be decoded.
     * @return An LoRDeck corresponding to the deck code string.
     */
    public static LoRDeck decode(String code) {
        LoRDeck deck = new LoRDeck();

        // Try to decode the deck. Only move forward if this does not fail.
        byte[] bytes;
        try {
            bytes = Base32.decode(code);
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Unrecognized deck code: " + code);
        }
        // Store bytes in an ArrayList to easily remove elements and use VarInt.pop().
        List<Byte> bytesList = new ArrayList<>();
        for (byte b : bytes) bytesList.add(b);

        int format = bytes[0] >>> 4;
        int version = bytes[0] & 0xF;
        bytesList.remove(0);

        // Encode cards grouped by number of copies, starting with 3 ofs.
        for (int n = 3; n > 0; n--) {
            int numGroupsOfN = VarInt.pop(bytesList);
            // Encode each set/region group in the larger group.
            for (int j = 0; j < numGroupsOfN; j++) {
                int numCardsInSetRegionGroup = VarInt.pop(bytesList);
                int set = VarInt.pop(bytesList);
                int regionID = VarInt.pop(bytesList);
                // Encode each individual card in the group.
                for (int k = 0; k < numCardsInSetRegionGroup; k++) {
                    String setString = padLeft(String.valueOf(set), "0", 2);
                    String regionString = LoRRegion.fromID(regionID).getCode();
                    int cardID = VarInt.pop(bytesList);
                    String cardIDString = padLeft(String.valueOf(cardID), "0", 3);

                    String cardCode = setString + regionString + cardIDString;
                    deck.add(cardCode, n);
                }
            }
        }

        // Any remaining bytes in the deck code are for cards with 4 or more copies.
        // For these cards, the encoding is [count] [cardCode].
        while (bytesList.size() > 0) {
            int count = VarInt.pop(bytesList);
            int set = VarInt.pop(bytesList);
            int regionID = VarInt.pop(bytesList);
            int cardID = VarInt.pop(bytesList);

            String setString = padLeft(String.valueOf(set), "0", 2);
            String regionString = LoRRegion.fromID(regionID).getCode();
            String cardIDString = padLeft(String.valueOf(cardID), "0", 3);

            String cardCode = setString + regionString + cardIDString;
            deck.add(cardCode, count);
        }

        return deck;
    }

    private static int getMaxVersion(LoRDeck deck) {
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();

        // Search through the deck to find the maximum version. This is used during
        // encoding to determine the minimum version that needs to be supported.
        int version = 1;
        for (LoRDeck.LoRCardCodeAndCount cardCodeAndCount : deck.getCards()) {
            LoRCardDatabase.LoRCard card = cardDatabase.getCard(cardCodeAndCount.getCardCode());
            for (LoRRegion region : card.getRegions())
                version = Math.max(version, region.getVersion());
        }

        return version;
    }

    private static List<List<LoRDeck.LoRCardCodeAndCount>> getGroupedOfs(List<LoRDeck.LoRCardCodeAndCount> cardCodeAndCounts) {
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();
        List<List<LoRDeck.LoRCardCodeAndCount>> result = new ArrayList<>();

        // Group cards by shared set and region.
        while (cardCodeAndCounts.size() > 0) {
            List<LoRDeck.LoRCardCodeAndCount> currentGroup = new ArrayList<>();

            // Get the first card and add it to the current set.
            LoRCardDatabase.LoRCard firstCard = cardDatabase.getCard(cardCodeAndCounts.get(0).getCardCode());
            currentGroup.add(cardCodeAndCounts.get(0));
            cardCodeAndCounts.remove(0);

            // Search through the remaining cards, adding any that share the same
            // set and region to the current group of cards.
            for (int i = cardCodeAndCounts.size() - 1; i >= 0; i--) {
                LoRCardDatabase.LoRCard currentCard = cardDatabase.getCard(cardCodeAndCounts.get(i).getCardCode());
                if (currentCard.getSet() == firstCard.getSet() && currentCard.getRegion().getID() == firstCard.getRegion().getID()) {
                    currentGroup.add(cardCodeAndCounts.get(i));
                    cardCodeAndCounts.remove(i);
                }
            }

            result.add(currentGroup);
        }

        return result;
    }

    private static void sortGroupOf(List<List<LoRDeck.LoRCardCodeAndCount>> groupOf) {
        // Sort each inner set/region group in alphanumeric order by card codes.
        for (List<LoRDeck.LoRCardCodeAndCount> cardCodeAndCounts : groupOf) {
            for (int j = 0; j < cardCodeAndCounts.size() - 1; j++) {
                int minIndex = j;
                for (int k = j + 1; k < cardCodeAndCounts.size(); k++)
                    if (cardCodeAndCounts.get(k).getCardCode().compareTo(cardCodeAndCounts.get(minIndex).getCardCode()) < 1)
                        minIndex = k;

                LoRDeck.LoRCardCodeAndCount placeholder = cardCodeAndCounts.get(j);
                cardCodeAndCounts.set(j, cardCodeAndCounts.get(minIndex));
                cardCodeAndCounts.set(minIndex, placeholder);
            }
        }

        // Then, sort each outer group by the number of set/region groups it contains,
        // using the first card code if the sizes are the same.
        for (int i = 0; i < groupOf.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < groupOf.size(); j++)
                if (groupOf.get(j).size() < groupOf.get(minIndex).size() || (groupOf.get(j).size() < groupOf.get(minIndex).size() && (groupOf.get(j).get(0).getCardCode().compareTo(groupOf.get(minIndex).get(0).getCardCode()) < 1)))
                    minIndex = j;

            List<LoRDeck.LoRCardCodeAndCount> placeholder = groupOf.get(i);
            groupOf.set(i, groupOf.get(minIndex));
            groupOf.set(minIndex, placeholder);
        }
    }

    private static void sortNOfs(List<LoRDeck.LoRCardCodeAndCount> nOfs) {
        // Sort nOfs by card code. Sorting by number of copies is not needed.
        for (int i = 0; i < nOfs.size() - 1; i++) {
            int minIndex = i;
            for (int j = i; j < nOfs.size(); j++)
                if (nOfs.get(j).getCardCode().compareTo(nOfs.get(minIndex).getCardCode()) < 1)
                    minIndex = j;

            LoRDeck.LoRCardCodeAndCount placeholder = nOfs.get(i);
            nOfs.set(i, nOfs.get(minIndex));
            nOfs.set(minIndex, placeholder);
        }
    }

    private static List<Integer> encodeGroupOf(List<List<LoRDeck.LoRCardCodeAndCount>> group) {
        List<Integer> result = new ArrayList<>(VarInt.get(group.size()));
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();

        // For each set/region group in the larger group...
        for (List<LoRDeck.LoRCardCodeAndCount> cardCodeAndCounts : group) {
            // Start by adding the number of cards in this set/region group to the result.
            result.addAll(VarInt.get(cardCodeAndCounts.size()));
            // Then, add the set and region ID to the result for this group.
            LoRCardDatabase.LoRCard firstCard = cardDatabase.getCard(cardCodeAndCounts.get(0).getCardCode());
            result.addAll(VarInt.get(firstCard.getSet()));
            result.addAll(VarInt.get(firstCard.getRegion().getID()));

            // Lastly, add the ID of each card in the set/region group to the result.
            for (LoRDeck.LoRCardCodeAndCount cardCodeAndCount : cardCodeAndCounts) {
                LoRCardDatabase.LoRCard card = cardDatabase.getCard(cardCodeAndCount.getCardCode());
                result.addAll(VarInt.get(card.getID()));
            }
        }

        return result;
    }

    private static List<Integer> encodeNOfs(List<LoRDeck.LoRCardCodeAndCount> nOfs) {
        List<Integer> result = new ArrayList<>();
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();

        // Encode each LoRDeck.LoRCardCodeAndCount by adding its number of copies, set number,
        // region ID, and card ID to the result.
        for (LoRDeck.LoRCardCodeAndCount cardCodeAndCount : nOfs) {
            LoRCardDatabase.LoRCard card = cardDatabase.getCard(cardCodeAndCount.getCardCode());
            result.addAll(VarInt.get(cardCodeAndCount.getCount()));
            result.addAll(VarInt.get(card.getSet()));
            result.addAll(VarInt.get(card.getRegion().getID()));
            result.addAll(VarInt.get(card.getID()));
        }

        return result;
    }

    /**
     * Pad the left of a string with a given string to fill the specified length.
     * @param input The original string.
     * @param val The string to be used as padding.
     * @param length The desired length of the resulting string.
     * @return A string of the desired length padded on the left with the given value.
     */
    public static String padLeft(String input, String val, int length) {
        StringBuilder sb = new StringBuilder(input);

        while (sb.length() < length)
            sb.insert(0, val);

        return sb.toString();
    }
}
