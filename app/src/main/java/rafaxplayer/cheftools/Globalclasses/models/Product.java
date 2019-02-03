package rafaxplayer.cheftools.Globalclasses.models;

/**
 * Created by rafaxplayer on 17/06/2015.
 */
public class Product {
    private int Id;
    private int formatoid;
    private int categoryid;
    private int supplierid;
    private String name;
    private String suppliername;
    private String categoryname;
    private String formatoname;


    public Product() {

    }

    public int getId() {
        return Id;
    }

    public void setId(int ID) {
        this.Id = ID;
    }

    public String getCategoryname() {
        return categoryname;
    }

    public void setCategoryname(String categoryname) {
        this.categoryname = categoryname;
    }

    public String getSuppliername() {
        return suppliername;
    }

    public void setSuppliername(String providername) {
        this.suppliername = providername;
    }

    public String getFormatoname() {
        return formatoname;
    }

    public void setFormatoname(String formatoname) {
        this.formatoname = formatoname;
    }

    public int getFormatoid() {
        return formatoid;
    }

    public void setFormatoid(int formatoid) {
        this.formatoid = formatoid;
    }

    public int getCategoryid() {
        return categoryid;
    }

    public void setCategoryid(int categoryid) {
        this.categoryid = categoryid;
    }

    public int getSupplierid() {
        return supplierid;
    }

    public void setSupplierid(int supplierid) {
        this.supplierid = supplierid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
