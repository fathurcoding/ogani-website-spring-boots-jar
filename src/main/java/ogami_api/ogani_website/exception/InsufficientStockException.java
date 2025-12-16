package ogami_api.ogani_website.exception;

/**
 * Exception thrown ketika stok produk tidak mencukupi.
 */
public class InsufficientStockException extends RuntimeException {
    
    public InsufficientStockException(String productName, int requested, int available) {
        super(String.format("Stok %s tidak mencukupi. Diminta: %d, Tersedia: %d", 
                productName, requested, available));
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}
