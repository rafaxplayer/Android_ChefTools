package rafaxplayer.cheftools.menus;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.menus.fragments.MenuDetalle_Fragment;
import rafaxplayer.cheftools.menus.fragments.MenuNewEdit_Fragment;

public class MenuDetalle_Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int ID = getIntent().getExtras().getInt("id");

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, MenuDetalle_Fragment.newInstance(ID), "detallemenu")
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

        return getString(R.string.activity_detallemenu);
    }

    public void showMenuEdit(int id) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container, MenuNewEdit_Fragment.newInstance(id), "neweditmenu");
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.addToBackStack(null);
        ft.commit();


    }
}
