package rafaxplayer.cheftools.recipes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.Web_Fragment;
import rafaxplayer.cheftools.recipes.fragments.NewEditRecipe_Fragment;


public class NewEditRecipe_Activity extends BaseActivity {

    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, NewEditRecipe_Fragment.newInstance(getIntent().getIntExtra("id", 0)), "neweditrecipe")
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

        return getString(R.string.activity_neweditmenu);
    }


    public void showWebfRm() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, new Web_Fragment(), "web");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();

            NewEditRecipe_Fragment fr = (NewEditRecipe_Fragment) getSupportFragmentManager().findFragmentByTag("neweditrecipe");

            if (requestCode == GlobalUttilities.SELECT_PICTURE) {

                if (selectedImageUri != null) {
                    Log.e("selectedImageUri", data.getData().toString());
                    if (fr != null) {
                        fr.updateImage(selectedImageUri);
                    }
                }
            } else if (requestCode == GlobalUttilities.RECIPE_WITH_CAPTURE) {
                Log.e("PHOTO", "RECIPE_WITH_CAPTURE");
                if (selectedImageUri != null) {
                    Log.e("selectedImageUri", selectedImageUri.toString());
                    if (fr != null) {
                        fr.refresh();
                        fr.updateImage(selectedImageUri);
                    }
                }
            } else if (requestCode == GlobalUttilities.CAPTURE_ID) {

                Bitmap photo = (Bitmap) data.getExtras().get("data");

                if (photo != null) {
                    Uri imageBmpUri = GlobalUttilities.getBmpUri(this, photo);
                    fr.updateImage(imageBmpUri);
                }

            }
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        NewEditRecipe_Fragment fr = (NewEditRecipe_Fragment) getSupportFragmentManager().findFragmentByTag("neweditrecipe");

        switch (requestCode) {

            case GlobalUttilities.PERMISSION_GALLERY:

                if (permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        fr.sendIntentPermission(GlobalUttilities.PERMISSION_GALLERY);

                    } else {
                        Toast.makeText(this, "No concedio su permiso", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case GlobalUttilities.PERMISSION_PHOTO:
                if (permissions[0].equals(Manifest.permission.CAMERA)) {

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                        fr.sendIntentPermission(GlobalUttilities.PERMISSION_PHOTO);

                    } else {
                        Toast.makeText(this, "No concedio su permiso", Toast.LENGTH_SHORT).show();
                    }
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }

    }
}
