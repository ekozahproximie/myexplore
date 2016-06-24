package com.spime.groupon;

public class Deal{
	public String stID=null;
	public String stTitle=null;
	public String stPlacementPriority=null;
	public String stMediumImageUrl=null;
	public String stLargeImageUrl=null;
	public String stSmallImageUrl=null;
	public String stDealUrl=null;
	public String stAnnouncementTitle=null;
	public String stHighlightsHtml=null;
	public String stPitchHtml=null;
	public boolean isTipped=false;
	public int iTippingPoint=0;
	public boolean isSoldOut=false;
	public boolean  isNowDeal=false;
	public int iSoldQuantity=0;
	public String stEndAt=null;
	public boolean isShippingAddressRequired=false;
	public String stTippedAt=null;
	public String stStatus=null;
	public Areas areas=null;
	public Says says=null;
	public Tags tags=null;
	public Options options=null;
	public Merchant merchant=null;
	@Override
	public String toString() {
		
		return stTitle;
	}
}
