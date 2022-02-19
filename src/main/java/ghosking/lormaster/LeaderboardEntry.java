package ghosking.lormaster;

import java.util.Comparator;

public class LeaderboardEntry {

    private String name;
    private int rank;
    private int lp;

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

    public static class LeaderboardEntryComparator implements Comparator<LeaderboardEntry> {

        @Override
        public int compare(LeaderboardEntry entry1, LeaderboardEntry entry2) {
            return (entry1.getLP() <= entry2.getLP()) ? 0 : 1;
        }
    }
}
