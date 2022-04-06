package ghosking.lormaster.lor;

public final class LoRRegion {

    private final int version;
    private final int id;
    private final String code;
    private final String name;

    /**
     * Constructor is private to ensure that instances of this class are
     * only created through one of the methods below:
     * fromID(int id), fromCode(String code), fromName(String name).
     */
    private LoRRegion(int version, int id, String code, String name) {
        this.version = version;
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public int getID() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    /**
     * @param id The region ID: an integer between 0 and 10.
     * @return An instance of LoRRegion that corresponds to the ID.
     */
    public static LoRRegion fromID(int id) {

        if (id == 0) {
            return new LoRRegion(1, id, "DE", "Demacia");
        }
        if (id == 1) {
            return new LoRRegion(1, id, "FR", "Freljord");
        }
        if (id == 2) {
            return new LoRRegion(1, id, "IO", "Ionia");
        }
        if (id == 3) {
            return new LoRRegion(1, id, "NX", "Noxus");
        }
        if (id == 4) {
            return new LoRRegion(1, id, "PZ", "Piltover & Zaun");
        }
        if (id == 5) {
            return new LoRRegion(1, id, "SI", "Shadow Isles");
        }
        if (id == 6) {
            return new LoRRegion(2, id, "BW", "Bilgewater");
        }
        if (id == 9) {
            return new LoRRegion(2, id, "MT", "Targon");
        }
        if (id == 7) {
            return new LoRRegion(3, id, "SH", "Shurima");
        }
        if (id == 10) {
            return new LoRRegion(4, id, "BC", "Bandle City");
        }

        throw new RuntimeException("Unrecognized region ID: " + id);
    }

    /**
     * @param code The region code: a two-character abbreviation of the region name.
     * @return An instance of LoRRegion that corresponds to the code.
     */
    public static LoRRegion fromCode(String code) {

        if (code.compareToIgnoreCase("DE") == 0) {
            return new LoRRegion(1, 0, "DE", "Demacia");
        }
        if (code.compareToIgnoreCase("FR") == 0) {
            return new LoRRegion(1, 1, "FR", "Freljord");
        }
        if (code.compareToIgnoreCase("IO") == 0) {
            return new LoRRegion(1, 2, "IO", "Ionia");
        }
        if (code.compareToIgnoreCase("NX") == 0) {
            return new LoRRegion(1, 3, "NX", "Noxus");
        }
        if (code.compareToIgnoreCase("PZ") == 0) {
            return new LoRRegion(1, 4, "PZ", "Piltover & Zaun");
        }
        if (code.compareToIgnoreCase("SI") == 0) {
            return new LoRRegion(1, 5, "SI", "Shadow Isles");
        }
        if (code.compareToIgnoreCase("BW") == 0) {
            return new LoRRegion(2, 6, "BW", "Bilgewater");
        }
        if (code.compareToIgnoreCase("MT") == 0) {
            return new LoRRegion(2, 9, "MT", "Targon");
        }
        if (code.compareToIgnoreCase("SH") == 0) {
            return new LoRRegion(3, 7, "SH", "Shurima");
        }
        if (code.compareToIgnoreCase("BC") == 0) {
            return new LoRRegion(4, 10, "BC", "Bandle City");
        }

        throw new RuntimeException("Unrecognized region code: " + code);
    }

    /**
     * @param name The name of the region.
     * @return An instance of LoRRegion that corresponds to the name.
     */
    public static LoRRegion fromName(String name) {

        if (name.compareToIgnoreCase("Demacia") == 0) {
            return new LoRRegion(1, 0, "DE", "Demacia");
        }
        if (name.compareToIgnoreCase("Freljord") == 0) {
            return new LoRRegion(1, 1, "FR", "Freljord");
        }
        if (name.compareToIgnoreCase("Ionia") == 0) {
            return new LoRRegion(1, 2, "IO", "Ionia");
        }
        if (name.compareToIgnoreCase("Noxus") == 0) {
            return new LoRRegion(1, 3, "NX", "Noxus");
        }
        if (name.compareToIgnoreCase("Piltover & Zaun") == 0) {
            return new LoRRegion(1, 4, "PZ", "Piltover & Zaun");
        }
        if (name.compareToIgnoreCase("Shadow Isles") == 0) {
            return new LoRRegion(1, 5, "SI", "Shadow Isles");
        }
        if (name.compareToIgnoreCase("Bilgewater") == 0) {
            return new LoRRegion(2, 6, "BW", "Bilgewater");
        }
        if (name.compareToIgnoreCase("Targon") == 0) {
            return new LoRRegion(2, 9, "MT", "Targon");
        }
        if (name.compareToIgnoreCase("Shurima") == 0) {
            return new LoRRegion(3, 7, "SH", "Shurima");
        }
        if (name.compareToIgnoreCase("Bandle City") == 0) {
            return new LoRRegion(4, 10, "BC", "Bandle City");
        }

        throw new RuntimeException("Unrecognized region name: " + name);
    }
}
