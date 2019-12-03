# Blockchain based Attendance Management System App

  

This is a walkthrough for development / contributing to this repository.  

  

## Technical Overview:

  

### Android App Overview : 

  

The application comprises of two modules - Teacher Module and Student Module. 

Upon starting the application, the user is provided with two options, to register as teacher or to act a student. 

  

If the user is a student, he/she has to fill out a form, after which he has to get it verified by a teacher using the verify button. This verify service is also used to enroll in a course upon being asked by the teacher. After verification, the students are given the button of marking attendance, which is used to mark their attendance in the teacher’s subject upon being asked by the teacher. 

  

If the user is a teacher, he/she has to select the teacher module. A password from teachers will be asked (hard-coded as “123456”). Then the teacher can register, if for the first time, or login. Upon register, a public private key is generated and stored in Downloads folder of the android device, so that users can take a backup for the same. Also, upon registering, a smart contract gets deployed for the teacher, enabling him to take attendance for students and synchronise it with blockchain. 

Upon successful login the teacher can add batches of students ( based on year they’re enrolled in), then add subjects he/she teaches per batch. For each subject, teacher needs to enroll students for the first time by clicking on the three dot menu and clicking on add students. After that another on the next screen, he/she can see the current enrolled students in the course, the bottom circle (+) button, helps in adding new student. Upon clicking it, two buttons are presented, namely START and STOP, the start button enables the google nearby service to discover devices nearby and adds them ( if the students simultaneously clicks on verify) to the subject list. Next, on the main screen, after we click on a subject, we are shown, START and STOP options which indicate to start and stop the attendance taking service. Until the service is stopped manually, students can mark their attendance using students module. The next feature on this subject screen in the menu is generate attendance list, which gives us a list of students v/s attendance daywise. The next feature in the menu is sync attendance, which starts making transactions against the teachers’ deployed contract.

  

The application uses google nearby service for marking attendance and IMEI for proof validation so that no proxies can be put. 

  

### Smart Contract:

The smart contract is stored at : 

Blockchain_Attendance_Android/app/src/main/java/online/AttendanceManagementSystem/AAMS/contract/Attendance.sol

  

Attendees is the main contract which we have written. The contract is self explanatory. You can add more functionality to it.

To learn how to write a smart contract you can explore this website: [https://cryptozombies.io/](https://cryptozombies.io/)

  
  

### Integrating Blockchain with Android:

  

Please be patient with this part of the project. You need the web3j library to convert smart contracts to the corresponding java code and use them just like the normal android code.

  

The version of web3j that you use will influence the compatibility of your solidity compiler and the web3j library. Also install the solidity compiler[the latest version] .

  
```
npm install -g --unsafe-perm solc
```
 

The version of web3j we used was 4.2.0: [https://github.com/arunikayadav42/Blockchain_Attendance_Android/tree/integrating_web3/app/src/main/solidity/web3j-4.2.0](https://github.com/arunikayadav42/Blockchain_Attendance_Android/tree/integrating_web3/app/src/main/solidity/web3j-4.2.0)./web3j solidity generate -b <location to the bin file from above>/<conract_name>.bin -a <location to the abi file from above>/<conract_name>.abi -o <contract output directory> -p <application_name>

  

A bin and an abi file is generated for each contract in your solidity code. These can then be used to generate the corresponding java code.

  
```
solc Attendance.sol --bin --abi --optimize -o ./
```

Attendance.sol is to be replaced by the name of your contract name and the -o flag corresponds to the output directory where you want your java source code generated from the abi and bin file to be stored. 

  

The commands required to compile the solidity code[Make sure you are in the repository in which your web3j folder is] :

  
```
./web3j solidity generate -b &lt;location to the bin file from above&gt;/&lt;conract_name&gt;.bin -a  &lt;location to the abi file from above&gt;/&lt;conract_name&gt;.abi -o &lt;contract output directory&gt; -p &lt;application_name&gt;
  

Eg: 

./web3j solidity generate -b /home/arunika/Documents/ApAttendance/app/src/main/java/online/forgottenbit/attendance1/contract/Attendees.bin -a /home/arunika/Documents/ApAttendance/app/src/main/java/online/forgottenbit/attendance1/contract/Attendees.abi -o /home/arunika/Documents/ApAttendance/app/src/main/java/online/forgottenbit/attendance1/contract/ -p online.AttendanceManagementSystem.AAMS
```
  

P.S. Do not forget the -a, -o and the -b flags.

  

After this the java code for the transaction needs to be written. The github repository we referred to for this was: 

  

[https://github.com/elliesheny/Web3j_project/tree/master/app](https://github.com/elliesheny/Web3j_project/tree/master/app)

  

In addition to this we used the metamask chrome extension to send ether to the teacher’s address so that he can make transactions in the form of attendance. These transactions were viewed on :

  

[https://rinkeby.etherscan.io/](https://rinkeby.etherscan.io/)

  

We can put the corresponding contract address deployed by the teacher and all transactions corresponding to that address can be viewed. The input and outputs of the transactions are in the abi format. These can be decoded with the help of an online tool:

[https://lab.miguelmota.com/ethereum-input-data-decoder/example/](https://lab.miguelmota.com/ethereum-input-data-decoder/example/)

__Developed by :  Arunika Yadav(1601CS56) | Mayank Wadhwani(1601CS51) | IIT Patna__

__Under the guidance of : Dr. Raju Halder__

