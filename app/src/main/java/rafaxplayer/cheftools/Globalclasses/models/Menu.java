package rafaxplayer.cheftools.Globalclasses.models;

public class Menu {
    int id;
    String name;
    String Entrantes;
    String Primeros;
    String Segundos;
    String Postre;
    String Comentario;
    String Fecha;


    public Menu() {

    }

    public Menu(String name, String entrantes, String primeros, String segundos, String postre, String comentario, String fecha) {

        this.name = name;
        this.Entrantes = entrantes;
        this.Primeros = primeros;
        this.Segundos = segundos;
        this.Postre = postre;
        this.Comentario = comentario;
        this.Fecha = fecha;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEntrantes() {
        return Entrantes;
    }

    public void setEntrantes(String entrantes) {
        Entrantes = entrantes;
    }

    public String getPrimeros() {
        return Primeros;
    }

    public void setPrimeros(String primeros) {
        Primeros = primeros;
    }

    public String getSegundos() {
        return Segundos;
    }

    public void setSegundos(String segundos) {
        Segundos = segundos;
    }

    public String getPostre() {
        return Postre;
    }

    public void setPostre(String postre) {
        Postre = postre;
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
        this.Fecha = fecha;
    }

}
