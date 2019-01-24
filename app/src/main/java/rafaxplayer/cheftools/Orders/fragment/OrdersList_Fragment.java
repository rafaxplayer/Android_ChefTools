package rafaxplayer.cheftools.Orders.fragment;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.models.Orders;
import rafaxplayer.cheftools.Orders.OrdersNewEdit_Activity;
import rafaxplayer.cheftools.Orders.Orders_Activity;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.DBHelper;
import rafaxplayer.cheftools.database.SqliteWrapper;


public class OrdersList_Fragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    @BindView(R.id.list_items)
    RecyclerView listOrders;
    @BindView(R.id.layoutempty)
    LinearLayout empty;
    @BindView(R.id.emptyText)
    TextView emptytxt;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    private OnSelectedCallback mCallback;
    private ActionMode mActionMode;
    private SqliteWrapper sql;
    private Boolean itemsFound;
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        OrdersAdapter adp;

        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, android.view.Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_action_mode, menu);
            //((Escandallos_Activity) getActivity()).getSupportActionBar().hide();
            ((BaseActivity) getActivity()).hideToolbarContent(true);
            adp = (OrdersAdapter) listOrders.getAdapter();

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
                    int items = ((OrdersAdapter) listOrders.getAdapter()).getSelectedItemCount();
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
                        ((Orders_Activity) getActivity()).showMenuEdit(((Orders) adp.mDataset.get(pos)).getId());

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
            ((OrdersAdapter) listOrders.getAdapter()).clearSelections();
            ((BaseActivity) getActivity()).hideToolbarContent(false);
            //((Escandallos_Activity) getActivity()).getSupportActionBar().show();
        }
    };

    public OrdersList_Fragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, v);
        emptytxt.setText(getString(R.string.new_orderslist));
        empty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent in = new Intent(getActivity(), OrdersNewEdit_Activity.class);
                in.putExtra("id", 0);

                startActivity(in);
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        onResume();
                                    }
                                }
        );

        listOrders.setHasFixedSize(true);
        listOrders.setLayoutManager(new LinearLayoutManager(getActivity()));
        listOrders.setItemAnimator(new DefaultItemAnimator());
        listOrders.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy >= 0 || dy <= 0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listOrders.smoothScrollToPosition(0);
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

        List<Orders> list = loadValues(null);
        if (list.size() > 0) {
            itemsFound = true;
            empty.setVisibility(View.GONE);

        } else {
            itemsFound = false;
            empty.setVisibility(View.VISIBLE);
            empty.bringToFront();
        }
        listOrders.setAdapter(new OrdersAdapter(list));
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCreateOptionsMenu(android.view.Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_lists, menu);

        if (!itemsFound) {

            menu.findItem(R.id.search).setVisible(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.newelement:
                Intent in = new Intent(getActivity(), OrdersNewEdit_Activity.class);
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
                        ((OrdersAdapter) listOrders.getAdapter()).getFilter().filter(s);
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
                                ((OrdersAdapter) listOrders.getAdapter()).getFilter().filter(input);
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

    private List<Orders> loadValues(String order) {

        return (List<Orders>) (Object) sql.getAllObjects("Orders", order);

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
        public void onSelect(int id);
    }

    public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> implements Filterable {

        public Boolean searchResultsok;
        private List<Orders> mDataset;
        private List<Orders> listorigin;
        private FilesFilter filefilter;
        private SparseBooleanArray selectedItems;

        // Adapter's Constructor
        public OrdersAdapter(List<Orders> myDataset) {
            mDataset = myDataset;
            listorigin = myDataset;
            this.searchResultsok = false;
            selectedItems = new SparseBooleanArray();
        }


        public void toggleSelection(int pos, ImageView img) {

            if (selectedItems.get(pos, false)) {
                selectedItems.delete(pos);
                Picasso.get().load(R.drawable.purchase).placeholder(R.drawable.purchase).into(img);

            } else {
                selectedItems.put(pos, true);
                if (mActionMode != null)
                    Picasso.get().load(R.drawable.checked).placeholder(R.drawable.purchase).into(img);

            }
            if (mActionMode != null)
                mActionMode.setTitle(String.valueOf(getSelectedItemCount()) + " Selected");

        }

        public void deleteItem(int pos) {

            int count = sql.DeleteWithId(((Orders) mDataset.get(pos)).getId(), DBHelper.TABLE_PEDIDOS);

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
        public OrdersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
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
            Picasso.get().load(R.drawable.purchase).into(holder.img);
            holder.sName.setText(((Orders) mDataset.get(position)).getName());
            holder.sDate.setText(((Orders) mDataset.get(position)).getFecha());
            boolean state = selectedItems.get(position, false);
            holder.itemView.setSelected(state);
            if (mActionMode != null) {
                if (state) {
                    Picasso.get()
                            .load(R.drawable.checked).placeholder(R.drawable.item_image_placeholder)
                            .into(holder.img);
                } else {
                    Picasso.get()
                            .load(R.drawable.purchase)
                            .resize(getResources().getDimensionPixelOffset(R.dimen.image_dimen_thumbnail), getResources().getDimensionPixelOffset(R.dimen.image_dimen_thumbnail))
                            .placeholder(R.drawable.item_image_placeholder)
                            .into(holder.img);
                }
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
            @BindView(R.id.imageList)
            ImageView img;
            @BindView(R.id.ButtonEdit)
            ImageButton edit;
            @BindView(R.id.ButtonDelete)
            ImageButton del;
            @BindView(R.id.text1)
            TextView sName;
            @BindView(R.id.text2)
            TextView sDate;

            int ID;

            public ViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);
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
                    Intent in = new Intent(getActivity(), OrdersNewEdit_Activity.class);
                    in.putExtra("id", ( mDataset.get(ViewHolder.this.getLayoutPosition())).getId());
                    getActivity().startActivity(in);
                } else {
                    if (mActionMode != null) {
                        v.setSelected(!v.isSelected());
                        toggleSelection(ViewHolder.this.getLayoutPosition(), img);
                        if (getResources().getBoolean(R.bool.dual_pane)) {
                            if (mCallback != null) {
                                mCallback.onSelect(((Orders) mDataset.get(ViewHolder.this.getLayoutPosition())).getId());
                            }
                        }

                    } else {
                        clearSelections();
                        toggleSelection(ViewHolder.this.getLayoutPosition(), img);
                        v.setSelected(true);
                        if (mCallback != null) {
                            mCallback.onSelect(((Orders) mDataset.get(ViewHolder.this.getLayoutPosition())).getId());
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

                    List<Orders> nFilesList = new ArrayList<Orders>();

                    for (Orders p : mDataset) {
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
                    mDataset = (ArrayList<Orders>) results.values;
                    searchResultsok = true;
                }
                notifyDataSetChanged();
            }
        }
    }
}
