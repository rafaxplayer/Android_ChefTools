package rafaxplayer.cheftools.products;

import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.products.fragments.ProductosMannager_Fragment;

public class Products_Activity extends BaseActivity implements ProductosMannager_Fragment.OnSelectedCallback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState==null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, ProductosMannager_Fragment.newInstance(0,false), "products");
            ft.commit();
        }
        if(!GlobalUttilities.isScreenLarge(getApplicationContext())) {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_template_for_all;
    }
    @Override
    protected String getCustomTitle() {

        return getString(R.string.products);
    }

    @Override
    public void onSelect(int pid) {

    }
}
