package rafaxplayer.cheftools.stocks.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ScrollDirectionListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.Stocks;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;
import rafaxplayer.cheftools.stocks.StocksNewEdit_Activity;
import rafaxplayer.cheftools.stocks.Stocks_Activity;

/**
 * A simple {@link Fragment} subclass.
 */
public class StocksList_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.list_items)
    RecyclerView listStocks;
    @BindView(R.id.layoutempty)
    LinearLayout empty;
    @BindView(R.id.emptyText)
    TextView emptytxt;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private OnSelectedCallback mCallback;
    private ActionMode mActionMode;
    private SqliteWrapper sql;
    private Boolean itemsFound;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        StocksAdapter adp;

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_action_mode, menu);

            ((BaseActivity) getActivity()).hideToolbarContent(true);
            adp = (StocksAdapter) listStocks.getAdapter();

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
                    int items = ((StocksAdapter) listStocks.getAdapter()).getSelectedItemCount();
                    if (items > 0) {

                        new MaterialDialog.Builder(getActivity())
                                .title(R.string.deleterecipetitle)
                                .content(getString(R.string.deleterecipesmsg).replace("###", String.valueOf(items)))
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
                        ((Stocks_Activity) getActivity()).showMenuEdit((adp.mDataset.get(pos)).getId());

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
            ((StocksAdapter) listStocks.getAdapter()).clearSelections();
            ((BaseActivity) getActivity()).hideToolbarContent(false);

        }
    };


    public StocksList_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, v);
        emptytxt.setText(getString(R.string.new_stocklist));
        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getActivity(), StocksNewEdit_Activity.class);
                in.putExtra("id", 0);

                startActivity(in);
            }
        });

        listStocks.setHasFixedSize(true);
        listStocks.setLayoutManager(new LinearLayoutManager(getActivity()));
        listStocks.setItemAnimator(new DefaultItemAnimator());
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

        fab.hide();
        fab.attachToRecyclerView(listStocks, new ScrollDirectionListener() {
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
                listStocks.smoothScrollToPosition(0);
            }
        });
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sql = new SqliteWrapper(getActivity());
        sql.open();
        itemsFound = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!sql.IsOpen()) {
            sql.open();
        }

        List<Stocks> list = loadValues(null);
        if (list.size() > 0) {
            itemsFound = true;
            empty.setVisibility(View.GONE);

        } else {
            itemsFound = false;
            empty.setVisibility(View.VISIBLE);
            empty.bringToFront();
        }
        listStocks.setAdapter(new StocksAdapter(list));
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_orders_list, menu);

        if (!itemsFound) {

            menu.findItem(R.id.search).setVisible(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.neworder:
                Intent in = new Intent(getActivity(), StocksNewEdit_Activity.class);
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
                        ((StocksAdapter) listStocks.getAdapter()).getFilter().filter(s);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });


                new MaterialDialog.Builder(getActivity())
                        .title(R.string.search)
                        .inputType(InputType.TYPE_CLASS_TEXT)
                        .input("Text to search...", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                ((StocksAdapter) listStocks.getAdapter()).getFilter().filter(input);
                                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

    private List<Stocks> loadValues(String order) {

        return (List<Stocks>) (Object) sql.getAllObjects("Stocks", order);

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

    public interface OnSelectedCallback {
        void onSelect(int id);
    }

    public class StocksAdapter extends RecyclerView.Adapter<StocksAdapter.ViewHolder> implements Filterable {

        public Boolean searchResultsok;
        private List<Stocks> mDataset;
        private List<Stocks> listorigin;
        private FilesFilter filefilter;
        private SparseBooleanArray selectedItems;

        // Adapter's Constructor
        public StocksAdapter(List<Stocks> myDataset) {
            mDataset = myDataset;
            listorigin = myDataset;
            this.searchResultsok = false;
            selectedItems = new SparseBooleanArray();
        }


        public void toggleSelection(int pos, ImageView img) {

            if (selectedItems.get(pos, false)) {
                selectedItems.delete(pos);
                Picasso.with(getActivity()).load(R.drawable.inventory).placeholder(R.drawable.inventory).into(img);

            } else {
                selectedItems.put(pos, true);
                if (mActionMode != null)
                    Picasso.with(getActivity()).load(R.drawable.checked).placeholder(R.drawable.inventory).into(img);

            }
            if (mActionMode != null)
                mActionMode.setTitle(String.valueOf(getSelectedItemCount()) + " Selected");

        }

        public void deleteItem(int pos) {

            int count = sql.DeleteWithId(((Stocks) mDataset.get(pos)).getId(), DBHelper.TABLE_INVENTARIOS);

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

        public String getRerecipesNameSelected() {
            StringBuilder sb = new StringBuilder();
            List<Integer> itemspos = getSelectedItems();
            if (itemspos.size() > 0)
                sb.append("\n------------------------------\n");
            for (int i = itemspos.size() - 1; i >= 0; i--) {

                sb.append((mDataset.get(itemspos.get(i))).getName());
                sb.append("\n");

            }
            return sb.toString();
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
        public StocksAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
            // Create a new view by inflating the row item xml.
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_orders_stocks, parent, false);

            // Set the view to the ViewHolder
            ViewHolder holder = new ViewHolder(v);


            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(getActivity()).load(R.drawable.inventory).into(holder.img);
            holder.sName.setText((mDataset.get(position)).getName());
            holder.sDate.setText((mDataset.get(position)).getFecha());
            boolean state = selectedItems.get(position, false);
            holder.itemView.setSelected(state);
            if (mActionMode != null) {
                if (state) {
                    Picasso.with(getActivity())
                            .load(R.drawable.checked).placeholder(R.drawable.item_image_placeholder)
                            .into(holder.img);
                } else {
                    Picasso.with(getActivity())
                            .load(R.drawable.inventory)
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
            public ImageView img;
            public ImageButton edit;
            public TextView sName;
            public TextView sDate;
            int ID;
            private ImageButton del;

            public ViewHolder(View v) {
                super(v);
                img = (ImageView) v.findViewById(R.id.imageList);
                sName = (TextView) v.findViewById(R.id.text1);
                sDate = (TextView) v.findViewById(R.id.text2);
                edit = (ImageButton) v.findViewById(R.id.ButtonEdit);
                del = (ImageButton) v.findViewById(R.id.ButtonDelete);
                v.setOnClickListener(this);
                v.setOnLongClickListener(this);
                edit.setOnClickListener(this);
                del.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {

                if (v.getId() == R.id.ButtonDelete) {
                    new MaterialDialog.Builder(getActivity())
                            .title(R.string.order_delete)
                            .content(R.string.deleteordermsg)
                            .theme(Theme.LIGHT)
                            .positiveText(R.string.yes)
                            .negativeText(R.string.not)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    deleteItem(ViewHolder.this.getLayoutPosition());
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
                    return;
                } else if (v.getId() == R.id.ButtonEdit) {
                    Intent in = new Intent(getActivity(), StocksNewEdit_Activity.class);
                    in.putExtra("id", ((Stocks) mDataset.get(ViewHolder.this.getLayoutPosition())).getId());
                    getActivity().startActivity(in);
                } else {
                    if (mActionMode != null) {
                        v.setSelected(!v.isSelected());
                        toggleSelection(ViewHolder.this.getLayoutPosition(), img);
                        if (getResources().getBoolean(R.bool.dual_pane)) {
                            if (mCallback != null) {
                                mCallback.onSelect(((Stocks) mDataset.get(ViewHolder.this.getLayoutPosition())).getId());
                            }
                        }

                    } else {
                        clearSelections();
                        toggleSelection(ViewHolder.this.getLayoutPosition(), img);
                        v.setSelected(true);
                        if (mCallback != null) {
                            mCallback.onSelect(((Stocks) mDataset.get(ViewHolder.this.getLayoutPosition())).getId());
                        }

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

                    List<Stocks> nFilesList = new ArrayList<Stocks>();

                    for (Stocks p : mDataset) {
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
                    mDataset = (ArrayList<Stocks>) results.values;
                    searchResultsok = true;
                }
                notifyDataSetChanged();
            }
        }
    }
}
