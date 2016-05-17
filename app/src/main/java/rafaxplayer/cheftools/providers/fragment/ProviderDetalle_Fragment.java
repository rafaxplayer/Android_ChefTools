package rafaxplayer.cheftools.providers.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.Supplier;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.providers.ProviderDetalle_Activity;
import rafaxplayer.cheftools.providers.Providers_Activity;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProviderDetalle_Fragment extends Fragment {
    private SqliteWrapper sql;
    private int ID;
    private TextView proName;
    private TextView proTelefono;
    private TextView proEmail;
    private TextView proDireccion;
    private TextView proCategoria;
    private TextView proCommentario;

    public static ProviderDetalle_Fragment newInstance(int id) {
        ProviderDetalle_Fragment fr = new ProviderDetalle_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fr.setArguments(args);
        return fr;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_provider_detalle, container, false);
        proName=(TextView)v.findViewById(R.id.providername);
        proTelefono=(TextView)v.findViewById(R.id.providerTelefono);
        proEmail=(TextView) v.findViewById(R.id.providerEmail);
        proDireccion=(TextView)v.findViewById(R.id.providerDireccion);
        proCategoria=(TextView)v.findViewById(R.id.providerCategory);
        proCommentario=(TextView)v.findViewById(R.id.providerComment);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        proTelefono.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (proTelefono.getText().length() > 0)
                    GlobalUttilities.call(getActivity(), proTelefono.getText().toString());

            }
        });
        proEmail.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (proEmail.getText().length() > 0)
                    GlobalUttilities.sendEmail(getActivity(), proEmail.getText().toString());

            }
        });


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SqliteWrapper(getActivity());
        sql.open();

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
    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detalle, menu);
        MenuItem share=menu.findItem(R.id.share);
        MenuItem edit=menu.findItem(R.id.edit);
        share.setTitle(R.string.menu_share_supplier);
        edit.setTitle(R.string.menu_edit_provider);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.edit:
                if (ID != 0) {
                    Boolean islayout = (getActivity().getSupportFragmentManager().findFragmentById(R.id.detalleprovider) != null);
                    if (getResources().getBoolean(R.bool.dual_pane) && islayout) {
                        ((Providers_Activity) getActivity()).showMenuEdit(ID);
                    } else {
                        ((ProviderDetalle_Activity) getActivity()).showMenuEdit(ID);
                    }

                }

                break;
            case R.id.share:
                if (!sql.IsOpen()) {
                    sql.open();
                }
                if (ID != 0) {
                    Supplier pro = (Supplier)sql.SelectWithId("Provider", DBHelper.TABLE_PROVEEDORES, ID);
                    String sharedStr = GlobalUttilities.shareDataText(getActivity(), pro);
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
    public void displayWithId(int id){
        if (!sql.IsOpen()) {
            sql.open();
        }
        Supplier pro = (Supplier)sql.SelectWithId("Provider", DBHelper.TABLE_PROVEEDORES,id);
        if (pro != null) {

            proName.setText(pro.getName());
            proTelefono.setText(pro.getTelefono());
            proEmail.setText(pro.getEmail());
            proDireccion.setText(pro.getDireccion());
            proCategoria.setText(pro.getCategoria());
            proCommentario.setText(pro.getComentario());

        }
        this.ID = id;
        setHasOptionsMenu(true);

    }
}
