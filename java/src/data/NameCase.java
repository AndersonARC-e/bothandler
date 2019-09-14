package data;

public enum NameCase {
    NOM("nom"),GEN("gen"),DAT("dat"),ACC("acc"),INS("ins"),ABL("abl");

    String type;

    public String getType() {
        return type;
    }

    NameCase (String type) {
        this.type = type;
    }
}
