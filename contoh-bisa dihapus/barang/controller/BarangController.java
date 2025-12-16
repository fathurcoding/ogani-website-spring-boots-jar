package ui.ft.ccit.faculty.transaksi.barang.controller;

import org.springframework.web.bind.annotation.*;

import ui.ft.ccit.faculty.transaksi.barang.model.Barang;
import ui.ft.ccit.faculty.transaksi.barang.view.BarangService;

import java.util.List;

@RestController
@RequestMapping("/api/barang")
public class BarangController {

    private final BarangService service;

    public BarangController(BarangService service) {
        this.service = service;
    }

    // GET list semua barang
    @GetMapping
    public List<Barang> list() {
        return service.getAll();
    }

    // GET satu barang by id
    @GetMapping("/{id}")
    public Barang get(@PathVariable String id) {
        return service.getById(id);
    }

    // SEARCH by nama
    @GetMapping("/search")
    public List<Barang> search(@RequestParam String q) {
        return service.searchByNama(q);
    }

    // POST - create barang baru
    @PostMapping
    public Barang create(@RequestBody Barang barang) {
        return service.save(barang);
    }

    // PUT - edit/update barang
    @PutMapping("/{id}")
    public Barang update(@PathVariable String id, @RequestBody Barang barang) {
        return service.update(id, barang);
    }

    // DELETE - hapus barang
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        service.delete(id);
    }
}
