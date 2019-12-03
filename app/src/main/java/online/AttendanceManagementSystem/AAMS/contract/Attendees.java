package online.AttendanceManagementSystem.AAMS.contract;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.2.0.
 */
public class Attendees extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50600080546001600160a01b031916331790556118c2806100326000396000f3fe608060405234801561001057600080fd5b50600436106100b45760003560e01c8063581d87fd11610071578063581d87fd1461077b57806361f8506d1461093e57806377afc1aa1461094657806377ec1d9014610a61578063a323e07e14610a9b578063a72aa54a14610ac7576100b4565b80630e0c353d146100b95780632fd130f71461024f57806330244c141461030057806334ea37b11461043957806345c784ba146104fa5780635042cbd6146106bc575b600080fd5b6100d6600480360360208110156100cf57600080fd5b5035610acf565b60405180898152602001886001600160a01b03166001600160a01b0316815260200180602001806020018060200187815260200186815260200185815260200184810384528a818151815260200191508051906020019080838360005b8381101561014b578181015183820152602001610133565b50505050905090810190601f1680156101785780820380516001836020036101000a031916815260200191505b5084810383528951815289516020918201918b019080838360005b838110156101ab578181015183820152602001610193565b50505050905090810190601f1680156101d85780820380516001836020036101000a031916815260200191505b5084810382528851815288516020918201918a019080838360005b8381101561020b5781810151838201526020016101f3565b50505050905090810190601f1680156102385780820380516001836020036101000a031916815260200191505b509b50505050505050505050505060405180910390f35b6102fe6004803603604081101561026557600080fd5b810190602081018135600160201b81111561027f57600080fd5b82018360208201111561029157600080fd5b803590602001918460018302840111600160201b831117156102b257600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550505090356001600160a01b03169150610cb49050565b005b6102fe6004803603606081101561031657600080fd5b6001600160a01b038235169190810190604081016020820135600160201b81111561034057600080fd5b82018360208201111561035257600080fd5b803590602001918460018302840111600160201b8311171561037357600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b8111156103c557600080fd5b8201836020820111156103d757600080fd5b803590602001918460018302840111600160201b831117156103f857600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550610e51945050505050565b6104e86004803603604081101561044f57600080fd5b810190602081018135600160201b81111561046957600080fd5b82018360208201111561047b57600080fd5b803590602001918460018302840111600160201b8311171561049c57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550505090356001600160a01b03169150610ef19050565b60408051918252519081900360200190f35b6102fe600480360360c081101561051057600080fd5b810190602081018135600160201b81111561052a57600080fd5b82018360208201111561053c57600080fd5b803590602001918460018302840111600160201b8311171561055d57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b8111156105af57600080fd5b8201836020820111156105c157600080fd5b803590602001918460018302840111600160201b831117156105e257600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561063457600080fd5b82018360208201111561064657600080fd5b803590602001918460018302840111600160201b8311171561066757600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295505082359350505060208101356001600160a01b03169060400135611094565b6102fe600480360360608110156106d257600080fd5b6001600160a01b038235169190810190604081016020820135600160201b8111156106fc57600080fd5b82018360208201111561070e57600080fd5b803590602001918460018302840111600160201b8311171561072f57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600092019190915250929550505090356001600160a01b031691506111a39050565b6102fe600480360360c081101561079157600080fd5b6001600160a01b038235169190810190604081016020820135600160201b8111156107bb57600080fd5b8201836020820111156107cd57600080fd5b803590602001918460018302840111600160201b831117156107ee57600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b81111561084057600080fd5b82018360208201111561085257600080fd5b803590602001918460018302840111600160201b8311171561087357600080fd5b91908080601f0160208091040260200160405190810160405280939291908181526020018383808284376000920191909152509295949360208101935035915050600160201b8111156108c557600080fd5b8201836020820111156108d757600080fd5b803590602001918460018302840111600160201b831117156108f857600080fd5b91908080601f01602080910402602001604051908101604052809392919081815260200183838082843760009201919091525092955050823593505050602001356113fe565b6104e8611529565b6109636004803603602081101561095c57600080fd5b503561152f565b60405180856001600160a01b03166001600160a01b031681526020018060200180602001848152602001838103835286818151815260200191508051906020019080838360005b838110156109c25781810151838201526020016109aa565b50505050905090810190601f1680156109ef5780820380516001836020036101000a031916815260200191505b50838103825285518152855160209182019187019080838360005b83811015610a22578181015183820152602001610a0a565b50505050905090810190601f168015610a4f5780820380516001836020036101000a031916815260200191505b50965050505050505060405180910390f35b610a8760048036036020811015610a7757600080fd5b50356001600160a01b031661167b565b604080519115158252519081900360200190f35b610a8760048036036040811015610ab157600080fd5b506001600160a01b0381351690602001356116cc565b6104e8611746565b6001602081815260009283526040928390208054818401546002808401805488516101009882161598909802600019011691909104601f810186900486028701860190975286865291956001600160a01b03909116949293830182828015610b785780601f10610b4d57610100808354040283529160200191610b78565b820191906000526020600020905b815481529060010190602001808311610b5b57829003601f168201915b5050505060038301805460408051602060026001851615610100026000190190941693909304601f8101849004840282018401909252818152949594935090830182828015610c085780601f10610bdd57610100808354040283529160200191610c08565b820191906000526020600020905b815481529060010190602001808311610beb57829003601f168201915b5050505060048301805460408051602060026001851615610100026000190190941693909304601f8101849004840282018401909252818152949594935090830182828015610c985780601f10610c6d57610100808354040283529160200191610c98565b820191906000526020600020905b815481529060010190602001808311610c7b57829003601f168201915b5050505050908060050154908060060154908060070154905088565b60015b6004548111610e4c57826040516020018082805190602001908083835b60208310610cf35780518252601f199092019160209182019101610cd4565b6001836020036101000a03801982511681845116808217855250505050505090500191505060405160208183030381529060405280519060200120600360008381526020019081526020016000206001016040516020018082805460018160011615610100020316600290048015610da25780601f10610d80576101008083540402835291820191610da2565b820191906000526020600020905b815481529060010190602001808311610d8e575b5050915050604051602081830303815290604052805190602001201415610e445760015b60008281526003602081905260409091200154811015610e425760008281526003602090815260408083208484526004019091529020600101546001600160a01b0384811691161415610e3a5760008281526003602090815260408083208484526004019091529020600701805460010190555b600101610dc6565b505b600101610cb7565b505050565b610e5961174c565b6001600160a01b038481168252602080830185815260408085018690526001606086018190526004805482019081905560009081526003855291909120855181546001600160a01b031916951694909417845590518051859493610ec193850192019061177d565b5060408201518051610edd91600284019160209091019061177d565b506060820151816003015590505050505050565b600060015b600454811161108c57836040516020018082805190602001908083835b60208310610f325780518252601f199092019160209182019101610f13565b6001836020036101000a03801982511681845116808217855250505050505090500191505060405160208183030381529060405280519060200120600360008381526020019081526020016000206001016040516020018082805460018160011615610100020316600290048015610fe15780601f10610fbf576101008083540402835291820191610fe1565b820191906000526020600020905b815481529060010190602001808311610fcd575b50509150506040516020818303038152906040528051906020012014156110845760015b600082815260036020819052604090912001548110156110825760008281526003602090815260408083208484526004019091529020600101546001600160a01b038581169116141561107a57600091825260036020908152604080842092845260049092019052902060070154905061108e565b600101611005565b505b600101610ef6565b505b92915050565b6000546001600160a01b031633146110ab57600080fd5b60028054600190810180835560408051610100810182528281526001600160a01b0387811660208084019182528385018e8152606085018e9052608085018d905260a085018c905260c085018a9052600060e086018190529687528782529490952083518155905195810180546001600160a01b03191696909216959095179055905180519194611142939085019291019061177d565b506060820151805161115e91600384019160209091019061177d565b506080820151805161117a91600484019160209091019061177d565b5060a0820151600582015560c0820151600682015560e090910151600790910155505050505050565b60015b60045481116113f857826040516020018082805190602001908083835b602083106111e25780518252601f1990920191602091820191016111c3565b6001836020036101000a038019825116818451168082178552505050505050905001915050604051602081830303815290604052805190602001206003600083815260200190815260200160002060010160405160200180828054600181600116156101000203166002900480156112915780601f1061126f576101008083540402835291820191611291565b820191906000526020600020905b81548152906001019060200180831161127d575b50509150506040516020818303038152906040528051906020012014156113f05760015b60025481116113ee57600081815260016020819052604090912001546001600160a01b03848116911614156113e657600082815260036020818152604080842092830154858552600180845282862082875260049095019093529320825481558282015481830180546001600160a01b0319166001600160a01b0390921691909117905560028084018054929361135d938386019391811615610100026000190116046117fb565b50600382018160030190805460018160011615610100020316600290046113859291906117fb565b50600482018160040190805460018160011615610100020316600290046113ad9291906117fb565b50600582810154908201556006808301549082015560079182015491015550600082815260036020819052604090912001805460010190555b6001016112b5565b505b6001016111a6565b50505050565b60015b600254811161152057600081815260016020819052604090912001546001600160a01b03888116911614156115185760408051610100810182528281526001600160a01b0389811660208084019182528385018b8152606085018b9052608085018a905260a0850189905260c08501889052600060e08601819052878152600180845296902085518155925195830180546001600160a01b0319169690941695909517909255925180519293926114be926002850192019061177d565b50606082015180516114da91600384019160209091019061177d565b50608082015180516114f691600484019160209091019061177d565b5060a0820151600582015560c0820151600682015560e0909101516007909101555b600101611401565b50505050505050565b60025481565b6003602090815260009182526040918290208054600180830180548651600261010094831615949094026000190190911692909204601f81018690048602830186019096528582526001600160a01b039092169492939092908301828280156115d95780601f106115ae576101008083540402835291602001916115d9565b820191906000526020600020905b8154815290600101906020018083116115bc57829003601f168201915b50505060028085018054604080516020601f600019610100600187161502019094169590950492830185900485028101850190915281815295969594509092509083018282801561166b5780601f106116405761010080835404028352916020019161166b565b820191906000526020600020905b81548152906001019060200180831161164e57829003601f168201915b5050505050908060030154905084565b600060015b60025481116116c157600081815260016020819052604090912001546001600160a01b03848116911614156116b95760019150506116c7565b600101611680565b50600090505b919050565b600080546001600160a01b031633146116e457600080fd5b60015b600254811161173c57600081815260016020819052604090912001546001600160a01b0385811691161415611734576000908152600160208190526040909120600601839055905061108e565b6001016116e7565b5060009392505050565b60045481565b604051806080016040528060006001600160a01b031681526020016060815260200160608152602001600081525090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106117be57805160ff19168380011785556117eb565b828001600101855582156117eb579182015b828111156117eb5782518255916020019190600101906117d0565b506117f7929150611870565b5090565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061183457805485556117eb565b828001600101855582156117eb57600052602060002091601f016020900482015b828111156117eb578254825591600101919060010190611855565b61188a91905b808211156117f75760008155600101611876565b9056fea265627a7a7231582090ac763d76fac809a49ffec0893e735545c7076fdac4b38542b935da93ee23e864736f6c634300050b0032";

    public static final String FUNC_ATTENDEES = "attendees";

    public static final String FUNC_MARKATTENDANCEBYSUBJECT = "markAttendanceBySubject";

    public static final String FUNC_ADDNEWSUBJECT = "addNewSubject";

    public static final String FUNC_GETSTUDENTATTENDANCEFORSUBJECT = "getStudentAttendanceForSubject";

    public static final String FUNC_ADDATTENDEE = "addAttendee";

    public static final String FUNC_ADDSTUDENTENROLLED = "addStudentEnrolled";

    public static final String FUNC_UPDATEATTENDEE = "updateAttendee";

    public static final String FUNC_ATTENDEESCOUNT = "attendeesCount";

    public static final String FUNC_LISTOFSUBJECTS = "listOfSubjects";

    public static final String FUNC_AUTHENTICATEUSER = "authenticateUser";

    public static final String FUNC_CHANGESTATUSEMPLOYEE = "changeStatusEmployee";

    public static final String FUNC_SUBJECTSERIALNUMBER = "subjectSerialNumber";

    @Deprecated
    protected Attendees(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Attendees(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Attendees(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Attendees(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteCall<Tuple8<BigInteger, String, String, String, String, BigInteger, BigInteger, BigInteger>> attendees(BigInteger param0) {
        final Function function = new Function(FUNC_ATTENDEES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple8<BigInteger, String, String, String, String, BigInteger, BigInteger, BigInteger>>(
                new Callable<Tuple8<BigInteger, String, String, String, String, BigInteger, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple8<BigInteger, String, String, String, String, BigInteger, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple8<BigInteger, String, String, String, String, BigInteger, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (String) results.get(3).getValue(), 
                                (String) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue(), 
                                (BigInteger) results.get(6).getValue(), 
                                (BigInteger) results.get(7).getValue());
                    }
                });
    }

    public RemoteCall<TransactionReceipt> markAttendanceBySubject(String _subject_id, String _public_key) {
        final Function function = new Function(
                FUNC_MARKATTENDANCEBYSUBJECT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_subject_id), 
                new org.web3j.abi.datatypes.Address(_public_key)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addNewSubject(String _attendance_giver, String _subject_id, String _batch_id) {
        final Function function = new Function(
                FUNC_ADDNEWSUBJECT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_attendance_giver), 
                new org.web3j.abi.datatypes.Utf8String(_subject_id), 
                new org.web3j.abi.datatypes.Utf8String(_batch_id)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> getStudentAttendanceForSubject(String _subject_id, String _public_key) {
        final Function function = new Function(FUNC_GETSTUDENTATTENDANCEFORSUBJECT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_subject_id), 
                new org.web3j.abi.datatypes.Address(_public_key)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<TransactionReceipt> addAttendee(String _name, String _roll, String _email, BigInteger _imei, String _public_key, BigInteger status) {
        final Function function = new Function(
                FUNC_ADDATTENDEE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_roll), 
                new org.web3j.abi.datatypes.Utf8String(_email), 
                new org.web3j.abi.datatypes.generated.Uint256(_imei), 
                new org.web3j.abi.datatypes.Address(_public_key), 
                new org.web3j.abi.datatypes.generated.Uint256(status)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> addStudentEnrolled(String _attendance_giver, String _subject_id, String _public_key) {
        final Function function = new Function(
                FUNC_ADDSTUDENTENROLLED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_attendance_giver), 
                new org.web3j.abi.datatypes.Utf8String(_subject_id), 
                new org.web3j.abi.datatypes.Address(_public_key)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> updateAttendee(String _user_add, String _name, String _roll, String _email, BigInteger _imei, BigInteger status) {
        final Function function = new Function(
                FUNC_UPDATEATTENDEE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_user_add), 
                new org.web3j.abi.datatypes.Utf8String(_name), 
                new org.web3j.abi.datatypes.Utf8String(_roll), 
                new org.web3j.abi.datatypes.Utf8String(_email), 
                new org.web3j.abi.datatypes.generated.Uint256(_imei), 
                new org.web3j.abi.datatypes.generated.Uint256(status)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> attendeesCount() {
        final Function function = new Function(FUNC_ATTENDEESCOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteCall<Tuple4<String, String, String, BigInteger>> listOfSubjects(BigInteger param0) {
        final Function function = new Function(FUNC_LISTOFSUBJECTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        return new RemoteCall<Tuple4<String, String, String, BigInteger>>(
                new Callable<Tuple4<String, String, String, BigInteger>>() {
                    @Override
                    public Tuple4<String, String, String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple4<String, String, String, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (String) results.get(2).getValue(), 
                                (BigInteger) results.get(3).getValue());
                    }
                });
    }

    public RemoteCall<Boolean> authenticateUser(String _user_add) {
        final Function function = new Function(FUNC_AUTHENTICATEUSER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_user_add)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> changeStatusEmployee(String _employeeAdd, BigInteger status) {
        final Function function = new Function(
                FUNC_CHANGESTATUSEMPLOYEE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_employeeAdd), 
                new org.web3j.abi.datatypes.generated.Uint256(status)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<BigInteger> subjectSerialNumber() {
        final Function function = new Function(FUNC_SUBJECTSERIALNUMBER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static Attendees load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Attendees(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Attendees load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Attendees(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Attendees load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Attendees(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Attendees load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Attendees(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<Attendees> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Attendees.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<Attendees> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(Attendees.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Attendees> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Attendees.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<Attendees> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(Attendees.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
