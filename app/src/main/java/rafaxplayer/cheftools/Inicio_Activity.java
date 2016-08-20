package rafaxplayer.cheftools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Orders.Orders_Activity;
import rafaxplayer.cheftools.config.Config_Activity;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.menus.Menus_Activity;
import rafaxplayer.cheftools.products.Products_Activity;
import rafaxplayer.cheftools.products.fragments.ProductosMannager_Fragment;
import rafaxplayer.cheftools.providers.Providers_Activity;
import rafaxplayer.cheftools.recipes.Recipes_Activity;
import rafaxplayer.cheftools.stocks.Stocks_Activity;
import rafaxplayer.cheftools.tools.Tools_Activity;


public class Inicio_Activity extends BaseActivity implements ProductosMannager_Fragment.OnSelectedCallback {

    String inFileName = "/data/data/rafaxplayer.cheftools/databases/" + DBHelper.DATABASE_NAME;
    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        File folder = new File(GlobalUttilities.PATH_BACKUPS);
        if (!folder.exists()) {
            if (folder.mkdirs()) {

            }
        }

    }

    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_inicio;
    }

    @Override
    protected String getCustomTitle() {

        return null;
    }

    public void menuClick(View v) {

        switch (v.getId()) {
            case R.id.recetas_menu:
                startActivity(new Intent(getApplicationContext(), Recipes_Activity.class));
                break;
            case R.id.products_menu:
                startActivity(new Intent(getApplicationContext(), Products_Activity.class));
                break;
            case R.id.providers_menu:
                startActivity(new Intent(getApplicationContext(), Providers_Activity.class));
                break;
            case R.id.orders_menu:
                startActivity(new Intent(getApplicationContext(), Orders_Activity.class));
                break;
            case R.id.menus_menu:
                startActivity(new Intent(getApplicationContext(), Menus_Activity.class));
                break;
            case R.id.inventories_menu:
                startActivity(new Intent(getApplicationContext(), Stocks_Activity.class));
                break;
            case R.id.settings_menu:
                startActivity(new Intent(getApplicationContext(), Config_Activity.class));
                break;
            case R.id.tools_menu:
                startActivity(new Intent(getApplicationContext(), Tools_Activity.class));
                break;
            default:
                break;
        }

    }

    @Override
    public void onSelect(int pid) {

    }

    @Override
    public void onBackPressed() {

        if (back_pressed + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.back_exit), Toast.LENGTH_SHORT).show();
            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(500);
            back_pressed = System.currentTimeMillis();
        }

    }
}
