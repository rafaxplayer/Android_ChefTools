package rafaxplayer.cheftools.recipes.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.models.Recipe;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.recipes.GalleryRecipesActivity;
import rafaxplayer.cheftools.recipes.NewEditRecipe_Activity;
import rafaxplayer.cheftools.recipes.Recipes_Activity;

public class RecipesList_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.list_items)
    RecyclerView listRecipes;
    @BindView(R.id.layoutempty)
    LinearLayout empty;
    @BindView(R.id.emptyText)
    TextView emptytxt;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.fabGallery)
    FloatingActionButton fabGallery;

    private OnSelectedrecipeCallback mCallback;
    private ActionMode mActionMode;

    private SqliteWrapper sql;
    private Boolean recipesFound;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        NewsAdapter adp;

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_action_mode, menu);
            ((BaseActivity) getActivity()).hideToolbarContent(true);
            //((Recipes_Activity) getActivity()).getSupportActionBar().hide();

            adp = (NewsAdapter) listRecipes.getAdapter();

            return true;
        }


        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    int items = ((NewsAdapter) listRecipes.getAdapter()).getSelectedItemCount();
                    if (items > 0) {

                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.deleterecipetitle)
                                .content(getString(R.string.deleterecipesmsg).replace("###", String.valueOf(items)))
                                .theme(Theme.LIGHT)
                                .positiveText(R.string.yes)
                                .negativeText(R.string.cancel)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        adp.deleteSelectedItems();
                                        mode.finish();
                                        dialog.dismiss();
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }

                    return true;

                case R.id.action_edit:
                    if (adp.getSelectedItemCount() > 0) {
                        int pos = adp.getSelectedItems().get(0);
                        ((Recipes_Activity) getActivity()).showRecipeEdit((adp.mDataset.get(pos)).getId());

                    }
                    mode.finish();
                    return true;
                default:
                    mode.finish();
                    return false;
            }
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            ((NewsAdapter) listRecipes.getAdapter()).clearSelections();
            ((BaseActivity) getActivity()).hideToolbarContent(false);


        }
    };

    public RecipesList_Fragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, v);
        emptytxt.setText(getString(R.string.menu_new_recipe));
        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getActivity(), NewEditRecipe_Activity.class);
                in.putExtra("id", 0);
                in.putExtra("uri", "");
                startActivity(in);
            }
        });

        listRecipes.setHasFixedSize(true);
        listRecipes.setLayoutManager(new LinearLayoutManager(getActivity()));
        listRecipes.setItemAnimator(new DefaultItemAnimator());
        listRecipes.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                            @Override
                                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                                super.onScrolled(recyclerView, dx, dy);
                                                Log.e("scroll", String.valueOf(dy));
                                                if (dy >= 0 || dy <= 0  && fab.isShown())
                                                    fab.hide();
                                            }
                                            @Override
                                            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                                                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                                                    fab.show();
                                                }
                                                super.onScrollStateChanged(recyclerView, newState);
                                            }
                                        });
        swipeRefreshLayout = v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        onResume();
                                    }
                                }
        );

        fabGallery.setVisibility(View.VISIBLE);
        //fab.hide();

        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), GalleryRecipesActivity.class));
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listRecipes.smoothScrollToPosition(0);
            }
        });

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SqliteWrapper(getActivity());
        sql.open();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_list_recipes, menu);

        if (!recipesFound) {

            menu.findItem(R.id.search).setVisible(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.newrecipe:
                Intent in = new Intent(getActivity(), NewEditRecipe_Activity.class);
                in.putExtra("id", 0);
                in.putExtra("uri", "");
                startActivity(in);
                break;

            case R.id.search:

                new MaterialDialog.Builder(getActivity())
                        .title(R.string.search)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("Text to search...", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                ((NewsAdapter) listRecipes.getAdapter()).getFilter().filter(input);
                                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                                dialog.dismiss();
                            }

                        }).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnSelectedrecipeCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        sql.open();
        List<Recipe> lstRecipes = loadRecipes(null);
        if (lstRecipes.size() > 0) {
            recipesFound = true;
            empty.setVisibility(View.GONE);

        } else {
            recipesFound = false;
            empty.setVisibility(View.VISIBLE);
            empty.bringToFront();
        }
        listRecipes.setAdapter(new NewsAdapter(lstRecipes));
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    private List<Recipe> loadRecipes(String order) {

        return (List<Recipe>) (Object) sql.getAllObjects("Recipe", order);

    }

    @Override
    public void onPause() {
        sql.close();
        super.onPause();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        setHasOptionsMenu(true);
    }

    @Override
    public void onRefresh() {
        onResume();
    }

    public interface OnSelectedrecipeCallback {
        public void onSelectRecipe(int pid);
    }

    public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> implements Filterable {

        public Boolean searchResultsok;
        private List<Recipe> mDataset;
        private List<Recipe> listorigin;
        private FilesFilter filefilter;
        private SparseBooleanArray selectedItems;

        // Adapter's Constructor
        public NewsAdapter(List<Recipe> myDataset) {
            mDataset = myDataset;
            listorigin = myDataset;
            this.searchResultsok = false;
            selectedItems = new SparseBooleanArray();
        }


        public void toggleSelection(int pos, ImageView img) {

            if (selectedItems.get(pos, false)) {
                selectedItems.delete(pos);
                Picasso.get().load((mDataset.get(pos)).getImg()).placeholder(R.drawable.item_image_placeholder).into(img);

            } else {
                selectedItems.put(pos, true);
                if (mActionMode != null)
                    Picasso.get().load(R.drawable.checked).placeholder(R.drawable.item_image_placeholder).into(img);

            }

            if (mActionMode != null)
                mActionMode.setTitle(String.valueOf(getSelectedItemCount()) + " Selected");

        }


        private boolean getselectedItem(int position) {
            return selectedItems.get(position);
        }

        public void deleteItem(int pos) {

            int count = sql.DeleteWithId((mDataset.get(pos)).getId(), DBHelper.TABLE_RECETAS);

            if (count > 0) {
                mDataset.remove(pos);

            }
            empty.setVisibility(mDataset.size() > 0 ? View.GONE : View.VISIBLE);
            notifyItemRemoved(pos);
        }

        public void clearSelections() {

            selectedItems.clear();
            notifyDataSetChanged();
        }

        public int getSelectedItemCount() {

            return selectedItems.size();
        }


        public void deleteSelectedItems() {
            final List<Integer> items = getSelectedItems();

            for (int i = items.size() - 1; i >= 0; i--) {
                deleteItem(items.get(i));

            }

        }


        public List<Integer> getSelectedItems() {
            List<Integer> items =
                    new ArrayList<Integer>(selectedItems.size());
            for (int i = 0; i < selectedItems.size(); i++) {
                items.add(selectedItems.keyAt(i));
            }
            return items;
        }

        @Override
        public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // Create a new view by inflating the row item xml.
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list, parent, false);

            // Set the view to the ViewHolder
            ViewHolder holder = new ViewHolder(v);


            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            Picasso.get().load((mDataset.get(position)).getImg())
                    .resize(getResources().getDimensionPixelOffset(R.dimen.image_dimen_thumbnail), getResources().getDimensionPixelOffset(R.dimen.image_dimen_thumbnail))
                    .placeholder(R.drawable.item_image_placeholder).into(holder.img);
            holder.sName.setText((mDataset.get(position)).getName());
            holder.sCategory.setText((mDataset.get(position)).getCategoty());
            boolean state = selectedItems.get(position, false);
            holder.itemView.setSelected(state);
            if (mActionMode != null) {
                if (state) {
                    Picasso.get()
                            .load(R.drawable.checked).placeholder(R.drawable.item_image_placeholder)
                            .into(holder.img);
                } else {
                    Picasso.get()
                            .load((mDataset.get(position)).getImg())
                            .resize(getResources().getDimensionPixelOffset(R.dimen.image_dimen_thumbnail), getResources().getDimensionPixelOffset(R.dimen.image_dimen_thumbnail))
                            .placeholder(R.drawable.item_image_placeholder)
                            .into(holder.img);
                }

            }

        }

        @Override
        public int getItemCount() {

            return mDataset.size();
        }

        @Override
        public Filter getFilter() {

            if (filefilter == null) {
                filefilter = new FilesFilter();
            }
            return filefilter;

        }

        // Create the ViewHolder class to keep references to your views
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            @BindView(R.id.imageList)
            ImageView img;
            @BindView(R.id.text1)
            TextView sName;
            @BindView(R.id.text2)
            TextView sCategory;
            int ID;

            public ViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);
                v.setOnClickListener(this);
                v.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {

                if (mActionMode != null) {
                    v.setSelected(!v.isSelected());
                    toggleSelection(ViewHolder.this.getLayoutPosition(), img);
                    if (getResources().getBoolean(R.bool.dual_pane)) {
                        if (mCallback != null) {
                            mCallback.onSelectRecipe((mDataset.get(ViewHolder.this.getLayoutPosition())).getId());
                        }
                    }

                } else {

                    clearSelections();
                    toggleSelection(ViewHolder.this.getLayoutPosition(), img);
                    v.setSelected(true);
                    if (mCallback != null) {
                        mCallback.onSelectRecipe((mDataset.get(ViewHolder.this.getLayoutPosition())).getId());
                    }

                }

            }

            @Override
            public boolean onLongClick(View v) {

                if (mActionMode != null) {
                    return false;
                }
                clearSelections();
                mActionMode = getActivity().startActionMode(mActionModeCallback);
                v.setSelected(!v.isSelected());
                toggleSelection(ViewHolder.this.getLayoutPosition(), img);
                return true;

            }
        }

        private class FilesFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // We implement here the filter logic
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = mDataset;
                    results.count = mDataset.size();
                } else {

                    List<Recipe> nFilesList = new ArrayList<Recipe>();

                    for (Recipe p : mDataset) {
                        if (p.getName().toLowerCase().contains(constraint.toString().toLowerCase()))
                            nFilesList.add(p);
                    }

                    results.values = nFilesList;
                    results.count = nFilesList.size();

                }
                return results;

            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0) {
                    mDataset = listorigin;
                    Toast.makeText(getActivity(), getString(R.string.noresults), Toast.LENGTH_LONG).show();
                    searchResultsok = false;
                } else {
                    mDataset = (ArrayList<Recipe>) results.values;
                    searchResultsok = true;
                }
                notifyDataSetChanged();
            }
        }
    }
}
