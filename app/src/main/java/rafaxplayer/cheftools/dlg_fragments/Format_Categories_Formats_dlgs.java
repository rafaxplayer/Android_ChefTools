package rafaxplayer.cheftools.dlg_fragments;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import rafaxplayer.cheftools.Globalclasses.Categorys_Formats_Adapter;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.SqliteWrapper;


public class Format_Categories_Formats_dlgs extends DialogFragment {
    private SqliteWrapper sql;
    private RecyclerView listItems;
    private Categorys_Formats_Adapter adp;
    private EditText editEntry;
    private ImageButton addCategory;
    private TextView texttitle;
    private String sqltable;
    private int tittle;

    public Format_Categories_Formats_dlgs() {

    }

    public static Format_Categories_Formats_dlgs newInstance(String Table, int tittle) {
        Format_Categories_Formats_dlgs f = new Format_Categories_Formats_dlgs();
        Bundle args = new Bundle();
        args.putString("table", Table);
        args.putInt("tittle", tittle);
        f.setArguments(args);
        return f;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SqliteWrapper(getActivity());
        sql.open();
        if (getArguments() != null) {
            this.sqltable = getArguments().getString("table");
            this.tittle = getArguments().getInt("tittle");
        }
        if (!GlobalUttilities.isScreenLarge(getActivity())) {
            getActivity().setRequestedOrientation(
                    ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dlg_categories_products, container, false);
        editEntry = (EditText) v.findViewById(R.id.editItem);
        addCategory = (ImageButton) v.findViewById(R.id.addCtegory);
        listItems = (RecyclerView) v.findViewById(R.id.list_items);
        texttitle = (TextView) v.findViewById(R.id.texttitle);
        listItems.setHasFixedSize(true);
        listItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        listItems.setItemAnimator(new DefaultItemAnimator());
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addEntry(editEntry.getText().toString());
            }
        });
        texttitle.setText(getString(tittle));
        texttitle.setTextColor(getResources().getColor(R.color.textTitletColor));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!sql.IsOpen()) {
            sql.open();
        }
        try {
            adp = new Categorys_Formats_Adapter(getActivity(), sql.getFormatsOrCategorysData(this.sqltable), this.sqltable, sql);
            listItems.setAdapter(adp);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null){
            return;
        }
        GlobalUttilities.setDialogFragmentSize(this);

    }

    private void addEntry(String Name) {
        if (Name.length() > 0) {
            ((Categorys_Formats_Adapter) listItems.getAdapter()).additem(Name);
            editEntry.setText("");
            GlobalUttilities.ocultateclado(getActivity(), editEntry);
            onResume();
        } else {
            Toast.makeText(getActivity(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();
            GlobalUttilities.animateView(getActivity(), editEntry);
        }
    }

}
