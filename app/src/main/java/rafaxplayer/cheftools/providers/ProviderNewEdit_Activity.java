package rafaxplayer.cheftools.providers;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.providers.fragment.ProviderNewEdit_Fragment;

public class ProviderNewEdit_Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, ProviderNewEdit_Fragment.newInstance(getIntent().getIntExtra("id", 0)), "neweditmenu")
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

        return getString(R.string.activity_newedit_provider);
    }


}
