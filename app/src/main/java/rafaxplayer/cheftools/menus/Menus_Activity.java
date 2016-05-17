package rafaxplayer.cheftools.menus;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.menus.fragments.DetalleMenu_Fragment;
import rafaxplayer.cheftools.menus.fragments.MenusList_Fragment;

public class Menus_Activity extends BaseActivity implements MenusList_Fragment.OnSelectedCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.container, new MenusList_Fragment(), "listmenus");
            ft.commit();
        }

    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected int getLayoutResourceId() {
        if(getResources().getBoolean(R.bool.dual_pane)){
            return R.layout.activity_menus;
        }else{
            return R.layout.activity_template_for_all;
        }

    }
    @Override
    protected String getCustomTitle() {

        return getString(R.string.activity_menus);
    }

    public void showMenuEdit(int num) {
        Intent in = new Intent(getApplicationContext(), MenuNewEdit_Activity.class);
        in.putExtra("id", num);
        startActivity(in);
    }

    @Override
    public void onSelect(int id) {

        DetalleMenu_Fragment frDetalle = (DetalleMenu_Fragment) getSupportFragmentManager().findFragmentById(R.id.detallemenu);

        if (frDetalle != null && frDetalle.isInLayout()) {
            frDetalle.displayWithId(id);
        } else {
            Intent i = new Intent(this, DetalleMenu_Activity.class);
            i.putExtra("id", id);

            startActivity(i);
        }
    }
}
