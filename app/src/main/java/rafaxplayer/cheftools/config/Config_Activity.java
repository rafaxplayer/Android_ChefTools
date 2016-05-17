package rafaxplayer.cheftools.config;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.dlg_fragments.Format_Categories_Formats_dlgs;
import rafaxplayer.cheftools.dlg_fragments.Fragment_backups_dlg;


public class Config_Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_config;
    }

    @Override
    protected String getCustomTitle() {

        return getString(R.string.action_settings);
    }

    public void menuClick(View v) {
        switch (v.getId()) {
            case R.id.categorys_products:
                DialogFragment frmcatProducts = Format_Categories_Formats_dlgs.newInstance(DBHelper.TABLE_PRODUCTOS_CATEGORY, R.string.admin_cat_pro);
                frmcatProducts.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                frmcatProducts.show(getSupportFragmentManager(), "dialogProductCategories");
                break;
            case R.id.products_formats:
                DialogFragment frmFormats = Format_Categories_Formats_dlgs.newInstance(DBHelper.TABLE_PRODUCTOS_FORMATO, R.string.admin_pro_format);
                frmFormats.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                frmFormats.show(getSupportFragmentManager(), "dialogFormatCategories");
                break;
            case R.id.categorys_recipes:
                DialogFragment frmCatRecipes = Format_Categories_Formats_dlgs.newInstance(DBHelper.TABLE_RECETAS_CATEGORIA, R.string.admin_cat_recipes);
                frmCatRecipes.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                frmCatRecipes.show(getSupportFragmentManager(), "dialogRecipesCategories");
                break;
            case R.id.backups:
                DialogFragment frmBackups = new Fragment_backups_dlg();
                frmBackups.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                frmBackups.show(getSupportFragmentManager(), "dialogBackups");
                break;
            default:
                break;
        }

    }

}
