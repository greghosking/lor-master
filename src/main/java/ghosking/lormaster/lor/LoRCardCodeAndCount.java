package ghosking.lormaster.lor;

public class LoRCardCodeAndCount {

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
        if (count > 0) {
            count--;
        }
    }
}
