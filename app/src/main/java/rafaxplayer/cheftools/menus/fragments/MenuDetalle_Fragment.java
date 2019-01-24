package rafaxplayer.cheftools.menus.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.models.Menu;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.menus.MenuDetalle_Activity;
import rafaxplayer.cheftools.menus.Menus_Activity;

public class MenuDetalle_Fragment extends Fragment {

    @BindView(R.id.menunamedetalle)
    TextView menuName;
    @BindView(R.id.menuEntrantes)
    TextView menuEntrantes;
    @BindView(R.id.menuPrimeros)
    TextView menuPrimeros;
    @BindView(R.id.menuSegundos)
    TextView menuSegundos;
    @BindView(R.id.menuPostres)
    TextView menuPostres;
    @BindView(R.id.menuComentario)
    TextView menuComentarios;
    private SqliteWrapper sql;
    private int ID;

    public static MenuDetalle_Fragment newInstance(int id) {
        MenuDetalle_Fragment fr = new MenuDetalle_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fr.setArguments(args);
        return fr;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu_detalle, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SqliteWrapper(getActivity());
        sql.open();

    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detalle, menu);
        MenuItem share = menu.findItem(R.id.share);
        MenuItem edit = menu.findItem(R.id.edit);
        share.setTitle(R.string.share_menu);
        edit.setTitle(R.string.edit_menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                if (ID != 0) {
                    Boolean islayout = (getActivity().getSupportFragmentManager().findFragmentById(R.id.detallemenu) != null);
                    if (getResources().getBoolean(R.bool.dual_pane) && islayout) {
                        ((Menus_Activity) getActivity()).showMenuEdit(ID);
                    } else {
                        ((MenuDetalle_Activity) getActivity()).showMenuEdit(ID);
                    }

                }

                break;
            case R.id.share:
                if (!sql.IsOpen()) {
                    sql.open();
                }
                if (ID != 0) {
                    Menu men = (Menu) sql.SelectWithId("Menu", DBHelper.TABLE_MENUSCARTAS, ID);
                    String sharedStr = GlobalUttilities.shareDataText(getActivity(), men);
                    GlobalUttilities.shareIntenttext(getActivity(),sharedStr);
                }

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getArguments() != null) {
            displayWithId(getArguments().getInt("id"));
            this.ID = getArguments().getInt("id");
        }
        setHasOptionsMenu(!(this.ID == 0));

    }

    public void displayWithId(int id) {
        if (!sql.IsOpen()) {
            sql.open();
        }
        Menu men = (Menu) sql.SelectWithId("Menu", DBHelper.TABLE_MENUSCARTAS, id);
        if (men != null) {

            menuName.setText(men.getName());
            menuEntrantes.setText(men.getEntrantes());
            menuPrimeros.setText(men.getPrimeros());
            menuSegundos.setText(men.getSegundos());
            menuPostres.setText(men.getPostre());
            menuComentarios.setText(men.getComentario());
        }
        this.ID = id;
        setHasOptionsMenu(true);

    }
}
