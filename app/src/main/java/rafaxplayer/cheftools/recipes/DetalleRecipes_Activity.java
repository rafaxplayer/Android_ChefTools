package rafaxplayer.cheftools.recipes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.recipes.fragments.DetalleRecipes_Fragment;
import rafaxplayer.cheftools.recipes.fragments.NewEditRecipe_Fragment;


public class DetalleRecipes_Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int ID = getIntent().getExtras().getInt("id");

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, DetalleRecipes_Fragment.newInstance(ID), "detalle")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit();
        }

    }

    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_template_for_all;
    }

    @Override
    protected String getCustomTitle() {

        return getString(R.string.activity_detallerecipe);
    }

    public void showRecipeEdit(int id) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, NewEditRecipe_Fragment.newInstance(id), "neweditrecipe");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            NewEditRecipe_Fragment fr = (NewEditRecipe_Fragment) getSupportFragmentManager().findFragmentByTag("neweditrecipe");

            if (requestCode == GlobalUttilities.SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    Log.e("selectedImageUri", data.getData().toString());
                    if (fr != null) {
                        fr.updateImage(selectedImageUri);
                    }
                }

            } else if (requestCode == GlobalUttilities.SELECT_PHOTO) {

                Bitmap photo = (Bitmap) data.getExtras().get("data");
                Log.e("Data", photo.toString());
                if (photo != null) {
                    Uri imageBmpUri = GlobalUttilities.getBmpUri(this, photo);
                    fr.updateImage(imageBmpUri);
                }

            }
        }

    }

}
