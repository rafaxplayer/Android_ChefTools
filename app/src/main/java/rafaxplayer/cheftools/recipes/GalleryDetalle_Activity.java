package rafaxplayer.cheftools.recipes;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rafaxplayer.cheftools.Globalclasses.BaseActivity;
import rafaxplayer.cheftools.Globalclasses.models.ImageGalleryModel;
import rafaxplayer.cheftools.R;


public class GalleryDetalle_Activity extends BaseActivity {
    @BindView(R.id.pager)
    ViewPager mViewPager;
    private int pos;
    private List<ImageGalleryModel> data;
    private int id;

    @Override
    protected int getLayoutResourceId() {

        return R.layout.activity_gallery_detalle;
    }

    @Override
    protected String getCustomTitle() {

        return "Detalle";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        data = getIntent().getParcelableArrayListExtra("data");
        pos = getIntent().getIntExtra("pos", 0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTittleDinamic(data.get(position).Title);
                id = data.get(position).ID;

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //mViewPager.setOffscreenPageLimit(8);
    }

    @Override
    public void onResume() {
        super.onResume();
        mViewPager.setAdapter(new CustomPagerAdapter(this, data));
        mViewPager.setCurrentItem(pos);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_detalle_gallery, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.view:
                Intent intent = new Intent(this, DetalleRecipes_Activity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class CustomPagerAdapter extends PagerAdapter {

        final Context mContext;
        final LayoutInflater mLayoutInflater;
        final List<ImageGalleryModel> data;

        public CustomPagerAdapter(Context context, List<ImageGalleryModel> dat) {
            mContext = context;
            data = dat;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {

            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);
            ImageView imageView = itemView.findViewById(R.id.imagePager);
            TextView textView = itemView.findViewById(R.id.textImage);
            final String path = (data.get(position)).ImagePath;
            final String title = (data.get(position)).Title;
            textView.setText(title);
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.parse(path), "image/*");
                    startActivity(intent);
                    return true;
                }
            });
            container.addView(itemView);
            Picasso.get().load(path)
                    .fit().centerCrop()
                    .placeholder(R.drawable.placeholder_recetas)
                    .into(imageView);
            return itemView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }
}
