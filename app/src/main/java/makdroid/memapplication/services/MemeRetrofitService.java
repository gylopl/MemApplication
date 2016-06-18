package makdroid.memapplication.services;

import makdroid.memapplication.model.ResponseMeme;
import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Grzecho on 14.06.2016.
 */
public interface MemeRetrofitService {
    @GET("get_memes")
    Call<ResponseMeme> getMemes();
}
