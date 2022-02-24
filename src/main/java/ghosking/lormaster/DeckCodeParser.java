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

import java.util.*;

public class DeckCodeParser {

    public static void main(String[] args) {

        // Example deck: Plunder.
        String expectedDeckCode = "CECQMAQGCQQCELJ2HQAQGBQIAICQCAIGAECAMCQBAIAQEAQCAIDAEEQBAQAQUAIBAMARM";

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

        // Count how many times each card appears in the deck.
        HashMap<String, Integer> cardCounts = new HashMap<>();
        for (String cardCode : deck) {
            if (cardCounts.containsKey(cardCode))
                cardCounts.put(cardCode, cardCounts.get(cardCode) + 1);
            else
                cardCounts.put(cardCode, 1);
        }
        System.out.println(cardCounts);

        // Group cards together based on how many copies of the card are in the deck (cards with three copies are grouped
        // together, cards with two copies are grouped together, and cards with a single copy are grouped together).
        HashMap<Integer, ArrayList<String>> cardsGroupedByCopies = new HashMap<>();
        cardsGroupedByCopies.put(3, new ArrayList<>());
        cardsGroupedByCopies.put(2, new ArrayList<>());
        cardsGroupedByCopies.put(1, new ArrayList<>());

        for (Map.Entry<String, Integer> cardCount : cardCounts.entrySet())
            cardsGroupedByCopies.get(cardCount.getValue()).add(cardCount.getKey());

        System.out.println(cardsGroupedByCopies);

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

                    if (sizeAtJ <= sizeAtIndexOfMin)
                        indexOfMin = j;
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

        System.out.println(formattedCardGroups);

        // ENCODE...
    }
}
