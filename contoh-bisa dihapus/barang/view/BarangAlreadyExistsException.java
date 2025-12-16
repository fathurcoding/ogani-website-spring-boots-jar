package ui.ft.ccit.faculty.transaksi.barang.view;

public class BarangAlreadyExistsException extends RuntimeException {

    public BarangAlreadyExistsException(String idBarang) {
        super("Barang dengan id " + idBarang + " sudah ada");
    }
}
