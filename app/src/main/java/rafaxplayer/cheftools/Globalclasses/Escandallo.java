package rafaxplayer.cheftools.Globalclasses;

/**
 * Created by rafaxplayer on 23/06/2015.
 */
public class Escandallo {
    int id;
    String Name;
    String Comentario;
    String Fecha;

    public Escandallo() {

    }

    public Escandallo(String name, String comentario, String fecha) {
        Name = name;
        Comentario = comentario;

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

    public String getComentario() {
        return Comentario;
    }

    public void setComentario(String comentario) {
        Comentario = comentario;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String fecha) {
        Fecha = fecha;
    }
}
