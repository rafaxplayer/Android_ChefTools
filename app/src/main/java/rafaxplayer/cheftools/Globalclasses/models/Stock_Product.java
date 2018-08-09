package rafaxplayer.cheftools.Globalclasses.models;

public class Stock_Product {
    int ID;
    int stockId;
    int productoId;
    int cantidad;
    int formatoid;
    int categoriaid;

    public Stock_Product() {

    }

    public Stock_Product(int ID, int stockId, int productoId, int cantidad, int formatoid, int categoriaid) {
        this.ID = ID;
        this.stockId = stockId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.formatoid = formatoid;
        this.categoriaid = categoriaid;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getFormatoid() {
        return formatoid;
    }

    public void setFormatoid(int formatoid) {
        this.formatoid = formatoid;
    }

    public int getCategoriaid() {
        return categoriaid;
    }

    public void setCategoriaid(int categoriaid) {
        this.categoriaid = categoriaid;
    }
}
