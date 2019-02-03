package rafaxplayer.cheftools;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import java.io.File;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Orders.Orders_Activity;
import rafaxplayer.cheftools.config.Config_Activity;
import rafaxplayer.cheftools.menus.Menus_Activity;
import rafaxplayer.cheftools.products.Products_Activity;
import rafaxplayer.cheftools.products.fragments.ProductosMannager_Fragment;
import rafaxplayer.cheftools.providers.Providers_Activity;
import rafaxplayer.cheftools.recipes.Recipes_Activity;
import rafaxplayer.cheftools.stocks.Stocks_Activity;
import rafaxplayer.cheftools.tools.Tools_Activity;


public class Inicio_Activity extends BaseActivity implements ProductosMannager_Fragment.OnSelectedCallback {

    private static long back_pressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_CONTACTS}, GlobalUttilities.PERMISSION_REQUEST);

            }
        }

        File folderBack = new File(GlobalUttilities.PATH_BACKUPS);
        if (!folderBack.exists()) {
            folderBack.mkdirs();
        }

        File folderImg = new File(GlobalUttilities.PATH_IMAGES_RECIPES);
        if (!folderImg.exists()) {
            folderImg.mkdirs();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GlobalUttilities.PERMISSION_REQUEST) {
            boolean grant = true;
            for (int i = 0; i < grantResults.length; i++) {

                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    grant = false;
                }

            }
            if (grant) {
                Toast.makeText(this, "Ok, permisos concedios", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Has declinado algun permiso, no tendras acceso a todas las funcionalidades de la aplicaciobn", Toast.LENGTH_SHORT).show();
            }
        }


    }
}
