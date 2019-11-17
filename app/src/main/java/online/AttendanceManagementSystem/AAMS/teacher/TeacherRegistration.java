package online.AttendanceManagementSystem.AAMS.teacher;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Security;

import online.AttendanceManagementSystem.AAMS.MainActivity;
import online.AttendanceManagementSystem.AAMS.R;
import online.AttendanceManagementSystem.AAMS.contract.Attendees;
import online.AttendanceManagementSystem.AAMS.util.Alice;
import online.AttendanceManagementSystem.AAMS.util.Web3jConstants;
import online.AttendanceManagementSystem.AAMS.util.Web3jUtils;

public class TeacherRegistration extends AppCompatActivity {

    TextView singIn, signUp;

    RelativeLayout signInLayout, signUpLayout;

    EditText name,email,mobile, signinName, signInEmail, signInMobile;
    String nameStr,emailStr,mobStr;
    Button submit, signInSubmit;

    TeacherDB teacherDB;

    int permission_req_code = 1000;

    static final String ERROR = "Error";
    static Attendees contract = null;
    static ECKeyPair KEY_PAIR =null;
    public static Credentials CREDENTIALS = null;
    public static String ADDRESS = null;

    private String walletPath, filename;
    private File path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_registration);

        if (!defaultPermissionCheck()) {
            askForPermission();
        }

        setupBouncyCastle();

        teacherDB = new TeacherDB(TeacherRegistration.this);

        singIn = findViewById(R.id.singin);
        signUp = findViewById(R.id.singup);

        signInLayout = findViewById(R.id.signinLayout);
        signUpLayout = findViewById(R.id.signupLayout);

        name = findViewById(R.id.editTeacherName);
        email = findViewById(R.id.editTeacherEmail);
        mobile = findViewById(R.id.editTeacherMob);
        submit = findViewById(R.id.submitTeacherDetails);

        signinName = findViewById(R.id.enterTeacherName);
        signInEmail = findViewById(R.id.enterTeacherEmail);
        signInMobile = findViewById(R.id.enterTeacherMob);
        signInSubmit = findViewById(R.id.loginSubmitTeacherDetails);


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpLayout.setVisibility(View.VISIBLE);
                signInLayout.setVisibility(View.GONE);
                signUp.setTextColor(Color.parseColor("#57CAD5"));
                singIn.setTextColor(Color.parseColor("#999999"));

            }
        });


        singIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpLayout.setVisibility(View.GONE);
                signInLayout.setVisibility(View.VISIBLE);
                signUp.setTextColor(Color.parseColor("#999999"));
                singIn.setTextColor(Color.parseColor("#57CAD5"));

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameStr = name.getText().toString().trim();
                emailStr = email.getText().toString().trim();
                mobStr = mobile.getText().toString().trim();

                if(nameStr == null || nameStr.isEmpty() || emailStr == null || emailStr.isEmpty() || mobStr == null || mobStr.isEmpty()){
                    showDialog("Empty Field(s)","All fields are mandatory. Fill all fields to reigster");
                    return;
                }

                if(!isValidEmail(emailStr)){
                    showDialog("Invalid Email ID", "Please enter valid eamil ID to register.");
                }

                long a = teacherDB.insertTRegistrationDetails(nameStr,emailStr,mobStr,getDeviceIMEI());

                Log.e("insert",".. "+a+"  ");
                System.out.println("Name: "+nameStr+" email:"+emailStr+" mobstr: "+mobStr);

                try {
                    if((fileExistance("public_key.pem")) && (fileExistance("private_key.pem"))){
                        Alice.PUBLIC_KEY = readFile("public_key.pem");
                        Alice.PRIVATE_KEY = readFile("private_key.pem");

                        KEY_PAIR = new ECKeyPair(Numeric.toBigInt(Alice.PRIVATE_KEY), Numeric.toBigInt(Alice.PUBLIC_KEY));

                        //generate the credantionals and address
                        CREDENTIALS = Credentials.create(KEY_PAIR);
                        ADDRESS = CREDENTIALS.getAddress();
                    } else {
                        createWallet();
                        deployContract();
                        Log.e("b", contract.getContractAddress());
                        toastAsync("Wallet generated successfully");
                    }
                } catch (FileNotFoundException e) {
                    Log.e("b", "error in wallet generation");
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e("b", "error in deploying contract");
                    e.printStackTrace();
                }
                toastAsync("Sign Up successfull");
                toastAsync("You can sign in");
            }
        });

        signInSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameStr = signinName.getText().toString().trim();
                emailStr = signInEmail.getText().toString().trim();
                mobStr = signInMobile.getText().toString().trim();

                if(nameStr == null || nameStr.isEmpty() || emailStr == null || emailStr.isEmpty() || mobStr == null || mobStr.isEmpty()){
                    showDialog("Empty Field(s)","All fields are mandatory. Fill all fields to signIn");
                    return;
                }

                if(!isValidEmail(emailStr)){
                    showDialog("Invalid Email ID", "Please enter valid eamil ID to register.");
                }

//                long a=teacherDB.insertTRegistrationDetails(nameStr,emailStr,mobStr,getDeviceIMEI());
                Cursor sT = teacherDB.getTRegDetails();

//                Log.e("insert",".. "+a+"  ");
                // create new private/public key pair
                int teacherExistsFlag = 0;
                if(sT!=null && sT.getCount() > 0){
                    Log.e("b"," teacher data");
                    while(sT.moveToNext()){
                        if(sT.getString(0).equals(nameStr) && sT.getString(1).equals(emailStr) &&
                                sT.getString(2).equals(mobStr)){
                            teacherExistsFlag = 1;
                            if(!((fileExistance("public_key.pem")) && (fileExistance("private_key.pem")))){
                                try {
                                    createWallet();
                                    toastAsync("Wallet generated successfully");
                                } catch (FileNotFoundException e) {
                                    Log.e("b", "error in wallet generation");
                                    e.printStackTrace();
                                }
                            }

                            //read the key pairs from the internal file

                            startActivity(new Intent(TeacherRegistration.this,
                                    TeacherDashboard.class));
                            finish();
                        }
                    }
                }

                if(teacherExistsFlag == 0){
                    toastAsync("User does not exist. Please Signup");
                    Log.e("b","not found teacher data");
                }
            }
        });

    }

    public void showDialog(String title, String message){
        new AlertDialog.Builder(TeacherRegistration.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }


    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    @SuppressLint("MissingPermission")
    public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            if (!defaultPermissionCheck()) {
                askForPermission();
            } else {
                deviceUniqueIdentifier = tm.getDeviceId();
            }

        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }


    private void askForPermission() {

        //asking  for storage permission from user at runtime

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_PHONE_STATE
        }, permission_req_code);


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //checking if user granted the permissions or not

        if (requestCode == permission_req_code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted :)", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "App will not work without permissions, Grant these permissions from settings. :|", Toast.LENGTH_LONG).show();
                startActivity(new Intent(TeacherRegistration.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        }
    }

    private boolean defaultPermissionCheck() {
        //checking if permissions is already granted
        int external_storage_write = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_PHONE_STATE);
        return external_storage_write == PackageManager.PERMISSION_GRANTED;
    }

    private void createWallet() throws FileNotFoundException {
        // create new private/public key pair
        final ECKeyPair[] keyPair = {null};
        try {
            keyPair[0] = Keys.createEcKeyPair();
            System.out.println("Key pair: "+keyPair[0]);
        } catch (InvalidAlgorithmParameterException e) {
            Log.e(ERROR,"Invalid Algorithm Exception");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            Log.e(ERROR,"No Such Algorithm Exception");
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            Log.e(ERROR,"No Such Provider Exception");
        }

        //save the fey pair to the class for further use
        BigInteger publicKey = keyPair[0].getPublicKey();
        Alice.PUBLIC_KEY = Numeric.toHexStringWithPrefix(publicKey);

        BigInteger privateKey = keyPair[0].getPrivateKey();
        Alice.PRIVATE_KEY = Numeric.toHexStringWithPrefix(privateKey);
        saveFile(Alice.PUBLIC_KEY, "public_key.pem");
        toastAsync("Public key made");
        Log.d("Public",Alice.PUBLIC_KEY);
        saveFile(Alice.PRIVATE_KEY, "private_key.pem");
        KEY_PAIR = new ECKeyPair(Numeric.toBigInt(Alice.PRIVATE_KEY), Numeric.toBigInt(Alice.PUBLIC_KEY));

        //generate the credantionals and address
        CREDENTIALS = Credentials.create(KEY_PAIR);
        ADDRESS = CREDENTIALS.getAddress();
        System.out.println("Address: "+ADDRESS);
    }

    //save the data into internal storage
    public void saveFile(String message, String filename) throws FileNotFoundException {
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        System.out.println("+++++++++++++++++++++++++++++++++++++"+path);
        if (!path.exists()) {
            path.mkdir();
        }
        walletPath = String.valueOf(path);

        FileOutputStream outputStream = new FileOutputStream(new File(path, filename));
        try {
//            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(message.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //load the contract using user credential
    public static Attendees deployContract() throws Exception {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        System.out.println("// Deploy contract");
        ContractGasProvider contractGasProvider = new DefaultGasProvider();

        contract = Attendees
                .deploy(TeacherDashboard.web3j, Web3jUtils.getCredentials(), contractGasProvider).send();

        String contractAddress = contract.getContractAddress();
        System.out.println("Contract address: " + contractAddress);
        Web3jConstants.CONTRACT_ADDRESS = contractAddress;
        return contract;
    }



    //read the file from the internal database
    public String readFile(String filename) {
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

    //check whether the file exists or not
    public boolean fileExistance(String fname){
        System.out.println("Base context :" + getBaseContext());
        path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, fname);
        System.out.println("file existance : " + file.exists());
        return file.exists();
    }

    public void toastAsync(final String message) {
        runOnUiThread(() -> {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupBouncyCastle() {
        final Provider provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME);
        if (provider == null) {
// Web3j will set up the provider lazily when it's first used.
            return;
        }
        if (provider.getClass().equals(BouncyCastleProvider.class)) {
// BC with same package name, shouldn't happen in real life.
            return;
        }
// Android registers its own BC provider. As it might be outdated and might not include
// all needed ciphers, we substitute it with a known BC bundled in the app.
// Android's BC has its package rewritten to "com.android.org.bouncycastle" and because
// of that it's possible to have another BC implementation loaded in VM.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME);
        Security.insertProviderAt(new org.spongycastle.jce.provider.BouncyCastleProvider(), 1);
    }
}
