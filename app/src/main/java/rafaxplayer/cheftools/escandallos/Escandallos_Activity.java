package rafaxplayer.cheftools.escandallos;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.models.Escandallo;
import rafaxplayer.cheftools.Globalclasses.models.Escandallo_Product;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.R;


public class Escandallos_Activity extends BaseActivity {
    @BindView(R.id.list_items)
    RecyclerView listProducts;
    @BindView(R.id.texttotal)
    TextView texttotal;
    @BindString(R.string.product_cost)
    String productCost;
    @BindString(R.string.cost_total)
    String costTotal;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.escandalloRecipe)
    EditText editnameRecipe;

    @OnClick(R.id.fab)
    public void submit(View view) {
        if (TextUtils.isEmpty(editnameRecipe.getText())) {
            Toast.makeText(getApplicationContext(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();

        } else {
            dialogNewProduct.show();
        }
    }


    private MaterialDialog dialogNewProduct;
    private EditText editnameProduct;
    private EditText editcostuni;
    private EditText editquantity;
    private Spinner spinerFormat;
    private TextView textviewuni;
    private TextView textFormat2;
    private TextView textCosteProducto;
    private double dataUni = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.bind(this);

        dialogNewProduct = createDilaogNewProduct();
        listProducts.setHasFixedSize(true);
        listProducts.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        listProducts.setItemAnimator(new DefaultItemAnimator());
        listProducts.setAdapter(new RecyclerAdapter(new ArrayList<Escandallo_Product>()));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_share, menu);
        MenuItem share = menu.findItem(R.id.action_share);
        share.setTitle(R.string.share_cost);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                if (listProducts.getAdapter().getItemCount() > 0) {
                    try {
                        Escandallo esc = new Escandallo();
                        esc.setName(editnameProduct.getText().toString());
                        esc.setDate(GlobalUttilities.getDateTime());
                        esc.setProducts(((RecyclerAdapter) listProducts.getAdapter()).getProducts());
                        esc.setCostetotal(((RecyclerAdapter) listProducts.getAdapter()).calculatecostetotal());
                        String strShared = GlobalUttilities.shareEscandalloText(getApplicationContext(), esc);

                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.putExtra(Intent.EXTRA_TEXT, strShared);
                        shareIntent.setType("text/plain");
                        startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.share_recipe_use)));

                    } catch (Exception e) {
                        Log.e("Error :", e.getMessage());
                    }

                } else {
                    Toast.makeText(Escandallos_Activity.this, "Error: No Cost product", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_escandallos;

    }

    @Override
    protected String getCustomTitle() {

        return getString(R.string.activity_escandallos);
    }

    private ArrayList<HashMap<String, Object>> generateSpinnerDta() {
        ArrayList<HashMap<String, Object>> lst = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> mp = new HashMap<String, Object>();
        mp.put("Cant", 1000);
        mp.put("Uni", "Gr");
        mp.put("Name", "KG");
        lst.add(mp);
        HashMap<String, Object> mp2 = new HashMap<String, Object>();
        mp2.put("Cant", 1000);
        mp2.put("Uni", "Cl");
        mp2.put("Name", "Ltr");
        lst.add(mp2);
        HashMap<String, Object> mp3 = new HashMap<String, Object>();
        mp3.put("Cant", 1);
        mp3.put("Uni", "Uni");
        mp3.put("Name", "Uni");
        lst.add(mp3);
        return lst;
    }

    private MaterialDialog createDilaogNewProduct() {
        return new MaterialDialog.Builder(Escandallos_Activity.this)
                .customView(R.layout.new_product_escandallo_dlg, true)
                .positiveText(R.string.done)
                .negativeText(R.string.cancel)
                .autoDismiss(false)
                .showListener(new DialogInterface.OnShowListener() {

                    @Override
                    public void onShow(DialogInterface dialog) {

                        View view = dialogNewProduct.getCustomView();
                        editnameProduct = ButterKnife.findById(view, R.id.editnameproduct);
                        editnameProduct.setText("");
                        editcostuni = ButterKnife.findById(view, R.id.editcostporuni);
                        editcostuni.setText("");
                        editcostuni.addTextChangedListener(new TextWatcher() {
                            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                            }

                            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                            }

                            public void afterTextChanged(Editable arg0) {
                                String str = editcostuni.getText().toString();
                                if (str.isEmpty()) return;
                                String str2 = GlobalUttilities.PerfectDecimal(str, 5, 2);

                                if (!str2.equals(str)) {
                                    editcostuni.setText(str2);
                                    int pos = editcostuni.getText().length();
                                    editcostuni.setSelection(pos);
                                }
                            }
                        });
                        editquantity = ButterKnife.findById(view, R.id.editunicant);
                        editquantity.setText("");
                        spinerFormat = ButterKnife.findById(view, R.id.spinnerFormat);
                        textCosteProducto = ButterKnife.findById(view, R.id.textcosteProduct);
                        final ArrayList<HashMap<String, Object>> spinnerFormatsData = generateSpinnerDta();
                        final SimpleAdapter AdapterProv = new SimpleAdapter(getApplicationContext(), spinnerFormatsData, R.layout.spinnerrow, new String[]{"Cant", "Name"}, new int[]{R.id.iddata, R.id.spinnertext});
                        spinerFormat.setAdapter(AdapterProv);
                        spinerFormat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                String newText = getString(R.string.product_cost).replace("#", ((TextView) view.findViewById(R.id.spinnertext)).getText().toString());
                                textviewuni.setText(newText);
                                textFormat2.setText((String) spinnerFormatsData.get(position).get("Uni"));
                                dataUni = Double.valueOf(((TextView) view.findViewById(R.id.iddata)).getText().toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                        textviewuni = ButterKnife.findById(view, R.id.textViewUni);
                        textFormat2 = ButterKnife.findById(view, R.id.textFormat2);
                        editcostuni = ButterKnife.findById(view, R.id.editcostporuni);
                        String newText = productCost.replace("#", "KG");
                        textviewuni.setText(newText);
                        textCosteProducto.setText(costTotal);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        if (TextUtils.isEmpty(editnameProduct.getText().toString())) {
                            Toast.makeText(getApplicationContext(), getString(R.string.dlgerror_namerecipe), Toast.LENGTH_LONG).show();
                            return;

                        }
                        if (TextUtils.isEmpty(editcostuni.getText().toString())) {
                            Toast.makeText(getApplicationContext(), getString(R.string.dlgerror_costuni), Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (TextUtils.isEmpty(editquantity.getText().toString())) {
                            Toast.makeText(getApplicationContext(), getString(R.string.dlgerror_quantity), Toast.LENGTH_LONG).show();
                            return;
                        }

                        try {

                            Double result = Double.valueOf(editquantity.getText().toString()) * Double.valueOf(editcostuni.getText().toString()) / Double.valueOf(dataUni);
                            String textResult = String.format("%s %s", costTotal, GlobalUttilities.FormatDecimal(result));
                            textCosteProducto.setText(textResult);

                            Escandallo_Product escpr = new Escandallo_Product();
                            escpr.setProductoname(editnameProduct.getText().toString());
                            escpr.setCostforuni(editcostuni.getText().toString());
                            escpr.setCantidad(editquantity.getText().toString());
                            escpr.setFormato(textFormat2.getText().toString());
                            escpr.setCoste(Double.valueOf(GlobalUttilities.FormatDecimal(result)));

                            ((RecyclerAdapter) listProducts.getAdapter()).addItem(escpr);
                            Double sum = ((RecyclerAdapter) listProducts.getAdapter()).calculatecostetotal();
                            texttotal.setText(String.format("%s %s%s", costTotal, GlobalUttilities.FormatDecimal(sum), "€"));

                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        public void run() {
                                            dialog.dismiss();
                                        }
                                    },
                                    5000);

                        } catch (NumberFormatException ex) {
                            Log.e("Error : ", ex.getMessage());

                        } catch (Exception e) {
                            Log.e("Error : ", e.getMessage());
                        }
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                        onBackPressed();
                    }
                })
                .build();

    }


    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

        public ArrayList<Escandallo_Product> mDataset;

        public RecyclerAdapter(ArrayList<Escandallo_Product> myDataset) {
            mDataset = myDataset;
        }

        public void deleteItem(int pos) {

            mDataset.remove(pos);

            Toast.makeText(getApplicationContext(), "Ok , item deleted", Toast.LENGTH_LONG).show();

            notifyItemRemoved(pos);
        }

        public void addItem(Escandallo_Product escPr) {

            mDataset.add(escPr);

            notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "Ok, Product added", Toast.LENGTH_LONG).show();

        }

        public ArrayList<Escandallo_Product> getProducts() {
            return mDataset;
        }

        public double calculatecostetotal() {

            double sum = 0;
            try {
                for (int i = 0; i < mDataset.size(); i++) {

                    sum = sum + mDataset.get(i).getCoste();
                }
            } catch (Exception e) {
                Log.e("error :", e.getMessage());
            }

            return Double.valueOf(sum);
        }

        @Override
        public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_simple, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerAdapter.ViewHolder viewHolder, int i) {
            Escandallo_Product escPr = (Escandallo_Product) mDataset.get(i);
            viewHolder.txtProd.setText(escPr.getProductoname());
            //Log.e("Cantidad", String.format("%s %s", escPr.getCantidad(), escPr.getFormato()));
            viewHolder.txtCantidad.setText(String.format("%s%s", escPr.getCantidad(), escPr.getFormato()));
            viewHolder.txtCoste.setText(String.format("%s€", String.valueOf(escPr.getCoste())));
        }


        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public ImageButton delButton;
            public TextView txtProd;
            public TextView txtCantidad;
            public TextView txtCoste;

            public ViewHolder(View v) {
                super(v);
                delButton = (ImageButton) v.findViewById(R.id.ButtonDeleteProduct);
                txtProd = (TextView) v.findViewById(R.id.text1);
                txtCantidad = (TextView) v.findViewById(R.id.text2);
                txtCoste = (TextView) v.findViewById(R.id.text3);
                delButton.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.ButtonDeleteProduct) {
                    deleteItem(ViewHolder.this.getLayoutPosition());
                    double sum = calculatecostetotal();
                    texttotal.setText(String.format("%s %s%s", costTotal, String.valueOf(sum), "€"));
                }

            }
        }
    }
}
