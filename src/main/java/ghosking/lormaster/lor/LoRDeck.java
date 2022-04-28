package ghosking.lormaster.lor;

import java.util.ArrayList;
import java.util.List;

public final class LoRDeck {

    public static final class LoRCardCodeAndCount {

        private final String cardCode;
        private int count;

        public LoRCardCodeAndCount(String cardCode, int count) {
            this.cardCode = cardCode;
            this.count = count;
        }

        public String getCardCode() {
            return cardCode;
        }

        public int getCount() {
            return count;
        }

        public void incrementCount() {
            count++;
        }

        public void decrementCount() {
            if (count > 0) count--;
        }
    }

    private final List<LoRCardCodeAndCount> cards;
    private String name;

    /**
     * Create an empty deck.
     */
    public LoRDeck() {
        cards = new ArrayList<>();
    }

    /**
     * Copy constructor.
     */
    public LoRDeck(LoRDeck deck) {
        cards = new ArrayList<>();
        cards.addAll(deck.getCards());
    }

    public List<LoRCardCodeAndCount> getCards() {
        return cards;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSize() {
        int size = 0;
        for (LoRCardCodeAndCount cardCodeAndCount : cards)
            size += cardCodeAndCount.getCount();

        return size;
    }

    public List<LoRRegion> getRegions() {
        List<LoRRegion> regions = new ArrayList<>();
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();

        // Search the deck to keep track of any regions in the deck, not counting
        // cards with multiple regions since they must have a matching region already
        // in the deck in order to be there.
        for (LoRCardCodeAndCount cardCodeAndCount : cards) {
            LoRCardDatabase.LoRCard card = cardDatabase.getCard(cardCodeAndCount.getCardCode());
            if (card.getRegions().size() > 1) continue;

            boolean isRegionInDeck = false;
            for (LoRRegion region : regions)
                if (region.getID() == card.getRegion().getID()) isRegionInDeck = true;

            if (!isRegionInDeck)
                regions.add(card.getRegion());
        }

        return regions;
    }

    public int getNumChampions() {
        int numChampions = 0;
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();

        for (LoRCardCodeAndCount cardCodeAndCount : cards) {
            LoRCardDatabase.LoRCard card = cardDatabase.getCard(cardCodeAndCount.getCardCode());
            if (card.getRarity() == LoRRarity.CHAMPION) numChampions += cardCodeAndCount.getCount();
        }

        return numChampions;
    }

    /**
     * Add a single copy of the specified card into the deck, in accordance with
     * the standard deck-building rules:
     * 1. A deck has a maximum of 40 cards.
     * 2. A deck may only have up to 3 copies of any given card.
     * 3. A deck may only have up to 6 champions.
     * 4. A deck may only consist of up to 2 regions.
     * @param cardCode The card code to be added.
     */
    public void add(String cardCode) throws DeckSizeLimitExceededException, ChampionCountLimitExceededException, RegionCountLimitExceededException, CardCopiesCountLimitExceededException {
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();

        if (getSize() >= 40) throw new DeckSizeLimitExceededException("Cannot add card to deck with 40 cards.");

        if (cardDatabase.getCard(cardCode).getRarity() == LoRRarity.CHAMPION && getNumChampions() >= 6)
            throw new ChampionCountLimitExceededException("Cannot add champion to deck with 6 champions.");

        if (getRegions().size() >= 2) {
            boolean hasMatchingRegion = false;
            for (LoRRegion deckRegion : getRegions()) {
                for (LoRRegion cardRegion : cardDatabase.getCard(cardCode).getRegions()) {
                    if (deckRegion.getID() == cardRegion.getID()) {
                        hasMatchingRegion = true;
                        break;
                    }
                }
            }
            if (!hasMatchingRegion)
                throw new RegionCountLimitExceededException("Cannot add card with new region to deck with 2 regions.");
        }

        boolean isCardInDeck = false;
        // Search the deck to see if the given card already exists in the deck.
        for (LoRCardCodeAndCount cardCodeAndCount : cards) {
            // If it does, increment the associated count by one.
            if (cardCodeAndCount.getCardCode().equalsIgnoreCase(cardCode)) {
                // But do not add another copy of the card if there are already
                // 3 copies in the deck.
                if (cardCodeAndCount.getCount() >= 3)
                    throw new CardCopiesCountLimitExceededException("Cannot add fourth copy of card to deck.");

                isCardInDeck = true;
                cardCodeAndCount.incrementCount();
            }
        }

        // If not, create a new LoRCardCodeAndCount instance for this card.
        if (!isCardInDeck) cards.add(new LoRCardCodeAndCount(cardCode, 1));
    }

    /**
     * Add multiple copies of the specified card into the deck, disregarding standard
     * deck building rules. (NOTE: this is only to be used when a deck is being
     * decoded, as it assumes the card does not exist in the deck and allows for
     * more than three copies of a card to be added to the deck.)
     * @param cardCode The card code to be added.
     * @param count The number of copies to be added.
     */
    public void add(String cardCode, int count) {
        cards.add(new LoRCardCodeAndCount(cardCode, count));
    }

    /**
     * Remove a single copy of the specified card, if it exists in the deck.
     * @param cardCode The card code to be removed.
     */
    public void remove(String cardCode) {
        // Search the deck for the given card.
        for (LoRCardCodeAndCount cardCodeAndCount : cards) {
            if (cardCodeAndCount.getCardCode().equalsIgnoreCase(cardCode)) {
                // If there is only one of this card left in the deck, remove the
                // LoRCardCodeAndCount from the deck entirely.
                if (cardCodeAndCount.getCount() == 1) cards.remove(cardCodeAndCount);
                // Otherwise, decrement the associated count by one.
                else cardCodeAndCount.decrementCount();

                return;
            }
        }
    }

    /**
     * Clear the contents of the deck.
     */
    public void clear() {
        cards.clear();
    }

    public void sort() {
        LoRCardDatabase cardDatabase = LoRCardDatabase.getInstance();
        for (int i = 0; i < cards.size() - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < cards.size(); j++) {
                LoRCardDatabase.LoRCard card = cardDatabase.getCard(cards.get(j).getCardCode());
                LoRCardDatabase.LoRCard minCostCard = cardDatabase.getCard(cards.get(minIndex).getCardCode());

                if (card.getCost() < minCostCard.getCost())
                    minIndex = j;
                else if (card.getCost() == minCostCard.getCost() && card.getName().compareToIgnoreCase(minCostCard.getName()) < 1)
                    minIndex = j;
            }
            LoRCardCodeAndCount placeholder = cards.get(i);
            cards.set(i, cards.get(minIndex));
            cards.set(minIndex, placeholder);
        }
    }

    public static class DeckSizeLimitExceededException extends Exception {
        public DeckSizeLimitExceededException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class ChampionCountLimitExceededException extends Exception {
        public ChampionCountLimitExceededException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class RegionCountLimitExceededException extends Exception {
        public RegionCountLimitExceededException(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class CardCopiesCountLimitExceededException extends Exception {
        public CardCopiesCountLimitExceededException(String errorMessage) {
            super(errorMessage);
        }
    }
}
