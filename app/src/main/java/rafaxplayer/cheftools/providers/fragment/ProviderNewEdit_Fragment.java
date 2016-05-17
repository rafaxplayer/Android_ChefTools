package rafaxplayer.cheftools.providers.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.Supplier;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProviderNewEdit_Fragment extends Fragment {
    private SqliteWrapper sql;
    private EditText Nametxt;
    private EditText Telefonotxt;
    private EditText Emailtxt;
    private EditText Direcciontxt;
    private EditText Categoriatxt;

    private EditText Comentariostxt;
    private Button save;
    private int ID;

    public static ProviderNewEdit_Fragment newInstance(int id) {
        ProviderNewEdit_Fragment f = new ProviderNewEdit_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", id);

        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_provider_new_edit, container, false);
        Nametxt = (EditText) v.findViewById(R.id.editnameprovider);
        Telefonotxt = (EditText) v.findViewById(R.id.editTelefono);
        Emailtxt=(EditText) v.findViewById(R.id.editEmail);
        Direcciontxt = (EditText) v.findViewById(R.id.editDireccion);
        Categoriatxt = (EditText) v.findViewById(R.id.editCategoria);

        Comentariostxt = (EditText) v.findViewById(R.id.editcomment);
        save = (Button) v.findViewById(R.id.buttonSave);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save();
            }
        });
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SqliteWrapper(getActivity());
        sql.open();

        if (getArguments() != null) {
            this.ID = getArguments().getInt("id");
        }
        this.setRetainInstance(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!sql.IsOpen()) {
            sql.open();
        }

        if (this.ID != 0) {
            displayWithId(this.ID);
        } else {
            ((BaseActivity) getActivity()).setTittleDinamic(getString(R.string.menu_new_provider));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_save, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            save();
            return true;
        }
        return onOptionsItemSelected(item);
    }

    private void save() {

        if (!sql.IsOpen()) {
            sql.open();
        }


        Supplier pro = new Supplier(Nametxt.getText().toString(),
                Telefonotxt.getText().toString(),
                Emailtxt.getText().toString(),
                Direcciontxt.getText().toString(),
                Comentariostxt.getText().toString(),
                Categoriatxt.getText().toString()
        );

        if (this.ID == 0) {
            if (TextUtils.isEmpty(Nametxt.getText())) {

                Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();
                GlobalUttilities.animateView(getActivity(),Nametxt);
                return;
            }
            if(sql.CheckIsDataAlreadyInDBorNot(DBHelper.TABLE_PROVEEDORES,DBHelper.NAME,Nametxt.getText().toString())){

                Toast.makeText(getActivity(), getString(R.string.dlgerror_dataexist), Toast.LENGTH_LONG).show();
                GlobalUttilities.animateView(getActivity(), Nametxt);
                return;
            }
            long id = sql.InsertObject(pro);
            if (id != -1) {

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.dlgsucces_saved)
                        .content(R.string.dlgnew_saved)
                        .positiveText(R.string.yes)

                        .negativeText(R.string.not)

                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                refresh();
                                Nametxt.requestFocus();
                                dialog.dismiss();
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                getActivity().onBackPressed();
                                dialog.dismiss();
                            }
                        })
                        .show();
            }

        } else {

            long count = sql.UpdateWithId(pro, this.ID);
            if (count > 0) {

                Toast.makeText(getActivity(), getString(R.string.dlgok_update), Toast.LENGTH_LONG).show();
                if (((ProviderDetalle_Fragment) getActivity().getSupportFragmentManager().findFragmentByTag("detalle")) != null) {
                    ((ProviderDetalle_Fragment) getActivity().getSupportFragmentManager().findFragmentByTag("detalle")).onResume();
                }
                getActivity().onBackPressed();
            }

        }
        sql.close();
    }

    public void displayWithId(int id){

        if (!sql.IsOpen()){
            sql.open();
        }

        Supplier pro = (Supplier) sql.SelectWithId("Provider", DBHelper.TABLE_PROVEEDORES, id);
        if (pro != null) {
            Nametxt.setText(pro.getName());
            Telefonotxt.setText(pro.getTelefono());
            Emailtxt.setText(pro.getEmail());
            Direcciontxt.setText(pro.getDireccion());
            Categoriatxt.setText(pro.getCategoria());
            Comentariostxt.setText(pro.getComentario());
            this.ID = id;

            ((BaseActivity) getActivity()).setTittleDinamic(getString(R.string.menu_edit_provider) + " " + pro.getName());
        }
        sql.close();
    }

    public void refresh() {

        Nametxt.setText("");
        Telefonotxt.setText("");
        Direcciontxt.setText("");
        Emailtxt.setText("");
        Categoriatxt.setText("");
        Comentariostxt.setText("");
        this.ID = 0;

    }

    @Override
    public void onPause() {
        super.onPause();
        sql.close();
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
}
