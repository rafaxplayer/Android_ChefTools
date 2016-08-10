package rafaxplayer.cheftools.providers;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.providers.fragment.ProviderDetalle_Fragment;
import rafaxplayer.cheftools.providers.fragment.ProvidersList_Fragment;

public class Providers_Activity extends BaseActivity implements ProvidersList_Fragment.OnSelectedCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new ProvidersList_Fragment(), "listprovider");
            ft.commit();
        }
    }

    @Override
    protected int getLayoutResourceId() {
        if (getResources().getBoolean(R.bool.dual_pane)) {
            return R.layout.activity_providers;
        } else {
            return R.layout.activity_template_for_all;
        }

    }

    @Override
    protected String getCustomTitle() {

        return getString(R.string.activity_providers);
    }

    public void showMenuEdit(int num) {
        Intent in = new Intent(getApplicationContext(), ProviderNewEdit_Activity.class);
        in.putExtra("id", num);
        startActivity(in);
    }

    @Override
    public void onSelect(int id) {

        ProviderDetalle_Fragment frDetalle = (ProviderDetalle_Fragment) getSupportFragmentManager().findFragmentById(R.id.detalleprovider);

        if (frDetalle != null && frDetalle.isInLayout()) {
            frDetalle.displayWithId(id);
        } else {
            Intent i = new Intent(this, ProviderDetalle_Activity.class);
            i.putExtra("id", id);

            startActivity(i);
        }
    }


}
