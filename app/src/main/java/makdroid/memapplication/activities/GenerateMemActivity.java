package makdroid.memapplication.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import makdroid.memapplication.FontPickerDialog;
import makdroid.memapplication.MemApplication;
import makdroid.memapplication.R;
import makdroid.memapplication.otto.AsyncSaveBitmapTaskEvent;

import static makdroid.memapplication.FontPickerDialog.SELECTED_FONT_SIZE;

public class GenerateMemActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback,
        FontPickerDialog.FontPickerDialogListener {

    private static final String EXTRA_TOP_TEXT = "EXTRA_TOP_TEXT";
    private static final String EXTRA_BOTTOM_TEXT = "EXTRA_BOTTOM_TEXT";
    private static final String EXTRA_COLOR = "EXTRA_COLOR";
    private final static String EXTRA_URL = "EXTRA_URL";
    private static final String MEM = "MEM";

    @Bind(R.id.edit_text)
    EditText mEditText;
    @Bind(R.id.imageViewMem)
    ImageView mImageMeme;
    @Bind(R.id.textViewColorPicker)
    TextView mTextColor;
    @Bind(R.id.text_view_font_size)
    TextView mTextFontSize;
    @Bind(R.id.pb_loading)
    ProgressBar mPbarLoading;

    private Bitmap mBitmapOriginal;
    private Bitmap mBitmapCanvas;
    private String topText;
    private String bottomText;
    private int color = Color.BLUE;
    private int mSelectedFontSizeId = 0;
    private int[] mArrayFontSize;

    @Inject
    Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_mem);
        ButterKnife.bind(this);
        initializeDependencyInjector();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        restoreState(savedInstanceState);
        initTextViews();
        loadImageFromUrl();
    }

    private void initializeDependencyInjector() {
        MemApplication memApplication = (MemApplication) getApplication();
        memApplication.getAppComponent().inject(this);
    }

    private void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            color = savedInstanceState.getInt(EXTRA_COLOR, Color.BLUE);
            topText = savedInstanceState.getString(EXTRA_TOP_TEXT);
            bottomText = savedInstanceState.getString(EXTRA_BOTTOM_TEXT);
            mSelectedFontSizeId = savedInstanceState.getInt(SELECTED_FONT_SIZE);
        }
    }

    private void initTextViews() {
        mArrayFontSize = getResources().getIntArray(R.array.font_size_int);
        mTextFontSize.setText(String.valueOf(mArrayFontSize[mSelectedFontSizeId]));
        mTextColor.setTextColor(color);
    }

    private void loadImageFromUrl() {
        Bundle extras = getIntent().getExtras();
        String url = extras.getString(EXTRA_URL);
        Picasso.with(this).load(url).into(mTarget);
    }

    final Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            mImageMeme.setImageBitmap(bitmap);
            mBitmapOriginal = bitmap;
            drawText();//run when configuration change for redraw
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            Log.v("BITMAP", "LOAD FAILED");
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(EXTRA_TOP_TEXT, topText);
        savedInstanceState.putString(EXTRA_BOTTOM_TEXT, bottomText);
        savedInstanceState.putInt(EXTRA_COLOR, color);
        savedInstanceState.putInt(SELECTED_FONT_SIZE, mSelectedFontSizeId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        bus.unregister(this);
//        if (mBitmapCanvas != null && !mBitmapCanvas.isRecycled()) {
//            mBitmapCanvas.recycle();
//            mBitmapCanvas = null;
//        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btn_add_top_text, R.id.btn_add_bottom_text})
    public void submit(View view) {
        if (view.getId() == R.id.btn_add_top_text) {
            topText = mEditText.getText().toString();
            mEditText.setText("");
            drawText();

        }
        if (view.getId() == R.id.btn_add_bottom_text) {
            bottomText = mEditText.getText().toString();
            mEditText.setText("");
            drawText();
        }
    }

    @OnClick(R.id.btn_change_color)
    public void showchangeColorDialog() {
        new ColorChooserDialog.Builder(this, R.string.color_palette)
                .accentMode(true)
                .doneButton(R.string.md_done_label)
                .cancelButton(R.string.md_cancel_label)
                .backButton(R.string.md_back_label)
                .preselect(color)
                .dynamicButtonColor(true).show();
    }

    @OnClick(R.id.btn_change_font_size)
    public void showChangeFontSizeDialog() {
        FontPickerDialog.newInstance(mSelectedFontSizeId).show(this);
    }

    @OnClick(R.id.btn_share)
    public void clickedShare() {
        mPbarLoading.setVisibility(View.VISIBLE);
        if (mBitmapCanvas != null)
            new SaveBitmapTask().execute(mBitmapCanvas);
        else
            new SaveBitmapTask().execute(mBitmapOriginal);
    }

    @Override
    public void onColorSelection(@NonNull ColorChooserDialog dialog, @ColorInt int selectedColor) {
        changeColor(selectedColor);
        drawText();
    }

    @Override
    public void onDialogPositiveClick(int selectedFontSize) {
        mSelectedFontSizeId = selectedFontSize;
        mTextFontSize.setText(String.valueOf(mArrayFontSize[mSelectedFontSizeId]));
        drawText();
    }

    private void changeColor(@ColorInt int color) {
        this.color = color;
        mTextColor.setTextColor(color);
    }

    private void drawText() {
        if (mBitmapCanvas != null) {
            mBitmapCanvas.recycle();
            mBitmapCanvas = null;
        }
        if (!TextUtils.isEmpty(topText) || !TextUtils.isEmpty(bottomText)) {
            float scale = getResources().getDisplayMetrics().density;
            mBitmapCanvas = mBitmapOriginal.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mBitmapCanvas);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(color);
            paint.setTextSize((int) (mArrayFontSize[mSelectedFontSizeId] * scale + 0.5f));
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);

            Rect bounds = new Rect();
            int x, y;
            if (!TextUtils.isEmpty(topText)) {
                paint.getTextBounds(topText, 0, topText.length(), bounds);
                x = (mBitmapCanvas.getWidth() / 2 - bounds.width() / 2);
                y = bounds.height();
                canvas.drawText(topText, x, y, paint);
            }

            if (!TextUtils.isEmpty(bottomText)) {
                bounds = new Rect();
                paint.getTextBounds(bottomText, 0, bottomText.length(), bounds);
                x = (mBitmapCanvas.getWidth() / 2 - bounds.width() / 2);
                y = mBitmapCanvas.getHeight() - bounds.height() / 2;
                canvas.drawText(bottomText, x, y, paint);
            }
            mImageMeme.setImageBitmap(mBitmapCanvas);

        }
    }

    @NonNull
    public static void start(Context context, String url) {
        Intent generateMemIntent = new Intent(context, GenerateMemActivity.class);
        generateMemIntent.putExtra(EXTRA_URL, url);
        context.startActivity(generateMemIntent);
    }

    private class SaveBitmapTask extends AsyncTask<Bitmap, Void, String> {
        String path;

        @Override
        protected String doInBackground(Bitmap... params) {
            path = MediaStore.Images.Media.insertImage(getContentResolver(), params[0], MEM, null);
            return path;
        }

        @Override
        protected void onPostExecute(String result) {
            bus.post(new AsyncSaveBitmapTaskEvent(result));
        }
    }

    @Subscribe
    public void onAsyncTaskResult(AsyncSaveBitmapTaskEvent event) {
        Toast.makeText(this, "Image saved", Toast.LENGTH_LONG).show();
        share(event.getPath());
    }

    @NonNull
    private void share(String path) {
        Uri bmpUri = Uri.parse(path);
        final Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, MEM);
        shareIntent.putExtra(android.content.Intent.EXTRA_STREAM, bmpUri);
        mPbarLoading.setVisibility(View.GONE);
        try {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_via)));
        } catch (ActivityNotFoundException activityNotFound) {
            Log.v("share", "share FAILED");
        }
    }

}
