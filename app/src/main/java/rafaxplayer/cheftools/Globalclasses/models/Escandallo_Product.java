package rafaxplayer.cheftools.Globalclasses.models;

/**
 * Created by rafaxplayer on 25/06/2015.
 */
public class Escandallo_Product {

    int escandalloid;
    String productoname;
    String cantidad;
    String formato;
    String costforuni;
    double coste;



    public Escandallo_Product() {

    }

    public Escandallo_Product(int escandalloid,String costforuni, String productoname, String cantidad, double coste) {
        this.escandalloid= escandalloid;
        this.costforuni = costforuni;
        this.productoname = productoname;
        this.cantidad = cantidad;
        this.coste = coste;
    }

    public int getEscandalloid() {
        return escandalloid;
    }

    public void setEscandalloid(int escandalloid) {
        this.escandalloid = escandalloid;
    }
    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getCostforuni() {
        return costforuni;
    }

    public void setCostforuni(String costforuni) {
        this.costforuni = costforuni;
    }

    public String getProductoname() {
        return productoname;
    }

    public void setProductoname(String productoname) {
        this.productoname = productoname;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public double getCoste() {
        return coste;
    }

    public void setCoste(double coste) {
        this.coste = coste;
    }
}
