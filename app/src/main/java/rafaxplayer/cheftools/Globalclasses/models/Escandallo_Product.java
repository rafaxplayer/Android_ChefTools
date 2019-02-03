package rafaxplayer.cheftools.Globalclasses.models;

/**
 * Created by rafaxplayer on 25/06/2015.
 */
public class Escandallo_Product {

    private int id;
    private int escandalloid;
    private String productoname;
    private String cantidad;
    private String formato;
    private String costforuni;
    private double coste;


    public Escandallo_Product() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
