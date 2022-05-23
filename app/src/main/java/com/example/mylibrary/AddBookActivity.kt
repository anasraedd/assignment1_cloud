package com.example.mylibrary

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_add_book.*
import kotlinx.android.synthetic.main.add_rate.rating
import kotlinx.android.synthetic.main.item_book.*
import java.util.*
import kotlin.collections.ArrayList

class AddBookActivity : AppCompatActivity() {
    var db: FirebaseFirestore? = null
    var list_name_categories = ArrayList<String>()
    var adapter: ArrayAdapter<String>? = null
    var rate: Float? = null
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_book)
        db = Firebase.firestore
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait")
        progressDialog.setCanceledOnTouchOutside(false)

        getCategories()
        list_name_categories.add("اختر التصنيف")

        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, list_name_categories)
        sp_categories.adapter  = adapter

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(R.layout.add_rate)




        
bottomSheetDialog.rating.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
rate =rating
    tv_rating.text = "$rating"
    add_rating.text = "تعديل التقييم"
   // Toast.makeText(this, "rating is : $rating ", Toast.LENGTH_LONG).show()
}



        add_rating.setOnClickListener {
           bottomSheetDialog.show()
        }

        btn_back.setOnClickListener {
            onBackPressed()
        }
        btn_cancle.setOnClickListener {
//            val bottomSheetDialog = BottomSheetDialog(this)
//            bottomSheetDialog.setContentView(R.layout.cancle_and_back)
            onBackPressed()

        }

        btn_seve_and_add.setOnClickListener {
//            var id = UUID.randomUUID().toString()
//            var name = et_name_book.text.toString()
//           var description = etdescription.text.toString()
//            var author = et_name_author.text.toString()
//            var yearRealese = et_year_relese.text.toString()
//            var rate = rate
//            var category = sp_categories.selectedItem.toString()

            AddBook(UUID.randomUUID().toString(), et_name_book.text.toString(),
                etdescription.text.toString(), et_name_author.text.toString(),
                rate, sp_categories.selectedItem.toString())
            //et_year_relese.text.toString(),
        }

    }

    private fun AddBook(id:String, name:String, desciption:String, author:String, rate: Float?, nameCategory:String ) { //yearRealese:String,
     if (id.isNotEmpty() && name.isNotEmpty()  && desciption.isNotEmpty()  && author.isNotEmpty() && rate != null
         && nameCategory != null && nameCategory != "اختر التصنيف"
     ){
             progressDialog.show()
//         var date= Date()
//         date.year = 2019
         val book = hashMapOf(
             "id" to id,
             "name" to name,
             "desciption" to desciption,
             "author" to author,
             "yearRealese" to Timestamp(Date()),
             "rate" to rate,
             "nameCategory" to nameCategory,
             "isFavorite" to false
         )

         db!!.collection("books").document("$id")
             .set(book)
             .addOnSuccessListener { documentReference ->
                 // Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
progressDialog.dismiss()
                 onBackPressed()
             }
             .addOnFailureListener { e ->
                 // Log.w(TAG, "Error adding document", e)
                 progressDialog.dismiss()
                 Toast.makeText(this, "أعد المحاولة فشلت عملية حفظ البيانات", Toast.LENGTH_LONG).show()
             }
     }else{
         Toast.makeText(this, "يجب ملئ البيانات المطلوبة", Toast.LENGTH_LONG).show()
     }
     }


    fun getCategories() {
        db!!.collection("categories").get()
            .addOnSuccessListener { querySnapshot ->
                for (i in 0 until querySnapshot.documents.size) {
                    list_name_categories.add(querySnapshot.documents.get(i).get("name_category").toString())

                }

            }
    }
}