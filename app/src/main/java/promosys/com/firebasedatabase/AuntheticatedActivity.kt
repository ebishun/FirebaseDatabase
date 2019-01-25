package promosys.com.firebasedatabase

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_auntheticated.*
import org.w3c.dom.Comment
import java.lang.ref.Reference
import com.google.firebase.database.DataSnapshot
import java.text.SimpleDateFormat
import java.util.*


class AuntheticatedActivity:AppCompatActivity(){

    private lateinit var databaseReference: DatabaseReference
    private var employeeList: ArrayList<Employee>? = null
    private var employeeAdapter: AttendanceAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auntheticated)

        databaseReference = FirebaseDatabase.getInstance().reference
        initRecView()

        btn_add.setOnClickListener {
            addEmployee()
        }

        btn_reset.setOnClickListener {
            resetAttendance()
        }

        databaseReference.addValueEventListener(postListener)
    }

    val postListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            employeeList!!.clear()
            for (locationSnapshot in dataSnapshot.child("Employee").children) {
                var location = locationSnapshot.getValue(Employee::class.java)
                var attObject = Employee(location!!.name, location.attend_status, location.timestamp,location.uuid)
                employeeList!!.add(attObject)
                employeeAdapter!!.notifyDataSetChanged()
            }
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Post failed, log a message
            Log.w("Auntheticated", "loadPost:onCancelled", databaseError.toException())
        }
    }



    private fun initRecView(){
        //set layout manager
        recvw_employee!!.layoutManager = LinearLayoutManager(this,LinearLayout.VERTICAL,false)

        //init list
        employeeList = ArrayList<Employee>()

        //init adapter
        employeeAdapter = AttendanceAdapter(employeeList!!)

        //set adapter to recycler view
        recvw_employee!!.adapter = employeeAdapter

    }

    private fun addEmployee(){
        val builder = AlertDialog.Builder(this)
        val view = layoutInflater.inflate(R.layout.add_employee,null)
        builder.setView(view)

        var edtEmployeeName: EditText?=null
        edtEmployeeName = view.findViewById(R.id.edtName)

        builder.setPositiveButton(android.R.string.ok) { dialog, p1 ->
            addToFirebase(edtEmployeeName!!.text.toString())
            dialog.cancel()
        }

        builder.show()
    }

    private fun resetAttendance(){
        for (i in 0..employeeList!!.size-1){
            var emp_uuid:String = employeeList!!.get(i).uuid
            employeeList!!.get(i).attend_status = 0
            employeeList!!.get(i).timestamp = "Status"

            setstatus(emp_uuid,employeeList!!.get(i).attend_status)
            updateData(emp_uuid,"timestamp",employeeList!!.get(i).timestamp)
        }
    }

    private fun addToFirebase(name: String){
        var attObject = Employee(name, 0, "Time","")

        employeeList!!.add(attObject)
        val key = databaseReference.child("Employee").push().key
        key?.let {
            attObject.uuid = key
            databaseReference.child("Employee").child(key).setValue(attObject)
        }
        employeeAdapter!!.notifyDataSetChanged()
    }

    private fun updateData(uuid: String, whichParam: String, value: String){
        try {
            databaseReference.child("Employee").child(uuid).child(whichParam).setValue(value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setstatus(uuid: String, value:Int){
        try {
            databaseReference.child("Employee").child(uuid).child("attend_status").setValue(value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun fromAttendanceAdapter(position:Int){
        Log.i("NotificationAdapter", "attList: $position")
        addEmployeeStatus(position)
    }

    fun fromAttendanceAdapterLongPressed(position:Int){
        Log.i("NotificationAdapter", "attList: $position")
        editEmployeeStatus(position)
    }

    private fun addEmployeeStatus(position:Int) {
        val context = this
        val builder = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.status_employee, null)
        builder.setView(view)

        var status:Int = 0

        var rbtnPresent: RadioButton?=null
        rbtnPresent = view.findViewById(R.id.rbtnPresent)

        var rbtnLeave: RadioButton?=null
        rbtnLeave = view.findViewById(R.id.rbtnLeave)

        var rbtnMC: RadioButton?=null
        rbtnMC = view.findViewById(R.id.rbtnMC)

        builder.setPositiveButton(android.R.string.ok) { dialog, p1 ->
            if(rbtnPresent.isChecked){
                status = 1
                employeeList!!.get(position).timestamp = getTime()
            }else if(rbtnLeave.isChecked){
                status = 2
                employeeList!!.get(position).timestamp = "Leave"
            }else if(rbtnMC.isChecked){
                status = 3
                employeeList!!.get(position).timestamp = "Klang Office"
            }
            employeeList!!.get(position).attend_status = status

            //saveList()
            var emp_uuid: String = employeeList!!.get(position).uuid
            setstatus(emp_uuid,status)
            updateData(emp_uuid,"timestamp",employeeList!!.get(position).timestamp)
            dialog.cancel()
        }
        builder.show()
    }

    private fun editEmployeeStatus(position:Int) {
        val context = this
        val builder = AlertDialog.Builder(context)
        val view = layoutInflater.inflate(R.layout.edit_employee, null)
        builder.setView(view)

        var status:Int = 0

        var edtEmployeeName: EditText?=null
        edtEmployeeName = view.findViewById(R.id.edtEmployeeName)

        var edtEmployeeTime: EditText?=null
        edtEmployeeTime = view.findViewById(R.id.edtEntryTime)

        edtEmployeeName.setText(employeeList!!.get(position).name)
        status = employeeList!!.get(position).attend_status
        if(status == 1){
            edtEmployeeTime.setVisibility(View.VISIBLE)
            edtEmployeeTime.setText(employeeList!!.get(position).timestamp)
        }
        builder.setPositiveButton(android.R.string.ok) { dialog, p1 ->
            employeeList!!.get(position).name = edtEmployeeName.text.toString()
            if(status == 1){
                employeeList!!.get(position).timestamp = edtEmployeeTime.text.toString()
            }

            updateData(employeeList!!.get(position).uuid,"name",employeeList!!.get(position).name)
            updateData(employeeList!!.get(position).uuid,"timestamp",employeeList!!.get(position).timestamp)
            dialog.cancel()
        }

        builder.setNegativeButton("Delete") { dialog, p1 ->
            deleteEmployee(employeeList!!.get(position).uuid)
            dialog.cancel()
        }


        builder.show()
    }

    private fun deleteEmployee(uuid: String){
        try {
            databaseReference.child("Employee").child(uuid).removeValue()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTime(): String {
        val sdf = SimpleDateFormat("hh:mm:ss a, dd MMM yyyy")
        //val sdf = SimpleDateFormat("hh:mm:ss a")
        var currentDate = sdf.format(Date())
        Log.i("MainActivity","currentTime: $currentDate")
        return currentDate.toString()
    }

    private fun getDate(): String {
        val sdf = SimpleDateFormat("EEEE, dd/M/yyyy")
        val currentDate = sdf.format(Date())
        Log.i("MainActivity","currentTime: $currentDate")
        return currentDate.toString()
    }

}