package ui.ft.ccit.faculty.transaksi;

public class DataNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String idValue;

    public DataNotFoundException(String resourceName, String idValue) {
        super(resourceName + " dengan id " + idValue + " tidak ditemukan");
        this.resourceName = resourceName;
        this.idValue = idValue;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getIdValue() {
        return idValue;
    }
}
