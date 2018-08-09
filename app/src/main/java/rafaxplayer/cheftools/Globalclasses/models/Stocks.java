package rafaxplayer.cheftools.Globalclasses.models;

public class Stocks {
    int id;
    String Name;
    String Comentario;
    String Fecha;

    public Stocks() {

    }

    public Stocks(String name, String comentario, String fecha) {
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
