pragma solidity >=0.4.25;

contract Attendees {

    // instantiation of structure
    struct AttendeesStructure {
        uint256 uid;
        address public_key;
        string name;
        string roll;
        string email;
        uint256 imei;
        string subject;
        string batch;
        uint256 status; // 1 = Active 2 = deleted
    }

    address owner;

    //mapping of structure for storing the attendees
    mapping(uint256 => AttendeesStructure) public attendees;
    uint256 public attendeesCount;

    //1540944000
    // constructor to save some attendees
    constructor() public {
        owner = msg.sender;
    }

    // modifier to add the attendee by owner only
    modifier onlyOwner{
        require(msg.sender == owner);
        _;
    }

    // add attendee to attendees mapping
    function addAttendee(string memory _name, 
        string memory _roll, 
        string memory _email, 
        uint256 _imei,
        string memory _subject,
        string memory _batch
        address _public_key, 
        uint256 status) onlyOwner public {

        attendeesCount++;
        attendees[attendeesCount] = AttendeesStructure(attendeesCount, _public_key, _name, _roll, _email, _imei, _subject, _batch, status);
    }

    // authenticate users
    function authenticateUser(address _user_add) public view returns (bool) {
        for (uint256 i = 1; i <= attendeesCount; i++) {
            if (attendees[i].public_key == _user_add) return true;
        }
        return false;
    }

    // for updating attendee
    function updateAttendee(address _user_add, 
        string memory _name,
        string memory _roll, 
        string memory _email, 
        uint256 _imei,
        string memory _subject,
        string memory _batch,
        uint256 status) public {
        for (uint256 i = 1; i <= attendeesCount; i++) {
            if (attendees[i].public_key == _user_add) {
                attendees[i] = AttendeesStructure(i, _user_add, _name, _roll, _email, _imei, _subject, _batch, status);
            }
        }
    }

    function changeStatusEmployee(address _employeeAdd, uint256 status) onlyOwner public returns (bool) {
        for (uint256 i = 1; i <= attendeesCount; i++) {
            if (attendees[i].public_key == _employeeAdd) {
                attendees[i].status = status;
//                attendees[i].public_key = 0*0;
                return true;
            }
        }
        return false;
    }

}

contract MarkAttendance is Attendees {

    // instantiation of structure
    struct AttendeeDetails {
        address attendance_giver;
        address attendee;
        uint256 attendance_opinion;
        uint256 timestamp;
        uint256 date_of_attendance;
        string _subject;
        string _batch;
        mapping(uint256 => Attendees) studentList;
    }

    //mapping of structure  for storing the attendeeDetails
    mapping(uint256 => AttendeeDetails) public attendeeDetails;
    uint256 public attendeeDetailsCount;

    //constructorModifiers()
    constructor() public {

    }

    // save mark attendance details to attendeeDetails mapping
    function markAttendance(address _attendee, uint256 _attendance_opinion, uint256 _date, string memory _subject, string memory _batch) public {
        attendeeDetailsCount ++;
        attendeeDetails[attendeeDetailsCount] = AttendeeDetails(msg.sender, _attendee, _attendance_opinion, now, _date, _subject, _batch);
    }

    // fetter function for attendee details count
    function getMarkedAttendeeDetailsCount() public view returns (uint256) {return attendeeDetailsCount;}

    //getter function for attendee details
    function getAttendeeDetails(uint256 _count, uint256 _date) public view returns (address, uint256, uint256) {
        address attendee_add = attendees[_count].public_key;
        uint256 doa = attendeeDetails[_count].date_of_attendance;
        uint256 present = 0;
        uint256 absent = 0;
        uint256 opinion = 3;
        for (uint256 i = 1; i <= attendeeDetailsCount; i++) {
            if (attendee_add == attendeeDetails[i].attendee && doa == _date)
            {
                if (attendeeDetails[i].attendance_opinion == 1) present++;
                else if (attendeeDetails[i].attendance_opinion == 2) absent++;
            }
        }

        if (present != 0 || absent != 0) {
            if (present < absent) opinion = 2;
            if (present > absent) opinion = 1;
            if (present == absent) opinion = 1;
        }

        return (attendee_add, opinion, _date);
    }

    // validate marking attendance
    function validateAttendance(address _attendee, uint256 _date) public view returns (bool){
        for (uint256 i = 1; i <= attendeeDetailsCount; i++) {
            if (attendeeDetails[i].attendance_giver == msg.sender && attendeeDetails[i].attendee == _attendee && attendeeDetails[i].date_of_attendance == _date) {
                return true;
            }
        }
        return false;
    }
}

contract EvaluateAttendance is MarkAttendance {

    // instantiation  of structure
    struct EvaluatedAttendee {
        address attendee_address;
        uint256 opinion;
        uint256 date_of_attendance;
        uint256 date_evaluated;
    }

    //mapping of structure for storing the evaluated_attendees
    mapping(uint256 => EvaluatedAttendee) public evaluated_attendees;
    uint256 public evaluateCount;

    uint256 public r_opinion = 3;
    address public r_attendee_address;
    uint256 public r_date_of_attendance;

    // constructor
    constructor() public {
    }

    // evaluate attendance result on the basic of attendee and date
    function evaluation(uint256 _date) public {
        evaluateCount = 1;
        r_opinion;
        r_date_of_attendance;
        r_attendee_address;
        for (uint256 i = 1; i <= attendeesCount; i++) {
            (r_attendee_address, r_opinion, r_date_of_attendance) = getAttendeeDetails(i, _date);
            evaluated_attendees[evaluateCount] = EvaluatedAttendee(r_attendee_address, r_opinion, r_date_of_attendance, now);
            evaluateCount++;
        }
    }

    // for getting attendance result as per date and address
    function attendanceResult(uint256 _date, address _addr) public view returns (uint256) {
        uint256 present = 0;
        uint256 absent = 0;
        uint256 opinion = 3;
        for (uint256 i = 1; i <= attendeeDetailsCount; i++) {
            if (_addr == attendeeDetails[i].attendee && attendeeDetails[i].date_of_attendance == _date)
            {
                if (attendeeDetails[i].attendance_opinion == 1) present++;
                else if (attendeeDetails[i].attendance_opinion == 2) absent++;
            }
        }
        if (present != 0 || absent != 0) {
            if (present < absent) opinion = 2;
            if (present > absent) opinion = 1;
            if (present == absent) opinion = 1;
        }
        return opinion;
    }

}