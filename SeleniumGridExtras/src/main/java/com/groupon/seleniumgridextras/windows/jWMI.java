package com.groupon.seleniumgridextras.windows;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;

/**
 * File: jWMI.java
 * Date: 12/21/09 
 * Author: Shaun Henry
 * Copyright Henry Ranch LLC 2009-2010.  All rights reserved.  http://www.henryranch.net
 *
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * + You must provide a link back to www.henryranch.net in any software or website which uses this software.
 * + Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * + Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer 
 *   in the documentation and/or other materials provided with the distribution.
 * + Neither the name of the HenryRanch LCC nor the names of its contributors nor authors may be used to endorse or promote products derived 
 *   from this software without specific prior written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE AUTHORS, OWNERS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES 
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.  
 *
 * A java bridge for querying the WMI interface.
 * @author Copyright 2009-2010 HenryRanch LLC. Author Shaun Henry, 2009-2010.
 * @version 1.0
 * */
public class jWMI
{
  private static final String CRLF = "\r\n";
  private static Logger logger = Logger.getLogger(jWMI.class);
  /**
   * Generate a VBScript string capable of querying the desired WMI information.
//   * @param wmiQueryString the query string to be passed to the WMI sub-system.
   * <br>i.e. "Select * from Win32_ComputerSystem"
   * @param wmiCommaSeparatedFieldName a comma separated list of the WMI fields to be collected from the query results.
   * <br>i.e. "Model"
   * @return the vbscript string.
   * */
  private static String getVBScript(String wmiQueryStr, String wmiCommaSeparatedFieldName)
  {
    String vbs = "Dim oWMI : Set oWMI = GetObject(\"winmgmts:\")"+CRLF;
    vbs += "Dim classComponent : Set classComponent = oWMI.ExecQuery(\""+wmiQueryStr+"\")"+CRLF;
    vbs += "Dim obj, strData"+CRLF;
    vbs += "For Each obj in classComponent"+CRLF;
    String[] wmiFieldNameArray = wmiCommaSeparatedFieldName.split(",");
    for(int i = 0; i < wmiFieldNameArray.length; i++)
    {
      vbs += "  strData = strData & obj."+wmiFieldNameArray[i]+" & VBCrLf"+CRLF;
    }
    vbs += "Next"+CRLF;
    vbs += "wscript.echo strData"+CRLF;
    return vbs;
  }

  /**
   * Get an environment variable from the windows OS
   * @param envVarName the name of the env var to get
   * @return the value of the env var
   * @throws Exception if the given envVarName does not exist
   * */
  private static String getEnvVar(String envVarName) throws Exception
  {
    String varName = "%"+envVarName+"%";
    String envVarValue = execute(new String[] {"cmd.exe", "/C", "echo "+varName});
    if(envVarValue.equals(varName))
    {
      throw new Exception("Environment variable '"+envVarName+"' does not exist!");
    }
    return envVarValue;
  }

  /**
   * Write the given data string to the given file
   * @param filename the file to write the data to
   * @param data a String ofdata to be written into the file
   * @throws Exception if the output file cannot be written
   * */
  private static void writeStrToFile(String filename, String data) throws Exception
  {
    FileWriter output = new FileWriter(filename);
    output.write(data);
    output.flush();
    output.close();
    output = null;
  }

  /**
   * Get the given WMI value from the WMI subsystem on the local computer
   * @param wmiQueryStr the query string as syntactically defined by the WMI reference
//   * @param wmiFieldName the field object that you want to get out of the query results
   * @return the value
   * @throws Exception if there is a problem obtaining the value
   * */
  public static String getWMIValue(String wmiQueryStr, String wmiCommaSeparatedFieldName) throws Exception
  {
    String vbScript = getVBScript(wmiQueryStr, wmiCommaSeparatedFieldName);
    String tmpDirName = getEnvVar("TEMP").trim();
    String tmpFileName = tmpDirName + File.separator + "jwmi.vbs";
    writeStrToFile(tmpFileName, vbScript);
    String output = execute(new String[] {"cmd.exe", "/C", "cscript.exe", tmpFileName});
    new File(tmpFileName).delete();

    return output.trim();
  }

  /**
   * Execute the application with the given command line parameters.
   * @param cmdArray an array of the command line params
   * @return the output as gathered from stdout of the process
//   * @throws an Exception upon encountering a problem
   * */
  private static String execute(String[] cmdArray) throws Exception
  {
    Process process = Runtime.getRuntime().exec(cmdArray);
    BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String output = "";
    String line = "";
    while((line = input.readLine()) != null)
    {
      //need to filter out lines that don't contain our desired output
      if(!line.contains("Microsoft") && !line.equals(""))
      {
        output += line +CRLF;
      }
    }
    process.destroy();
    process = null;
    return output.trim();
  }

  public static void executeDemoQueries()
  {
    try
    {
      logger.debug(getWMIValue("Select * from Win32_ComputerSystem", "Model"));
      logger.debug(getWMIValue("Select Name from Win32_ComputerSystem", "Name"));
      //System.out.println(getWMIValue("Select Description from Win32_PnPEntity", "Description"));
      //System.out.println(getWMIValue("Select Description, Manufacturer from Win32_PnPEntity", "Description,Manufacturer"));
      //System.out.println(getWMIValue("Select * from Win32_Service WHERE State = 'Stopped'", "Name"));
      //this will return everything since the field is incorrect and was not used to a filter
      //System.out.println(getWMIValue("Select * from Win32_Service", "Name"));
      //this will return nothing since there is no field specified
      logger.debug(getWMIValue("Select Name from Win32_ComputerSystem", ""));
      //this is a failing case where the Win32_Service class does not contain the 'Name' field
      //System.out.println(getWMIValue("Select * from Win32_Service", "Name"));
    }
    catch(Exception e)
    {
      logger.error(e.toString());
    }
  }


  //WMI class definitions below here:
  public static final String CLASS_Win32_1394Controller = "Win32_1394Controller";
  public static final String CLASS_Win32_1394ControllerDevice = "Win32_1394ControllerDevice";
  public static final String CLASS_Win32_AccountSID = "Win32_AccountSID";
  public static final String CLASS_Win32_ActionCheck = "Win32_ActionCheck";
  public static final String CLASS_Win32_ActiveRoute = "Win32_ActiveRoute";
  public static final String CLASS_Win32_AllocatedResource = "Win32_AllocatedResource";
  public static final String CLASS_Win32_ApplicationCommandLine = "Win32_ApplicationCommandLine";
  public static final String CLASS_Win32_ApplicationService = "Win32_ApplicationService";
  public static final String CLASS_Win32_AssociatedBattery = "Win32_AssociatedBattery";
  public static final String CLASS_Win32_AssociatedProcessorMemory = "Win32_AssociatedProcessorMemory";
  public static final String CLASS_Win32_AutochkSetting = "Win32_AutochkSetting";
  public static final String CLASS_Win32_BaseBoard = "Win32_BaseBoard";
  public static final String CLASS_Win32_Battery = "Win32_Battery";
  public static final String CLASS_Win32_Binary = "Win32_Binary";
  public static final String CLASS_Win32_BindImageAction = "Win32_BindImageAction";
  public static final String CLASS_Win32_BIOS = "Win32_BIOS";
  public static final String CLASS_Win32_BootConfiguration = "Win32_BootConfiguration";
  //public static final String CLASS_Win32_Bus Win32_CacheMemory = "Win32_Bus Win32_CacheMemory";
  public static final String CLASS_Win32_CDROMDrive = "Win32_CDROMDrive";
  public static final String CLASS_Win32_CheckCheck = "Win32_CheckCheck";
  public static final String CLASS_Win32_CIMLogicalDeviceCIMDataFile = "Win32_CIMLogicalDeviceCIMDataFile";
  public static final String CLASS_Win32_ClassicCOMApplicationClasses = "Win32_ClassicCOMApplicationClasses";
  public static final String CLASS_Win32_ClassicCOMClass = "Win32_ClassicCOMClass";
  public static final String CLASS_Win32_ClassicCOMClassSetting = "Win32_ClassicCOMClassSetting";
  public static final String CLASS_Win32_ClassicCOMClassSettings = "Win32_ClassicCOMClassSettings";
  public static final String CLASS_Win32_ClassInforAction = "Win32_ClassInforAction";
  public static final String CLASS_Win32_ClientApplicationSetting = "Win32_ClientApplicationSetting";
  public static final String CLASS_Win32_CodecFile = "Win32_CodecFile";
  public static final String CLASS_Win32_COMApplicationSettings = "Win32_COMApplicationSettings";
  public static final String CLASS_Win32_COMClassAutoEmulator = "Win32_COMClassAutoEmulator";
  public static final String CLASS_Win32_ComClassEmulator = "Win32_ComClassEmulator";
  public static final String CLASS_Win32_CommandLineAccess = "Win32_CommandLineAccess";
  public static final String CLASS_Win32_ComponentCategory = "Win32_ComponentCategory";
  public static final String CLASS_Win32_ComputerSystem = "Win32_ComputerSystem";
  public static final String CLASS_Win32_ComputerSystemProcessor = "Win32_ComputerSystemProcessor";
  public static final String CLASS_Win32_ComputerSystemProduct = "Win32_ComputerSystemProduct";
  public static final String CLASS_Win32_ComputerSystemWindowsProductActivationSetting = "Win32_ComputerSystemWindowsProductActivationSetting";
  public static final String CLASS_Win32_Condition = "Win32_Condition";
  public static final String CLASS_Win32_ConnectionShare = "Win32_ConnectionShare";
  public static final String CLASS_Win32_ControllerHastHub = "Win32_ControllerHastHub";
  public static final String CLASS_Win32_CreateFolderAction = "Win32_CreateFolderAction";
  public static final String CLASS_Win32_CurrentProbe = "Win32_CurrentProbe";
  public static final String CLASS_Win32_DCOMApplication = "Win32_DCOMApplication";
  public static final String CLASS_Win32_DCOMApplicationAccessAllowedSetting = "Win32_DCOMApplicationAccessAllowedSetting";
  public static final String CLASS_Win32_DCOMApplicationLaunchAllowedSetting = "Win32_DCOMApplicationLaunchAllowedSetting";
  public static final String CLASS_Win32_DCOMApplicationSetting = "Win32_DCOMApplicationSetting";
  public static final String CLASS_Win32_DependentService = "Win32_DependentService";
  public static final String CLASS_Win32_Desktop = "Win32_Desktop";
  public static final String CLASS_Win32_DesktopMonitor = "Win32_DesktopMonitor";
  public static final String CLASS_Win32_DeviceBus = "Win32_DeviceBus";
  public static final String CLASS_Win32_DeviceMemoryAddress = "Win32_DeviceMemoryAddress";
  public static final String CLASS_Win32_Directory = "Win32_Directory";
  public static final String CLASS_Win32_DirectorySpecification = "Win32_DirectorySpecification";
  public static final String CLASS_Win32_DiskDrive = "Win32_DiskDrive";
  public static final String CLASS_Win32_DiskDrivePhysicalMedia = "Win32_DiskDrivePhysicalMedia";
  public static final String CLASS_Win32_DiskDriveToDiskPartition = "Win32_DiskDriveToDiskPartition";
  public static final String CLASS_Win32_DiskPartition = "Win32_DiskPartition";
  public static final String CLASS_Win32_DiskQuota = "Win32_DiskQuota";
  public static final String CLASS_Win32_DisplayConfiguration = "Win32_DisplayConfiguration";
  public static final String CLASS_Win32_DisplayControllerConfiguration = "Win32_DisplayControllerConfiguration";
  public static final String CLASS_Win32_DMAChanner = "Win32_DMAChanner";
  public static final String CLASS_Win32_DriverForDevice = "Win32_DriverForDevice";
  public static final String CLASS_Win32_DriverVXD = "Win32_DriverVXD";
  public static final String CLASS_Win32_DuplicateFileAction = "Win32_DuplicateFileAction";
  public static final String CLASS_Win32_Environment = "Win32_Environment";
  public static final String CLASS_Win32_EnvironmentSpecification = "Win32_EnvironmentSpecification";
  public static final String CLASS_Win32_ExtensionInfoAction = "Win32_ExtensionInfoAction";
  public static final String CLASS_Win32_Fan = "Win32_Fan";
  public static final String CLASS_Win32_FileSpecification = "Win32_FileSpecification";
  public static final String CLASS_Win32_FloppyController = "Win32_FloppyController";
  public static final String CLASS_Win32_FloppyDrive = "Win32_FloppyDrive";
  public static final String CLASS_Win32_FontInfoAction = "Win32_FontInfoAction";
  public static final String CLASS_Win32_Group = "Win32_Group";
  public static final String CLASS_Win32_GroupDomain = "Win32_GroupDomain";
  public static final String CLASS_Win32_GroupUser = "Win32_GroupUser";
  public static final String CLASS_Win32_HeatPipe = "Win32_HeatPipe";
  public static final String CLASS_Win32_IDEController = "Win32_IDEController";
  public static final String CLASS_Win32_IDEControllerDevice = "Win32_IDEControllerDevice";
  public static final String CLASS_Win32_ImplementedCategory = "Win32_ImplementedCategory";
  public static final String CLASS_Win32_InfraredDevice = "Win32_InfraredDevice";
  public static final String CLASS_Win32_IniFileSpecification = "Win32_IniFileSpecification";
  public static final String CLASS_Win32_InstalledSoftwareElement = "Win32_InstalledSoftwareElement";
  public static final String CLASS_Win32_IP4PersistedRouteTable = "Win32_IP4PersistedRouteTable";
  public static final String CLASS_Win32_IP4RouteTable = "Win32_IP4RouteTable";
  public static final String CLASS_Win32_IRQResource = "Win32_IRQResource";
  public static final String CLASS_Win32_Keyboard = "Win32_Keyboard";
  public static final String CLASS_Win32_LaunchCondition = "Win32_LaunchCondition";
  public static final String CLASS_Win32_LoadOrderGroup = "Win32_LoadOrderGroup";
  public static final String CLASS_Win32_LoadOrderGroupServiceDependencies = "Win32_LoadOrderGroupServiceDependencies";
  public static final String CLASS_Win32_LoadOrderGroupServiceMembers = "Win32_LoadOrderGroupServiceMembers";
  public static final String CLASS_Win32_LocalTime = "Win32_LocalTime";
  public static final String CLASS_Win32_LoggedOnUser = "Win32_LoggedOnUser";
  public static final String CLASS_Win32_LogicalDisk = "Win32_LogicalDisk";
  public static final String CLASS_Win32_LogicalDiskRootDirectory = "Win32_LogicalDiskRootDirectory";
  public static final String CLASS_Win32_LogicalDiskToPartition = "Win32_LogicalDiskToPartition";
  public static final String CLASS_Win32_LogicalFileAccess = "Win32_LogicalFileAccess";
  public static final String CLASS_Win32_LogicalFileAuditing = "Win32_LogicalFileAuditing";
  public static final String CLASS_Win32_LogicalFileGroup = "Win32_LogicalFileGroup";
  public static final String CLASS_Win32_LogicalFileOwner = "Win32_LogicalFileOwner";
  public static final String CLASS_Win32_LogicalFileSecuritySetting = "Win32_LogicalFileSecuritySetting";
  public static final String CLASS_Win32_LogicalMemoryConfiguration = "Win32_LogicalMemoryConfiguration";
  public static final String CLASS_Win32_LogicalProgramGroup = "Win32_LogicalProgramGroup";
  public static final String CLASS_Win32_LogicalProgramGroupDirectory = "Win32_LogicalProgramGroupDirectory";
  public static final String CLASS_Win32_LogicalProgramGroupItem = "Win32_LogicalProgramGroupItem";
  public static final String CLASS_Win32_LogicalProgramGroupItemDataFile = "Win32_LogicalProgramGroupItemDataFile";
  public static final String CLASS_Win32_LogicalShareAccess = "Win32_LogicalShareAccess";
  public static final String CLASS_Win32_LogicalShareAuditing = "Win32_LogicalShareAuditing";
  public static final String CLASS_Win32_LogicalShareSecuritySetting = "Win32_LogicalShareSecuritySetting";
  public static final String CLASS_Win32_LogonSession = "Win32_LogonSession";
  public static final String CLASS_Win32_LogonSessionMappedDisk = "Win32_LogonSessionMappedDisk";
  public static final String CLASS_Win32_MappedLogicalDisk = "Win32_MappedLogicalDisk";
  public static final String CLASS_Win32_MemoryArray = "Win32_MemoryArray";
  public static final String CLASS_Win32_MemoryArrayLocation = "Win32_MemoryArrayLocation";
  public static final String CLASS_Win32_MemoryDevice = "Win32_MemoryDevice";
  public static final String CLASS_Win32_MemoryDeviceArray = "Win32_MemoryDeviceArray";
  public static final String CLASS_Win32_MemoryDeviceLocation = "Win32_MemoryDeviceLocation";
  public static final String CLASS_Win32_MIMEInfoAction = "Win32_MIMEInfoAction";
  public static final String CLASS_Win32_MotherboardDevice = "Win32_MotherboardDevice";
  public static final String CLASS_Win32_MoveFileAction = "Win32_MoveFileAction";
  public static final String CLASS_Win32_NamedJobObject = "Win32_NamedJobObject";
  public static final String CLASS_Win32_NamedJobObjectActgInfo = "Win32_NamedJobObjectActgInfo";
  public static final String CLASS_Win32_NamedJobObjectLimit = "Win32_NamedJobObjectLimit";
  public static final String CLASS_Win32_NamedJobObjectLimitSetting = "Win32_NamedJobObjectLimitSetting";
  public static final String CLASS_Win32_NamedJobObjectProcess = "Win32_NamedJobObjectProcess";
  public static final String CLASS_Win32_NamedJobObjectSecLimit = "Win32_NamedJobObjectSecLimit";
  public static final String CLASS_Win32_NamedJobObjectSecLimitSetting = "Win32_NamedJobObjectSecLimitSetting";
  public static final String CLASS_Win32_NamedJobObjectStatistics = "Win32_NamedJobObjectStatistics";
  public static final String CLASS_Win32_NetworkAdapter = "Win32_NetworkAdapter";
  public static final String CLASS_Win32_NetworkAdapterConfiguration = "Win32_NetworkAdapterConfiguration";
  public static final String CLASS_Win32_NetworkAdapterSetting = "Win32_NetworkAdapterSetting";
  public static final String CLASS_Win32_NetworkClient = "Win32_NetworkClient";
  public static final String CLASS_Win32_NetworkConnection = "Win32_NetworkConnection";
  public static final String CLASS_Win32_NetworkLoginProfile = "Win32_NetworkLoginProfile";
  public static final String CLASS_Win32_NetworkProtocol = "Win32_NetworkProtocol";
  public static final String CLASS_Win32_NTDomain = "Win32_NTDomain";
  public static final String CLASS_Win32_NTEventlogFile = "Win32_NTEventlogFile";
  public static final String CLASS_Win32_NTLogEvent = "Win32_NTLogEvent";
  public static final String CLASS_Win32_NTLogEventComputer = "Win32_NTLogEventComputer";
  public static final String CLASS_Win32_NTLogEvnetLog = "Win32_NTLogEvnetLog";
  public static final String CLASS_Win32_NTLogEventUser = "Win32_NTLogEventUser";
  public static final String CLASS_Win32_ODBCAttribute = "Win32_ODBCAttribute";
  public static final String CLASS_Win32_ODBCDataSourceAttribute = "Win32_ODBCDataSourceAttribute";
  public static final String CLASS_Win32_ODBCDataSourceSpecification = "Win32_ODBCDataSourceSpecification";
  public static final String CLASS_Win32_ODBCDriverAttribute = "Win32_ODBCDriverAttribute";
  public static final String CLASS_Win32_ODBCDriverSoftwareElement = "Win32_ODBCDriverSoftwareElement";
  public static final String CLASS_Win32_ODBCDriverSpecification = "Win32_ODBCDriverSpecification";
  public static final String CLASS_Win32_ODBCSourceAttribute = "Win32_ODBCSourceAttribute";
  public static final String CLASS_Win32_ODBCTranslatorSpecification = "Win32_ODBCTranslatorSpecification";
  public static final String CLASS_Win32_OnBoardDevice = "Win32_OnBoardDevice";
  public static final String CLASS_Win32_OperatingSystem = "Win32_OperatingSystem";
  public static final String CLASS_Win32_OperatingSystemAutochkSetting = "Win32_OperatingSystemAutochkSetting";
  public static final String CLASS_Win32_OperatingSystemQFE = "Win32_OperatingSystemQFE";
//  public static final String CLASS_Win32_OSRecoveryConfiguración = "Win32_OSRecoveryConfiguración";
  public static final String CLASS_Win32_PageFile = "Win32_PageFile";
  public static final String CLASS_Win32_PageFileElementSetting = "Win32_PageFileElementSetting";
  public static final String CLASS_Win32_PageFileSetting = "Win32_PageFileSetting";
  public static final String CLASS_Win32_PageFileUsage = "Win32_PageFileUsage";
  public static final String CLASS_Win32_ParallelPort = "Win32_ParallelPort";
  public static final String CLASS_Win32_Patch = "Win32_Patch";
  public static final String CLASS_Win32_PatchFile = "Win32_PatchFile";
  public static final String CLASS_Win32_PatchPackage = "Win32_PatchPackage";
  public static final String CLASS_Win32_PCMCIAControler = "Win32_PCMCIAControler";
  public static final String CLASS_Win32_PerfFormattedData_ASP_ActiveServerPages = "Win32_PerfFormattedData_ASP_ActiveServerPages";
  public static final String CLASS_Win32_PerfFormattedData_ASPNET_114322_ASPNETAppsv114322 = "Win32_PerfFormattedData_ASPNET_114322_ASPNETAppsv114322";
  public static final String CLASS_Win32_PerfFormattedData_ASPNET_114322_ASPNETv114322 = "Win32_PerfFormattedData_ASPNET_114322_ASPNETv114322";
  public static final String CLASS_Win32_PerfFormattedData_ASPNET_2040607_ASPNETAppsv2040607 = "Win32_PerfFormattedData_ASPNET_2040607_ASPNETAppsv2040607";
  public static final String CLASS_Win32_PerfFormattedData_ASPNET_2040607_ASPNETv2040607 = "Win32_PerfFormattedData_ASPNET_2040607_ASPNETv2040607";
  public static final String CLASS_Win32_PerfFormattedData_ASPNET_ASPNET = "Win32_PerfFormattedData_ASPNET_ASPNET";
  public static final String CLASS_Win32_PerfFormattedData_ASPNET_ASPNETApplications = "Win32_PerfFormattedData_ASPNET_ASPNETApplications";
  public static final String CLASS_Win32_PerfFormattedData_aspnet_state_ASPNETStateService = "Win32_PerfFormattedData_aspnet_state_ASPNETStateService";
  public static final String CLASS_Win32_PerfFormattedData_ContentFilter_IndexingServiceFilter = "Win32_PerfFormattedData_ContentFilter_IndexingServiceFilter";
  public static final String CLASS_Win32_PerfFormattedData_ContentIndex_IndexingService = "Win32_PerfFormattedData_ContentIndex_IndexingService";
  public static final String CLASS_Win32_PerfFormattedData_DTSPipeline_SQLServerDTSPipeline = "Win32_PerfFormattedData_DTSPipeline_SQLServerDTSPipeline";
  public static final String CLASS_Win32_PerfFormattedData_Fax_FaxServices = "Win32_PerfFormattedData_Fax_FaxServices";
  public static final String CLASS_Win32_PerfFormattedData_InetInfo_InternetInformationServicesGlobal = "Win32_PerfFormattedData_InetInfo_InternetInformationServicesGlobal";
  public static final String CLASS_Win32_PerfFormattedData_ISAPISearch_HttpIndexingService = "Win32_PerfFormattedData_ISAPISearch_HttpIndexingService";
  public static final String CLASS_Win32_PerfFormattedData_MSDTC_DistributedTransactionCoordinator = "Win32_PerfFormattedData_MSDTC_DistributedTransactionCoordinator";
  public static final String CLASS_Win32_PerfFormattedData_NETCLRData_NETCLRData = "Win32_PerfFormattedData_NETCLRData_NETCLRData";
  public static final String CLASS_Win32_PerfFormattedData_NETCLRNetworking_NETCLRNetworking = "Win32_PerfFormattedData_NETCLRNetworking_NETCLRNetworking";
  public static final String CLASS_Win32_PerfFormattedData_NETDataProviderforOracle_NETCLRData = "Win32_PerfFormattedData_NETDataProviderforOracle_NETCLRData";
  public static final String CLASS_Win32_PerfFormattedData_NETDataProviderforSqlServer_NETDataProviderforSqlServer = "Win32_PerfFormattedData_NETDataProviderforSqlServer_NETDataProviderforSqlServer";
  public static final String CLASS_Win32_PerfFormattedData_NETFramework_NETCLRExceptions = "Win32_PerfFormattedData_NETFramework_NETCLRExceptions";
  public static final String CLASS_Win32_PerfFormattedData_NETFramework_NETCLRInterop = "Win32_PerfFormattedData_NETFramework_NETCLRInterop";
  public static final String CLASS_Win32_PerfFormattedData_NETFramework_NETCLRJit = "Win32_PerfFormattedData_NETFramework_NETCLRJit";
  public static final String CLASS_Win32_PerfFormattedData_NETFramework_NETCLRLoading = "Win32_PerfFormattedData_NETFramework_NETCLRLoading";
  public static final String CLASS_Win32_PerfFormattedData_NETFramework_NETCLRLocksAndThreads = "Win32_PerfFormattedData_NETFramework_NETCLRLocksAndThreads";
  public static final String CLASS_Win32_PerfFormattedData_NETFramework_NETCLRMemory = "Win32_PerfFormattedData_NETFramework_NETCLRMemory";
  public static final String CLASS_Win32_PerfFormattedData_NETFramework_NETCLRRemoting = "Win32_PerfFormattedData_NETFramework_NETCLRRemoting";
  public static final String CLASS_Win32_PerfFormattedData_NETFramework_NETCLRSecurity = "Win32_PerfFormattedData_NETFramework_NETCLRSecurity";
  public static final String CLASS_Win32_PerfFormattedData_NTFSDRV_ControladordealmacenamientoNTFSdeSMTP = "Win32_PerfFormattedData_NTFSDRV_ControladordealmacenamientoNTFSdeSMTP";
  public static final String CLASS_Win32_PerfFormattedData_Outlook_Outlook = "Win32_PerfFormattedData_Outlook_Outlook";
  public static final String CLASS_Win32_PerfFormattedData_PerfDisk_LogicalDisk = "Win32_PerfFormattedData_PerfDisk_LogicalDisk";
  public static final String CLASS_Win32_PerfFormattedData_PerfDisk_PhysicalDisk = "Win32_PerfFormattedData_PerfDisk_PhysicalDisk";
  public static final String CLASS_Win32_PerfFormattedData_PerfNet_Browser = "Win32_PerfFormattedData_PerfNet_Browser";
  public static final String CLASS_Win32_PerfFormattedData_PerfNet_Redirector = "Win32_PerfFormattedData_PerfNet_Redirector";
  public static final String CLASS_Win32_PerfFormattedData_PerfNet_Server = "Win32_PerfFormattedData_PerfNet_Server";
  public static final String CLASS_Win32_PerfFormattedData_PerfNet_ServerWorkQueues = "Win32_PerfFormattedData_PerfNet_ServerWorkQueues";
  public static final String CLASS_Win32_PerfFormattedData_PerfOS_Cache = "Win32_PerfFormattedData_PerfOS_Cache";
  public static final String CLASS_Win32_PerfFormattedData_PerfOS_Memory = "Win32_PerfFormattedData_PerfOS_Memory";
  public static final String CLASS_Win32_PerfFormattedData_PerfOS_Objects = "Win32_PerfFormattedData_PerfOS_Objects";
  public static final String CLASS_Win32_PerfFormattedData_PerfOS_PagingFile = "Win32_PerfFormattedData_PerfOS_PagingFile";
  public static final String CLASS_Win32_PerfFormattedData_PerfOS_Processor = "Win32_PerfFormattedData_PerfOS_Processor";
  public static final String CLASS_Win32_PerfFormattedData_PerfOS_System = "Win32_PerfFormattedData_PerfOS_System";
  public static final String CLASS_Win32_PerfFormattedData_PerfProc_FullImage_Costly = "Win32_PerfFormattedData_PerfProc_FullImage_Costly";
  public static final String CLASS_Win32_PerfFormattedData_PerfProc_Image_Costly = "Win32_PerfFormattedData_PerfProc_Image_Costly";
  public static final String CLASS_Win32_PerfFormattedData_PerfProc_JobObject = "Win32_PerfFormattedData_PerfProc_JobObject";
  public static final String CLASS_Win32_PerfFormattedData_PerfProc_JobObjectDetails = "Win32_PerfFormattedData_PerfProc_JobObjectDetails";
  public static final String CLASS_Win32_PerfFormattedData_PerfProc_Process = "Win32_PerfFormattedData_PerfProc_Process";
  public static final String CLASS_Win32_PerfFormattedData_PerfProc_ProcessAddressSpace_Costly = "Win32_PerfFormattedData_PerfProc_ProcessAddressSpace_Costly";
  public static final String CLASS_Win32_PerfFormattedData_PerfProc_Thread = "Win32_PerfFormattedData_PerfProc_Thread";
  public static final String CLASS_Win32_PerfFormattedData_PerfProc_ThreadDetails_Costly = "Win32_PerfFormattedData_PerfProc_ThreadDetails_Costly";
  public static final String CLASS_Win32_PerfFormattedData_RemoteAccess_RASPort = "Win32_PerfFormattedData_RemoteAccess_RASPort";
  public static final String CLASS_Win32_PerfFormattedData_RemoteAccess_RASTotal = "Win32_PerfFormattedData_RemoteAccess_RASTotal";
  public static final String CLASS_Win32_PerfFormattedData_RSVP_RSVPInterfaces = "Win32_PerfFormattedData_RSVP_RSVPInterfaces";
  public static final String CLASS_Win32_PerfFormattedData_RSVP_RSVPService = "Win32_PerfFormattedData_RSVP_RSVPService";
  public static final String CLASS_Win32_PerfFormattedData_Spooler_PrintQueue = "Win32_PerfFormattedData_Spooler_PrintQueue";
  public static final String CLASS_Win32_PerfFormattedData_TapiSrv_Telephony = "Win32_PerfFormattedData_TapiSrv_Telephony";
  public static final String CLASS_Win32_PerfFormattedData_Tcpip_ICMP = "Win32_PerfFormattedData_Tcpip_ICMP";
  public static final String CLASS_Win32_PerfFormattedData_Tcpip_IP = "Win32_PerfFormattedData_Tcpip_IP";
  public static final String CLASS_Win32_PerfFormattedData_Tcpip_NBTConnection = "Win32_PerfFormattedData_Tcpip_NBTConnection";
  public static final String CLASS_Win32_PerfFormattedData_Tcpip_NetworkInterface = "Win32_PerfFormattedData_Tcpip_NetworkInterface";
  public static final String CLASS_Win32_PerfFormattedData_Tcpip_TCP = "Win32_PerfFormattedData_Tcpip_TCP";
  public static final String CLASS_Win32_PerfFormattedData_Tcpip_UDP = "Win32_PerfFormattedData_Tcpip_UDP";
  public static final String CLASS_Win32_PerfFormattedData_TermService_TerminalServices = "Win32_PerfFormattedData_TermService_TerminalServices";
  public static final String CLASS_Win32_PerfFormattedData_TermService_TerminalServicesSession = "Win32_PerfFormattedData_TermService_TerminalServicesSession";
  public static final String CLASS_Win32_PerfFormattedData_W3SVC_WebService = "Win32_PerfFormattedData_W3SVC_WebService";
  public static final String CLASS_Win32_PerfRawData_ASP_ActiveServerPages = "Win32_PerfRawData_ASP_ActiveServerPages";
  public static final String CLASS_Win32_PerfRawData_ASPNET_114322_ASPNETAppsv114322 = "Win32_PerfRawData_ASPNET_114322_ASPNETAppsv114322";
  public static final String CLASS_Win32_PerfRawData_ASPNET_114322_ASPNETv114322 = "Win32_PerfRawData_ASPNET_114322_ASPNETv114322";
  public static final String CLASS_Win32_PerfRawData_ASPNET_2040607_ASPNETAppsv2040607 = "Win32_PerfRawData_ASPNET_2040607_ASPNETAppsv2040607";
  public static final String CLASS_Win32_PerfRawData_ASPNET_2040607_ASPNETv2040607 = "Win32_PerfRawData_ASPNET_2040607_ASPNETv2040607";
  public static final String CLASS_Win32_PerfRawData_ASPNET_ASPNET = "Win32_PerfRawData_ASPNET_ASPNET";
  public static final String CLASS_Win32_PerfRawData_ASPNET_ASPNETApplications = "Win32_PerfRawData_ASPNET_ASPNETApplications";
  public static final String CLASS_Win32_PerfRawData_aspnet_state_ASPNETStateService = "Win32_PerfRawData_aspnet_state_ASPNETStateService";
  public static final String CLASS_Win32_PerfRawData_ContentFilter_IndexingServiceFilter = "Win32_PerfRawData_ContentFilter_IndexingServiceFilter";
  public static final String CLASS_Win32_PerfRawData_ContentIndex_IndexingService = "Win32_PerfRawData_ContentIndex_IndexingService";
  public static final String CLASS_Win32_PerfRawData_DTSPipeline_SQLServerDTSPipeline = "Win32_PerfRawData_DTSPipeline_SQLServerDTSPipeline";
  public static final String CLASS_Win32_PerfRawData_Fax_FaxServices = "Win32_PerfRawData_Fax_FaxServices";
  public static final String CLASS_Win32_PerfRawData_InetInfo_InternetInformationServicesGlobal = "Win32_PerfRawData_InetInfo_InternetInformationServicesGlobal";
  public static final String CLASS_Win32_PerfRawData_ISAPISearch_HttpIndexingService = "Win32_PerfRawData_ISAPISearch_HttpIndexingService";
  public static final String CLASS_Win32_PerfRawData_MSDTC_DistributedTransactionCoordinator = "Win32_PerfRawData_MSDTC_DistributedTransactionCoordinator";
  public static final String CLASS_Win32_PerfRawData_NETCLRData_NETCLRData = "Win32_PerfRawData_NETCLRData_NETCLRData";
  public static final String CLASS_Win32_PerfRawData_NETCLRNetworking_NETCLRNetworking = "Win32_PerfRawData_NETCLRNetworking_NETCLRNetworking";
  public static final String CLASS_Win32_PerfRawData_NETDataProviderforOracle_NETCLRData = "Win32_PerfRawData_NETDataProviderforOracle_NETCLRData";
  public static final String CLASS_Win32_PerfRawData_NETDataProviderforSqlServer_NETDataProviderforSqlServer = "Win32_PerfRawData_NETDataProviderforSqlServer_NETDataProviderforSqlServer";
  public static final String CLASS_Win32_PerfRawData_NETFramework_NETCLRExceptions = "Win32_PerfRawData_NETFramework_NETCLRExceptions";
  public static final String CLASS_Win32_PerfRawData_NETFramework_NETCLRInterop = "Win32_PerfRawData_NETFramework_NETCLRInterop";
  public static final String CLASS_Win32_PerfRawData_NETFramework_NETCLRJit = "Win32_PerfRawData_NETFramework_NETCLRJit";
  public static final String CLASS_Win32_PerfRawData_NETFramework_NETCLRLoading = "Win32_PerfRawData_NETFramework_NETCLRLoading";
  public static final String CLASS_Win32_PerfRawData_NETFramework_NETCLRLocksAndThreads = "Win32_PerfRawData_NETFramework_NETCLRLocksAndThreads";
  public static final String CLASS_Win32_PerfRawData_NETFramework_NETCLRMemory = "Win32_PerfRawData_NETFramework_NETCLRMemory";
  public static final String CLASS_Win32_PerfRawData_NETFramework_NETCLRRemoting = "Win32_PerfRawData_NETFramework_NETCLRRemoting";
  public static final String CLASS_Win32_PerfRawData_NETFramework_NETCLRSecurity = "Win32_PerfRawData_NETFramework_NETCLRSecurity";
  public static final String CLASS_Win32_PerfRawData_NTFSDRV_ControladordealmacenamientoNTFSdeSMTP = "Win32_PerfRawData_NTFSDRV_ControladordealmacenamientoNTFSdeSMTP";
  public static final String CLASS_Win32_PerfRawData_Outlook_Outlook = "Win32_PerfRawData_Outlook_Outlook";
  public static final String CLASS_Win32_PerfRawData_PerfDisk_LogicalDisk = "Win32_PerfRawData_PerfDisk_LogicalDisk";
  public static final String CLASS_Win32_PerfRawData_PerfDisk_PhysicalDisk = "Win32_PerfRawData_PerfDisk_PhysicalDisk";
  public static final String CLASS_Win32_PerfRawData_PerfNet_Browser = "Win32_PerfRawData_PerfNet_Browser";
  public static final String CLASS_Win32_PerfRawData_PerfNet_Redirector = "Win32_PerfRawData_PerfNet_Redirector";
  public static final String CLASS_Win32_PerfRawData_PerfNet_Server = "Win32_PerfRawData_PerfNet_Server";
  public static final String CLASS_Win32_PerfRawData_PerfNet_ServerWorkQueues = "Win32_PerfRawData_PerfNet_ServerWorkQueues";
  public static final String CLASS_Win32_PerfRawData_PerfOS_Cache = "Win32_PerfRawData_PerfOS_Cache";
  public static final String CLASS_Win32_PerfRawData_PerfOS_Memory = "Win32_PerfRawData_PerfOS_Memory";
  public static final String CLASS_Win32_PerfRawData_PerfOS_Objects = "Win32_PerfRawData_PerfOS_Objects";
  public static final String CLASS_Win32_PerfRawData_PerfOS_PagingFile = "Win32_PerfRawData_PerfOS_PagingFile";
  public static final String CLASS_Win32_PerfRawData_PerfOS_Processor = "Win32_PerfRawData_PerfOS_Processor";
  public static final String CLASS_Win32_PerfRawData_PerfOS_System = "Win32_PerfRawData_PerfOS_System";
  public static final String CLASS_Win32_PerfRawData_PerfProc_FullImage_Costly = "Win32_PerfRawData_PerfProc_FullImage_Costly";
  public static final String CLASS_Win32_PerfRawData_PerfProc_Image_Costly = "Win32_PerfRawData_PerfProc_Image_Costly";
  public static final String CLASS_Win32_PerfRawData_PerfProc_JobObject = "Win32_PerfRawData_PerfProc_JobObject";
  public static final String CLASS_Win32_PerfRawData_PerfProc_JobObjectDetails = "Win32_PerfRawData_PerfProc_JobObjectDetails";
  public static final String CLASS_Win32_PerfRawData_PerfProc_Process = "Win32_PerfRawData_PerfProc_Process";
  public static final String CLASS_Win32_PerfRawData_PerfProc_ProcessAddressSpace_Costly = "Win32_PerfRawData_PerfProc_ProcessAddressSpace_Costly";
  public static final String CLASS_Win32_PerfRawData_PerfProc_Thread = "Win32_PerfRawData_PerfProc_Thread";
  public static final String CLASS_Win32_PerfRawData_PerfProc_ThreadDetails_Costly = "Win32_PerfRawData_PerfProc_ThreadDetails_Costly";
  public static final String CLASS_Win32_PerfRawData_RemoteAccess_RASPort = "Win32_PerfRawData_RemoteAccess_RASPort";
  public static final String CLASS_Win32_PerfRawData_RemoteAccess_RASTotal = "Win32_PerfRawData_RemoteAccess_RASTotal";
  public static final String CLASS_Win32_PerfRawData_RSVP_RSVPInterfaces = "Win32_PerfRawData_RSVP_RSVPInterfaces";
  public static final String CLASS_Win32_PerfRawData_RSVP_RSVPService = "Win32_PerfRawData_RSVP_RSVPService";
  public static final String CLASS_Win32_PerfRawData_Spooler_PrintQueue = "Win32_PerfRawData_Spooler_PrintQueue";
  public static final String CLASS_Win32_PerfRawData_TapiSrv_Telephony = "Win32_PerfRawData_TapiSrv_Telephony";
  public static final String CLASS_Win32_PerfRawData_Tcpip_ICMP = "Win32_PerfRawData_Tcpip_ICMP";
  public static final String CLASS_Win32_PerfRawData_Tcpip_IP = "Win32_PerfRawData_Tcpip_IP";
  public static final String CLASS_Win32_PerfRawData_Tcpip_NBTConnection = "Win32_PerfRawData_Tcpip_NBTConnection";
  public static final String CLASS_Win32_PerfRawData_Tcpip_NetworkInterface = "Win32_PerfRawData_Tcpip_NetworkInterface";
  public static final String CLASS_Win32_PerfRawData_Tcpip_TCP = "Win32_PerfRawData_Tcpip_TCP";
  public static final String CLASS_Win32_PerfRawData_Tcpip_UDP = "Win32_PerfRawData_Tcpip_UDP";
  public static final String CLASS_Win32_PerfRawData_TermService_TerminalServices = "Win32_PerfRawData_TermService_TerminalServices";
  public static final String CLASS_Win32_PerfRawData_TermService_TerminalServicesSession = "Win32_PerfRawData_TermService_TerminalServicesSession";
  public static final String CLASS_Win32_PerfRawData_W3SVC_WebService = "Win32_PerfRawData_W3SVC_WebService";
  public static final String CLASS_Win32_PhysicalMedia = "Win32_PhysicalMedia";
  public static final String CLASS_Win32_PhysicalMemory = "Win32_PhysicalMemory";
  public static final String CLASS_Win32_PhysicalMemoryArray = "Win32_PhysicalMemoryArray";
  public static final String CLASS_Win32_PhysicalMemoryLocation = "Win32_PhysicalMemoryLocation";
  public static final String CLASS_Win32_PingStatus = "Win32_PingStatus";
  public static final String CLASS_Win32_PNPAllocatedResource = "Win32_PNPAllocatedResource";
  public static final String CLASS_Win32_PnPDevice = "Win32_PnPDevice";
  public static final String CLASS_Win32_PnPEntity = "Win32_PnPEntity";
  public static final String CLASS_Win32_PnPSignedDriver = "Win32_PnPSignedDriver";
  public static final String CLASS_Win32_PnPSignedDriverCIMDataFile = "Win32_PnPSignedDriverCIMDataFile";
  public static final String CLASS_Win32_PointingDevice = "Win32_PointingDevice";
  public static final String CLASS_Win32_PortableBattery = "Win32_PortableBattery";
  public static final String CLASS_Win32_PortConnector = "Win32_PortConnector";
  public static final String CLASS_Win32_PortResource = "Win32_PortResource";
  public static final String CLASS_Win32_POTSModem = "Win32_POTSModem";
  public static final String CLASS_Win32_POTSModemToSerialPort = "Win32_POTSModemToSerialPort";
  public static final String CLASS_Win32_Printer = "Win32_Printer";
  public static final String CLASS_Win32_PrinterConfiguration = "Win32_PrinterConfiguration";
  public static final String CLASS_Win32_PrinterController = "Win32_PrinterController";
  public static final String CLASS_Win32_PrinterDriver = "Win32_PrinterDriver";
  public static final String CLASS_Win32_PrinterDriverDll = "Win32_PrinterDriverDll";
  public static final String CLASS_Win32_PrinterSetting = "Win32_PrinterSetting";
  public static final String CLASS_Win32_PrinterShare = "Win32_PrinterShare";
  public static final String CLASS_Win32_PrintJob = "Win32_PrintJob";
  public static final String CLASS_Win32_Process = "Win32_Process";
  public static final String CLASS_Win32_Processor = "Win32_Processor";
  public static final String CLASS_Win32_Product = "Win32_Product";
  public static final String CLASS_Win32_ProductCheck = "Win32_ProductCheck";
  public static final String CLASS_Win32_ProductResource = "Win32_ProductResource";
  public static final String CLASS_Win32_ProductSoftwareFeatures = "Win32_ProductSoftwareFeatures";
  public static final String CLASS_Win32_ProgIDSpecification = "Win32_ProgIDSpecification";
  public static final String CLASS_Win32_ProgramGroup = "Win32_ProgramGroup";
  public static final String CLASS_Win32_ProgramGroupContents = "Win32_ProgramGroupContents";
  public static final String CLASS_Win32_Property = "Win32_Property";
  public static final String CLASS_Win32_ProtocolBinding = "Win32_ProtocolBinding";
  public static final String CLASS_Win32_Proxy = "Win32_Proxy";
  public static final String CLASS_Win32_PublishComponentAction = "Win32_PublishComponentAction";
  public static final String CLASS_Win32_QuickFixEngineering = "Win32_QuickFixEngineering";
  public static final String CLASS_Win32_QuotaSetting = "Win32_QuotaSetting";
  public static final String CLASS_Win32_Refrigeration = "Win32_Refrigeration";
  public static final String CLASS_Win32_Registry = "Win32_Registry";
  public static final String CLASS_Win32_RegistryAction = "Win32_RegistryAction";
  public static final String CLASS_Win32_RemoveFileAction = "Win32_RemoveFileAction";
  public static final String CLASS_Win32_RemoveIniAction = "Win32_RemoveIniAction";
  public static final String CLASS_Win32_ReserveCost = "Win32_ReserveCost";
  public static final String CLASS_Win32_ScheduledJob = "Win32_ScheduledJob";
  public static final String CLASS_Win32_SCSIController = "Win32_SCSIController";
  public static final String CLASS_Win32_SCSIControllerDevice = "Win32_SCSIControllerDevice";
  public static final String CLASS_Win32_SecuritySettingOfLogicalFile = "Win32_SecuritySettingOfLogicalFile";
  public static final String CLASS_Win32_SecuritySettingOfLogicalShare = "Win32_SecuritySettingOfLogicalShare";
  public static final String CLASS_Win32_SelfRegModuleAction = "Win32_SelfRegModuleAction";
  public static final String CLASS_Win32_SerialPort = "Win32_SerialPort";
  public static final String CLASS_Win32_SerialPortConfiguration = "Win32_SerialPortConfiguration";
  public static final String CLASS_Win32_SerialPortSetting = "Win32_SerialPortSetting";
  public static final String CLASS_Win32_ServerConnection = "Win32_ServerConnection";
  public static final String CLASS_Win32_ServerSession = "Win32_ServerSession";
  public static final String CLASS_Win32_Service = "Win32_Service";
  public static final String CLASS_Win32_ServiceControl = "Win32_ServiceControl";
  public static final String CLASS_Win32_ServiceSpecification = "Win32_ServiceSpecification";
  public static final String CLASS_Win32_ServiceSpecificationService = "Win32_ServiceSpecificationService";
  public static final String CLASS_Win32_SessionConnection = "Win32_SessionConnection";
  public static final String CLASS_Win32_SessionProcess = "Win32_SessionProcess";
  public static final String CLASS_Win32_Share = "Win32_Share";
  public static final String CLASS_Win32_ShareToDirectory = "Win32_ShareToDirectory";
  public static final String CLASS_Win32_ShortcutAction = "Win32_ShortcutAction";
  public static final String CLASS_Win32_ShortcutFile = "Win32_ShortcutFile";
  public static final String CLASS_Win32_ShortcutSAP = "Win32_ShortcutSAP";
  public static final String CLASS_Win32_SID = "Win32_SID";
  public static final String CLASS_Win32_SoftwareElement = "Win32_SoftwareElement";
  public static final String CLASS_Win32_SoftwareElementAction = "Win32_SoftwareElementAction";
  public static final String CLASS_Win32_SoftwareElementCheck = "Win32_SoftwareElementCheck";
  public static final String CLASS_Win32_SoftwareElementCondition = "Win32_SoftwareElementCondition";
  public static final String CLASS_Win32_SoftwareElementResource = "Win32_SoftwareElementResource";
  public static final String CLASS_Win32_SoftwareFeature = "Win32_SoftwareFeature";
  public static final String CLASS_Win32_SoftwareFeatureAction = "Win32_SoftwareFeatureAction";
  public static final String CLASS_Win32_SoftwareFeatureCheck = "Win32_SoftwareFeatureCheck";
  public static final String CLASS_Win32_SoftwareFeatureParent = "Win32_SoftwareFeatureParent";
  public static final String CLASS_Win32_SoftwareFeatureSoftwareElements = "Win32_SoftwareFeatureSoftwareElements";
  public static final String CLASS_Win32_SoundDevice = "Win32_SoundDevice";
  public static final String CLASS_Win32_StartupCommand = "Win32_StartupCommand";
  public static final String CLASS_Win32_SubDirectory = "Win32_SubDirectory";
  public static final String CLASS_Win32_SystemAccount = "Win32_SystemAccount";
  public static final String CLASS_Win32_SystemBIOS = "Win32_SystemBIOS";
  public static final String CLASS_Win32_SystemBootConfiguration = "Win32_SystemBootConfiguration";
  public static final String CLASS_Win32_SystemDesktop = "Win32_SystemDesktop";
  public static final String CLASS_Win32_SystemDevices = "Win32_SystemDevices";
  public static final String CLASS_Win32_SystemDriver = "Win32_SystemDriver";
  public static final String CLASS_Win32_SystemDriverPNPEntity = "Win32_SystemDriverPNPEntity";
  public static final String CLASS_Win32_SystemEnclosure = "Win32_SystemEnclosure";
  public static final String CLASS_Win32_SystemLoadOrderGroups = "Win32_SystemLoadOrderGroups";
  public static final String CLASS_Win32_SystemLogicalMemoryConfiguration = "Win32_SystemLogicalMemoryConfiguration";
  public static final String CLASS_Win32_SystemNetworkConnections = "Win32_SystemNetworkConnections";
  public static final String CLASS_Win32_SystemOperatingSystem = "Win32_SystemOperatingSystem";
  public static final String CLASS_Win32_SystemPartitions = "Win32_SystemPartitions";
  public static final String CLASS_Win32_SystemProcesses = "Win32_SystemProcesses";
  public static final String CLASS_Win32_SystemProgramGroups = "Win32_SystemProgramGroups";
  public static final String CLASS_Win32_SystemResources = "Win32_SystemResources";
  public static final String CLASS_Win32_SystemServices = "Win32_SystemServices";
  public static final String CLASS_Win32_SystemSlot = "Win32_SystemSlot";
  public static final String CLASS_Win32_SystemSystemDriver = "Win32_SystemSystemDriver";
  public static final String CLASS_Win32_SystemTimeZone = "Win32_SystemTimeZone";
  public static final String CLASS_Win32_SystemUsers = "Win32_SystemUsers";
  public static final String CLASS_Win32_TapeDrive = "Win32_TapeDrive";
  public static final String CLASS_Win32_TCPIPPrinterPort = "Win32_TCPIPPrinterPort";
  public static final String CLASS_Win32_TemperatureProbe = "Win32_TemperatureProbe";
  public static final String CLASS_Win32_Terminal = "Win32_Terminal";
  public static final String CLASS_Win32_TerminalService = "Win32_TerminalService";
  public static final String CLASS_Win32_TerminalServiceSetting = "Win32_TerminalServiceSetting";
  public static final String CLASS_Win32_TerminalServiceToSetting = "Win32_TerminalServiceToSetting";
  public static final String CLASS_Win32_TerminalTerminalSetting = "Win32_TerminalTerminalSetting";
  public static final String CLASS_Win32_Thread = "Win32_Thread";
  public static final String CLASS_Win32_TimeZone = "Win32_TimeZone";
  public static final String CLASS_Win32_TSAccount = "Win32_TSAccount";
  public static final String CLASS_Win32_TSClientSetting = "Win32_TSClientSetting";
  public static final String CLASS_Win32_TSEnvironmentSetting = "Win32_TSEnvironmentSetting";
  public static final String CLASS_Win32_TSGeneralSetting = "Win32_TSGeneralSetting";
  public static final String CLASS_Win32_TSLogonSetting = "Win32_TSLogonSetting";
  public static final String CLASS_Win32_TSNetworkAdapterListSetting = "Win32_TSNetworkAdapterListSetting";
  public static final String CLASS_Win32_TSNetworkAdapterSetting = "Win32_TSNetworkAdapterSetting";
  public static final String CLASS_Win32_TSPermissionsSetting = "Win32_TSPermissionsSetting";
  public static final String CLASS_Win32_TSRemoteControlSetting = "Win32_TSRemoteControlSetting";
  public static final String CLASS_Win32_TSSessionDirectory = "Win32_TSSessionDirectory";
  public static final String CLASS_Win32_TSSessionDirectorySetting = "Win32_TSSessionDirectorySetting";
  public static final String CLASS_Win32_TSSessionSetting = "Win32_TSSessionSetting";
  public static final String CLASS_Win32_TypeLibraryAction = "Win32_TypeLibraryAction";
  public static final String CLASS_Win32_UninterruptiblePowerSupply = "Win32_UninterruptiblePowerSupply";
  public static final String CLASS_Win32_USBController = "Win32_USBController";
  public static final String CLASS_Win32_USBControllerDevice = "Win32_USBControllerDevice";
  public static final String CLASS_Win32_USBHub = "Win32_USBHub";
  public static final String CLASS_Win32_UserAccount = "Win32_UserAccount";
  public static final String CLASS_Win32_UserDesktop = "Win32_UserDesktop";
  public static final String CLASS_Win32_UserInDomain = "Win32_UserInDomain";
  public static final String CLASS_Win32_UTCTime = "Win32_UTCTime";
  public static final String CLASS_Win32_VideoController = "Win32_VideoController";
  public static final String CLASS_Win32_VideoSettings = "Win32_VideoSettings";
  public static final String CLASS_Win32_VoltageProbe = "Win32_VoltageProbe";
  public static final String CLASS_Win32_VolumeQuotaSetting = "Win32_VolumeQuotaSetting";
  public static final String CLASS_Win32_WindowsProductActivation = "Win32_WindowsProductActivation";
  public static final String CLASS_Win32_WMIElementSetting = "Win32_WMIElementSetting";
  public static final String CLASS_Win32_WMISetting = "Win32_WMISetting";
}
