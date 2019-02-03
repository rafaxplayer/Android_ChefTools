package rafaxplayer.cheftools.Globalclasses.models;

/**
 * Created by rafaxplayer on 23/06/2015.
 */
public class Escandallo {

    private int id;
    private String Name;
    private String Fecha;
    private String comment;
    private double Costetotal;


    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }

    public Escandallo() {

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getCostetotal() {
        return Costetotal;
    }

    public void setCostetotal(double costetotal) {
        this.Costetotal = costetotal;
    }
}
