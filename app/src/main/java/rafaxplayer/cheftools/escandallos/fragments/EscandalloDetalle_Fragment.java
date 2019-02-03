package rafaxplayer.cheftools.escandallos.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.models.Escandallo;
import rafaxplayer.cheftools.Globalclasses.models.Escandallo_Product;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.escandallos.EscandallosDetalle_Activity;
import rafaxplayer.cheftools.escandallos.Escandallos_Activity;

public class EscandalloDetalle_Fragment extends Fragment {
    @BindView(R.id.escandallonamedetalle)
    TextView escandalloName;
    @BindView(R.id.dateescandallo)
    TextView escandalloDate;
    @BindView(R.id.comments)
    EditText escandalloComments;
    @BindView(R.id.list_items)
    RecyclerView listProducts;
    @BindView(R.id.detalletexttotal)
    TextView escandalloCoste;
    @BindString(R.string.cost_total)
    String costTotal;

    private int ID;
    private SqliteWrapper sql;

    public EscandalloDetalle_Fragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static EscandalloDetalle_Fragment newInstance(int id) {
        EscandalloDetalle_Fragment fragment = new EscandalloDetalle_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", id);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SqliteWrapper(getActivity());
        sql.open();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_escandallo_detalle, container, false);
        ButterKnife.bind(this, v);
        listProducts.setHasFixedSize(true);
        listProducts.setLayoutManager(new LinearLayoutManager(getActivity()));
        listProducts.setItemAnimator(new DefaultItemAnimator());
        return v;
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
        MenuItem share = menu.findItem(R.id.share);
        MenuItem edit = menu.findItem(R.id.edit);
        share.setTitle(R.string.share_escandallo);
        edit.setTitle(R.string.edit_escandallo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                if (ID != 0) {
                    boolean islayout = (getActivity().getSupportFragmentManager().findFragmentById(R.id.detalleescandallo) != null);

                    if (getResources().getBoolean(R.bool.dual_pane) && islayout) {
                        ((Escandallos_Activity) getActivity()).showMenuEdit(ID);
                    } else {
                        ((EscandallosDetalle_Activity) getActivity()).showMenuEdit(ID);
                    }

                }

                break;
            case R.id.share:
                if (!sql.IsOpen()) {
                    sql.open();
                }
                if (ID != 0) {
                    try {
                        Escandallo esc = (Escandallo) sql.SelectWithId("Escandallo", DBHelper.TABLE_ESCANDALLOS, ID);
                        ArrayList<Escandallo_Product> products = new ArrayList<>();
                        String strShared = GlobalUttilities.shareEscandalloText(getActivity(), esc, products);
                        GlobalUttilities.shareIntenttext(getActivity(), strShared);

                    } catch (Exception e) {
                        Log.e("Error :", e.getMessage());
                    }

                }

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void displayWithId(int id) {

        if (!sql.IsOpen()) {
            sql.open();
        }

        Escandallo esc = (Escandallo) sql.SelectWithId("Escandallo", DBHelper.TABLE_ESCANDALLOS, id);

        if (esc != null) {
            escandalloName.setText(esc.getName());
            escandalloComments.setText(esc.getComment());
            escandalloDate.setText(esc.getFecha());
            escandalloCoste.setText(String.format("%s %s%s", costTotal, String.valueOf(esc.getCostetotal()), "€"));
            ArrayList<Escandallo_Product> listpr = (ArrayList<Escandallo_Product>) (Object) sql.getProductListWithListId("Escandallo_product", esc.getId());
            listProducts.setAdapter(new RecyclerAdapter(listpr));
        }

        this.ID = id;
        setHasOptionsMenu(true);
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        private final ArrayList<Escandallo_Product> mDataset;

        RecyclerAdapter(ArrayList<Escandallo_Product> myDataset) {
            mDataset = myDataset;
        }

        @NonNull
        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_simple, parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapter.ViewHolder viewHolder, int i) {
            Escandallo_Product escPr = mDataset.get(i);
            viewHolder.txtProd.setText(escPr.getProductoname());
            viewHolder.txtCantidad.setText(String.format("%s%s", escPr.getCantidad(), escPr.getFormato()));
            viewHolder.txtCoste.setText(String.format("%s€", String.valueOf(escPr.getCoste())));
        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final TextView txtProd;
            final TextView txtCantidad;
            final TextView txtCoste;

            ViewHolder(View v) {
                super(v);
                v.findViewById(R.id.ButtonDeleteProduct).setVisibility(View.GONE);
                v.findViewById(R.id.ButtonEditProduct).setVisibility(View.GONE);
                txtProd = v.findViewById(R.id.text1);
                txtCantidad = v.findViewById(R.id.text2);
                txtCoste = v.findViewById(R.id.text3);
            }
        }
    }

}
