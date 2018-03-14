package jdo.reg.services;


public class RegRequest {
	private BodyParams bodyParams;

	public BodyParams getBodyParams() {
		return bodyParams;
	}

	public void setBodyParams(BodyParams bodyParams) {
		this.bodyParams = bodyParams;
	}

	public HeaderParams getHeaderParams() {
		return headerParams;
	}

	public void setHeaderParams(HeaderParams headerParams) {
		this.headerParams = headerParams;
	}

	private HeaderParams headerParams;

}
