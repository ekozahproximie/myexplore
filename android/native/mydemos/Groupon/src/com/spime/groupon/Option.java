package com.spime.groupon;

public class Option{
	public int iID=0;
	public String stTitle=null;
	public int iSoldQuantity=0;
	public boolean isSoldOut=false;
	public boolean isLimitedQuantity=false;
	public int iInitialQuantity=0;
	public int iRemainingQuantity=0;
	public int iDiscountPercent=0;
	public int iMinimumPurchaseQuantity=0;
	public int iMaximumPurchaseQuantity=0;
	public String stExpiresAt=null;
	public String stBuyUrl=null;
	public Price price=null;
	public Value value=null;
	public Discount discount=null;
	public Details details=null;
	public RedemptionLocations locations=null;
}
