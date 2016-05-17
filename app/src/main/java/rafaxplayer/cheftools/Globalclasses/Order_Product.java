package rafaxplayer.cheftools.Globalclasses;

/**
 * Created by rafaxplayer on 25/06/2015.
 */
public class Order_Product {
    int ID;
    int listaId;
    int productoId;
    int cantidad;
    int formatoid;
    int categoriaid;

    public Order_Product() {

    }

    public Order_Product(int ID, int listaId, int productoId, int cantidad, int formatoid, int categoriaid) {
        this.ID = ID;
        this.listaId = listaId;

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

    public int getListaId() {
        return listaId;
    }


    public void setListaId(int listaId) {
        this.listaId = listaId;
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
