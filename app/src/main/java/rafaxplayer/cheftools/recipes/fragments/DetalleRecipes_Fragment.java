package rafaxplayer.cheftools.recipes.fragments;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.Recipe;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.recipes.DetalleRecipes_Activity;
import rafaxplayer.cheftools.recipes.Recipes_Activity;

public class DetalleRecipes_Fragment extends Fragment {
    private int ID;
    private SqliteWrapper sql;
    @BindView(R.id.imgRecipedetalle)
    ImageView imageDetalle;
    @BindView(R.id.recipenamedetalle)
    TextView textNamedetalle;
    @BindView(R.id.titleUrl)
    TextView titleUrl;
    @BindView(R.id.recipecategorydetalle)
    TextView textCategorydetalle;
    @BindView(R.id.textIngredientes)
    TextView textIngredientsdetalle;
    @BindView(R.id.textElaboration)
    TextView textTEladetalle;
    @BindView(R.id.textUrl)
    TextView textUrldetalle;

    private String img = "";

    public DetalleRecipes_Fragment() {
        // Required empty public constructor
    }

    public static DetalleRecipes_Fragment newInstance(int id) {
        DetalleRecipes_Fragment fr = new DetalleRecipes_Fragment();
        Bundle args = new Bundle();
        args.putInt("id", id);
        fr.setArguments(args);
        return fr;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_recipe_detalle, container, false);
        ButterKnife.bind(this, v);


        textUrldetalle = (TextView) v.findViewById(R.id.textUrl);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        imageDetalle.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (DetalleRecipes_Fragment.this.img.length() > 0 && DetalleRecipes_Fragment.this.img != "null") {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(DetalleRecipes_Fragment.this.img), "image/*");
                    startActivity(intent);
                }
                return false;
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detalle, menu);
        MenuItem share = menu.findItem(R.id.share);
        MenuItem edit = menu.findItem(R.id.edit);
        share.setTitle(R.string.sharerecipe);
        edit.setTitle(R.string.menu_edit_recipe);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                if (ID != 0) {
                    Boolean islayout = (getActivity().getSupportFragmentManager().findFragmentById(R.id.detalle) != null);
                    if (getResources().getBoolean(R.bool.dual_pane) && islayout) {
                        ((Recipes_Activity) getActivity()).showRecipeEdit(ID);
                    } else {
                        ((DetalleRecipes_Activity) getActivity()).showRecipeEdit(ID);
                    }

                }
                break;
            case R.id.share:
                if (!sql.IsOpen()) {
                    sql.open();
                }
                if (ID != 0) {
                    Recipe rec = (Recipe) sql.SelectWithId("Recipe", DBHelper.TABLE_RECETAS, ID);
                    String sharedStr = GlobalUttilities.shareDataText(getActivity(), rec);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("*/*");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, sharedStr);
                    if (!rec.getImg().equals("null")) {
                        Uri uri = Uri.parse(rec.getImg());
                        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                    }
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.share_recipe_use)));
                }
                Toast.makeText(getActivity(), "Share", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public void displayWithId(int id) {
        if (!sql.IsOpen()) {
            sql.open();
        }
        Recipe rec = (Recipe) sql.SelectWithId("Recipe", DBHelper.TABLE_RECETAS, id);
        if (rec != null) {
            this.img = rec.getImg();
            Picasso.with(getActivity()).load(Uri.parse(rec.getImg()))
                    .placeholder(R.drawable.placeholder_recetas)
                    .resize(getResources().getDimensionPixelOffset(R.dimen.image_dimen_width), getResources().getDimensionPixelOffset(R.dimen.image_dimen_height))
                    .into(imageDetalle);
            textNamedetalle.setText(rec.getName());
            textCategorydetalle.setText(rec.getCategoty());
            textIngredientsdetalle.setText(rec.getIngredients());
            textTEladetalle.setText(rec.getElaboration());
            titleUrl.setVisibility(rec.getUrl().length() > 0 ? View.VISIBLE : View.GONE);
            textUrldetalle.setText(rec.getUrl());
        }
        this.ID = id;
        setHasOptionsMenu(true);

    }


}
