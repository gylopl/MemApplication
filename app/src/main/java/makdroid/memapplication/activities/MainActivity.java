package makdroid.memapplication.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import makdroid.memapplication.MemApplication;
import makdroid.memapplication.R;
import makdroid.memapplication.adapters.MemeAdapter;
import makdroid.memapplication.model.ResponseMeme;
import makdroid.memapplication.services.MemeRetrofitService;
import makdroid.memapplication.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Inject
    MemeRetrofitService memeRetrofitService;

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.btn_refresh)
    Button mButtonRefresh;
    @Bind(R.id.pb_loading)
    ProgressBar mPbarLoading;

    private MemeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeDependencyInjector();
        initViews();
        loadDataFromJSON();
    }

    private void initializeDependencyInjector() {
        MemApplication memApplication = (MemApplication) getApplication();
        memApplication.getAppComponent().inject(this);
    }

    private void initViews() {
        ButterKnife.bind(this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this));
    }

    private void loadDataFromJSON() {
        mPbarLoading.setVisibility(View.VISIBLE);
        if (NetworkUtils.checkNetworkConnection(this))
            requestJSON();
        else
            showErrorConnection();
    }

    private void requestJSON() {
        if (NetworkUtils.checkNetworkConnection(this)) {
            Call<ResponseMeme> memes = memeRetrofitService.getMemes();
            memes.enqueue(new Callback<ResponseMeme>() {
                @Override
                public void onResponse(Call<ResponseMeme> call, Response<ResponseMeme> response) {
                    mButtonRefresh.setVisibility(View.GONE);
                    ResponseMeme jsonResponse = response.body();
                    adapter = new MemeAdapter(jsonResponse.data.memes, getApplicationContext());
                    recyclerView.setAdapter(adapter);
                    mPbarLoading.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<ResponseMeme> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                }
            });
        } else {
            showErrorConnection();
        }
    }

    private void showErrorConnection() {
        Toast.makeText(this, R.string.error_no_internet, Toast.LENGTH_LONG).show();
        mButtonRefresh.setVisibility(View.VISIBLE);
        mPbarLoading.setVisibility(View.GONE);
    }

    @OnClick(R.id.btn_refresh)
    public void refreshListView() {
        loadDataFromJSON();
    }

    public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context) {
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                    View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    int position = recyclerView.getChildLayoutPosition(view);
                    onMemeCardClick(adapter.getItem(position).url);
                    return super.onSingleTapConfirmed(e);
                }

                @Override
                public void onLongPress(MotionEvent e) {
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            mGestureDetector.onTouchEvent(e);
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    @NonNull
    void onMemeCardClick(String url) {
        GenerateMemActivity.start(this, url);
    }

}
