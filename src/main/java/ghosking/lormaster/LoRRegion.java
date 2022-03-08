package ghosking.lormaster;

public class LoRRegion {

    private final int version;
    private final int id;
    private final String code;
    private final String name;

    private LoRRegion(int version, int id, String code, String name) {
        this.version = version;
        this.id = id;
        this.code = code;
        this.name = name;
    }

    public int getVersion() {
        return version;
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

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

    @Override
    public String toString() {
        return name;
    }
}
