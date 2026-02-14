package pl.czerwiu.flickr.upldr.flickr;

import com.flickr4java.flickr.uploader.UploadMetaData;

import java.util.Map;

/**
 * Extension of UploadMetaData that adds dedup_check parameter.
 * Flickr4Java does not natively support this parameter.
 */
public class DedupUploadMetaData extends UploadMetaData {

    private final int dedupCheck;

    /**
     * @param dedupCheck 1 = check all user's photos, 2 = check recent uploads only
     */
    public DedupUploadMetaData(int dedupCheck) {
        this.dedupCheck = dedupCheck;
    }

    @Override
    public Map<String, String> getUploadParameters() {
        Map<String, String> parameters = super.getUploadParameters();
        parameters.put("dedup_check", String.valueOf(dedupCheck));
        return parameters;
    }
}
