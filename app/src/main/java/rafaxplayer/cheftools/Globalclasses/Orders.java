package rafaxplayer.cheftools.Globalclasses;

/**
 * Created by rafaxplayer on 23/06/2015.
 */
public class Orders {
    int id;
    String Name;
    String Comentario;
    int Supplierid;
    String Fecha;

    public Orders() {

    }

    public Orders(String name, int supplierid,String comentario, String fecha) {
        Name = name;
        Comentario = comentario;
        Supplierid=supplierid;
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

    public int getSupplierid() {
        return Supplierid;
    }

    public void setSupplierid(int supplierid) {
        this.Supplierid = supplierid;
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
