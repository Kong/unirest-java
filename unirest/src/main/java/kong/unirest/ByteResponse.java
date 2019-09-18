package kong.unirest;

public class ByteResponse extends BaseResponse<byte[]> {
    private final byte[] body;

    public ByteResponse(RawResponse r) {
        super(r);
        this.body = r.getContentAsBytes();
    }

    @Override
    public byte[] getBody() {
        return body;
    }
}
