package rafaxplayer.cheftools.recipes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.recipes.fragments.DetalleRecipes_Fragment;
import rafaxplayer.cheftools.recipes.fragments.RecipesList_Fragment;


public class Recipes_Activity extends BaseActivity implements RecipesList_Fragment.OnSelectedrecipeCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new RecipesList_Fragment(), "listrecipes");
            ft.commit();
        }

    }

    @Override
    protected int getLayoutResourceId() {
        if (getResources().getBoolean(R.bool.dual_pane)) {
            return R.layout.activity_recipes;
        } else {
            return R.layout.activity_template_for_all;
        }

    }

    @Override
    protected String getCustomTitle() {

        return getString(R.string.activity_recipes);
    }

    public void showRecipeEdit(int num) {
        Intent in = new Intent(getApplicationContext(), NewEditRecipe_Activity.class);
        in.putExtra("id", num);
        startActivity(in);
    }

    @Override
    public void onSelectRecipe(int id) {

        DetalleRecipes_Fragment frDetalle = (DetalleRecipes_Fragment) getSupportFragmentManager().findFragmentById(R.id.detalle);

        if (frDetalle != null && frDetalle.isInLayout()) {
            frDetalle.displayWithId(id);
        } else {
            Intent i = new Intent(this, DetalleRecipes_Activity.class);
            i.putExtra("id", id);

            startActivity(i);
        }
    }

}
