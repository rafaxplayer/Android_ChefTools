package rafaxplayer.cheftools.Globalclasses.models;

import java.util.ArrayList;

/**
 * Created by rafaxplayer on 23/06/2015.
 */
public class Escandallo {

    String Name;
    String Date;
    double Costetotal;
    ArrayList<Escandallo_Product> Products;

    public Escandallo() {

    }

    public Escandallo(String name, String comentario, String date, ArrayList<Escandallo_Product> products, double costetotal) {
        Name = name;
        Costetotal = costetotal;
        Date = date;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public ArrayList<Escandallo_Product> getProducts() {
        return Products;
    }

    public void setProducts(ArrayList<Escandallo_Product> products) {
        Products = products;
    }

    public double getCostetotal() {
        return Costetotal;
    }

    public void setCostetotal(double costetotal) {
        this.Costetotal = costetotal;
    }
}
