package ui.ft.ccit.faculty.transaksi.barang.view;

public class BarangNotFoundException extends RuntimeException {

    public BarangNotFoundException(String idBarang) {
        super("Barang dengan id " + idBarang + " tidak ditemukan");
    }
}
