package rafaxplayer.cheftools.Globalclasses.models;

/**
 * Created by rafaxplayer on 23/06/2015.
 */
public class Escandallo {

    int id;
    String Name;
    String Fecha;
    double Costetotal;

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public Escandallo() {

    }

    public Escandallo(String name, String comentario, String fecha, double costetotal) {
        Name = name;
        Costetotal = costetotal;
        Fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public double getCostetotal() {
        return Costetotal;
    }

    public void setCostetotal(double costetotal) {
        this.Costetotal = costetotal;
    }
}
