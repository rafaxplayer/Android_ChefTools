package rafaxplayer.cheftools.providers.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.models.Supplier;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;

public class ProviderNewEdit_Fragment extends Fragment {
    @BindView(R.id.editnameprovider)
    EditText Nametxt;
    @BindView(R.id.editTelefono)
    EditText Telefonotxt;
    @BindView(R.id.editEmail)
    EditText Emailtxt;
    @BindView(R.id.editDireccion)
    EditText Direcciontxt;
    @BindView(R.id.editCategoria)
    EditText Categoriatxt;
    @BindView(R.id.editcomment)
    EditText Comentariostxt;
    @BindView(R.id.buttonSave)
    Button save;
    @BindView(R.id.searchContact)
    ImageButton searchContact;

    private SqliteWrapper sql;
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
        ButterKnife.bind(this, v);

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
        searchContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(contactPickerIntent, GlobalUttilities.CONTACT_SELECT);
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
                GlobalUttilities.animateView(getActivity(), Nametxt);
                return;
            }
            if (sql.CheckIsDataAlreadyInDBorNot(DBHelper.TABLE_PROVEEDORES, DBHelper.NAME, Nametxt.getText().toString())) {

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
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                refresh();
                                Nametxt.requestFocus();
                                dialog.dismiss();
                            }
                        })

                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
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

    public void displayWithId(int id) {

        if (!sql.IsOpen()) {
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == GlobalUttilities.CONTACT_SELECT) {

                Uri result = data.getData();
                String email = getContactData(result,
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + "=?",
                        new String[]{result.getLastPathSegment()},
                        ContactsContract.CommonDataKinds.Email.DATA
                );

                String phone = getContactData(result,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        new String[]{result.getLastPathSegment()},
                        ContactsContract.CommonDataKinds.Phone.DATA
                );

                String name = getContactData(result,
                        result,
                        null,
                        null,
                        ContactsContract.Contacts.DISPLAY_NAME
                );

                Nametxt.setText(name);
                Emailtxt.setText(email);
                Telefonotxt.setText(phone);

                if (email.length() == 0) {
                    Toast.makeText(getActivity(), "Email not found", Toast.LENGTH_SHORT).show();
                }
                if (phone.length() == 0) {
                    Toast.makeText(getActivity(), "Phone not found", Toast.LENGTH_SHORT).show();
                }
                if (name.length() == 0) {
                    Toast.makeText(getActivity(), "Name not found", Toast.LENGTH_SHORT).show();
                }

            }
        } else {
            Log.e("Activity", "Failed to pick contact");
        }

    }


    private String getContactData(Uri data, Uri uri, String selection, String[] args, String column) {

        String infoContact = "";
        Cursor cursor = null;

        try {

            Log.v("INFO CONTACT", "Got a contact result: "
                    + data.toString());

            // query for everything email
            cursor = getActivity().getContentResolver().query(uri, null, selection, args, null);

            int Idx = cursor.getColumnIndex(column);
            // let's just get the first email
            if (cursor.moveToFirst()) {
                infoContact = cursor.getString(Idx);

                Log.v("INFO CONTACT", "Got name: " + infoContact);

            } else {
                Log.w("INFO CONTACT", "No results");
            }
        } catch (Exception e) {
            Log.e("INFO CONTACT", "Failed to get name data", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return infoContact;
    }
}
