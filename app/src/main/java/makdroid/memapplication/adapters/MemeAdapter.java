package makdroid.memapplication.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import makdroid.memapplication.utils.FileUtil;
import makdroid.memapplication.R;
import makdroid.memapplication.model.Meme;

/**
 * Created by Grzecho on 21.04.2016.
 */
public class MemeAdapter extends RecyclerView.Adapter<MemeAdapter.MemeViewHolder> {
    private List<Meme> mMemeCollection;
    private Context mContext;
    Picasso mPicasso;

    public MemeAdapter(Collection<Meme> memeCollection, Context context) {
        this.mMemeCollection = new ArrayList<>(memeCollection);
        this.mContext = context;
        mPicasso = new Picasso.Builder(mContext).downloader(new OkHttp3Downloader(FileUtil.getDiskCacheDir(mContext), Integer.MAX_VALUE)).build();
    }

    @Override
    public MemeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.card_row,
                        parent,
                        false);
        return new MemeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MemeViewHolder holder, final int position) {
        final Meme meme = mMemeCollection.get(position);
        holder.name.setText(meme.name);
        mPicasso.setIndicatorsEnabled(true);
        mPicasso.load(meme.url).networkPolicy(NetworkPolicy.OFFLINE)
                .resize(50, 50).into(holder.imageMeme, new Callback() {
            @Override
            public void onSuccess() {
                Log.v("Picasso", "FROM CACHE DISK");
            }

            @Override
            public void onError() {
                mPicasso
                        .load(meme.url).resize(50, 50)
                        .into(holder.imageMeme, new Callback() {
                            @Override
                            public void onSuccess() {
                                Log.v("Picasso", "FROM NETWORK");
                            }

                            @Override
                            public void onError() {
                                Log.v("Picasso", "Could not fetch image");
                            }
                        });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mMemeCollection.size();
    }

    public Meme getItem(int position) {
        return mMemeCollection.get(position);
    }

    final static class MemeViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.meme_name)
        TextView name;
        @Bind(R.id.imageView)
        ImageView imageMeme;

        public MemeViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
