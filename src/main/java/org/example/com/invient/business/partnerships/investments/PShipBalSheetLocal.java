package com.invient.business.partnerships.investments;

import java.util.Date;
import org.w3c.dom.Element;

public interface PShipBalSheetLocal
{
public String getBalanceSheetXmlForAdd(int nPShipId, Date dtAsofDate); 
public String getBalanceSheetXmlForUpdate(int nPShipId, Date dtAsofDate);
public String getBalanceSheets(String strPShipId, String strSortCol, String strSortDirection);
public void processXml(String strActionType, String strXML);
public void processAdd(Element objElement);
public void processUpdate(Element objElement);
public void processDelete(Element objElement);

}