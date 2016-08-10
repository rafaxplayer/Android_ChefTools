package rafaxplayer.cheftools.Globalclasses;

public class Supplier {
    int id;
    String Name;
    String Telefono;
    String Email;
    String Direccion;
    String Comentario;

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    String Categoria;

    public Supplier() {

    }

    public Supplier(String name, String telefono, String email, String direccion, String comentario, String categoria) {
        Name = name;
        Telefono = telefono;
        Email = email;
        Direccion = direccion;
        Comentario = comentario;
        Categoria = categoria;

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

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getComentario() {
        return Comentario;
    }

    public void setComentario(String comentario) {
        Comentario = comentario;
    }

    public String getCategoria() {
        return Categoria;
    }

    public void setCategoria(String categoria) {
        Categoria = categoria;
    }


}
