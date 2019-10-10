package entity;
/**
 * an entity class that stores constants and global variables, including the IP address of the server,
 * the default port and all the tasks the server is expected to execute.
 */
public class ConstantsAndGlobalVars {
	public final static int DEFAULT_PORT = 5555;
	public static String ipAddress;
	public final static String getReservationsTask="getreservations";
	public final static String loginTask = "login";
	public final static String[] searchType = {"Book Title","Category","Author Name"};
	public final static String searchBookTask = "searchBook";
	public final static String logoutTask = "logout";
	public final static String getBorrowsTask = "getborrows";
	public final static String getHistoryTask="gethistory";
	public final static String updateSettingTask="updateSetting";
	public final static String getNumberOfBooksTask="getNumberOfBooks";
	public final static String returnBookTask="returnBook";
	public final static String addNewUserTask="addNewUser";
	public final static String addBookTask = "addBook";
	public final static String removeBookTask = "removeBook";
	public final static String getBookTask = "getBook";
	public final static String updateBookTask = "updateBook";
	public final static String getUserTask = "getUser";
	public final static String checkUserTask="checkuser";
	public final static String checkCopyTask="checkcopy";
	public final static String checkBookTypeTask="checkbooktype";
	public final static String updateBorrowTask="updateborrowtable";
	public final static String checkNumOfCopyTask="checknumofcopy";
	public final static String addCopyTask = "addCopy";
	public final static String getMessagesTask = "messages";
	public final static String getNumberOfMessagesTask = "messagesNumber";
	public final static String getFaultsHistoryTask = "getFaultsHistory";
	public final static String checkEmployeeTypeTask = "checkEmployeeType";
	public final static String getUserPermissionsTask = "getUserPermissions";
	public final static String updateBookCopyTask = "updatebookcopytable";
	public final static String checkBorrowTask= "checkborrow";
	public final static String UpdateBorrowTableAfterDelayingTask= "updateBorrowtableafterdelaying";
	public final static String UpdateDelayTableTask= "updatedelaytable";
	public final static String getAllCategoriesTask = "getAllCategories";
	public final static String getMemberPermissionsTask = "getMemberPermissions";
	public final static String changeBorrowPermissionTask = "changeBorrowPermissin";
	public final static String changeReservePermissionTask = "changeReservePermission";
	public final static String changeMemberStatusTask = "changeMemberStatus";
	public final static String getNumberOfAvailableCopies = "getAvailbleCopies";
	public final static String getNumberOfReservesTask = "getNumberOfReserves";
	public final static String addReserveTask = "addReserve";
	public final static String checkIfReserveExistTask = "checkifreserveexist";
	public final static String updateReservestatusToDoneTask = "updatereservestatustodone";
	public final static String getAllMembersTask = "getAllMembers";
	public final static String sendMessageForDelayTask = "sendmessagefordelay";
	public final static String getAllEmployeesTask = "getAllEmployees";
	public final static String getNumOfAllMembersTask = "getNumOfAllMembers";
	public final static String getNumOfActiveMembersTask = "getNumOfActiveMembers";
	public final static String getNumOfFrozenMembersTask = "getNumOfFrozenMembers";
	public final static String getNumOfLockedMembersTask = "getNumOfLockedMembers";
	public final static String getNumOfBorrowedBooksTask = "getNumOfBorrowedBooks";
	public final static String getNumOfLateReturnMembersTask = "getNumOfLateReturnMembers";
	public final static String addFaultTask = "addFault";
	public final static String checkIfReservedTask = "checkifreserved";
	public final static String deleteMessageTask = "deleteMessage";
	public final static String checkReportExistenceTask = "checkReportExistence";
	public final static String addReportToDBTask = "addReportToDB";
	public final static String getDataForBorrowedReportTask = "getDataForBorrowedReport";
	public final static String getDataForLateReturnedReportTask = "getDataForLateReturnedReport";
}

