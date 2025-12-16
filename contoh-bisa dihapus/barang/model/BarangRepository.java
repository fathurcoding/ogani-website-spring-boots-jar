package ui.ft.ccit.faculty.transaksi.barang.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BarangRepository extends JpaRepository<Barang, String> {

    // cari berdasarkan nama mengandung kata tertentu
    /*~~(class org.openrewrite.java.tree.J$Erroneous cannot be cast to class org.openrewrite.java.tree.J$Assignment (org.openrewrite.java.tree.J$Erroneous and org.openrewrite.java.tree.J$Assignment are in unnamed module of loader 'app'))~~>*/@Query("SELECT b FROM Barang b WHERE UPPER(b.nama) LIKE UPPER(:keyword) ESCAPE '\\'")
    List<Barang> findByNamaContainingIgnoreCase(String keyword);

    // contoh lain: barang dengan stok kurang dari angka tertentu
    List<Barang> findByStokLessThan(Integer stok);
}
