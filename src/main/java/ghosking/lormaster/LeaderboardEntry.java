package ghosking.lormaster;

public class LeaderboardEntry {

    private final String name;
    private int rank;
    private final int lp;

    public LeaderboardEntry(String name, int rank, int lp) {
        this.name = name;
        this.rank = rank;
        this.lp = lp;
    }

    public String getName() {
        return name;
    }

    public int getRank() {
        return rank;
    }

    public int getLP() {
        return lp;
    }

    public void setRank(int rank) {
        if (rank < 1) return;
        this.rank = rank;
    }

    @Override
    public String toString() {
        return getRank() + ". " + getName() + " (" + getLP() + " LP)";
    }
}
