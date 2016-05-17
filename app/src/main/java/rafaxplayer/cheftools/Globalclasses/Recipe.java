package rafaxplayer.cheftools.Globalclasses;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by exhowi on 06/02/2015.
 */

public class Recipe {
    int id;
    String name;
    String img;
    String Categoty;
    String Ingredients;
    String Elaboration;
    String url;



    public Recipe(){

    }

    public Recipe(String name, String img, String ingredients, String elaboration,  String url, String categoty) {
        this.name = name;
        this.img = img;

        this.Ingredients = ingredients;
        this.Elaboration = elaboration;

        this.url=url;

        this.Categoty = categoty;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoty() {
        return Categoty;
    }

    public void setCategoty(String categoty) {
        Categoty = categoty;
    }



    public String getElaboration() {
        return Elaboration;
    }

    public void setElaboration(String elaboration) {
        Elaboration = elaboration;
    }

    public String getIngredients() {
        return Ingredients;
    }

    public void setIngredients(String ingredients) {
        Ingredients = ingredients;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
