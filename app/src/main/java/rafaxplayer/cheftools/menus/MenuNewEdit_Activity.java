package rafaxplayer.cheftools.menus;

import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.menus.fragments.MenuNewEdit_Fragment;

public class MenuNewEdit_Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, MenuNewEdit_Fragment.newInstance(getIntent().getIntExtra("id", 0)), "neweditmenu")
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
}
