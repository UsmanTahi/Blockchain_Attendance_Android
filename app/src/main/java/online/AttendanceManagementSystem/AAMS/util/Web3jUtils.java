package online.AttendanceManagementSystem.AAMS.util;

import android.os.Environment;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthAccounts;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.ExecutionException;

import online.AttendanceManagementSystem.AAMS.teacher.TeacherDashboard;


public class Web3jUtils {

    private static String ADDRESS = null;
    private static ECKeyPair KEY_PAIR = null;
    private static Credentials CREDENTIALS = null;
    private static Web3j web3j = TeacherDashboard.web3j;
    private static String address;

    //returns the balance
    public static BigInteger getBalanceWei(Web3j web3j, String address) throws InterruptedException, ExecutionException {
        Web3jUtils.web3j = TeacherDashboard.web3j;
        Web3jUtils.address = address;
        EthGetBalance balance = web3j
                .ethGetBalance(address, DefaultBlockParameterName.LATEST)
                .sendAsync()
                .get();

        return balance.getBalance();
    }


    //send the signed transaction and wair for receipt
    public static TransactionReceipt waitForReceipt(Web3j web3j, String transactionHash)
            throws Exception
    {

        int attempts = Web3jConstants.CONFIRMATION_ATTEMPTS;
        int sleep_millis = Web3jConstants.SLEEP_DURATION;

        java8.util.Optional<TransactionReceipt> receipt = getReceipt(web3j, transactionHash);

        while(attempts-- > 0 && receipt != null) {
            Thread.sleep(sleep_millis);
            receipt = getReceipt(web3j, transactionHash);
        }

        if (attempts <= 0) {
            throw new RuntimeException("No Tx receipt received");
        }

        return receipt.get();
    }

    //return the receipt of the transaction
    public static java8.util.Optional<TransactionReceipt> getReceipt(Web3j web3j, String transactionHash)
            throws Exception
    {
        EthGetTransactionReceipt receipt = web3j
                .ethGetTransactionReceipt(transactionHash)
                .sendAsync()
                .get();

        return receipt.getTransactionReceipt();
    }

    //get the nonce of the transaction
    public static BigInteger getNonce(Web3j web3j, String address) throws InterruptedException, ExecutionException {
        EthGetTransactionCount ethGetTransactionCount =
                web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).sendAsync().get();

        return ethGetTransactionCount.getTransactionCount();
    }

    //get the coinbase of the ethereum wallet
    static public String getCoinbase() {
        return getAccount(0);
    }

    //get the account from web3j
    static String getAccount(int i) {
        try {
            EthAccounts accountsResponse = web3j.ethAccounts().sendAsync().get();
            List<String> accounts = accountsResponse.getAccounts();

            return accounts.get(i);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return "<no address>";
        }
    }

    //get the nonce of the transaction
    public static BigInteger getNonce(String address) throws Exception {
        return Web3jUtils.getNonce(TeacherDashboard.web3j, address);
    }

    //wait for receipt of the transaction
    public static TransactionReceipt waitForReceipt(String transactionHash) throws Exception {
        return Web3jUtils.waitForReceipt(TeacherDashboard.web3j, transactionHash);
    }

    public static String getWalletAddress(){
        CREDENTIALS = getCredentials();
        ADDRESS = CREDENTIALS.getAddress();
        return ADDRESS;
    }

    public static Credentials getCredentials(){
        Alice.PUBLIC_KEY = readFile("public_key.pem");
        Alice.PRIVATE_KEY = readFile("private_key.pem");
        KEY_PAIR = new ECKeyPair(Numeric.toBigInt(Alice.PRIVATE_KEY), Numeric.toBigInt(Alice.PUBLIC_KEY));
        //generate the credantionals and address
        CREDENTIALS = Credentials.create(KEY_PAIR);
        return CREDENTIALS;
    }

    private static String readFile(String filename) {
        FileInputStream inputStream;
        try
        {
            inputStream =  new FileInputStream(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS)+ "/" + filename);
            InputStreamReader isr = new InputStreamReader(inputStream); BufferedReader bufferedReader = new BufferedReader(isr); StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }
}
