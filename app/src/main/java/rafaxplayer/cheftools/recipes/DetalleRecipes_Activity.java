package rafaxplayer.cheftools.recipes;

import android.content.Intent;
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

    private int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ID = getIntent().getExtras().getInt("id");

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
        ft.add(R.id.container, NewEditRecipe_Fragment.newInstance(id), "editrecipe");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("onActivityResult", "onActivityResult: ");
        if (resultCode == RESULT_OK) {

            final Uri selectedImageUri = data.getData();
            Log.e("onActivityResult image", selectedImageUri.toString());
            if (requestCode == GlobalUttilities.SELECT_PICTURE || requestCode == GlobalUttilities.CAPTURE_ID) {
                Log.e("onActivityResult slect", selectedImageUri.toString());
                if (selectedImageUri != null) {
                    final NewEditRecipe_Fragment fr = (NewEditRecipe_Fragment) getSupportFragmentManager().findFragmentByTag("editrecipe");
                    if (fr != null) {
                        Log.e("onActivityResult fr", "fragment ok");
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        Log.i("tag","A Kiss after 5 seconds");
                                        fr.updateImage(selectedImageUri);
                                    }
                                }, 1000);

                    }
                }
            } else if (requestCode == GlobalUttilities.RECIPE_WITH_CAPTURE) {

                if (selectedImageUri != null) {
                    NewEditRecipe_Fragment fr = (NewEditRecipe_Fragment) getSupportFragmentManager().findFragmentByTag("editrecipe");
                    if (fr != null) {
                        fr.refresh();
                        fr.updateImage(selectedImageUri);
                    }
                }
            }
        }
    }


}
