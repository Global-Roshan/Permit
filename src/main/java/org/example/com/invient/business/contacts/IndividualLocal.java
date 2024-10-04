package com.invient.business.contacts;

import org.w3c.dom.Element;

public interface IndividualLocal
{
public String getIndividualXmlForAdd();
public String getIndividualXmlForUpdate(int nIndlId);
/**
* Processes the input xml string
* @param strActionType - string representing action to be performed
* @param strXml - input xml
* @return void
* @throws InvientFundsException
*/
public void processXml(String strActionType, String strXml);
/**
* Returns Integer - individual id of the added individual
* @param org.w3c.dom.Element objElement
* @return Integer
* @throws InvientFundsException
*/
public Integer processAdd(Element objElement);
/**
* Returns Integer - individual id of the updated individual
* @param org.w3c.dom.Element objElement
* @return Integer
* @throws InvientFundsException
*/
public Integer processUpdate(Element objElement);
/**
* Returns Integer - individual id of the deleted individual
* @param org.w3c.dom.Element objElement
* @return Integer
* @throws InvientFundsException
*/
public Integer processDelete(Element objElement);
/**
* Returns Integer - External reference id of the individual
* @param org.w3c.dom.Element objElement
* @return Integer
* @throws InvientFundsException
*/
public Integer getIndividualIdByExtRefId(String strExtRefId);
}