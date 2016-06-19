package makdroid.memapplication.otto;

/**
 * Created by Grzecho on 19.06.2016.
 */
public class AsyncSaveBitmapTaskEvent {
    private String path;

    public AsyncSaveBitmapTaskEvent(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
