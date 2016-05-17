package rafaxplayer.cheftools.recipes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.ImageGalleryModel;
import rafaxplayer.cheftools.R;
import rafaxplayer.cheftools.database.SqliteWrapper;

public class GalleryRecipesActivity extends BaseActivity {
    private RecyclerView mRecyclerView;
    private SqliteWrapper sql;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = (RecyclerView) findViewById(R.id.imageList);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);
        sql = new SqliteWrapper(this);
        sql.open();
    }

    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_gallery_recipes;
    }

    @Override
    protected String getCustomTitle() {

        return getString(R.string.gallery_recipes);
    }

    @Override
    public void onResume() {
        super.onResume();
        List<ImageGalleryModel> lst = sql.getImages();
        mRecyclerView.setAdapter(new GalleryAdapter(this, lst));
    }

    public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.ViewHolder> {

        List<ImageGalleryModel> listImages;
        Context context;

        public GalleryAdapter(Context con, List<ImageGalleryModel> list) {

            this.listImages = list;
            this.context = con;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Picasso.with(context).load(((ImageGalleryModel) listImages.get(position)).ImagePath)
                    .resizeDimen(R.dimen.dimen_gallery_thumbnail_min, R.dimen.dimen_gallery_thumbnail_min)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder_recetas)
                    .into(holder.img);
            holder.txtTitle.setText(((ImageGalleryModel) listImages.get(position)).Title);
        }

        @Override
        public int getItemCount() {
            return listImages.size();
        }

        @Override
        public GalleryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {
            // Create a new view by inflating the row item xml.
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.image_gallery_recipes, parent, false);
            // Set the view to the ViewHolder
            ViewHolder holder = new ViewHolder(v);
            return holder;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public ImageView img;
            public TextView txtTitle;

            public ViewHolder(View v) {
                super(v);
                img = (ImageView) v.findViewById(R.id.imageGallery);
                txtTitle = (TextView) v.findViewById(R.id.textImageGallery);
                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,GalleryDetalle_Activity.class);
                intent.putParcelableArrayListExtra("data", (ArrayList<? extends Parcelable>) listImages);
                intent.putExtra("pos", ViewHolder.this.getAdapterPosition());

                startActivity(intent);
            }
        }
    }
}
