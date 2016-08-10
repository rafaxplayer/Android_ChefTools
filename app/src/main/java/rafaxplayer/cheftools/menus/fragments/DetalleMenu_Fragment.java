package rafaxplayer.cheftools.menus.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.Menu;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.menus.DetalleMenu_Activity;
import rafaxplayer.cheftools.menus.Menus_Activity;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetalleMenu_Fragment extends Fragment {
    private TextView menuName;
    private TextView menuEntrantes;
    private TextView menuPrimeros;
    private TextView menuSegundos;
    private TextView menuPostres;
    private TextView menuComentarios;
    private SqliteWrapper sql;
    private int ID;

    public static DetalleMenu_Fragment newInstance(int id) {
        DetalleMenu_Fragment fr = new DetalleMenu_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fr.setArguments(args);
        return fr;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu_detalle, container, false);
        menuName = (TextView) v.findViewById(R.id.menunamedetalle);
        menuEntrantes = (TextView) v.findViewById(R.id.menuEntrantes);
        menuPrimeros = (TextView) v.findViewById(R.id.menuPrimeros);
        menuSegundos = (TextView) v.findViewById(R.id.menuSegundos);
        menuPostres = (TextView) v.findViewById(R.id.menuPostres);
        menuComentarios = (TextView) v.findViewById(R.id.menuComentario);
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
                        ((DetalleMenu_Activity) getActivity()).showMenuEdit(ID);
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
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("*/*");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, sharedStr);

                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_recipe_use)));
                }
                //Toast.makeText(getActivity(), "Share", Toast.LENGTH_LONG).show();

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
