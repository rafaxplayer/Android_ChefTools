package rafaxplayer.cheftools.menus.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.models.Menu;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;


public class MenuNewEdit_Fragment extends Fragment {

    @BindView(R.id.editnamemenu)
    EditText Nametxt;
    @BindView(R.id.editEntrantes)
    EditText Entrantestxt;
    @BindView(R.id.editPrimeros)
    EditText Primerostxt;
    @BindView(R.id.editsegundos)
    EditText Segundostxt;
    @BindView(R.id.editpostres)
    EditText Postrestxt;
    @BindView(R.id.editcomment)
    EditText Comentariostxt;
    @BindView(R.id.buttonSave)
    Button save;
    private SqliteWrapper sql;
    private int ID;

    public static MenuNewEdit_Fragment newInstance(int id) {
        MenuNewEdit_Fragment f = new MenuNewEdit_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", id);

        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menu_new_edit, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save();
            }
        });
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
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();

        sql.open();

        if (this.ID != 0) {
            displayWithId(this.ID);
        } else {
            ((BaseActivity) getActivity()).setTittleDinamic(getString(R.string.menu_new_recipe));
        }
    }

    private void save() {

        if (!sql.IsOpen()) {
            sql.open();
        }

        Menu men = new Menu(Nametxt.getText().toString(),
                Entrantestxt.getText().toString(),
                Primerostxt.getText().toString(),
                Segundostxt.getText().toString(),
                Postrestxt.getText().toString(),
                Comentariostxt.getText().toString(),
                GlobalUttilities.getDateTime()

        );

        if (this.ID == 0) {
            if (Nametxt.getText().length() == 0) {

                Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();
                GlobalUttilities.animateView(getActivity(), Nametxt);
                return;
            }
            if (sql.CheckIsDataAlreadyInDBorNot(DBHelper.TABLE_MENUSCARTAS, DBHelper.NAME, Nametxt.getText().toString())) {

                Toast.makeText(getActivity(), getString(R.string.dlgerror_dataexist), Toast.LENGTH_LONG).show();
                GlobalUttilities.animateView(getActivity(), Nametxt);
                getActivity().onBackPressed();
                return;
            }

            long id = sql.InsertObject(men);
            if (id != -1) {

                new MaterialDialog.Builder(getActivity())
                        .title(getString(R.string.dlgsucces_saved))
                        .content(getString(R.string.dlgnew_saved))

                        .positiveText(R.string.yes)

                        .negativeText(R.string.not)

                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                refresh();
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

            long count = sql.UpdateWithId(men, this.ID);
            if (count > 0) {

                Toast.makeText(getActivity(), getString(R.string.dlgok_update), Toast.LENGTH_LONG).show();
                if (getActivity().getSupportFragmentManager().findFragmentByTag("detalle") != null) {
                    getActivity().getSupportFragmentManager().findFragmentByTag("detalle").onResume();
                }
                getActivity().onBackPressed();
            }

        }

        sql.close();

    }

    private void displayWithId(int id) {
        if (!sql.IsOpen()) {
            sql.open();
        }
        Menu men = (Menu) sql.SelectWithId("Menu", DBHelper.TABLE_MENUSCARTAS, id);
        if (men != null) {
            Nametxt.setText(men.getName());
            Entrantestxt.setText(men.getEntrantes());
            Primerostxt.setText(men.getPrimeros());
            Segundostxt.setText(men.getSegundos());
            Postrestxt.setText(men.getPostre());
            Comentariostxt.setText(men.getComentario());

            this.ID = id;

            ((BaseActivity) getActivity()).setTittleDinamic(getString(R.string.edit_menu) + " " + men.getName());
        }
        sql.close();
    }

    private void refresh() {
        Nametxt.setText("");
        Entrantestxt.setText("");
        Primerostxt.setText("");
        Segundostxt.setText("");
        Postrestxt.setText("");
        Comentariostxt.setText("");
    }
}
