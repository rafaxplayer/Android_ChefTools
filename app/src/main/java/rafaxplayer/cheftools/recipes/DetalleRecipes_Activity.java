package rafaxplayer.cheftools.recipes;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.recipes.fragments.DetalleRecipes_Fragment;
import rafaxplayer.cheftools.recipes.fragments.NewEditRecipe_Fragment;


public class DetalleRecipes_Activity extends BaseActivity {

    private int ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ID= getIntent().getExtras().getInt("id");

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
}
