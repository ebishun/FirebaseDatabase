package promosys.com.firebasedatabase;

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView

class AttendanceAdapter(private val myList: ArrayList<Employee>) : RecyclerView.Adapter<AttendanceAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val context: Context = view.context
        var activityMain: AuntheticatedActivity? = null
        init {
            activityMain = context as AuntheticatedActivity
        }

        val txt_employee_name: TextView = view.findViewById(R.id.txt_employee_name)
        val txt_timestamp: TextView = view.findViewById(R.id.txt_timestamp)
        val imgStatusIcon: ImageView = view.findViewById(R.id.imgStatusIcon)
        val layoutImg: RelativeLayout = view.findViewById(R.id.relative_layout_2)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_layout, parent, false)

        return MyViewHolder(itemView)
    }

override fun onBindViewHolder(holder: AttendanceAdapter.MyViewHolder, position: Int) {
    val myObject = myList[position]

    holder.txt_employee_name.text = myObject.name
    holder.txt_timestamp.text = myObject.timestamp

    if(myObject.attend_status == 0){
        holder.txt_employee_name.setBackgroundResource(R.color.colorDefault)
        holder.txt_timestamp.setBackgroundResource(R.color.colorDefault)
        holder.layoutImg.setBackgroundResource(R.color.colorDefault_light)
        holder.imgStatusIcon.setImageResource(R.drawable.ic_check_box_outline_blank_black_24dp)

    }else if(myObject.attend_status == 1){
        holder.txt_employee_name.setBackgroundResource(R.color.colorPresent)
        holder.txt_timestamp.setBackgroundResource(R.color.colorPresent)
        holder.layoutImg.setBackgroundResource(R.color.colorPresent_light)
        holder.imgStatusIcon.setImageResource(R.drawable.ic_check_circle_black_24dp)

    }else if(myObject.attend_status == 2 || myObject.attend_status == 3){
        holder.txt_employee_name.setBackgroundResource(R.color.colorAbsent)
        holder.txt_timestamp.setBackgroundResource(R.color.colorAbsent)
        holder.layoutImg.setBackgroundResource(R.color.colorAbsent_light)
        holder.imgStatusIcon.setImageResource(R.drawable.ic_cancel_black_24dp)
    }

    //for click on view
    holder.itemView.setOnClickListener {
        holder.activityMain!!.fromAttendanceAdapter(position)
    }

    //for long click
    holder.itemView.setOnLongClickListener {
        holder.activityMain!!.fromAttendanceAdapterLongPressed(position)
        return@setOnLongClickListener true
    }
}

override fun getItemCount(): Int {
    return myList.size
}

}