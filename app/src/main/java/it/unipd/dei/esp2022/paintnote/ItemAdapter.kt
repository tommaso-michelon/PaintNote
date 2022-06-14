package it.unipd.dei.esp2022.paintnote

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import it.unipd.dei.esp2022.paintnote.activity.MainActivity
import it.unipd.dei.esp2022.paintnote.activity.NoteActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Adapter for the recycler view in MainActivity, it displays Note objects
 */
class ItemAdapter(private val context: Context, private val dataset: ArrayList<Note>): RecyclerView.Adapter<ItemAdapter.ItemViewHolder>()  {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a Note object.
    class ItemViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val itemConstraintLayout: ConstraintLayout = view.findViewById(R.id.itemConstraintLayout)
        val itemTitle: TextView = view.findViewById(R.id.item_title)
        val shareBtn: ImageButton = view.findViewById(R.id.shareBtn)
        val deleteBtn: ImageButton = view.findViewById(R.id.deleteBtn)
    }

    /**
     * Function that creates new views,
     * called by the layout manager
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(adapterLayout)
    }

    /**
     * Function that replaces the contents of a view,
     * called by the layout manager
     */
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = dataset[position]    //item is the current note
        holder.itemTitle.text =  item.title

        //background color
        if(position % 2 == 0 ){
            holder.itemConstraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            holder.shareBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
            holder.deleteBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.black))
        } else {
            holder.itemConstraintLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.item1))
            holder.shareBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.item1))
            holder.deleteBtn.setBackgroundColor(ContextCompat.getColor(context, R.color.item1))
        }

        holder.itemTitle.setOnClickListener {
            val intent = Intent(it.context, NoteActivity::class.java)
            intent.putExtra("id", item.id)
            it.context.startActivity(intent)
        }

        holder.shareBtn.setOnClickListener {
            if(context is MainActivity) shareBitmap(context, item.bitmap!!)
        }

        holder.deleteBtn.setOnClickListener {
            if(context is MainActivity) context.deleteNote(item)
        }

    }

    /**
     * Function that returns the size of the dataset
     */
    override fun getItemCount(): Int {
        return dataset.size
    }

    /**
     * Function that allows the sharing of the note's paint as an image
     */
    private fun shareBitmap(context: Context, bitmap: Bitmap) {
        try {
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()  //make directory
            val stream = FileOutputStream("$cachePath/image.png")   //overwrites this image every time
            bitmap.compress(CompressFormat.PNG, 100, stream)
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val imagePath = File(context.cacheDir, "images")
        val newFile = File(imagePath, "image.png")
        val contentUri = FileProvider.getUriForFile(context, "it.unipd.dei.esp2022.paintnote.fileprovider", newFile)

        if(contentUri != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //temp permission for receiving app to read this file
            shareIntent.setDataAndType(contentUri, context.contentResolver.getType(contentUri))
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
            context.startActivity(Intent.createChooser(shareIntent, "Choose an app"))
        }
    }


}
