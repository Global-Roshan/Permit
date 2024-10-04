package com.invient.business.exports;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
public interface Exports
{
/**
* This method retrieves all records of ExcelExports table from database convert them to xml and return them as xml string.
* @author Chirag
* @return String - return string containing xml
* @throws InvientFundsException
*/
public String getAllExports(String strOrderColName, boolean blnDesc);
/**
* This method takes input as string containing xml for exporting data in database into Excel file.
* It processes string containing xml and generate sql query based on criteria in xml.
*
* @author Chirag
* @param String - strExportFilePath will contain path where excel file will be stored.
* @param String - strExportXML will contain xml string which needs to be parsed and generate sql query based on
* criteria in xml.
* @throws InvientFundsException if SAXException or IOException occurs or if export file name is not found in export xml.
*/
public ByteArrayOutputStream export(String strExportXML,String strDBType, String strDSName, int nRequestID, byte [] byteArray, String strSheetName);
/**
* This method gives the ExcelStream
* @author Nilabh
* @param strExpParamXml
* @param strDbType
* @param nRequestId
* @param strRptPath
* @return
* @throws InvientFundsException
* @throws IOException
*/
public byte[] getExcelByteArrayOutputStream(String strExpParamXml, String strDBType, int nRequestId, String strRptPath, byte[] byteArray, String strSheetName);
/**
* This method gives the ExcelStream, given the file name and if it is a overnight report, it will check for the stored file.
* It will return the stored file is present, if not generates and returns that file
* @author Vishwanath
* @param strExportFileName
* @param strExpParamXml
* @param strDbType
* @param nRequestId
* @param strRptPath
* @return
* @throws InvientFundsException
* @throws IOException
*/
public byte[] getExcelByteArrayOutputStream(String strExportFileName, String strExpParamXml, String strDBType, int nRequestId, String strRptPath, byte[] byteArray, String strSheetName) ;
}