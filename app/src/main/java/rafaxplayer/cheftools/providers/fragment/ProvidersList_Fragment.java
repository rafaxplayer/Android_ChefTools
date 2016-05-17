package rafaxplayer.cheftools.providers.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.GlobalUttilities;
import rafaxplayer.cheftools.Globalclasses.Supplier;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.providers.ProviderNewEdit_Activity;
import rafaxplayer.cheftools.providers.Providers_Activity;


public class ProvidersList_Fragment extends DialogFragment implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView listProviders;
    private LinearLayout empty;
    private TextView emptytxt;
    private OnSelectedCallback mCallback;
    private ActionMode mActionMode;
    private FloatingActionButton fab;
    private SqliteWrapper sql;
    private Boolean itemsFound;
    private SwipeRefreshLayout swipeRefreshLayout;
    public ProvidersList_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        empty = (LinearLayout) v.findViewById(R.id.layoutempty);
        emptytxt = (TextView) v.findViewById(R.id.emptyText);
        emptytxt.setText(getString(R.string.menu_new_provider));
        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getActivity(), ProviderNewEdit_Activity.class);
                in.putExtra("id", 0);
                in.putExtra("uri", "");
                startActivity(in);
            }
        });
        listProviders = (RecyclerView) v.findViewById(R.id.list_items);
        listProviders.setHasFixedSize(true);
        listProviders.setLayoutManager(new LinearLayoutManager(getActivity()));
        listProviders.setItemAnimator(new DefaultItemAnimator());
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        onResume();
                                    }
                                }
        );
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.hide();
        fab.attachToRecyclerView(listProviders, new ScrollDirectionListener() {
            @Override
            public void onScrollDown() {
                fab.hide(true);
            }

            @Override
            public void onScrollUp() {
                fab.show(true);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listProviders.smoothScrollToPosition(0);
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
    public void onResume() {
        super.onResume();
        sql.open();
        List<Supplier> lstProv = loadValues(null);
        if (lstProv.size() > 0) {

            itemsFound = true;
            empty.setVisibility(View.GONE);

        } else {
            itemsFound = false;
            empty.setVisibility(View.VISIBLE);
        }
        //Log.e("listproviders size",String.valueOf(lstProv.size()));
        listProviders.setAdapter(new ProvidersAdapter(lstProv));
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_provider_list, menu);

        if (!itemsFound) {

            menu.findItem(R.id.search).setVisible(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.newprovider:
                Intent in = new Intent(getActivity(), ProviderNewEdit_Activity.class);
                in.putExtra("id", 0);

                startActivity(in);
                break;

            case R.id.search:
                final EditText inputSearch = new EditText(getActivity());

                inputSearch.setInputType(InputType.TYPE_CLASS_TEXT);
                inputSearch.setHint(getString(R.string.searchhint));
                inputSearch.setPadding(20, 20, 20, 20);
                inputSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        ((ProvidersAdapter) listProviders.getAdapter()).getFilter().filter(s);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                AlertDialogWrapper.Builder alertDialogBuilder = new AlertDialogWrapper.Builder(
                        getActivity());

                alertDialogBuilder.setView(inputSearch);
                alertDialogBuilder
                        .setPositiveButton(getString(R.string.done),
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        dialog.dismiss();
                                    }
                                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onResume();
                        dialog.dismiss();
                    }
                }).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnSelectedCallback) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public void onRefresh() {
        onResume();
    }

    public interface OnSelectedCallback {
        public void onSelect(int id);
    }

    private List<Supplier> loadValues(String order) {

        return (List<Supplier>) (Object) sql.getAllObjects("Provider", order);

    }

    @Override
    public void onPause() {
        super.onPause();
        sql.close();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
        setHasOptionsMenu(true);
    }

    public class ProvidersAdapter extends RecyclerView.Adapter<ProvidersAdapter.ViewHolder> implements Filterable {

        public Boolean searchResultsok;
        private List<Supplier> mDataset;
        private List<Supplier> listorigin;
        private FilesFilter filefilter;
        private SparseBooleanArray selectedItems;

        // Adapter's Constructor
        public ProvidersAdapter(List<Supplier> myDataset) {
            mDataset = myDataset;
            listorigin = myDataset;
            this.searchResultsok = false;
            selectedItems = new SparseBooleanArray();
        }


        public void toggleSelection(int pos, ImageView img) {

            if (selectedItems.get(pos, false)) {
                selectedItems.delete(pos);
                Picasso.with(getActivity()).load(R.drawable.providers).placeholder(R.drawable.providers).into(img);

            } else {
                selectedItems.put(pos, true);
                if (mActionMode != null)
                    Picasso.with(getActivity()).load(R.drawable.checked).placeholder(R.drawable.providers).into(img);

            }
            if (mActionMode != null)
                mActionMode.setTitle(String.valueOf(getSelectedItemCount()) + " Selected");

        }

        public void deleteItem(int pos) {

            int count = sql.DeleteWithId(((Supplier) mDataset.get(pos)).getId(), DBHelper.TABLE_PROVEEDORES);

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
        public ProvidersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
            // Create a new view by inflating the row item xml.
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_suppliers, parent, false);

            // Set the view to the ViewHolder
            ViewHolder holder = new ViewHolder(v);


            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(getActivity()).load(R.drawable.providers).into(holder.img);
            holder.sName.setText(((Supplier) mDataset.get(position)).getName());
            holder.sCat.setText(((Supplier) mDataset.get(position)).getCategoria());
            boolean state = selectedItems.get(position, false);
            holder.itemView.setSelected(state);
            if (mActionMode != null) {
                if (state) {
                    Picasso.with(getActivity())
                            .load(R.drawable.checked).placeholder(R.drawable.item_image_placeholder)
                            .into(holder.img);
                } else {
                    Picasso.with(getActivity())
                            .load(R.drawable.providers)
                            .resize(getResources().getDimensionPixelOffset(R.dimen.image_dimen_thumbnail), getResources().getDimensionPixelOffset(R.dimen.image_dimen_thumbnail))
                            .placeholder(R.drawable.item_image_placeholder)
                            .into(holder.img);
                }
            }
            if (getActivity().getResources().getBoolean(R.bool.dual_pane)) {
                holder.sCall.setVisibility(View.GONE);
            }

        }

        // Return the size of your dataset (invoked by the layout manager)
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
            public ImageView img;
            public ImageButton sCall;
            public TextView sName;
            public TextView sCat;

            int ID;

            public ViewHolder(View v) {
                super(v);
                img = (ImageView) v.findViewById(R.id.imageList);
                sName = (TextView) v.findViewById(R.id.text1);
                sCat = (TextView) v.findViewById(R.id.text2);
                sCall = (ImageButton) v.findViewById(R.id.ButtonCall);
                sCall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GlobalUttilities.call(getActivity(), mDataset.get(ViewHolder.this.getLayoutPosition()).getTelefono());
                    }
                });
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
                            mCallback.onSelect(((Supplier) mDataset.get(ViewHolder.this.getLayoutPosition())).getId());
                        }
                    }

                } else {
                    clearSelections();
                    toggleSelection(ViewHolder.this.getLayoutPosition(), img);
                    v.setSelected(true);
                    if (mCallback != null) {
                        mCallback.onSelect(((Supplier) mDataset.get(ViewHolder.this.getLayoutPosition())).getId());
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

                    List<Supplier> nFilesList = new ArrayList<Supplier>();

                    for (Supplier p : mDataset) {
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
                    mDataset = (ArrayList<Supplier>) results.values;
                    searchResultsok = true;
                }
                notifyDataSetChanged();
            }
        }
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        ProvidersAdapter adp;

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_action_mode, menu);
            //((Providers_Activity) getActivity()).getSupportActionBar().hide();
            ((BaseActivity)getActivity()).hideToolbarContent(true);
            adp = (ProvidersAdapter) listProviders.getAdapter();

            return true;
        }

        // Called each time the action mode is shown. Always called after onCreateActionMode, but
        // may be called multiple times if the mode is invalidated.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, android.view.Menu menu) {
            return false; // Return false if nothing is done
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    int items = ((ProvidersAdapter) listProviders.getAdapter()).getSelectedItemCount();
                    if (items > 0) {

                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.delete_provider)
                                .content(getString(R.string.deleterecipesmsg).replace("###", String.valueOf(items)))
                                .theme(Theme.LIGHT)
                                .positiveText(R.string.yes)
                                .negativeText(R.string.cancel)
                                .callback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        adp.deleteSelectedItems();
                                        mode.finish();
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                    }

                    return true;

                case R.id.action_edit:
                    if (adp.getSelectedItemCount() > 0) {
                        int pos = adp.getSelectedItems().get(0);
                        ((Providers_Activity) getActivity()).showMenuEdit(((Supplier) adp.mDataset.get(pos)).getId());

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
            ((ProvidersAdapter) listProviders.getAdapter()).clearSelections();

            //((Providers_Activity) getActivity()).getSupportActionBar().show();
            ((BaseActivity)getActivity()).hideToolbarContent(false);
        }
    };
}
